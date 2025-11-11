package com.example.sistemarh.recrutamento.controller;

import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioService;
import com.example.sistemarh.candidatura.Candidatura;
import com.example.sistemarh.candidatura.CandidaturaService;
// IMPORT ADICIONADO
import com.example.sistemarh.financeiro.repository.RegraSalarialRepository;
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

    @Autowired
    private RegraSalarialRepository regraSalarialRepository;

    @GetMapping
    public String menuRecrutamento() {
        return "recrutamento/menu";
    }

    @GetMapping("/gestao-vagas")
    public String gestaoVagas(Model model,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String departamento,
                              @RequestParam(required = false) Double salarioMin) {

        model.addAttribute("vaga", new Vaga());

        List<Vaga> vagasFiltradas = vagaService.filtrarVagas(status, departamento, salarioMin);
        model.addAttribute("vagas", vagasFiltradas);

        model.addAttribute("editMode", false);

        Map<String, String> filtrosAtuais = new HashMap<>();
        if (status != null) filtrosAtuais.put("status", status);
        if (departamento != null) filtrosAtuais.put("departamento", departamento);
        if (salarioMin != null) filtrosAtuais.put("salarioMin", salarioMin.toString());
        model.addAttribute("filtros", filtrosAtuais);
        model.addAttribute("regrasSalariais", regraSalarialRepository.buscarTodas());

        return "recrutamento/gestao-vagas";
    }

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
            model.addAttribute("filtros", new HashMap<String, String>());
            model.addAttribute("regrasSalariais", regraSalarialRepository.buscarTodas());

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
                        (u instanceof com.example.sistemarh.financeiro.model.Funcionario &&
                                "Recrutador".equals(((com.example.sistemarh.financeiro.model.Funcionario)u).getCargo()))
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


    @GetMapping("/avaliar-candidatos")
    public String avaliarCandidatos(Model model) {
        // MODIFICAÇÃO 1 (Opcional): Mostrar "Pendentes" E "Em Análise"
        // Se você quiser que os candidatos "Pendentes" apareçam direto aqui (pulando o agendamento)
        // Descomente a linha abaixo e comente a original.

        // List<Candidatura> candidaturas = candidaturaService.listarTodas().stream()
        //         .filter(c -> "Em Análise".equalsIgnoreCase(c.getStatus()) || "Pendente".equalsIgnoreCase(c.getStatus()))
        //         .collect(Collectors.toList());
        // model.addAttribute("candidaturasEmAnalise", candidaturas);

        // Linha Original (Mantida por enquanto, mas se você pular o Passo 6, esta lista ficará vazia)
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

            // --- INÍCIO DA MODIFICAÇÃO (PASSO 7) ---
            // Só tentamos salvar o feedback se uma entrevista REALMENTE existir.
            // Se o Passo 6 foi pulado, isso evita o erro.
            try {
                entrevistaService.salvarResultadoEntrevista(c.getCpfCandidatoDoArquivo(), c.getIdVagaDoArquivo(), nota, feedback);
            } catch (RuntimeException e) {
                // Ignora o erro "Nenhuma entrevista agendada..."
                System.err.println("Aviso: " + e.getMessage() + ". O status será atualizado, mas o feedback não foi salvo.");
            }
            // --- FIM DA MODIFICAÇÃO ---

            candidaturaService.atualizarStatus(candidaturaId, status);

            redirectAttributes.addFlashAttribute("success", "Candidatura " + status.toLowerCase() + " com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recrutamento/avaliar-candidatos";
    }

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