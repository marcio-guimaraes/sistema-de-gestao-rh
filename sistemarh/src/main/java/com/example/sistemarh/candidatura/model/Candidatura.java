package com.example.sistemarh.candidatura.model;

import com.example.sistemarh.recrutamento.model.Vaga;
import java.io.Serializable;
import java.time.LocalDate;


public class Candidatura implements Serializable {


    private static final long serialVersionUID = 2L;

    // Atributos
    private long id;
    private String status;
    private LocalDate dataCandidatura;
    private Candidato candidato;
    private Vaga vaga;


    private Candidatura(Builder builder) {
        this.id = builder.id;
        this.status = builder.status;
        this.dataCandidatura = builder.dataCandidatura;
        this.candidato = builder.candidato;
        this.vaga = builder.vaga;
    }

    // --- Getters ---
    public long getId() { return id; }
    public String getStatus() { return status; }
    public LocalDate getDataCandidatura() { return dataCandidatura; }
    public Candidato getCandidato() { return candidato; }
    public Vaga getVaga() { return vaga; }

    // --- Setters ---
    // (status pode ser mudado pelo Service)
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.join(";",
                String.valueOf(this.id),       //0
                this.candidato.getCpf(),       //1
                String.valueOf(this.vaga.getId()), //2
                this.status,                   //3
                this.dataCandidatura.toString() //4
        );
    }


    public static class Builder {
        private long id;
        private String status = "Pendente";
        private LocalDate dataCandidatura = LocalDate.now();
        private final Candidato candidato;
        private final Vaga vaga;

        public Builder(long id, Candidato candidato, Vaga vaga) {
            if (candidato == null || vaga == null) {
                throw new IllegalArgumentException("Candidato e Vaga são obrigatórios.");
            }
            this.id = id;
            this.candidato = candidato;
            this.vaga = vaga;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder dataCandidatura(LocalDate data) {
            this.dataCandidatura = data;
            return this;
        }

        public Candidatura build() {
            return new Candidatura(this);
        }
    }
}