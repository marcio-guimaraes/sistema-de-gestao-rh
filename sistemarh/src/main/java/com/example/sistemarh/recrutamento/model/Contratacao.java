package com.example.sistemarh.recrutamento.model;

import com.example.sistemarh.candidatura.model.Candidato;
import java.time.LocalDate;

public class Contratacao {
    private Vaga vaga;
    private Candidato candidato;
    private LocalDate dataSolicitacao;
    private LocalDate dataAprovacaoGestor;
    private LocalDate dataEfetivacao;
    private String status;

    private Contratacao(Builder builder) {
        this.vaga = builder.vaga;
        this.candidato = builder.candidato;
        this.dataSolicitacao = (builder.dataSolicitacao != null) ? builder.dataSolicitacao : LocalDate.now();
        this.dataAprovacaoGestor = builder.dataAprovacaoGestor;
        this.dataEfetivacao = builder.dataEfetivacao;
        this.status = builder.status;
    }

    // --- GETTERS ---
    public Vaga getVaga() { return vaga; }
    public Candidato getCandidato() { return candidato; }
    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public LocalDate getDataAprovacaoGestor() { return dataAprovacaoGestor; }
    public LocalDate getDataEfetivacao() { return dataEfetivacao; }
    public String getStatus() { return status; }

    public void setDataAprovacaoGestor(LocalDate dataAprovacaoGestor) { this.dataAprovacaoGestor = dataAprovacaoGestor; }
    public void setDataEfetivacao(LocalDate dataEfetivacao) { this.dataEfetivacao = dataEfetivacao; }
    public void setStatus(String status) { this.status = status; }

    public static class Builder {
        // Atributos obrigatórios
        private final Vaga vaga;
        private final Candidato candidato;

        private LocalDate dataSolicitacao = null;
        private LocalDate dataAprovacaoGestor = null;
        private LocalDate dataEfetivacao = null;
        private String status = "Solicitada"; // Status inicial padrão

        public Builder(Vaga vaga, Candidato candidato) {
            if (vaga == null || candidato == null) {
                throw new IllegalArgumentException("Vaga e Candidato são obrigatórios para a contratação.");
            }
            this.vaga = vaga;
            this.candidato = candidato;
        }

        public Builder dataSolicitacao(LocalDate dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; return this; }
        public Builder dataAprovacaoGestor(LocalDate dataAprovacaoGestor) { this.dataAprovacaoGestor = dataAprovacaoGestor; return this; }
        public Builder dataEfetivacao(LocalDate dataEfetivacao) { this.dataEfetivacao = dataEfetivacao; return this; }
        public Builder status(String status) { this.status = status; return this; }

        public Contratacao build() {
            return new Contratacao(this);
        }
    }
}