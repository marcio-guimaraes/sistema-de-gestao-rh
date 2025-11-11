package com.example.sistemarh.financeiro.model;

import com.example.sistemarh.administracao.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Funcionario extends Usuario {

    protected String matricula;
    protected LocalDate dataAdmissao;
    protected Double baseSalario;
    protected String status;
    protected String departamento;
    protected String cargo;
    protected long regraSalarialId;

    protected RegraSalario regraSalario;

    public Funcionario(String nome, String cpf, String login, String senha,
                       String matricula, LocalDate dataAdmissao, Double baseSalario, String status, String departamento, String cargo, long regraSalarialId) {
        super(nome, cpf, login, senha);
        this.matricula = matricula;
        this.dataAdmissao = dataAdmissao;
        this.baseSalario = baseSalario;
        this.status = status;
        this.departamento = departamento;
        this.cargo = cargo;
        this.regraSalarialId = regraSalarialId;
    }

    // Construtor simplificado
    public Funcionario(String nome, String cpf, String login, String senha,
                       String matricula, LocalDate dataAdmissao, Double baseSalario, String status) {
        super(nome, cpf, login, senha);
        this.matricula = matricula;
        this.dataAdmissao = dataAdmissao;
        this.baseSalario = baseSalario;
        this.status = status;
        this.departamento = "N/A";
        this.cargo = "N/A";
        this.regraSalarialId = 1;
    }

    // Getters
    public String getMatricula() {
        return matricula;
    }
    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }
    public String getStatus() {
        return status;
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
    public long getRegraSalarialId() {
        return regraSalarialId;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    public void setRegraSalario(RegraSalario regraSalario) {
        this.regraSalario = regraSalario;
    }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public void setBaseSalario(Double baseSalario) { this.baseSalario = baseSalario; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setRegraSalarialId(long regraSalarialId) {this.regraSalarialId = regraSalarialId;}


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
}