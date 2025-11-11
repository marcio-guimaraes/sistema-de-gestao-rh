package com.example.sistemarh.candidatura.model;

import com.example.sistemarh.common.model.Pessoa;
import java.io.Serializable;
import java.time.LocalDate;

public class Candidato extends Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    private String formacao;
    private String experiencia;
    private double pretensaoSalarial;
    private String disponibilidade;
    private LocalDate dataCadastro;
    private String documentosAdicionais;

    private Candidato(Builder builder) {
        super(builder.nome, builder.cpf);
        this.formacao = builder.formacao;
        this.experiencia = builder.experiencia;
        this.pretensaoSalarial = builder.pretensaoSalario;
        this.disponibilidade = builder.disponibilidade;
        this.dataCadastro = builder.dataCadastro;
        this.documentosAdicionais = builder.documentosAdicionais;
    }

    public String getFormacao() { return formacao; }
    public String getExperiencia() { return experiencia; }
    public double getPretensaoSalarial() { return pretensaoSalarial; }
    public String getDisponibilidade() { return disponibilidade; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public String getDocumentosAdicionais() { return documentosAdicionais; }

    @Override
    public String toString() {
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

        public Builder formacao(String formacao) { this.formacao = formacao; return this; }
        public Builder experiencia(String experiencia) { this.experiencia = experiencia; return this; }
        public Builder pretensaoSalario(double pretensaoSalario) { this.pretensaoSalario = pretensaoSalario; return this; }
        public Builder disponibilidade(String disponibilidade) { this.disponibilidade = disponibilidade; return this; }
        public Builder dataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; return this; }
        public Builder documentosAdicionais(String documentosAdicionais) { this.documentosAdicionais = documentosAdicionais; return this; }

        public Candidato build() {
            return new Candidato(this);
        }
    }
}
