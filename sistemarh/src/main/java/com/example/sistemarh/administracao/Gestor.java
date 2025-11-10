package com.example.sistemarh.administracao;

import com.example.sistemarh.financeiro.Funcionario;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.model.Recrutador;
import com.example.sistemarh.recrutamento.model.Vaga;

import java.time.LocalDate;

public class Gestor extends Funcionario {

    public Gestor(Funcionario funcionario) {
        super(funcionario.getNome(), funcionario.getCpf(), funcionario.login, funcionario.senha, funcionario.getMatricula(), funcionario.getDataAdmissao(), funcionario.getBaseSalario(), funcionario.getStatus(), funcionario.getDepartamento(), funcionario.getCargo());
    }

    // NOVO CONSTRUTOR ADICIONADO
    public Gestor(String nome, String cpf, String login, String senha, String matricula, LocalDate dataAdmissao, Double baseSalario, String status) {
        super(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
    }


    public void gerarRelatorio() {

    }

    public void aprovarContrato(Usuario usuario, Vaga vaga) {
        matricula = "placeholder";
        baseSalario = 1000.0;
        status = "ativo";
        Funcionario funcionario = new Funcionario(usuario.getNome(), usuario.getCpf(), usuario.getLogin(), usuario.getSenha(), matricula, dataAdmissao, baseSalario, status, departamento, cargo);
    }

    public void criarVaga(Vaga.Builder builder) {
        Vaga vaga = new Vaga(builder);
    }

    public void editarVaga(Vaga vaga) {
    }

    public void fecharVaga(Vaga vaga) {
        vaga.setStatus("Inativa");
    }

    public void atribuirRecrutador(Vaga vaga, Recrutador recrutador) {

    }

    public void aprovarContratacao(Contratacao contratacao) {
        contratacao.setStatus("Contratado");
    }

    public void gerarRelatorioGestao() {

    }
}