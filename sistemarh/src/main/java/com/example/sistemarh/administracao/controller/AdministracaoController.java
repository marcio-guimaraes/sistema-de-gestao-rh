package com.example.sistemarh.administracao.controller;
import com.example.sistemarh.financeiro.model.Funcionario;

import com.example.sistemarh.administracao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

import com.example.sistemarh.administracao.UsuarioDTO;

import com.example.sistemarh.administracao.UsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Administracao")
public class AdministracaoController {

    @Autowired
    private UsuarioService usuarioService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
    }

    @GetMapping
    public String menuAdministracao() {
        return "adm/menu";
    }

    @GetMapping("/Gestao")
    public String gestaoDeUsuarios(Model model,
                                   // Parâmetros de busca
                                   @RequestParam(name = "nome", required = false) String nomeParam,
                                   @RequestParam(name = "cpf", required = false) String cpfParam,
                                   @RequestParam(name = "status", required = false) String statusParam,
                                   @RequestParam(name = "departamento", required = false) String deptoParam) {

        // 1. Busca a lista COMPLETA de usuários (que pode conter Funcionario)
        List<Usuario> listaCompleta = usuarioService.listarTodos();

        // 2. Aplica a lógica de filtro
        List<Usuario> usuariosFiltrados = listaCompleta.stream()

                // --- FILTROS DE 'USUARIO' (Nome e CPF) ---
                .filter(usuario -> {
                    // Filtro de Nome (funciona para todos os Usuarios)
                    if (nomeParam != null && !nomeParam.isEmpty()) {
                        return usuario.getNome().toLowerCase().contains(nomeParam.toLowerCase());
                    }
                    return true;
                })
                .filter(usuario -> {
                    // Filtro de CPF (funciona para todos os Usuarios)
                    if (cpfParam != null && !cpfParam.isEmpty()) {
                        return usuario.getCpf().equals(cpfParam);
                    }
                    return true;
                })

                // --- FILTROS DE 'FUNCIONARIO' (Status e Departamento) ---
                .filter(usuario -> {
                    // Filtro de Status
                    if (statusParam == null || statusParam.isEmpty()) {
                        return true; // Se o filtro não foi preenchido, mantém o usuário
                    }

                    // !! AQUI ESTÁ A MUDANÇA !!
                    // Verifique se este 'usuario' é uma instância de 'Funcionario'
                    if (usuario instanceof Funcionario) {
                        // Se for, podemos converter (fazer o cast) com segurança
                        Funcionario func = (Funcionario) usuario;
                        // E agora podemos filtrar pelo status
                        return func.getStatus().equalsIgnoreCase(statusParam);
                    }

                    // Se o filtro de status foi preenchido, mas o usuário NÃO é um Funcionário,
                    // ele deve ser removido (pois não pode ter o status procurado).
                    return false;
                })
                .filter(usuario -> {
                    // Filtro de Departamento (mesma lógica do status)
                    if (deptoParam == null || deptoParam.isEmpty()) {
                        return true;
                    }

                    if (usuario instanceof Funcionario) {
                        Funcionario func = (Funcionario) usuario;
                        return func.getDepartamento().equalsIgnoreCase(deptoParam);
                    }

                    return false;
                })
                .collect(Collectors.toList()); // Coleta os resultados em uma nova lista

        // 3. Envia a lista FILTRADA para o model
        model.addAttribute("usuarios", usuariosFiltrados);

        return "adm/admGestao";
    }

    @GetMapping("/Relatorio")
    public String gerarRelatorioAdm(Model model,
                                    // Parâmetros de busca
                                    @RequestParam(name = "nome", required = false) String nomeParam,
                                    @RequestParam(name = "cpf", required = false) String cpfParam,
                                    @RequestParam(name = "status", required = false) String statusParam,
                                    @RequestParam(name = "departamento", required = false) String deptoParam) {

        // 1. Busca a lista COMPLETA de usuários (que pode conter Funcionario)
        List<Usuario> listaCompleta = usuarioService.listarTodos();

        // 2. Aplica a lógica de filtro
        List<Usuario> usuariosFiltrados = listaCompleta.stream()

                // --- FILTROS DE 'USUARIO' (Nome e CPF) ---
                .filter(usuario -> {
                    // Filtro de Nome (funciona para todos os Usuarios)
                    if (nomeParam != null && !nomeParam.isEmpty()) {
                        return usuario.getNome().toLowerCase().contains(nomeParam.toLowerCase());
                    }
                    return true;
                })
                .filter(usuario -> {
                    // Filtro de CPF (funciona para todos os Usuarios)
                    if (cpfParam != null && !cpfParam.isEmpty()) {
                        return usuario.getCpf().equals(cpfParam);
                    }
                    return true;
                })

                // --- FILTROS DE 'FUNCIONARIO' (Status e Departamento) ---
                .filter(usuario -> {
                    // Filtro de Status
                    if (statusParam == null || statusParam.isEmpty()) {
                        return true; // Se o filtro não foi preenchido, mantém o usuário
                    }

                    // !! AQUI ESTÁ A MUDANÇA !!
                    // Verifique se este 'usuario' é uma instância de 'Funcionario'
                    if (usuario instanceof Funcionario) {
                        // Se for, podemos converter (fazer o cast) com segurança
                        Funcionario func = (Funcionario) usuario;
                        // E agora podemos filtrar pelo status
                        return func.getStatus().equalsIgnoreCase(statusParam);
                    }

                    // Se o filtro de status foi preenchido, mas o usuário NÃO é um Funcionário,
                    // ele deve ser removido (pois não pode ter o status procurado).
                    return false;
                })
                .filter(usuario -> {
                    // Filtro de Departamento (mesma lógica do status)
                    if (deptoParam == null || deptoParam.isEmpty()) {
                        return true;
                    }

                    if (usuario instanceof Funcionario) {
                        Funcionario func = (Funcionario) usuario;
                        return func.getDepartamento().equalsIgnoreCase(deptoParam);
                    }

                    return false;
                })
                .collect(Collectors.toList()); // Coleta os resultados em uma nova lista

        // 3. Envia a lista FILTRADA para o model
        model.addAttribute("usuarios", usuariosFiltrados);

        return "adm/admRelatorio";
    }

    @GetMapping("/Relatório/export-csv") // URL NOVA para o export
    public void exportarRelatorioCSV(HttpServletResponse response, // Para escrever o arquivo

                                     // 1. Recebe EXATAMENTE os mesmos parâmetros de filtro
                                     @RequestParam(name = "nome", required = false) String nomeParam,
                                     @RequestParam(name = "cpf", required = false) String cpfParam,
                                     @RequestParam(name = "status", required = false) String statusParam,
                                     @RequestParam(name = "departamento", required = false) String deptoParam) throws IOException {

        // 2. Define o cabeçalho da resposta HTTP
        response.setContentType("text/csv; charset=utf-8"); // Tipo do arquivo
        response.setHeader("Content-Disposition", "attachment; filename=\"relatorio_usuarios.csv\""); // Nome do arquivo

        // 3. Roda a MESMA lógica de filtro que você já tem
        List<Usuario> listaCompleta = usuarioService.listarTodos();
        List<Usuario> usuariosFiltrados = listaCompleta.stream()
                .filter(usuario -> {
                    if (nomeParam != null && !nomeParam.isEmpty()) {
                        return usuario.getNome().toLowerCase().contains(nomeParam.toLowerCase());
                    }
                    return true;
                })
                .filter(usuario -> {
                    if (cpfParam != null && !cpfParam.isEmpty()) {
                        return usuario.getCpf().equals(cpfParam);
                    }
                    return true;
                })
                .filter(usuario -> {
                    if (statusParam == null || statusParam.isEmpty()) {
                        return true;
                    }
                    if (usuario instanceof Funcionario) {
                        return ((Funcionario) usuario).getStatus().equalsIgnoreCase(statusParam);
                    }
                    return false;
                })
                .filter(usuario -> {
                    if (deptoParam == null || deptoParam.isEmpty()) {
                        return true;
                    }
                    if (usuario instanceof Funcionario) {
                        return ((Funcionario) usuario).getDepartamento().equalsIgnoreCase(deptoParam);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 4. Escreve o arquivo CSV linha por linha
        PrintWriter writer = response.getWriter();

        // Escreve o cabeçalho (a primeira linha do CSV)
        writer.println("Nome;CPF;Email;Perfil;Status;Departamento");

        // Escreve os dados de cada usuário
        for (Usuario usuario : usuariosFiltrados) {
            // Valores padrão (caso não seja um funcionário)
            String perfil = "N/A";
            String status = "N/A";
            String depto = "N/A";
            String email = "N/A"; // Você precisa adicionar o getEmail() na classe Usuario

            // Tenta pegar o e-mail (login)
            if (usuario.getLogin() != null) {
                email = usuario.getLogin();
            }

            // Se for um Funcionário, pega os dados extras
            if (usuario instanceof Funcionario) {
                Funcionario func = (Funcionario) usuario;
                perfil = func.getCargo();
                status = func.getStatus();
                depto = func.getDepartamento();
            }

            // Monta a linha do CSV (usando ';' como separador)
            String linha = String.join(";",
                    "\"" + usuario.getNome() + "\"", // Coloca entre aspas para evitar erros
                    "\"" + usuario.getCpf() + "\"",
                    "\"" + email + "\"",
                    "\"" + perfil + "\"",
                    "\"" + status + "\"",
                    "\"" + depto + "\""
            );
            writer.println(linha);
        }

        writer.flush();
        writer.close();
    }

    @GetMapping("/Relatorio/export-pdf") // URL NOVA para o export
    public void exportarRelatorioPDF(HttpServletResponse response,
                                     // 1. Recebe EXATAMENTE os mesmos parâmetros de filtro
                                     @RequestParam(name = "nome", required = false) String nomeParam,
                                     @RequestParam(name = "cpf", required = false) String cpfParam,
                                     @RequestParam(name = "status", required = false) String statusParam,
                                     @RequestParam(name = "departamento", required = false) String deptoParam) throws IOException, DocumentException {

        // 2. Roda a MESMA lógica de filtro que você já tem
        List<Usuario> listaCompleta = usuarioService.listarTodos();
        List<Usuario> usuariosFiltrados = listaCompleta.stream()
                .filter(usuario -> { // Filtro de Nome
                    if (nomeParam != null && !nomeParam.isEmpty()) {
                        return usuario.getNome().toLowerCase().contains(nomeParam.toLowerCase());
                    }
                    return true;
                })
                .filter(usuario -> { // Filtro de CPF
                    if (cpfParam != null && !cpfParam.isEmpty()) {
                        return usuario.getCpf().equals(cpfParam);
                    }
                    return true;
                })
                .filter(usuario -> { // Filtro de Status (com check 'instanceof')
                    if (statusParam == null || statusParam.isEmpty()) return true;
                    if (usuario instanceof Funcionario) {
                        return ((Funcionario) usuario).getStatus().equalsIgnoreCase(statusParam);
                    }
                    return false;
                })
                .filter(usuario -> { // Filtro de Depto (com check 'instanceof')
                    if (deptoParam == null || deptoParam.isEmpty()) return true;
                    if (usuario instanceof Funcionario) {
                        return ((Funcionario) usuario).getDepartamento().equalsIgnoreCase(deptoParam);
                    }
                    return false;
                })
                .collect(Collectors.toList());

        // 3. Define o cabeçalho da resposta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"relatorio_usuarios.pdf\"");

        // 4. Inicia a criação do Documento PDF
        Document document = new Document(PageSize.A4.rotate()); // A4 em modo paisagem
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // 5. Define fontes
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontHeader.setSize(12);
        fontHeader.setColor(Color.WHITE);

        Font fontBody = FontFactory.getFont(FontFactory.HELVETICA);
        fontBody.setSize(10);

        // 6. Adiciona um Título
        Paragraph title = new Paragraph("Relatório de Usuários Cadastrados", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // 7. Cria a Tabela (6 colunas, como no seu HTML)
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{3f, 2f, 3f, 2f, 1.5f, 2.5f}); // Largura relativa das colunas

        // 8. Cria o Cabeçalho da Tabela
        String[] headers = {"Nome", "CPF", "Email", "Perfil", "Status", "Departamento"};
        for (String headerTitle : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new Color(51, 51, 51)); // Cor escura (dark)
            cell.setPadding(5);
            cell.setPhrase(new Phrase(headerTitle, fontHeader));
            cell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
            table.addCell(cell);
        }

        // 9. Adiciona os dados dos usuários na tabela
        for (Usuario usuario : usuariosFiltrados) {
            // Valores padrão
            String perfil = "N/A";
            String status = "N/A";
            String depto = "N/A";
            String email = usuario.getLogin(); // Assumindo que login é o email

            // Se for um Funcionário, pega os dados extras
            if (usuario instanceof Funcionario) {
                Funcionario func = (Funcionario) usuario;
                perfil = func.getCargo();
                status = func.getStatus();
                depto = func.getDepartamento();
            }

            table.addCell(new Phrase(usuario.getNome(), fontBody));
            table.addCell(new Phrase(usuario.getCpf(), fontBody));
            table.addCell(new Phrase(email, fontBody));
            table.addCell(new Phrase(perfil, fontBody));
            table.addCell(new Phrase(status, fontBody));
            table.addCell(new Phrase(depto, fontBody));
        }

        // 10. Adiciona a tabela ao documento
        document.add(table);

        // 11. Fecha o documento
        document.close();
    }

    @GetMapping("/Login")
    public String realizaLoginGet() {
        return "adm/login";
    }

    @PostMapping("/Login")
    public String realizaLoginPost(@RequestParam String username, @RequestParam String password) {
        // Correção: Adicionado tratamento para senha nula/vazia
        if (password == null || password.isEmpty()) {
            return "redirect:/Administracao/Login?error=true";
        }

        boolean loginValido = usuarioService.validarLogin(username, password);

        if (loginValido) {
            return "redirect:/";
        } else {
            return "redirect:/Administracao/Login?error=true";
        }
    }

    @GetMapping("/Cadastro")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("editMode", false);
        // Retorna o nome do seu arquivo HTML (sem .html)
        return "adm/cadastroNovoUsuario";
    }

    /**
     * 2. GET (EDITAR): Mostra o formulário preenchido
     */
    @GetMapping("/Gestao/Editar/{cpf}")
    public String mostrarFormularioEdicao(@PathVariable("cpf") String cpf, Model model) {

        // 1. Busca o usuário no seu service/repository
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorCpf(cpf);

        if (usuarioOpt.isEmpty()) {
            // Se não achar, volta para a lista
            return "redirect:/Administracao/Gestao";
        }

        Usuario usuario = usuarioOpt.get();
        UsuarioDTO dto = new UsuarioDTO();

        // 2. Converte a Entidade para o DTO
        dto.setNome(usuario.getNome());
        dto.setCpf(usuario.getCpf());
        dto.setLogin(usuario.getLogin());
        // (Não enviamos a senha para o HTML)

        // 3. Se for um Funcionário, preenche os campos extras
        if (usuario instanceof Funcionario) {
            Funcionario f = (Funcionario) usuario;
            dto.setMatricula(f.getMatricula());
            dto.setDataAdmissao(f.getDataAdmissao());
            dto.setBaseSalario(f.getBaseSalario());
            dto.setStatus(f.getStatus());
            dto.setDepartamento(f.getDepartamento());
            dto.setPerfil(f.getCargo());
        }

        // 4. Envia o DTO e o 'editMode' para o HTML
        model.addAttribute("usuarioDTO", dto);
        model.addAttribute("editMode", true);

        // 5. Retorna o nome do seu arquivo de formulário
        return "adm/cadastroNovoUsuario";
    }

    @GetMapping("/Gestao/Excluir/{cpf}")
    public String handleDelete(@PathVariable("cpf") String cpf) {

        try {
            usuarioService.excluirUsuario(cpf); // Chama o repository
        } catch (Exception e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            // (Você pode adicionar uma mensagem de erro na tela aqui)
        }

        // Volta para a lista de gestão
        return "redirect:/Administracao/Gestao";
    }

    /**
     * 3. POST (SALVAR / ATUALIZAR): Recebe os dados do formulário
     */
    @PostMapping("/Salvar")
    public String salvar(@ModelAttribute("usuarioDTO") UsuarioDTO dto,
                         BindingResult bindingResult, // <-- Apanha os erros de conversão
                         RedirectAttributes ra,
                         Model model) { // <-- Adicionado para o caso de erro

        // 3. Verifica se o BindingResult apanhou algum erro (ex: "" para Double)
        if (bindingResult.hasErrors()) {
            System.err.println("### ERRO DE BINDING (400) DETETADO ###");
            // Itera pelos erros e imprime no console (para depuração)
            for (FieldError error : bindingResult.getFieldErrors()) {
                System.err.println("Campo: " + error.getField() + " - Erro: " + error.getDefaultMessage());
            }

            // Se deu erro, não tenta salvar. Devolve o utilizador ao formulário.

            // Verifica se estávamos no modo de edição para devolver o utilizador corretamente
            if (dto.getCpf() != null && !dto.getCpf().isEmpty()) {
                model.addAttribute("editMode", true);
            } else {
                model.addAttribute("editMode", false);
            }

            // Adiciona os erros ao Model para que o HTML os possa mostrar
            model.addAttribute("bindingResult", bindingResult);
            // Retorna o utilizador PARA O FORMULÁRIO (não um redirect)
            return "adm/cadastroNovoUsuario";
        }

        // 4. Se não houve erros de binding, a sua lógica de salvar continua
        try {
            usuarioService.salvarDto(dto);

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao salvar: " + e.getMessage());
            ra.addFlashAttribute("usuarioDTO", dto);

            if (usuarioService.buscarPorCpf(dto.getCpf()).isPresent()) {
                return "redirect:/Administracao/Gestao/Editar/" + dto.getCpf();
            }
            return "redirect:/Administracao/Cadastro";
        }

        return "redirect:/Administracao/Gestao"; // Sucesso!
    }
}