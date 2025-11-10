package com.example.sistemarh.financeiro;

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

    public static List<String> listarFuncionarios() {
        List<String> funcionarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_FUNCIONARIOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                funcionarios.add(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler funcionários: " + e.getMessage());
        }
        return funcionarios;
    }

    public void calcularFolha() {
        List<String> funcionarios = listarFuncionarios();
        double totalBruto = 0;
        double totalDescontos = 0;
        double totalLiquido = 0;

        for (String linha : funcionarios) {
            String[] dados = linha.split(";");
            if (dados.length < 5) continue;

            String status = dados[4];
            if (!status.equalsIgnoreCase("Ativo")) continue;

            double baseSalario = Double.parseDouble(dados[3]);
            totalBruto += baseSalario;
            totalDescontos += baseSalario * 0.10;
            totalLiquido += baseSalario * 0.90;
        }

        this.valorTotalBruto = totalBruto;
        this.valorTotalDescontos = totalDescontos;
        this.valorTotalLiquido = totalLiquido;
    }


    public String exportarRelatórioTela() {
        return "Folha de Pagamento - " + mesReferencia + "/" + anoReferencia + "\n" +
                "Funcionários processados: " + listarFuncionarios().size() + "\n" +
                "Valor Total Bruto: R$ " + valorTotalBruto + "\n" +
                "Total de Descontos: R$ " + valorTotalDescontos + "\n" +
                "Valor Total Líquido: R$ " + valorTotalLiquido;
    }

    public File exportarRelatorioArquivo() {

        File arquivo = new File("relatorio_folha_" + mesReferencia + "_" + anoReferencia + ".csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write("ID;Mês Referência;Ano Referência;Data Processamento;Valor Bruto;Valor Descontos;Valor Líquido");
            writer.newLine();

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

    public static List<Funcionario> filtrarFuncionariosDoArquivo(String cargo,String tipoContratacao,String status,String departamento) {
        List<Funcionario> lista = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("funcionarios.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");
                if (campos.length < 9) continue;
                String matricula = campos[0];
                String nome = campos[1];
                String cpf = campos[2];
                String login = campos[3];
                LocalDate dataAdmissao = LocalDate.parse(campos[4]);
                Double baseSalario = Double.parseDouble(campos[5]);
                String statusFuncionario = campos[6];
                String dept = campos[7];
                String cargoFunc = campos[8];
                String tipo = campos.length > 9 ? campos[9] : "";

                Funcionario f = new Funcionario(nome, cpf, login, "senha", matricula, dataAdmissao, baseSalario, statusFuncionario, dept, cargoFunc);

                lista.add(f);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de funcionários: " + e.getMessage());
        }

        return lista.stream()
                .filter(f -> cargo == null || cargo.isEmpty() || cargo.equalsIgnoreCase(f.getCargo()))
                .filter(f -> tipoContratacao == null || tipoContratacao.isEmpty() ||
                        (f.getRegraSalario() != null && tipoContratacao.equalsIgnoreCase(f.getRegraSalario().getNomeRegra())))
                .filter(f -> status == null || status.isEmpty() || status.equalsIgnoreCase(f.getStatus()))
                .filter(f -> departamento == null || departamento.isEmpty() || departamento.equalsIgnoreCase(f.getDepartamento()))
                .collect(Collectors.toList());
    }

    public static class Builder {
        private long id;
        private int mesReferencia;
        private int anoReferencia;
        private LocalDate dataProcessamento;
        private Funcionario funcionario;

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