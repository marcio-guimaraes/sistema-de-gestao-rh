package com.example.sistemarh.financeiro.service;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.model.RegraSalario;
import com.example.sistemarh.financeiro.repository.FuncionarioRepository;
import com.example.sistemarh.financeiro.repository.RegraSalarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList; // <-- IMPORT ADICIONADO
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class FinanceiroService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private RegraSalarioRepository regraSalarioRepository;

    private static final AtomicLong contadorFolhaId = new AtomicLong(System.currentTimeMillis());

    /**
     * Processa a folha de pagamento para um determinado mês e ano.
     */
    public FolhaPagamento processarFolha(int mes, int ano) {

        RegraSalario regras = regraSalarioRepository.carregar();
        if (regras == null) {
            throw new RuntimeException("Regras salariais não configuradas. Acesse 'Configurar Regras' primeiro.");
        }

        List<Funcionario> funcionarios = funcionarioRepository.buscarTodos();
        List<Funcionario> funcionariosAtivosProcessados = new ArrayList<>(); // <-- LISTA ADICIONADA

        double totalBruto = 0;
        double totalDescontos = 0;
        double totalLiquido = 0;

        for (Funcionario func : funcionarios) {

            if (!func.getStatus().equalsIgnoreCase("Ativo")) {
                continue;
            }

            func.setRegraSalario(regras);
            double salarioLiquidoIndividual = func.calcularSalario();

            double base = func.getBaseSalario();
            double beneficios = regras.getValorValeAlimentacao() + regras.getValorValeTransporte();

            double descontos = (base * regras.getPercentualINSS() / 100)
                    + (base * regras.getPercentualRRF() / 100)
                    + (regras.getValorValeTransporte() * regras.getPercentualDescVT() / 100);

            totalBruto += (base + beneficios);
            totalDescontos += descontos;
            totalLiquido += salarioLiquidoIndividual;

            funcionariosAtivosProcessados.add(func); // <-- ADICIONADO
        }

        FolhaPagamento folhaProcessada = new FolhaPagamento.Builder(
                contadorFolhaId.incrementAndGet(),
                mes,
                ano,
                LocalDate.now(),
                null
        )
                .valorTotalBruto(totalBruto)
                .valorTotalDescontos(totalDescontos)
                .valorTotalLiquido(totalLiquido)
                .funcionariosProcessados(funcionariosAtivosProcessados) // <-- ADICIONADO
                .build();

        return folhaProcessada;
    }

    public void salvarNovoFuncionario(Funcionario f) {
        funcionarioRepository.salvar(f);
    }

    public void salvarRegras(RegraSalario r) {
        regraSalarioRepository.salvar(r);
    }

    // <-- MÉTODO ADICIONADO -->
    public RegraSalario carregarRegras() {
        return regraSalarioRepository.carregar();
    }

    /**
     * Formata um objeto FolhaPagamento para exibição simples em tela/console.
     */
    public String formatarFolhaParaTela(FolhaPagamento folha) {
        return "Folha de Pagamento - " + folha.getMesReferencia() + "/" + folha.getAnoReferencia() + "\n" +
                "Data de Processamento: " + folha.getDataProcessamento() + "\n" +
                "Valor Total Bruto: R$ " + folha.getValorTotalBruto() + "\n" +
                "Total de Descontos: R$ " + folha.getValorTotalDescontos() + "\n" +
                "Valor Total Líquido: R$ " + folha.getValorTotalLiquido();
    }

    /**
     * Exporta os dados de uma FolhaPagamento para um arquivo CSV.
     */
    public File exportarFolhaParaCSV(FolhaPagamento folha) {

        String nomeArquivo = "relatorio_folha_" + folha.getMesReferencia() + "_" + folha.getAnoReferencia() + ".csv";
        File arquivo = new File(nomeArquivo);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("ID;Mês Referência;Ano Referência;Data Processamento;Valor Bruto;Valor Descontos;Valor Líquido");
            writer.newLine();

            writer.write(
                    folha.getId() + ";" +
                            folha.getMesReferencia() + ";" +
                            folha.getAnoReferencia() + ";" +
                            folha.getDataProcessamento() + ";" +
                            folha.getValorTotalBruto() + ";" +
                            folha.getValorTotalDescontos() + ";" +
                            folha.getValorTotalLiquido()
            );
            writer.newLine();

            System.out.println("Relatório CSV exportado com sucesso: " + arquivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao exportar relatório: " + e.getMessage());
        }

        return arquivo;
    }
}