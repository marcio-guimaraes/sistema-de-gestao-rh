package com.example.sistemarh.recrutamento.controller;

import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @GetMapping("/gestao-vagas")
    public String gestaoVagas(Model model) {
        // CORREÇÃO: Usar o construtor vazio
        model.addAttribute("vaga", new Vaga());
        model.addAttribute("vagas", vagaService.listarTodasVagas());
        model.addAttribute("editMode", false);
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


    @GetMapping("/solicitar-contratacao")
    public String solicitarContratacao(Model model) {
        model.addAttribute("candidaturas", candidaturaService.listarTodas().stream()
                .filter(c -> "Aprovado".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList()));
        return "recrutamento/solicitar-contratacao";
    }

    @PostMapping("/solicitar-contratacao/salvar")
    public String salvarSolicitacao(@RequestParam long candidaturaId) {
        try {
            contratacaoService.solicitarContratacao(candidaturaId);
        } catch (RuntimeException e) {
            return "redirect:/recrutamento/solicitar-contratacao?error=" + e.getMessage();
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