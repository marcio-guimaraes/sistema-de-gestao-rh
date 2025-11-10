package com.example.sistemarh.recrutamento.model;

import com.example.sistemarh.candidatura.Candidato;
import java.time.LocalDate;

public class Contratacao {
    private long id;
    private Vaga vaga;
    private Candidato candidato;
    private LocalDate dataSolicitacao;
    private LocalDate dataAprovacaoGestor;
    private LocalDate dataEfetivacao;
    private String status;
    private String regimeContratacao;

    private String cpfCandidatoDoArquivo;
    private long idVagaDoArquivo;

    private Contratacao(Builder builder) {
        this.id = builder.id;
        this.vaga = builder.vaga;
        this.candidato = builder.candidato;
        this.dataSolicitacao = (builder.dataSolicitacao != null) ? builder.dataSolicitacao : LocalDate.now();
        this.dataAprovacaoGestor = builder.dataAprovacaoGestor;
        this.dataEfetivacao = builder.dataEfetivacao;
        this.status = builder.status;
        this.regimeContratacao = builder.regimeContratacao;

        if (candidato != null) this.cpfCandidatoDoArquivo = candidato.getCpf();
        if (vaga != null) this.idVagaDoArquivo = vaga.getId();
    }

    private Contratacao(long id, String cpfCandidato, long idVaga, String status, LocalDate dataSolic, LocalDate dataAprov, LocalDate dataEfetiv, String regime) {
        this.id = id;
        this.cpfCandidatoDoArquivo = cpfCandidato;
        this.idVagaDoArquivo = idVaga;
        this.status = status;
        this.dataSolicitacao = dataSolic;
        this.dataAprovacaoGestor = dataAprov;
        this.dataEfetivacao = dataEfetiv;
        this.regimeContratacao = regime;
    }

    public long getId() { return id; }
    public Vaga getVaga() { return vaga; }
    public Candidato getCandidato() { return candidato; }
    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public LocalDate getDataAprovacaoGestor() { return dataAprovacaoGestor; }
    public LocalDate getDataEfetivacao() { return dataEfetivacao; }
    public String getStatus() { return status; }
    public String getRegimeContratacao() { return regimeContratacao; }

    public String getCpfCandidatoDoArquivo() { return cpfCandidatoDoArquivo; }
    public long getIdVagaDoArquivo() { return idVagaDoArquivo; }

    public void setDataAprovacaoGestor(LocalDate dataAprovacaoGestor) { this.dataAprovacaoGestor = dataAprovacaoGestor; }
    public void setDataEfetivacao(LocalDate dataEfetivacao) { this.dataEfetivacao = dataEfetivacao; }
    public void setStatus(String status) { this.status = status; }
    public void setRegimeContratacao(String regime) { this.regimeContratacao = regime; }
    public void setId(long id) { this.id = id; }

    public void setCandidato(Candidato c) { this.candidato = c; }
    public void setVaga(Vaga v) { this.vaga = v; }


    public static class Builder {
        private long id;
        private final Vaga vaga;
        private final Candidato candidato;

        private LocalDate dataSolicitacao = null;
        private LocalDate dataAprovacaoGestor = null;
        private LocalDate dataEfetivacao = null;
        private String status = "Solicitada";
        private String regimeContratacao;

        public Builder(Vaga vaga, Candidato candidato) {
            if (vaga == null || candidato == null) {
                throw new IllegalArgumentException("Vaga e Candidato são obrigatórios para a contratação.");
            }
            this.vaga = vaga;
            this.candidato = candidato;
            this.regimeContratacao = vaga.getRegime();
        }

        public Builder id(long id) { this.id = id; return this; }
        public Builder dataSolicitacao(LocalDate data) { this.dataSolicitacao = data; return this; }
        public Builder dataAprovacaoGestor(LocalDate data) { this.dataAprovacaoGestor = data; return this; }
        public Builder dataEfetivacao(LocalDate data) { this.dataEfetivacao = data; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder regimeContratacao(String regime) { this.regimeContratacao = regime; return this; }

        public Contratacao build() {
            return new Contratacao(this);
        }
    }

    public static Contratacao fromRepository(long id, String cpfCandidato, long idVaga, String status, LocalDate dataSolic, LocalDate dataAprov, LocalDate dataEfetiv, String regime) {
        return new Contratacao(id, cpfCandidato, idVaga, status, dataSolic, dataAprov, dataEfetiv, regime);
    }
}