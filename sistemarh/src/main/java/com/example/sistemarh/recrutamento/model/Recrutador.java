package com.example.sistemarh.recrutamento.model;

import com.example.sistemarh.common.model.Pessoa;

public class Recrutador extends Pessoa {

    private final long idRecrutador;

    private Recrutador(Builder builder) {
        super(builder.nome, builder.cpf);
        this.idRecrutador = builder.idRecrutador;
    }

    public long getIdRecrutador() {
        return idRecrutador;
    }

    public static class Builder {
        private final long idRecrutador;
        private final String nome;
        private final String cpf;

        public Builder(long idRecrutador, String nome, String cpf) {
            if (nome == null || nome.trim().isEmpty() || cpf == null || cpf.trim().isEmpty()) {
                throw new IllegalArgumentException("ID, Nome e CPF do recrutador são obrigatórios.");
            }
            this.idRecrutador = idRecrutador;
            this.nome = nome;
            this.cpf = cpf;
        }

        public Recrutador build() {
            return new Recrutador(this);
        }
    }
}