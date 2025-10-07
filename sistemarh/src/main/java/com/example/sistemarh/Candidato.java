package com.example.sistemarh;

public class Candidato {
    private String formacao;
    private String experiencia;
    private double pretensaoSalario;
    private String disponibilidade;
    private String dataCadastro;

    public Candidato(String formacao, String experiencia, double pretensaoSalario, String disponibilidade, String dataCadastro) {
        this.formacao = formacao;
        this.experiencia = experiencia;
        this.pretensaoSalario = pretensaoSalario;
        this.disponibilidade = disponibilidade;
        this.dataCadastro = dataCadastro;
    }

    public void cadastrar(){}

    public void editar(){}
    public void excluir(){}

    public String consultarDados(){
        return null;
    }
}
