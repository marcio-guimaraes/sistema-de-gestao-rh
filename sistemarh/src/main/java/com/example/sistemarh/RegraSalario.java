package com.example.sistemarh;

public class RegraSalario {
    private String regime;
    private double valeTransporte;
    private double valeAlimentacao;
    private double aliquotaImposto;

    public RegraSalario(String regime, double valeTransporte, double valeAlimentacao, double aliquotaImposto) {
        this.regime = regime;
        this.valeTransporte = valeTransporte;
        this.valeAlimentacao = valeAlimentacao;
        this.aliquotaImposto = aliquotaImposto;
    }
}

