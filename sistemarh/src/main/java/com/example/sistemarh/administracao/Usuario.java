package com.example.sistemarh.administracao;

import com.example.sistemarh.common.model.Pessoa;

public class Usuario extends Pessoa {
    protected String senha;
    protected String login;

    protected Usuario(String nome, String cpf, String login, String senha) {
        super(nome, cpf);
        this.login = login;
        this.senha = senha;
    }
}
