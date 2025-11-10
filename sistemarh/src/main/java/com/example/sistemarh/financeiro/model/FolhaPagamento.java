package com.example.sistemarh.financeiro.model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FolhaPagamento {
    private long id;
    private int mesReferencia;
    private int anoReferencia;
    private LocalDate dataProcessamento;
    private double valorTotalBruto;
    private double valorTotalDescontos;
    private double valorTotalLiquido;
    private Funcionario funcionario; // Este campo parece ser um erro de design, mas mantemos

    private List<Funcionario> funcionariosProcessados; // <-- CAMPO ADICIONADO

    private FolhaPagamento(Builder builder) {
        this.id = builder.id;
        this.mesReferencia = builder.mesReferencia;
        this.anoReferencia = builder.anoReferencia;
        this.dataProcessamento = builder.dataProcessamento;
        this.valorTotalBruto = builder.valorTotalBruto;
        this.valorTotalDescontos = builder.valorTotalDescontos;
        this.valorTotalLiquido = builder.valorTotalLiquido;
        this.funcionario = builder.funcionario;
        this.funcionariosProcessados = builder.funcionariosProcessados; // <-- CAMPO ADICIONADO
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

    // <-- MÉTODO ADICIONADO -->
    public List<Funcionario> getFuncionariosProcessados() {
        return funcionariosProcessados;
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

        private List<Funcionario> funcionariosProcessados; // <-- CAMPO ADICIONADO

        public Builder(long id, int mesReferencia, int anoReferencia, LocalDate dataProcessamento, Funcionario funcionario) {
            this.id = id;
            this.mesReferencia = mesReferencia;
            this.anoReferencia = anoReferencia;
            this.dataProcessamento = dataProcessamento;
            this.funcionario = funcionario;
        }

        public FolhaPagamento.Builder valorTotalBruto(double valorTotalBruto) { this.valorTotalBruto = valorTotalBruto; return this; }
        public FolhaPagamento.Builder valorTotalDescontos(double valorTotalDescontos) { this.valorTotalDescontos = valorTotalDescontos; return this; }
        public FolhaPagamento.Builder valorTotalLiquido(double valorTotalLiquido) { this.valorTotalLiquido = valorTotalLiquido; return this; }

        // <-- MÉTODO ADICIONADO -->
        public FolhaPagamento.Builder funcionariosProcessados(List<Funcionario> funcionarios) {
            this.funcionariosProcessados = funcionarios;
            return this;
        }

        public FolhaPagamento build() {
            return new FolhaPagamento(this);
        }
    }
}