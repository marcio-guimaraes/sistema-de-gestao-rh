package com.example.sistemarh.common.model;

public class Pessoa {
    private String nome;
    private String cpf;

    protected Pessoa(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    // --- MÉTODOS ADICIONADOS ---
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    // --- FIM DOS MÉTODOS ADICIONADOS ---
}