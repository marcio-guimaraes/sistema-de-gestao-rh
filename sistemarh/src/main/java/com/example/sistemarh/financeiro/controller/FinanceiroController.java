package com.example.sistemarh.financeiro.controller;

import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.repository.RegraSalarialRepository;
import com.example.sistemarh.financeiro.service.FuncionarioService;
import com.example.sistemarh.financeiro.service.FolhaPagamentoService;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.recrutamento.service.ContratacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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

    @Autowired
    private RegraSalarialRepository regraSalarialRepository;

    private RegraSalario getRegraPadraoFallback() {
        return regraSalarialRepository.buscarPorId(1)
                .orElse(new RegraSalario(1, "Padrão", 0.0, 0.0, 0.0, 0.0, 0.0));
    }

    private void carregarRegraSalarialDoFuncionario(Funcionario f) {
        if (f == null) return;

        RegraSalario regra = regraSalarialRepository.buscarPorId(f.getRegraSalarialId())
                .orElse(getRegraPadraoFallback()); // Usa a Regra 1 como fallback

        f.setRegraSalario(regra);
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
        model.addAttribute("regrasSalariais", regraSalarialRepository.buscarTodas());
        model.addAttribute("funcionarios", funcionarioService.listarAtivos());

        return "financeiro/cadastro-funcionario";
    }

    @PostMapping("/cadastrar-funcionario/admitir")
    public String admitirFuncionario(@RequestParam long contratacaoId,
                                     @RequestParam String cargo,
                                     @RequestParam String departamento,
                                     @RequestParam double salario,
                                     @RequestParam long regraSalarialId) {
        try {
            funcionarioService.admitirFuncionario(contratacaoId, salario, cargo, departamento, regraSalarialId);
        } catch (RuntimeException e) {
            return "redirect:/financeiro/cadastrar-funcionario?error=" + e.getMessage();
        }
        return "redirect:/financeiro/cadastrar-funcionario";
    }

    @PostMapping("/cadastrar-funcionario/atualizar")
    public String atualizarFuncionario(@RequestParam String cpf,
                                       @RequestParam String cargo,
                                       @RequestParam String departamento,
                                       @RequestParam double salario,
                                       @RequestParam long regraSalarialId) {
        try {
            funcionarioService.atualizarFuncionario(cpf, cargo, departamento, salario, regraSalarialId);
        } catch (RuntimeException e) {
            return "redirect:/financeiro/cadastrar-funcionario?errorUpdate=" + e.getMessage();
        }
        return "redirect:/financeiro/cadastrar-funcionario";
    }

    @GetMapping("/configurar-regras")
    public String configurarRegras(Model model) {
        model.addAttribute("regras", regraSalarialRepository.buscarTodas());
        model.addAttribute("novaRegra", new RegraSalario());
        model.addAttribute("editMode", false);
        return "financeiro/configurar-regras";
    }

    @GetMapping("/configurar-regras/editar/{id}")
    public String editarRegraGet(@PathVariable("id") long id, Model model) {
        Optional<RegraSalario> regraOpt = regraSalarialRepository.buscarPorId(id);

        if (regraOpt.isPresent()) {
            model.addAttribute("novaRegra", regraOpt.get());
        } else {
            return "redirect:/financeiro/configurar-regras";
        }

        model.addAttribute("regras", regraSalarialRepository.buscarTodas());
        model.addAttribute("editMode", true);
        return "financeiro/configurar-regras";
    }

    @PostMapping("/configurar-regras/salvar")
    public String salvarRegra(@ModelAttribute("novaRegra") RegraSalario novaRegra) {
        if (novaRegra.getNomeRegra() == null || novaRegra.getNomeRegra().isEmpty()) {
            novaRegra.setNomeRegra("Regra Padrão");
        }
        regraSalarialRepository.salvar(novaRegra);
        return "redirect:/financeiro/configurar-regras";
    }

    @GetMapping("/configurar-regras/excluir/{id}")
    public String excluirRegra(@PathVariable("id") long id) {
        if (id == 1) {
            return "redirect:/financeiro/configurar-regras";
        }
        regraSalarialRepository.excluirPorId(id);
        return "redirect:/financeiro/configurar-regras";
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

        for(Funcionario f : funcionarios) {
            carregarRegraSalarialDoFuncionario(f);
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
            carregarRegraSalarialDoFuncionario(fSelecionado);
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