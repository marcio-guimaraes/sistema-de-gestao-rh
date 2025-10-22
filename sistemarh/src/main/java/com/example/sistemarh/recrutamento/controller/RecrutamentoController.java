package com.example.sistemarh.recrutamento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recrutamento")
public class RecrutamentoController {

    @GetMapping
    public String menuRecrutamento() {
        return "recrutamento/menu";
    }

    @GetMapping("/cadastrar-candidato")
    public String cadastrarCandidato() {
        return "recrutamento/cadastro-candidato";
    }

    @GetMapping("/realizar-candidatura")
    public String realizarCandidatura() {
        return "recrutamento/realizar-candidatura";
    }

    @GetMapping("/marcar-entrevista")
    public String marcarEntrevista() {
        return "recrutamento/marcar-entrevista";
    }

    @GetMapping("/solicitar-contratacao")
    public String solicitarContratacao() {
        return "recrutamento/solicitar-contratacao";
    }

    @GetMapping("/consultar-contratacoes")
    public String consultarContratacoes() {
        return "recrutamento/consultar-contratacoes";
    }
}