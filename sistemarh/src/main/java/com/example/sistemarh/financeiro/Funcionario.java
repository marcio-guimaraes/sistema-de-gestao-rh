package com.example.sistemarh.financeiro;

import com.example.sistemarh.administracao.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Funcionario extends Usuario {
    private static final String ARQUIVO_FUNCIONARIOS = "funcionarios.txt";

    protected String matricula;
    protected LocalDate dataAdmissao;
    protected Double baseSalario;
    protected String status;
    protected String departamento;
    protected String cargo;

    protected RegraSalario regraSalario;

    public Funcionario(String nome, String cpf, String login, String senha,
                       String matricula, LocalDate dataAdmissao, Double baseSalario, String status, String departamento, String cargo) {
        super(nome, cpf, login, senha); // chama o construtor da superclasse Usuario
        this.matricula = matricula;
        this.dataAdmissao = dataAdmissao;
        this.baseSalario = baseSalario;
        this.status = status;
        this.departamento = departamento;
        this.cargo = cargo;
        salvarFuncionarioNoArquivo();
    }

    // Gets
    public String getMatricula() {
        return matricula;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getBaseSalario() {
        return baseSalario;
    }

    public RegraSalario getRegraSalario() {
        return regraSalario;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getCargo() {
        return cargo;
    }

    // sets
    public void setRegraSalario(RegraSalario regraSalario) {
        this.regraSalario = regraSalario;
    }

    // Metodos
    public double calcularSalario() {
        if (regraSalario == null) {
            System.err.println("Regra salarial não definida para o funcionário " + getNome());
            return baseSalario;
        }

        double bruto = baseSalario
                + regraSalario.getValorValeAlimentacao()
                + regraSalario.getValorValeTransporte();

        double descontos = (baseSalario * regraSalario.getPercentualINSS() / 100)
                + (baseSalario * regraSalario.getPercentualRRF() / 100)
                + (regraSalario.getValorValeTransporte() * regraSalario.getPercentualDescVT() / 100);

        return bruto - descontos;
    }

    private void salvarFuncionarioNoArquivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_FUNCIONARIOS, true))) {
            writer.write(toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar funcionário: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%.2f;%s",
                getNome(), getCpf(), matricula, dataAdmissao, baseSalario, status);
    }
}