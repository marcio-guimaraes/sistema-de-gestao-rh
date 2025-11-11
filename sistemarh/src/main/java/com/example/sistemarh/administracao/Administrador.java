package com.example.sistemarh.administracao;

import com.example.sistemarh.financeiro.model.Funcionario;

import java.time.LocalDate;

public class Administrador extends Funcionario {
    public Administrador(String nome, String cpf, String login, String senha, String matricula, LocalDate dataAdmissao, Double baseSalario, String status) {
        super(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
    }
}