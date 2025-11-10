package com.example.sistemarh.administracao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Dentro de AdministracaoController.java
@Controller
@RequestMapping("/Administração") // Define a base da URL para este controller
public class AdministracaoController {

    @GetMapping
    public String menuAdministracao() {
        return "adm/menu";
    }

    @GetMapping("/Gestão")
    public String gestaoDeUsuarios() {
        return "adm/admGestao";
    }

    @GetMapping("/Relatório")
    public String gerarRelatorioAdm() {
        return "adm/admRelatorio";
    }

    // A rota de login e cadastro de usuário também é responsabilidade do admin
    // Pode ficar aqui ou em um controller separado de autenticação
    @GetMapping("/Login") // Exemplo: /Administração/Login
    public String realizaLogin() {
        return "adm/login";
    }

    @GetMapping("/Cadastro") // Exemplo: /Administração/Cadastro
    public String cadastro() {
        return "adm/cadastroNovoUsuario";
    }
}