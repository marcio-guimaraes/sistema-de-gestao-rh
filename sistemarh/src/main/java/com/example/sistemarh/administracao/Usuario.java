package com.example.sistemarh.administracao;

import com.example.sistemarh.common.model.Pessoa;

public class Usuario extends Pessoa {
    protected String senha;
    protected String login;

    public Usuario(String nome, String cpf, String login, String senha) {
        super(nome, cpf);
        this.login = login;
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public String getLogin() {
        return login;
    }

    // --- MÉTODOS ADICIONADOS ---
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    // --- FIM DOS MÉTODOS ADICIONADOS ---
}