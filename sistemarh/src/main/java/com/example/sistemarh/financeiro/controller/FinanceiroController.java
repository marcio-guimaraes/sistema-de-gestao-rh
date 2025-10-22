package com.example.sistemarh.financeiro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    @GetMapping
    public String menuFinanceiro() {
        return "financeiro/menu";
    }

    @GetMapping("/cadastrar-funcionario")
    public String cadastrarFuncionario() {
        return "financeiro/cadastro-funcionario";
    }

    @GetMapping("/configurar-regras")
    public String configurarRegras() {
        return "financeiro/configurar-regras";
    }

    @GetMapping("/gerar-folha")
    public String gerarFolha() {
        return "financeiro/gerar-folha";
    }

    @GetMapping("/relatorio")
    public String relatorioFinanceiro() {
        return "financeiro/relatorio";
    }

    @GetMapping("/contracheques")
    public String contracheques() {
        return "financeiro/contracheques";
    }
}