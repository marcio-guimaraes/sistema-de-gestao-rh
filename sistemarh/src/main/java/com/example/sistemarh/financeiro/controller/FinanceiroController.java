package com.example.sistemarh.financeiro.controller;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.financeiro.service.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    // --- MÉTODOS GET (Exibir telas) ---

    @GetMapping
    public String menuFinanceiro() {
        return "financeiro/menu";
    }

    @GetMapping("/cadastrar-funcionario")
    public String cadastrarFuncionario() {
        return "financeiro/cadastro-funcionario";
    }

    @GetMapping("/configurar-regras")
    public String configurarRegras(Model model) {
        // BÔNUS: Carrega as regras atuais para exibir na tela
        RegraSalario regras = financeiroService.carregarRegras();
        if (regras == null) {
            // Se não houver regras, cria um objeto vazio para não quebrar a tela
            regras = new RegraSalario(1L, "Padrao", 0, 6, 0, 14, 27.5);
        }
        model.addAttribute("regras", regras);
        return "financeiro/configurar-regras";
    }

    @GetMapping("/gerar-folha")
    public String gerarFolha() {
        return "financeiro/gerar-folha";
    }

    @GetMapping("/relatorio")
    public String relatorioFinanceiro() {
        // Este método agora só exibe a tela de relatório vazia.
        // O POST em /gerar-folha é que vai processar e exibir os dados.
        return "financeiro/relatorio";
    }

    @GetMapping("/contracheques")
    public String contracheques() {
        return "financeiro/contracheques";
    }

    // --- MÉTODOS POST (Receber dados dos formulários) ---

    @PostMapping("/configurar-regras")
    public String salvarRegras(
            @RequestParam double valorValeTransporte,
            @RequestParam double percentualDescVT,
            @RequestParam double valorValeAlimentacao,
            @RequestParam double percentualINSS,
            @RequestParam double percentualRRF) {

        RegraSalario regras = new RegraSalario(
                1L, "Regra Padrão", valorValeTransporte, percentualDescVT,
                valorValeAlimentacao, percentualINSS, percentualRRF
        );

        financeiroService.salvarRegras(regras);
        return "redirect:/financeiro";
    }

    @PostMapping("/cadastrar-funcionario")
    public String salvarFuncionario(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String login,
            @RequestParam String senha,
            @RequestParam String matricula,
            @RequestParam LocalDate dataAdmissao,
            @RequestParam Double baseSalario,
            @RequestParam String status,
            @RequestParam String departamento,
            @RequestParam String cargo) {

        Funcionario novoFuncionario = new Funcionario(
                nome, cpf, login, senha, matricula, dataAdmissao,
                baseSalario, status, departamento, cargo
        );

        financeiroService.salvarNovoFuncionario(novoFuncionario);
        return "redirect:/financeiro";
    }

    @PostMapping("/gerar-folha")
    public String processarEExibirFolha(
            @RequestParam int mes,
            @RequestParam int ano,
            Model model) {

        try {
            FolhaPagamento folha = financeiroService.processarFolha(mes, ano);
            model.addAttribute("folhaProcessada", folha);
            return "financeiro/relatorio";

        } catch (Exception e) {
            System.err.println(e.getMessage());
            model.addAttribute("erro", "Erro ao processar folha: " + e.getMessage());
            return "financeiro/gerar-folha";
        }
    }
}