package com.example.sistemarh.financeiro.model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FolhaPagamento {
    private static final String ARQUIVO_FUNCIONARIOS = "funcionarios.txt";
    private long id;
    private int mesReferencia;
    private int anoReferencia;
    private LocalDate dataProcessamento;
    private double valorTotalBruto;
    private double valorTotalDescontos;
    private double valorTotalLiquido;
    private Funcionario funcionario;

    private FolhaPagamento(Builder builder) {
        this.id = builder.id;
        this.mesReferencia = builder.mesReferencia;
        this.anoReferencia = builder.anoReferencia;
        this.dataProcessamento = builder.dataProcessamento;
        this.valorTotalBruto = builder.valorTotalBruto;
        this.valorTotalDescontos = builder.valorTotalDescontos;
        this.valorTotalLiquido = builder.valorTotalLiquido;
        this.funcionario = builder.funcionario;
    }

    // Gets
    public long getId() {
        return id;
    }
    public int getMesReferencia() {
        return mesReferencia;
    }
    public int getAnoReferencia() {
        return anoReferencia;
    }
    public LocalDate getDataProcessamento() {
        return dataProcessamento;
    }
    public double getValorTotalBruto() {
        return valorTotalBruto;
    }
    public double getValorTotalDescontos() {
        return valorTotalDescontos;
    }
    public double getValorTotalLiquido() {
        return valorTotalLiquido;
    }

    // Métodos



    public String exportarRelatórioTela() {
        return "Folha de Pagamento - " + mesReferencia + "/" + anoReferencia + "\n" +
                "Funcionários processados: " + listarFuncionarios().size() + "\n" +
                "Valor Total Bruto: R$ " + valorTotalBruto + "\n" +
                "Total de Descontos: R$ " + valorTotalDescontos + "\n" +
                "Valor Total Líquido: R$ " + valorTotalLiquido;
        //adicionar uma tabela com todos os funcionarios, com nome e valores
    }

    public File exportarRelatorioArquivo() {

        File arquivo = new File("relatorio_folha_" + mesReferencia + "_" + anoReferencia + ".csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("ID;Mês Referência;Ano Referência;Data Processamento;Valor Bruto;Valor Descontos;Valor Líquido");
            writer.newLine();

            // Linha de dados
            writer.write(
                    id + ";" +
                            mesReferencia + ";" +
                            anoReferencia + ";" +
                            dataProcessamento + ";" +
                            valorTotalBruto + ";" +
                            valorTotalDescontos + ";" +
                            valorTotalLiquido
            );
            writer.newLine();

            System.out.println("Relatório CSV exportado com sucesso: " + arquivo.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao exportar relatório: " + e.getMessage());
        }

        return arquivo;
    }



    public static class Builder {
        // Atributos obrigatórios
        private long id;
        private int mesReferencia;
        private int anoReferencia;
        private LocalDate dataProcessamento;
        private Funcionario funcionario;

        // Atributos opcionais
        private double valorTotalBruto;
        private double valorTotalDescontos;
        private double valorTotalLiquido;

        public Builder(long id, int mesReferencia, int anoReferencia, LocalDate dataProcessamento, Funcionario funcionario) {
            this.id = id;
            this.mesReferencia = mesReferencia;
            this.anoReferencia = anoReferencia;
            this.dataProcessamento = dataProcessamento;
            this.funcionario = funcionario;
        }

        public FolhaPagamento.Builder valorTotalBruto(double valorTotalBruto) { this.valorTotalBruto = valorTotalBruto; return this; }
        public FolhaPagamento.Builder valorTotalDescontos(double valorTotalDescontos) { this.valorTotalDescontos = valorTotalDescontos; return this; }
        public FolhaPagamento.Builder valorTotalLiquido(double valorTotalLiquido) { this.valorTotalLiquido = Builder.this.valorTotalLiquido; return this; }

        public FolhaPagamento build() {
            return new FolhaPagamento(this);
        }
    }

}
