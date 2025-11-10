package com.example.sistemarh.recrutamento.model;

import com.example.sistemarh.candidatura.Candidato;

import java.time.LocalDateTime;

public class Entrevista {
    private long id;
    private LocalDateTime dataHora;
    private String local;
    private Recrutador recrutador;
    private Candidato candidato;
    private Vaga vaga;
    private String feedback;
    private Double nota;

    private Entrevista(Builder builder) {
        this.id = builder.id;
        this.dataHora = builder.dataHora;
        this.local = builder.local;
        this.recrutador = builder.recrutador;
        this.candidato = builder.candidato;
        this.vaga = builder.vaga;
        this.feedback = builder.feedback;
        this.nota = builder.nota;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getLocal() {
        return local;
    }

    public Recrutador getRecrutador() {
        return recrutador;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public String getFeedback() {
        return feedback;
    }

    public Double getNota() {
        return nota;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static class Builder {
        private long id;
        private final LocalDateTime dataHora;
        private final Recrutador recrutador;
        private final Candidato candidato;
        private final Vaga vaga;

        private String local = "Online";
        private String feedback = null;
        private Double nota = null;

        public Builder(LocalDateTime dataHora, Recrutador recrutador, Candidato candidato, Vaga vaga) {
            if (dataHora == null || recrutador == null || candidato == null || vaga == null) {
                throw new IllegalArgumentException("Data/Hora, Recrutador, Candidato e Vaga são obrigatórios para a entrevista.");
            }
            this.dataHora = dataHora;
            this.recrutador = recrutador;
            this.candidato = candidato;
            this.vaga = vaga;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder local(String local) {
            this.local = local;
            return this;
        }

        public Builder feedback(String feedback) {
            this.feedback = feedback;
            return this;
        }

        public Builder nota(Double nota) {
            this.nota = nota;
            return this;
        }

        public Entrevista build() {
            return new Entrevista(this);
        }
    }
}