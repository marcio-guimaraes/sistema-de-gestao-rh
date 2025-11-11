package com.example.sistemarh.financeiro.controller;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.repository.RegraSalarialRepository;
import com.example.sistemarh.financeiro.service.FuncionarioService;
import com.example.sistemarh.financeiro.service.FolhaPagamentoService;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.recrutamento.service.ContratacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ContratacaoService contratacaoService;

    @Autowired
    private FolhaPagamentoService folhaPagamentoService;

    @Autowired
    private RegraSalarialRepository regraSalarialRepository;

    private RegraSalario getRegraPadraoFallback() {
        return regraSalarialRepository.buscarPorId(1)
                .orElse(new RegraSalario(1, "Padrão", 0.0, 0.0, 0.0, 0.0, 0.0));
    }

    private void carregarRegraSalarialDoFuncionario(Funcionario f) {
        if (f == null) return;

        RegraSalario regra = regraSalarialRepository.buscarPorId(f.getRegraSalarialId())
                .orElse(getRegraPadraoFallback()); // Usa a Regra 1 como fallback

        f.setRegraSalario(regra);
    }

    @GetMapping
    public String menuFinanceiro() {
        return "financeiro/menu";
    }

    @GetMapping("/cadastrar-funcionario")
    public String cadastrarFuncionario(Model model) {
        List<com.example.sistemarh.recrutamento.model.Contratacao> aprovados =
                contratacaoService.listarTodas().stream()
                        .filter(c -> "Aprovada pelo Gestor".equalsIgnoreCase(c.getStatus()))
                        .collect(Collectors.toList());

        model.addAttribute("contratacoesAprovadas", aprovados);
        model.addAttribute("regrasSalariais", regraSalarialRepository.buscarTodas());
        model.addAttribute("funcionarios", funcionarioService.listarAtivos());

        return "financeiro/cadastro-funcionario";
    }

    @PostMapping("/cadastrar-funcionario/admitir")
    public String admitirFuncionario(@RequestParam long contratacaoId,
                                     @RequestParam String cargo,
                                     @RequestParam String departamento,
                                     @RequestParam double salario,
                                     @RequestParam long regraSalarialId) {
        try {
            funcionarioService.admitirFuncionario(contratacaoId, salario, cargo, departamento, regraSalarialId);
        } catch (RuntimeException e) {
            return "redirect:/financeiro/cadastrar-funcionario?error=" + e.getMessage();
        }
        return "redirect:/financeiro/cadastrar-funcionario";
    }

    @PostMapping("/cadastrar-funcionario/atualizar")
    public String atualizarFuncionario(@RequestParam String cpf,
                                       @RequestParam String cargo,
                                       @RequestParam String departamento,
                                       @RequestParam double salario,
                                       @RequestParam long regraSalarialId) {
        try {
            funcionarioService.atualizarFuncionario(cpf, cargo, departamento, salario, regraSalarialId);
        } catch (RuntimeException e) {
            return "redirect:/financeiro/cadastrar-funcionario?errorUpdate=" + e.getMessage();
        }
        return "redirect:/financeiro/cadastrar-funcionario";
    }

    @GetMapping("/configurar-regras")
    public String configurarRegras(Model model) {
        model.addAttribute("regras", regraSalarialRepository.buscarTodas());
        model.addAttribute("novaRegra", new RegraSalario());
        model.addAttribute("editMode", false);
        return "financeiro/configurar-regras";
    }

    @GetMapping("/configurar-regras/editar/{id}")
    public String editarRegraGet(@PathVariable("id") long id, Model model) {
        Optional<RegraSalario> regraOpt = regraSalarialRepository.buscarPorId(id);

        if (regraOpt.isPresent()) {
            model.addAttribute("novaRegra", regraOpt.get());
        } else {
            return "redirect:/financeiro/configurar-regras";
        }

        model.addAttribute("regras", regraSalarialRepository.buscarTodas());
        model.addAttribute("editMode", true);
        return "financeiro/configurar-regras";
    }

    @PostMapping("/configurar-regras/salvar")
    public String salvarRegra(@ModelAttribute("novaRegra") RegraSalario novaRegra) {
        if (novaRegra.getNomeRegra() == null || novaRegra.getNomeRegra().isEmpty()) {
            novaRegra.setNomeRegra("Regra Padrão");
        }
        regraSalarialRepository.salvar(novaRegra);
        return "redirect:/financeiro/configurar-regras";
    }

    @GetMapping("/configurar-regras/excluir/{id}")
    public String excluirRegra(@PathVariable("id") long id) {
        if (id == 1) {
            return "redirect:/financeiro/configurar-regras";
        }
        regraSalarialRepository.excluirPorId(id);
        return "redirect:/financeiro/configurar-regras";
    }

    @GetMapping("/gerar-folha")
    public String gerarFolha(@RequestParam(required = false) Integer mes,
                             @RequestParam(required = false) Integer ano,
                             Model model) {

        LocalDate hoje = LocalDate.now();
        int mesSelecionado = (mes != null) ? mes : hoje.getMonthValue();
        int anoSelecionado = (ano != null) ? ano : hoje.getYear();

        model.addAttribute("mesSelecionado", mesSelecionado);
        model.addAttribute("anoSelecionado", anoSelecionado);

        if (mes != null && ano != null) {
            try {
                FolhaPagamento folha = folhaPagamentoService.calcularFolhaPagamento(mes, ano);
                model.addAttribute("folha", folha);
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        return "financeiro/gerar-folha";
    }

    @PostMapping("/gerar-folha/processar")
    public String processarFolha(@RequestParam int mes, @RequestParam int ano) {
        folhaPagamentoService.gerarFolhaPagamento(mes, ano);
        return "redirect:/financeiro/gerar-folha?mes=" + mes + "&ano=" + ano;
    }

    @GetMapping("/relatorio")
    public String relatorioFinanceiro(Model model) {
        List<Funcionario> funcionarios = funcionarioService.listarAtivos();

        for(Funcionario f : funcionarios) {
            carregarRegraSalarialDoFuncionario(f);
        }

        model.addAttribute("funcionarios", funcionarios);
        return "financeiro/relatorio";
    }

    @GetMapping("/contracheques")
    public String contracheques(@RequestParam(required = false) String cpf,
                                @RequestParam(required = false) Integer mes,
                                @RequestParam(required = false) Integer ano,
                                Model model) {

        List<Funcionario> funcionarios = funcionarioService.listarAtivos();
        model.addAttribute("funcionarios", funcionarios);

        LocalDate hoje = LocalDate.now();
        int mesSelecionado = (mes != null) ? mes : hoje.getMonthValue();
        int anoSelecionado = (ano != null) ? ano : hoje.getYear();

        LocalDate primeiroDia = LocalDate.of(anoSelecionado, mesSelecionado, 1);
        LocalDate ultimoDia = primeiroDia.with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String periodoReferencia = primeiroDia.format(formatter) + " a " + ultimoDia.format(formatter);

        model.addAttribute("mesSelecionado", mesSelecionado);
        model.addAttribute("anoSelecionado", anoSelecionado);
        model.addAttribute("periodoReferencia", periodoReferencia);

        Funcionario fSelecionado = null;

        if (cpf != null && !cpf.isEmpty()) {
            fSelecionado = funcionarioService.buscarPorCpf(cpf).orElse(null);
        } else if (!funcionarios.isEmpty()) {
            fSelecionado = funcionarios.get(0); // Pega o primeiro como padrão
        }

        if (fSelecionado != null) {
            carregarRegraSalarialDoFuncionario(fSelecionado);
            model.addAttribute("funcionario", fSelecionado);

            double salarioBruto = fSelecionado.getBaseSalario() + fSelecionado.getRegraSalario().getValorValeAlimentacao() + fSelecionado.getRegraSalario().getValorValeTransporte();
            double descontos = salarioBruto - fSelecionado.calcularSalario();

            model.addAttribute("salarioBruto", salarioBruto);
            model.addAttribute("totalDescontos", descontos);
            model.addAttribute("salarioLiquido", fSelecionado.calcularSalario());
        }

        return "financeiro/contracheques";
    }

    @GetMapping("/contracheques/txt")
    public void exportarContrachequeTXT(@RequestParam String cpf,
                                        @RequestParam Integer mes,
                                        @RequestParam Integer ano,
                                        HttpServletResponse response) {

        try {
            Funcionario fSelecionado = funcionarioService.buscarPorCpf(cpf)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

            carregarRegraSalarialDoFuncionario(fSelecionado);

            LocalDate primeiroDia = LocalDate.of(ano, mes, 1);
            String nomeMes = primeiroDia.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            String periodo = nomeMes + "/" + ano;

            response.setContentType("text/plain; charset=UTF-8"); // Garante UTF-8
            String nomeArquivo = "contracheque_" + cpf + "_" + mes + "_" + ano + ".txt";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");

            java.util.function.Function<Double, String> formatarMoeda = (valor) -> String.format(new Locale("pt", "BR"), "R$ %,.2f", valor);

            try (java.io.PrintWriter writer = response.getWriter()) {

                // --- Cabeçalho ---
                writer.println("+-------------------------------------------------------------------+");
                writer.println("|                      CONTRACHEQUE DE PAGAMENTO                    |");
                writer.println("+-------------------------------------------------------------------+");
                writer.printf("| FUNCIONÁRIO: %-52s |%n", fSelecionado.getNome());
                writer.printf("| CPF: %-60s |%n", fSelecionado.getCpf());
                writer.printf("| CARGO: %-58s |%n", fSelecionado.getCargo());
                writer.printf("| DEPTO: %-58s |%n", fSelecionado.getDepartamento());
                writer.printf("| PERÍODO: %-56s |%n", periodo);
                writer.println("+--------+-------------------------+-------------+------------------+");
                writer.println("| CÓDIGO | DESCRIÇÃO               | VENCIMENTOS | DESCONTOS        |");
                writer.println("+--------+-------------------------+-------------+------------------+");

                // --- Cálculos ---
                double base = fSelecionado.getBaseSalario();
                RegraSalario regra = fSelecionado.getRegraSalario();
                double va = regra.getValorValeAlimentacao();
                double vt = regra.getValorValeTransporte();

                double descInss = base * regra.getPercentualINSS() / 100;
                double descIrrf = base * regra.getPercentualRRF() / 100;
                double descVt = vt * regra.getPercentualDescVT() / 100;

                double totalVencimentos = base + va + vt;
                double totalDescontos = descInss + descIrrf + descVt;
                double liquido = fSelecionado.calcularSalario();

                // --- Linhas de Itens ---
                writer.printf("| 101    | %-23s | %11s | %-16s |%n", "SALÁRIO BASE", formatarMoeda.apply(base), "");
                writer.printf("| 102    | %-23s | %11s | %-16s |%n", "VALE-ALIMENTAÇÃO", formatarMoeda.apply(va), "");
                writer.printf("| 103    | %-23s | %11s | %-16s |%n", "VALE-TRANSPORTE (BASE)", formatarMoeda.apply(vt), "");

                writer.printf("| 901    | %-23s | %11s | %16s |%n", "INSS (" + regra.getPercentualINSS() + "%)", "", formatarMoeda.apply(descInss));
                writer.printf("| 902    | %-23s | %11s | %16s |%n", "IRRF (" + regra.getPercentualRRF() + "%)", "", formatarMoeda.apply(descIrrf));
                writer.printf("| 903    | %-23s | %11s | %16s |%n", "DESC. VALE-TRANSPORTE", "", formatarMoeda.apply(descVt));

                // --- Rodapé ---
                writer.println("+--------+-------------------------+-------------+------------------+");
                writer.printf("| TOTAIS:                          | %11s | %16s |%n", formatarMoeda.apply(totalVencimentos), formatarMoeda.apply(totalDescontos));
                writer.println("+----------------------------------+-------------+------------------+");

                String liquidoStr = formatarMoeda.apply(liquido);
                writer.printf("| LÍQUIDO A RECEBER:               | %30s |%n", liquidoStr); // 11 + 1 + 16 + 2 = 30
                writer.println("+----------------------------------+--------------------------------+");

            }

        } catch (Exception e) {
            response.setContentType("text/plain");
            try {
                response.sendError(500, "Erro ao gerar contracheque: " + e.getMessage());
            } catch (IOException ioException) {
            }
        }
    }

    @GetMapping("/gerar-folha/txt")
    public void exportarFolhaTXT(@RequestParam Integer mes,
                                 @RequestParam Integer ano,
                                 HttpServletResponse response) {
        try {
            // 1. Calcula (sem salvar)
            FolhaPagamento folha = folhaPagamentoService.calcularFolhaPagamento(mes, ano);

            // 2. Configura a resposta
            response.setContentType("text/plain; charset=UTF-8");
            String nomeArquivo = String.format("folha_pagamento_%d_%d.txt", mes, ano);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");

            java.util.function.Function<Double, String> fMoeda = (v) -> String.format(new Locale("pt", "BR"), "R$ %,.2f", v);

            // 3. Escreve o arquivo formatado
            try (java.io.PrintWriter writer = response.getWriter()) {

                writer.println("+---------------------------------------------------------------------------------+");
                writer.printf("| FOLHA DE PAGAMENTO - Referência: %-39s / %d |%n", folha.getMesPorExtenso().toUpperCase(), folha.getAnoReferencia());
                writer.println("+---------------------------------------------------------------------------------+");
                writer.println();
                writer.println("DETALHAMENTO POR FUNCIONÁRIO:");
                writer.println("+---------------------------+-----------------+--------------+------------+-------------+");
                writer.printf("| %-25s | %-15s | %-12s | %-11s | %-10s |%n", "Funcionário", "Sal. Base", "Benefícios", "Descontos", "Líquido");
                writer.println("+---------------------------+-----------------+--------------+------------+-------------+");

                if (folha.getFuncionarios() == null || folha.getFuncionarios().isEmpty()) {
                    writer.println("| Nenhum funcionário ativo encontrado para este período.                          |");
                } else {
                    for (Funcionario f : folha.getFuncionarios()) {
                        double base = f.getBaseSalario();
                        RegraSalario r = f.getRegraSalario();
                        double beneficios = r.getValorValeAlimentacao() + r.getValorValeTransporte();
                        double brutoFunc = base + beneficios;
                        double liquidoFunc = f.calcularSalario();
                        double descontosFunc = brutoFunc - liquidoFunc;

                        writer.printf("| %-25.25s | %15s | %12s | %10s | %10s |%n",
                                f.getNome(), fMoeda.apply(base), fMoeda.apply(beneficios), fMoeda.apply(descontosFunc), fMoeda.apply(liquidoFunc));
                    }
                }
                writer.println("+---------------------------+-----------------+--------------+------------+-------------+");
                writer.println();
                writer.println("TOTAIS DA FOLHA:");
                writer.println("+----------------------------------+");
                writer.printf("| Total Bruto:     %15s |%n", fMoeda.apply(folha.getValorTotalBruto()));
                writer.printf("| Total Descontos: %15s |%n", fMoeda.apply(folha.getValorTotalDescontos()));
                writer.printf("| Total Líquido:   %15s |%n", fMoeda.apply(folha.getValorTotalLiquido()));
                writer.println("+----------------------------------+");
            }

        } catch (Exception e) {
            response.setContentType("text/plain");
            try {
                response.sendError(500, "Erro ao gerar TXT da folha: " + e.getMessage());
            } catch (IOException ioException) {
                // Erro
            }
        }
    }
}