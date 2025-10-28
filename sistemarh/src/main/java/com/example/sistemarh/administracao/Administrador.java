package com.example.sistemarh.administracao;

import com.example.sistemarh.financeiro.Funcionario;

import java.time.LocalDate;

public class Administrador extends Funcionario {
    public Administrador(String nome, String cpf, String login, String senha, String matricula, LocalDate dataAdmissao, Double baseSalario, String status) {
        super(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
    }

    public Usuario criarUsuario(String nome, String cpf, String login, String senha) {
        Usuario usuario = new Usuario(nome, cpf, login, senha);
        return usuario;
    }

    public void editarUsuario(Usuario usuario) {

    }

    public void excluirUsuario(Usuario usuario) {

    }

    public void atribuirPerfil(Usuario usuario) {

    }

    public void configurarRegrasSalariais() {

    }
}
