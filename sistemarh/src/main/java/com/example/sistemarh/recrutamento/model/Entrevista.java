package com.example.sistemarh.recrutamento.model;

import com.example.sistemarh.candidatura.model.Candidato;
import java.time.LocalDateTime;

public class Entrevista {
    private LocalDateTime dataHora;
    private String local; // Pode ser "Online", "Presencial - Sala X", etc.
    private Recrutador recrutador; // O recrutador que realizará a entrevista
    private Candidato candidato; // O candidato entrevistado
    private Vaga vaga; // A vaga relacionada
    private String feedback; // Campo para feedback pós-entrevista (opcional)
    private Double nota; // Nota da entrevista (opcional)

    private Entrevista(Builder builder) {
        this.dataHora = builder.dataHora;
        this.local = builder.local;
        this.recrutador = builder.recrutador;
        this.candidato = builder.candidato;
        this.vaga = builder.vaga;
        this.feedback = builder.feedback;
        this.nota = builder.nota;
    }

    public LocalDateTime getDataHora() { return dataHora; }
    public String getLocal() { return local; }
    public Recrutador getRecrutador() { return recrutador; }
    public Candidato getCandidato() { return candidato; }
    public Vaga getVaga() { return vaga; }
    public String getFeedback() { return feedback; }
    public Double getNota() { return nota; }

    public static class Builder {
        private final LocalDateTime dataHora;
        private final Recrutador recrutador;
        private final Candidato candidato;
        private final Vaga vaga;

        private String local = "Online"; // Valor padrão
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

        public Builder local(String local) { this.local = local; return this; }
        public Builder feedback(String feedback) { this.feedback = feedback; return this; }
        public Builder nota(Double nota) { this.nota = nota; return this; }

        public Entrevista build() {
            return new Entrevista(this);
        }
    }
}