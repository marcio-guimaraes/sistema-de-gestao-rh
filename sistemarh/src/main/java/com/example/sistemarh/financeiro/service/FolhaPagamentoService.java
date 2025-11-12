package com.example.sistemarh.financeiro.service;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.financeiro.repository.FolhaPagamentoRepository;
import com.example.sistemarh.financeiro.repository.RegraSalarialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class FolhaPagamentoService {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private RegraSalarialRepository regraSalarialRepository;

    private RegraSalario getRegraPadraoFallback() {
        return regraSalarialRepository.buscarPorId(1)
                .orElse(new RegraSalario(1, "CLT Padrão", 0.0, 6.0, 500.0, 14.0, 27.5));
    }

    public FolhaPagamento calcularFolhaPagamento(int mes, int ano) {
        List<Funcionario> funcionariosAtivos = funcionarioService.listarAtivos();

        double totalBruto = 0.0;
        double totalDescontos = 0.0;
        double totalLiquido = 0.0;

        for (Funcionario f : funcionariosAtivos) {
            RegraSalario regra = regraSalarialRepository.buscarPorId(f.getRegraSalarialId())
                    .orElse(getRegraPadraoFallback());
            f.setRegraSalario(regra);

            double salarioLiquido = f.calcularSalario();
            double salarioBruto = f.getBaseSalario() + f.getRegraSalario().getValorValeAlimentacao() + f.getRegraSalario().getValorValeTransporte();
            double descontos = salarioBruto - salarioLiquido;

            totalBruto += salarioBruto;
            totalDescontos += descontos;
            totalLiquido += salarioLiquido;
        }

        String mesExtenso = Month.of(mes).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));

        FolhaPagamento folha = new FolhaPagamento.Builder(0, mes, ano, LocalDate.now(), null)
                .valorTotalBruto(totalBruto)
                .valorTotalDescontos(totalDescontos)
                .valorTotalLiquido(totalLiquido)
                .funcionarios(funcionariosAtivos)
                .mesPorExtenso(mesExtenso)
                .build();

        return folha;
    }

    public FolhaPagamento gerarFolhaPagamento(int mes, int ano) {
        FolhaPagamento folhaCalculada = this.calcularFolhaPagamento(mes, ano);

        return folhaPagamentoRepository.salvar(folhaCalculada);
    }

    public List<FolhaPagamento> listarTodas() {
        return folhaPagamentoRepository.buscarTodas();
    }
}