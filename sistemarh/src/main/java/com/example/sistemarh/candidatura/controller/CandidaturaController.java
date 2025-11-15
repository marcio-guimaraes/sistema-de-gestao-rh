package com.example.sistemarh.candidatura.controller;

import com.example.sistemarh.candidatura.Candidato;
import com.example.sistemarh.candidatura.CandidatoService;
import com.example.sistemarh.candidatura.CandidaturaService;
import com.example.sistemarh.recrutamento.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/cadastro")
public class CandidaturaController {

    @Autowired
    private CandidatoService candidatoService;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private CandidaturaService candidaturaService;

    @GetMapping
    public String menuCadastro() {
        return "cadastro/menu";
    }


    @GetMapping("/candidato")
    public String cadastroCandidatoGet(Model model) {
        model.addAttribute("candidato", new Candidato());
        model.addAttribute("editMode", false);
        return "cadastro/cadastroCandidato";
    }

    @PostMapping("/candidato/salvar")
    public String cadastroCandidatoPost(@ModelAttribute Candidato candidato,
                                        @RequestParam(defaultValue = "false") boolean isEditMode) {
        try {
            candidatoService.salvarCandidato(candidato, isEditMode);
        } catch (RuntimeException e) {

            String redirectUrl = isEditMode
                    ? "/cadastro/candidato/editar/" + candidato.getCpf()
                    : "/cadastro/candidato";

            return "redirect:" + redirectUrl + "?error=" + e.getMessage();
        }
        return "redirect:/cadastro/gestao-candidatos";
    }

    @GetMapping("/gestao-candidatos")
    public String gestaoCandidatos(Model model) {
        model.addAttribute("candidatos", candidatoService.listarTodos());
        return "cadastro/gestao-candidatos";
    }

    @GetMapping("/candidato/editar/{cpf}")
    public String editarCandidatoGet(@PathVariable("cpf") String cpf, Model model) {
        Optional<Candidato> candidatoOpt = candidatoService.buscarPorCpf(cpf);
        if (candidatoOpt.isPresent()) {
            model.addAttribute("candidato", candidatoOpt.get());
            model.addAttribute("editMode", true);
            return "cadastro/cadastroCandidato";
        }
        return "redirect:/cadastro/gestao-candidatos";
    }

    @GetMapping("/candidato/excluir/{cpf}")
    public String excluirCandidato(@PathVariable("cpf") String cpf) {
        candidaturaService.listarTodas().stream()
                .filter(c -> c.getCpfCandidatoDoArquivo().equals(cpf))
                .forEach(c -> {
                    try {
                        candidaturaService.excluirCandidaturaSePendente(c.getId());
                    } catch (RuntimeException e) {
                        System.err.println("Não foi possível excluir candidatura pendente: " + e.getMessage());
                    }
                });
        candidatoService.excluirCandidato(cpf);
        return "redirect:/cadastro/gestao-candidatos";
    }


    @GetMapping("/candidatura")
    public String candidaturaVagaGet(Model model) {
        model.addAttribute("candidatos", candidatoService.listarTodos());
        model.addAttribute("vagas", vagaService.filtrarVagas("Aberta", null, null));
        return "cadastro/candidaturaVaga";
    }

    @PostMapping("/registrar-candidatura")
    public String candidaturaVagaPost(@RequestParam String candidatoCpf,
                                      @RequestParam Long vagaId) {
        try {
            candidaturaService.registrarCandidatura(candidatoCpf, vagaId, "Pendente");
        } catch (RuntimeException e) {
            return "redirect:/cadastro/candidatura?error=" + e.getMessage();
        }
        return "redirect:/cadastro/status";
    }


    @GetMapping("/status")
    public String statusCandidatura(@RequestParam(required = false) Long vagaId,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String formacao,    // NOVO
                                    @RequestParam(required = false) String experiencia, // NOVO
                                    Model model) {

        model.addAttribute("vagas", vagaService.listarTodasVagas());


        model.addAttribute("candidaturas", candidaturaService.listarComFiltros(vagaId, status, formacao, experiencia)); // ATUALIZADO


        model.addAttribute("vagaFiltro", vagaId);
        model.addAttribute("statusFiltro", status);
        model.addAttribute("formacaoFiltro", formacao);
        model.addAttribute("experienciaFiltro", experiencia);

        return "cadastro/statusCandidatura";
    }


    @GetMapping("/candidatura/excluir/{id}")
    public String excluirCandidatura(@PathVariable("id") long id, Model model) {
        try {
            candidaturaService.excluirCandidatura(id);
        } catch (RuntimeException e) {
            return "redirect:/cadastro/status?error=" + e.getMessage();
        }
        return "redirect:/cadastro/status";
    }
}