package com.example.sistemarh.candidatura;

import com.example.sistemarh.common.model.Pessoa;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Candidato extends Pessoa {

    private String formacao;
    private String experiencia;
    private double pretensaoSalarial;
    private String disponibilidade;
    private LocalDate dataCadastro;
    private String documentosAdicionais;


    public Candidato() {
        super();
        this.dataCadastro = LocalDate.now();
    }

    private Candidato(Builder builder) {
        super(builder.nome, builder.cpf);
        this.formacao = builder.formacao;
        this.experiencia = builder.experiencia;
        this.pretensaoSalarial = builder.pretensaoSalario;
        this.disponibilidade = builder.disponibilidade;
        this.dataCadastro = builder.dataCadastro;
        this.documentosAdicionais = builder.documentosAdicionais;
    }

    //sets
    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public void setPretensaoSalarial(double pretensao) {
        this.pretensaoSalarial = pretensao;
    }

    public void setDisponibilidade(String disp) {
        this.disponibilidade = disp;
    }

    public void setDocumentosAdicionais(String docs) {
        this.documentosAdicionais = docs;
    }


    //gets
    public String getExperiencia() {
        return this.experiencia;
    }

    public double getPretensaoSalarial() {
        return this.pretensaoSalarial;
    }

    public String getFormacao() {
        return this.formacao;
    }

    public String getDisponibilidade() {
        return this.disponibilidade;
    }

    public LocalDate getDataCadastro() {
        return this.dataCadastro;
    }

    public String getDocumentosAdicionais() {
        return this.documentosAdicionais;
    }

    //print
    public String toLinhaArquivo() {
        return String.join(";",
                this.getNome(),
                this.getCpf(),
                formacao,
                experiencia,
                String.valueOf(pretensaoSalarial),
                disponibilidade,
                dataCadastro.toString(),
                documentosAdicionais
        );
    }

    @Override
    public String toString() {
        return toLinhaArquivo();
    }


//construtor builder

    public static class Builder {

        private final String nome;
        private final String cpf;

        private String formacao = " ";
        private String experiencia = " ";
        private double pretensaoSalario = 0.0;
        private String disponibilidade = " ";
        private LocalDate dataCadastro = LocalDate.now();
        private String documentosAdicionais = " ";


        public Builder(String nome, String cpf) {
            if (nome == null || nome.trim().isEmpty() || cpf == null || cpf.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome e CPF são obrigatórios, tente novamente: \n");
            }
            this.nome = nome;
            this.cpf = cpf;
        }


        public Builder formacao(String formacao) {
            this.formacao = formacao;
            return this;
        }

        public Builder experiencia(String experiencia) {
            this.experiencia = experiencia;
            return this;
        }

        public Builder pretensaoSalario(double pretensaoSalario) {
            this.pretensaoSalario = pretensaoSalario;
            return this;
        }

        public Builder disponibilidade(String disponibilidade) {
            this.disponibilidade = disponibilidade;
            return this;
        }

        public Builder dataCadastro(LocalDate dataCadastro) {
            this.dataCadastro = dataCadastro;
            return this;
        }

        public Builder documentosAdicionais(String documentosAdicionais) {
            this.documentosAdicionais = documentosAdicionais;
            return this;
        }

        public Candidato build() {
            return new Candidato(this);
        }
    }
}