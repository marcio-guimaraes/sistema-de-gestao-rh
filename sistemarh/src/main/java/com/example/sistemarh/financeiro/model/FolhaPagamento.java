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
    private List<Funcionario> funcionarios;
    private String mesPorExtenso;

    private FolhaPagamento(Builder builder) {
        this.id = builder.id;
        this.mesReferencia = builder.mesReferencia;
        this.anoReferencia = builder.anoReferencia;
        this.dataProcessamento = builder.dataProcessamento;
        this.valorTotalBruto = builder.valorTotalBruto;
        this.valorTotalDescontos = builder.valorTotalDescontos;
        this.valorTotalLiquido = builder.valorTotalLiquido;
        this.funcionarios = builder.funcionarios;
        this.mesPorExtenso = builder.mesPorExtenso;
    }

    // Getters
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
    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }
    public String getMesPorExtenso() {
        return mesPorExtenso;
    }


    public static class Builder {
        private long id;
        private int mesReferencia;
        private int anoReferencia;
        private LocalDate dataProcessamento;
        private List<Funcionario> funcionarios;
        private String mesPorExtenso; // NOVO

        private double valorTotalBruto;
        private double valorTotalDescontos;
        private double valorTotalLiquido;

        public Builder(long id, int mesReferencia, int anoReferencia, LocalDate dataProcessamento, Funcionario funcionario) {
            this.id = id;
            this.mesReferencia = mesReferencia;
            this.anoReferencia = anoReferencia;
            this.dataProcessamento = dataProcessamento;
            this.funcionarios = null;
        }


        public Builder funcionarios(List<Funcionario> funcionarios) { this.funcionarios = funcionarios; return this; }
        public Builder mesPorExtenso(String mesPorExtenso) { this.mesPorExtenso = mesPorExtenso; return this; }

        public FolhaPagamento.Builder valorTotalBruto(double valorTotalBruto) { this.valorTotalBruto = valorTotalBruto; return this; }
        public FolhaPagamento.Builder valorTotalDescontos(double valorTotalDescontos) { this.valorTotalDescontos = valorTotalDescontos; return this; }
        public FolhaPagamento.Builder valorTotalLiquido(double valorTotalLiquido) { this.valorTotalLiquido = valorTotalLiquido; return this; }

        public FolhaPagamento build() {
            return new FolhaPagamento(this);
        }
    }
}