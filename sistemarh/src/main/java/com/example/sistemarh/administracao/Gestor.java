package com.example.sistemarh.administracao;

import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.model.Recrutador;
import com.example.sistemarh.recrutamento.model.Vaga;

public class Gestor extends Funcionario {
    // CONSTRUTOR
    public Gestor(Funcionario funcionario) {
        super(funcionario.getNome(), funcionario.getCpf(), funcionario.login, funcionario.senha, funcionario.getMatricula(), funcionario.getDataAdmissao(), funcionario.getBaseSalario(), funcionario.getStatus(), funcionario.getDepartamento(), funcionario.getCargo());
    }

    public void gerarRelatorio() {

    }

    public void aprovarContrato(Usuario usuario, Vaga vaga) {
        matricula = "placeholder";
        baseSalario = 1000.0;
        status = "ativo";
        Funcionario funcionario = new Funcionario(usuario.getNome(), usuario.getCpf(), usuario.login, usuario.senha, matricula, dataAdmissao, baseSalario, status, departamento, cargo);
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
