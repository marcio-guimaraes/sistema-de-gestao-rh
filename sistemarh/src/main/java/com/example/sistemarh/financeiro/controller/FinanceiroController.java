package com.example.sistemarh.financeiro.controller;

import com.example.sistemarh.financeiro.Funcionario;
import com.example.sistemarh.financeiro.FuncionarioService;
import com.example.sistemarh.financeiro.FolhaPagamentoService;
import com.example.sistemarh.financeiro.RegraSalario;
import com.example.sistemarh.recrutamento.service.ContratacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional; // Importar
import java.util.stream.Collectors;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private ContratacaoService contratacaoService;

    @Autowired
    private FolhaPagamentoService folhaPagamentoService;

    // REGRA PADRÃO (simplificação)
    private RegraSalario getRegraPadrao() {
        return new RegraSalario(1, "CLT", 0.0, 6.0, 500.0, 14.0, 27.5);
    }

    @GetMapping
    public String menuFinanceiro() {
        return "financeiro/menu";
    }

    @GetMapping("/cadastrar-funcionario")
    public String cadastrarFuncionario(Model model) {
        List<com.example.sistemarh.recrutamento.model.Contratacao> aprovados =
                contratacaoService.listarTodas().stream()
                        .filter(c -> "Aprovada pelo Gestor".equalsIgnoreCase(c.getStatus()))
                        .collect(Collectors.toList());

        model.addAttribute("contratacoesAprovadas", aprovados);
        return "financeiro/cadastro-funcionario";
    }

    @PostMapping("/cadastrar-funcionario/admitir")
    public String admitirFuncionario(@RequestParam long contratacaoId,
                                     @RequestParam String cargo,
                                     @RequestParam String departamento,
                                     @RequestParam double salario) {
        try {
            funcionarioService.admitirFuncionario(contratacaoId, salario, cargo, departamento);
        } catch (RuntimeException e) {
            return "redirect:/financeiro/cadastrar-funcionario?error=" + e.getMessage();
        }
        return "redirect:/financeiro/relatorio";
    }


    @GetMapping("/configurar-regras")
    public String configurarRegras() {
        return "financeiro/configurar-regras";
    }

    @GetMapping("/gerar-folha")
    public String gerarFolha() {
        return "financeiro/gerar-folha";
    }

    @PostMapping("/gerar-folha/processar")
    public String processarFolha(@RequestParam int mes, @RequestParam int ano) {
        folhaPagamentoService.gerarFolhaPagamento(mes, ano);
        return "redirect:/financeiro/relatorio";
    }

    @GetMapping("/relatorio")
    public String relatorioFinanceiro(Model model) {
        List<Funcionario> funcionarios = funcionarioService.listarAtivos();
        RegraSalario regraPadrao = getRegraPadrao();

        for(Funcionario f : funcionarios) {
            f.setRegraSalario(regraPadrao);
        }

        model.addAttribute("funcionarios", funcionarios);
        return "financeiro/relatorio";
    }

    @GetMapping("/contracheques")
    public String contracheques(@RequestParam(required = false) String cpf, Model model) {
        List<Funcionario> funcionarios = funcionarioService.listarAtivos();
        model.addAttribute("funcionarios", funcionarios);

        Funcionario fSelecionado = null;

        if (cpf != null && !cpf.isEmpty()) {
            fSelecionado = funcionarioService.buscarPorCpf(cpf).orElse(null);
        } else if (!funcionarios.isEmpty()) {
            fSelecionado = funcionarios.get(0); // Pega o primeiro como padrão
        }

        if (fSelecionado != null) {
            fSelecionado.setRegraSalario(getRegraPadrao());
            model.addAttribute("funcionario", fSelecionado);

            double salarioBruto = fSelecionado.getBaseSalario() + fSelecionado.getRegraSalario().getValorValeAlimentacao() + fSelecionado.getRegraSalario().getValorValeTransporte();
            double descontos = salarioBruto - fSelecionado.calcularSalario();

            model.addAttribute("salarioBruto", salarioBruto);
            model.addAttribute("totalDescontos", descontos);
            model.addAttribute("salarioLiquido", fSelecionado.calcularSalario());
        }

        return "financeiro/contracheques";
    }
}