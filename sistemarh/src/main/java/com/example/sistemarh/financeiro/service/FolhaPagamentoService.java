package com.example.sistemarh.financeiro.service;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.financeiro.repository.FolhaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FolhaPagamentoService {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    public FolhaPagamento gerarFolhaPagamento(int mes, int ano) {

        List<Funcionario> funcionariosAtivos = funcionarioService.listarAtivos();

        // Regra hardcoded para simplificar
        RegraSalario regraPadrao = new RegraSalario(1, "CLT", 0.0, 6.0, 500.0, 14.0, 27.5);

        double totalBruto = 0.0;
        double totalDescontos = 0.0;
        double totalLiquido = 0.0;

        for (Funcionario f : funcionariosAtivos) {
            f.setRegraSalario(regraPadrao);

            double salarioLiquido = f.calcularSalario();
            double salarioBruto = f.getBaseSalario() + f.getRegraSalario().getValorValeAlimentacao() + f.getRegraSalario().getValorValeTransporte();
            double descontos = salarioBruto - salarioLiquido;

            totalBruto += salarioBruto;
            totalDescontos += descontos;
            totalLiquido += salarioLiquido;
        }

        FolhaPagamento folha = new FolhaPagamento.Builder(0, mes, ano, LocalDate.now(), null)
                .valorTotalBruto(totalBruto)
                .valorTotalDescontos(totalDescontos)
                .valorTotalLiquido(totalLiquido)
                .build();

        return folhaPagamentoRepository.salvar(folha);
    }

    public List<FolhaPagamento> listarTodas() {
        return folhaPagamentoRepository.buscarTodas();
    }
}