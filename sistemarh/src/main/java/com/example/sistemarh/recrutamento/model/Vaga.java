package com.example.sistemarh.recrutamento.model;

import java.time.LocalDate;

public class Vaga {
    private long id;
    private String titulo;
    private String descricao;
    private double salarioMin;
    private double salarioMax;
    private String regime;
    private String status;
    private String departamento;
    private String requisitos;
    private LocalDate dataCriacao;

    public Vaga(Builder builder) {
        this.id = builder.id;
        this.titulo = builder.titulo;
        this.descricao = builder.descricao;
        this.salarioMin = builder.salarioMin;
        this.salarioMax = builder.salarioMax;
        this.regime = builder.regime;
        this.status = builder.status;
        this.departamento = builder.departamento;
        this.requisitos = builder.requisitos;
        this.dataCriacao = (builder.dataCriacao != null) ? builder.dataCriacao : LocalDate.now();
    }

    public long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getSalarioMin() {
        return salarioMin;
    }

    public double getSalarioMax() {
        return salarioMax;
    }

    public String getRegime() {
        return regime;
    }

    public String getStatus() {
        return status;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setSalarioMin(double salarioMin) {
        this.salarioMin = salarioMin;
    }

    public void setSalarioMax(double salarioMax) {
        this.salarioMax = salarioMax;
    }

    public void setRegime(String regime) {
        this.regime = regime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }


    public static class Builder {
        private final long id;
        private final String titulo;

        private String descricao = "";
        private double salarioMin = 0.0;
        private double salarioMax = 0.0;
        private String regime = "CLT";
        private String status = "Aberta";
        private String departamento = "";
        private String requisitos = "";
        private LocalDate dataCriacao = null;

        public Builder(long id, String titulo) {
            if (titulo == null || titulo.trim().isEmpty()) {
                throw new IllegalArgumentException("Título da vaga não pode ser vazio.");
            }
            this.id = id;
            this.titulo = titulo;
        }

        public Builder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder salario(double min, double max) {
            this.salarioMin = min;
            this.salarioMax = max;
            return this;
        }

        public Builder regime(String regime) {
            this.regime = regime;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder departamento(String departamento) {
            this.departamento = departamento;
            return this;
        }

        public Builder requisitos(String requisitos) {
            this.requisitos = requisitos;
            return this;
        }

        public Builder dataCriacao(LocalDate dataCriacao) {
            this.dataCriacao = dataCriacao;
            return this;
        }

        public Vaga build() {
            return new Vaga(this);
        }
    }
}