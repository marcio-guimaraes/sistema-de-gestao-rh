package com.example.sistemarh.candidatura.controller;

// Importações dos Models
import com.example.sistemarh.candidatura.model.Candidato;
import com.example.sistemarh.candidatura.model.Candidatura;
import com.example.sistemarh.recrutamento.model.Vaga;

// Importações dos Services (o "cérebro")
import com.example.sistemarh.candidatura.service.CandidatoService;
import com.example.sistemarh.candidatura.service.CandidaturaService;
import com.example.sistemarh.candidatura.service.ExclusaoInvalidaException;
import com.example.sistemarh.recrutamento.service.VagaService;

// Importações do Spring
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cadastro")
public class CandidaturaController {

    // --- Injeção dos Services ---
    private final CandidatoService candidatoService;
    private final CandidaturaService candidaturaService;
    private final VagaService vagaService;

    public CandidaturaController(CandidatoService candidatoService,
                                 CandidaturaService candidaturaService,
                                 VagaService vagaService) {
        this.candidatoService = candidatoService;
        this.candidaturaService = candidaturaService;
        this.vagaService = vagaService;
    }

    // --- Métodos para "Cadastro de Candidato" ---
    @GetMapping("/candidato")
    public String mostrarFormularioCadastroCandidato(Model model) {
        Candidato.Builder dummyBuilder = new Candidato.Builder("Novo Candidato", "000.000.000-00");
        model.addAttribute("candidato", dummyBuilder.build());
        return "cadastro/cadastroCandidato";
    }

    @PostMapping("/salvar-candidato")
    public String processarCadastroCandidato(
            @RequestParam String nome, @RequestParam String cpf,
            @RequestParam(required = false) String formacao,
            @RequestParam(required = false) String experiencia,
            @RequestParam(required = false) Double pretensaoSalarial,
            @RequestParam(required = false) String disponibilidade,
            @RequestParam(required = false) String documentosAdicionais,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataCadastro,
            RedirectAttributes redirectAttributes) {

        try {
            Candidato.Builder builder = new Candidato.Builder(nome, cpf);

            if (formacao != null) builder.formacao(formacao);
            if (experiencia != null) builder.experiencia(experiencia);
            if (pretensaoSalarial != null) builder.pretensaoSalario(pretensaoSalarial);
            if (disponibilidade != null) builder.disponibilidade(disponibilidade);
            if (dataCadastro != null) builder.dataCadastro(dataCadastro);
            if (documentosAdicionais != null) builder.documentosAdicionais(documentosAdicionais);

            candidatoService.criarCandidato(builder.build());
            redirectAttributes.addFlashAttribute("sucesso", "Candidato salvo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/cadastro/candidato";
    }

    // --- Métodos para "Candidatura à Vaga" ---

    @GetMapping("/candidatura")
    public String mostrarFormularioCandidaturaVaga(Model model) {
        List<Candidato> candidatos = candidatoService.listarTodosCandidatos();
        List<Vaga> vagas = vagaService.listarTodasVagas();

        // CORRIGIDO (era "todosCandidatos")
        model.addAttribute("candidatos", candidatos);
        // CORRIGIDO (era "todasVagas")
        model.addAttribute("vagas", vagas);

        return "cadastro/candidaturaVaga";
    }

    @PostMapping("/registrar-candidatura")
    public String processarRegistroCandidatura(

            @RequestParam String cpfCandidato,

            @RequestParam long idVaga,
            RedirectAttributes redirectAttributes) {
        try {
            candidaturaService.registrarCandidatura(cpfCandidato, idVaga);
            redirectAttributes.addFlashAttribute("sucesso", "Candidatura registrada com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/cadastro/status";
    }

    // --- Métodos para "Status da Candidatura" ---
    @GetMapping("/status")
    public String mostrarStatusCandidatura(Model model) {
        List<Candidatura> candidaturas = candidaturaService.listarTodasCandidaturas();
        model.addAttribute("candidaturas", candidaturas);
        return "cadastro/statusCandidatura";
    }

    @GetMapping("/status/excluir")
    public String excluirCandidatura(@RequestParam long id, RedirectAttributes redirectAttributes) {
        try {
            candidaturaService.excluirCandidatura(id);
            redirectAttributes.addFlashAttribute("sucesso", "Candidatura excluída com sucesso!");
        } catch (ExclusaoInvalidaException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/cadastro/status";
    }

    @GetMapping
    public String menuCadastro() {
        return "cadastro/menu";
    }
}