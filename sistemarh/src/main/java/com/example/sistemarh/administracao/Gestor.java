package com.example.sistemarh.administracao;

import com.example.sistemarh.financeiro.Funcionario;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.model.Recrutador;
import com.example.sistemarh.recrutamento.model.Vaga;

import java.time.LocalDate;

public class Gestor extends Funcionario {

    public Gestor(Funcionario funcionario) {
        super(funcionario.getNome(), funcionario.getCpf(), funcionario.getLogin(), funcionario.getSenha(), funcionario.getMatricula(), funcionario.getDataAdmissao(), funcionario.getBaseSalario(), funcionario.getStatus(), funcionario.getDepartamento(), funcionario.getCargo());
    }

    public Gestor(String nome, String cpf, String login, String senha, String matricula, LocalDate dataAdmissao, Double baseSalario, String status) {
        super(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
    }

    // Métodos específicos do Gestor (já estavam aqui)
    public void gerarRelatorio() {}
    public void aprovarContrato(Usuario usuario, Vaga vaga) {}
    public void criarVaga(Vaga.Builder builder) {}
    public void editarVaga(Vaga vaga) {}
    public void fecharVaga(Vaga vaga) {}
    public void atribuirRecrutador(Vaga vaga, Recrutador recrutador) {}
    public void aprovarContratacao(Contratacao contratacao) {}
    public void gerarRelatorioGestao() {}
}