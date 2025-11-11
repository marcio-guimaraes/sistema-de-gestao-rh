package com.example.sistemarh.administracao; // Ou onde você guarda seus DTOs

import java.time.LocalDate;

// Este DTO agora tem todos os campos que o seu Repository precisa
public class UsuarioDTO {

    // Campos do Usuário
    private String nome;
    private String cpf;
    private String login;
    private String senha;

    // Campos do Funcionario (do seu Repository)
    private String matricula;
    private LocalDate dataAdmissao;
    private Double baseSalario;
    private String status;
    private String departamento;
    private String perfil; // Mapeia para "cargoOuPerfil"

    // Gere Getters e Setters para TODOS os campos abaixo
    // (O Spring precisa deles para ler e escrever os dados)

    // ... getters e setters ...

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public Double getBaseSalario() {
        return baseSalario;
    }

    public void setBaseSalario(Double baseSalario) {
        this.baseSalario = baseSalario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    // ... etc ...
}