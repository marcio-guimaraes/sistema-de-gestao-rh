package com.example.sistemarh.recrutamento.controller;

import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioService;
import com.example.sistemarh.candidatura.Candidatura; // Importar
import com.example.sistemarh.candidatura.CandidaturaService;
import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.service.ContratacaoService;
import com.example.sistemarh.recrutamento.service.EntrevistaService;
import com.example.sistemarh.recrutamento.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recrutamento")
public class RecrutamentoController {

    @Autowired
    private VagaService vagaService;

    @Autowired
    private CandidaturaService candidaturaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EntrevistaService entrevistaService;

    @Autowired
    private ContratacaoService contratacaoService;

    @GetMapping
    public String menuRecrutamento() {
        return "recrutamento/menu";
    }


    // --- TASK 2: FILTROS DE VAGA ---
    @GetMapping("/gestao-vagas")
    public String gestaoVagas(Model model,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String departamento,
                              @RequestParam(required = false) Double salarioMin) {

        model.addAttribute("vaga", new Vaga());

        // Usa o VagaService para filtrar
        List<Vaga> vagasFiltradas = vagaService.filtrarVagas(status, departamento, salarioMin);
        model.addAttribute("vagas", vagasFiltradas);

        model.addAttribute("editMode", false);

        // Devolve os filtros para o HTML
        Map<String, String> filtrosAtuais = new HashMap<>();
        if (status != null) filtrosAtuais.put("status", status);
        if (departamento != null) filtrosAtuais.put("departamento", departamento);
        if (salarioMin != null) filtrosAtuais.put("salarioMin", salarioMin.toString());
        model.addAttribute("filtros", filtrosAtuais);

        return "recrutamento/gestao-vagas";
    }
    // --- FIM DA TASK 2 ---


    @PostMapping("/gestao-vagas/salvar")
    public String salvarVaga(@ModelAttribute Vaga vaga) {
        if (vaga.getId() == 0) {
            vagaService.criarVaga(vaga);
        } else {
            vagaService.atualizarVaga(vaga.getId(), vaga);
        }
        return "redirect:/recrutamento/gestao-vagas";
    }

    @GetMapping("/gestao-vagas/editar/{id}")
    public String editarVaga(@PathVariable("id") long id, Model model) {
        Optional<Vaga> vagaOpt = vagaService.buscarVagaPorId(id);
        if (vagaOpt.isPresent()) {
            model.addAttribute("vaga", vagaOpt.get());
            model.addAttribute("vagas", vagaService.listarTodasVagas());
            model.addAttribute("editMode", true);
            model.addAttribute("filtros", new HashMap<String, String>()); // Mapa vazio para modo de edição
            return "recrutamento/gestao-vagas";
        }
        return "redirect:/recrutamento/gestao-vagas";
    }

    @GetMapping("/gestao-vagas/excluir/{id}")
    public String excluirVaga(@PathVariable("id") long id) {
        vagaService.excluirVaga(id);
        return "redirect:/recrutamento/gestao-vagas";
    }

    @GetMapping("/marcar-entrevista")
    public String marcarEntrevista(Model model) {
        model.addAttribute("candidaturas", candidaturaService.listarTodas().stream()
                .filter(c -> "Pendente".equalsIgnoreCase(c.getStatus()) || "Em Análise".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList()));

        model.addAttribute("recrutadores", usuarioService.listarTodos().stream()
                .filter(u -> u instanceof com.example.sistemarh.administracao.Gestor ||
                        (u instanceof com.example.sistemarh.financeiro.Funcionario &&
                                "Recrutador".equals(((com.example.sistemarh.financeiro.Funcionario)u).getCargo()))
                )
                .collect(Collectors.toList()));

        return "recrutamento/marcar-entrevista";
    }

    @PostMapping("/marcar-entrevista/salvar")
    public String salvarEntrevista(@RequestParam long candidaturaId,
                                   @RequestParam String recrutadorLogin,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data,
                                   @RequestParam String local) {

        entrevistaService.agendarEntrevista(candidaturaId, recrutadorLogin, data, local);
        return "redirect:/recrutamento/marcar-entrevista";
    }


    // --- MÉTODOS ATUALIZADOS PARA A TASK 1 ---
    @GetMapping("/avaliar-candidatos")
    public String avaliarCandidatos(Model model) {
        model.addAttribute("candidaturasEmAnalise", candidaturaService.listarComFiltros(null, "Em Análise"));
        return "recrutamento/avaliar-candidatos";
    }

    @PostMapping("/avaliar/salvar")
    public String salvarAvaliacao(@RequestParam long candidaturaId,
                                  @RequestParam String status, // "Aprovado" ou "Reprovado"
                                  @RequestParam(required = false) Double nota,
                                  @RequestParam(required = false) String feedback,
                                  RedirectAttributes redirectAttributes) {

        try {
            Candidatura c = candidaturaService.buscarPorId(candidaturaId)
                    .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

            // 1. Salva o resultado da entrevista
            entrevistaService.salvarResultadoEntrevista(c.getCpfCandidatoDoArquivo(), c.getIdVagaDoArquivo(), nota, feedback);

            // 2. Atualiza o status da candidatura
            candidaturaService.atualizarStatus(candidaturaId, status);

            redirectAttributes.addFlashAttribute("success", "Candidatura " + status.toLowerCase() + " com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recrutamento/avaliar-candidatos";
    }
    // --- FIM DOS MÉTODOS ATUALIZADOS ---


    @GetMapping("/solicitar-contratacao")
    public String solicitarContratacao(Model model) {
        model.addAttribute("candidaturas", candidaturaService.listarTodas().stream()
                .filter(c -> "Aprovado".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList()));
        return "recrutamento/solicitar-contratacao";
    }

    @PostMapping("/solicitar-contratacao/salvar")
    public String salvarSolicitacao(@RequestParam long candidaturaId, RedirectAttributes redirectAttributes) {
        try {
            contratacaoService.solicitarContratacao(candidaturaId);
        } catch (RuntimeException e) {
            // CORREÇÃO: Enviar o erro de volta para a página
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/recrutamento/solicitar-contratacao";
        }
        return "redirect:/recrutamento/consultar-contratacoes";
    }


    @GetMapping("/consultar-contratacoes")
    public String consultarContratacoes(Model model) {
        model.addAttribute("contratacoes", contratacaoService.listarTodas());
        return "recrutamento/consultar-contratacoes";
    }

    @GetMapping("/consultar-contratacoes/aprovar/{id}")
    public String aprovarContratacao(@PathVariable("id") long id) {
        contratacaoService.aprovarContratacao(id);
        return "redirect:/recrutamento/consultar-contratacoes";
    }

    @GetMapping("/consultar-contratacoes/rejeitar/{id}")
    public String rejeitarContratacao(@PathVariable("id") long id) {
        contratacaoService.rejeitarContratacao(id);
        return "redirect:/recrutamento/consultar-contratacoes";
    }
}