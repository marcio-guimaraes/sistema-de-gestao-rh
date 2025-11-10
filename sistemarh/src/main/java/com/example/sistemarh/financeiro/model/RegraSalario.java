package com.example.sistemarh.financeiro.model;

public class RegraSalario {
    private long id;
    private String nomeRegra;
    private double valorValeTransporte;
    private double percentualDescVT;
    private double valorValeAlimentacao;
    private double percentualINSS;
    private double percentualRRF;

    public RegraSalario(long id, String nomeRegra, double valorValeTransporte, double percentualDescVT,
                        double valorValeAlimentacao, double percentualINSS, double percentualRRF) {
        this.id = id;
        this.nomeRegra = nomeRegra;
        this.valorValeTransporte = valorValeTransporte;
        this.percentualDescVT = percentualDescVT;
        this.valorValeAlimentacao = valorValeAlimentacao;
        this.percentualINSS = percentualINSS;
        this.percentualRRF = percentualRRF;
    }

    // Gets
    public long getId() {
        return id;
    }

    public String getNomeRegra() {
        return nomeRegra;
    }

    public double getValorValeTransporte() {
        return valorValeTransporte;
    }

    public double getPercentualDescVT() {
        return percentualDescVT;
    }

    public double getValorValeAlimentacao() {
        return valorValeAlimentacao;
    }

    public double getPercentualINSS() {
        return percentualINSS;
    }

    public double getPercentualRRF() {
        return percentualRRF;
    }

    // Sets
    public void setNomeRegra(String nomeRegra) {
        this.nomeRegra = nomeRegra;
    }

    public void setValorValeTransporte(double valorValeTransporte) {
        this.valorValeTransporte = valorValeTransporte;
    }

    public void setPercentualDescVT(double percentualDescVT) {
        this.percentualDescVT = percentualDescVT;
    }

    public void setValorValeAlimentacao(double valorValeAlimentacao) {
        this.valorValeAlimentacao = valorValeAlimentacao;
    }

    public void setPercentualINSS(double percentualINSS) {
        this.percentualINSS = percentualINSS;
    }

    public void setPercentualRRF(double percentualRRF) {
        this.percentualRRF = percentualRRF;
    }
}

