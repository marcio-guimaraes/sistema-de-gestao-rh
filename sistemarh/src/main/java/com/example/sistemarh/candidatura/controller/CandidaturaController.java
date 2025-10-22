package com.example.sistemarh.candidatura.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cadastro")
public class CandidaturaController {

    @GetMapping
    public String menuCadastro() {
        return "cadastro/menu";
    }

    @GetMapping("/candidato")
    public String cadastroCandidato() {
        return "cadastro/cadastroCandidato";
    }

    @GetMapping("/candidatura")
    public String candidaturaVaga() {
        return "cadastro/candidaturaVaga";
    }

    @GetMapping("/status")
    public String statusCandidatura() {
        return "cadastro/statusCandidatura";
    }
}