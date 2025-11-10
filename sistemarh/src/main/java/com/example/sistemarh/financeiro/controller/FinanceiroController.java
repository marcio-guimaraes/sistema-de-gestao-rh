package com.example.sistemarh.financeiro.controller;

import com.example.sistemarh.financeiro.service.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

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