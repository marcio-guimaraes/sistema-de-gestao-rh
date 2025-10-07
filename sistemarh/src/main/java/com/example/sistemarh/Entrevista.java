package com.example.sistemarh;

public class Entrevista {
    private String dataHora;
    private String local;
    private Recrutador recrutador;
    private Candidato candidato;
    private Vaga vaga;

    public Entrevista(String dataHora, String local, Recrutador recrutador, Candidato candidato, Vaga vaga) {
        this.dataHora = dataHora;
        this.local = local;
        this.recrutador = recrutador;
        this.candidato = candidato;
        this.vaga = vaga;
    }
}

