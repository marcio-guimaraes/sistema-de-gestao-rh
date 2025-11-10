package com.example.sistemarh.candidatura;


import com.example.sistemarh.recrutamento.model.Vaga;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ExclusaoInvalidaException extends Exception {
    public ExclusaoInvalidaException(String message) {
        super(message);
    }
}


public class Candidatura {

    private long id;
    private String status;
    private LocalDate dataCandidatura;

    private Candidato candidato;
    private Vaga vaga;

    private String cpfCandidatoDoArquivo;
    private long idVagaDoArquivo;

    private static final String ARQUIVO_CANDIDATURAS = "candidaturas.txt";
    private static long proximoId;

    static {
        proximoId = inicializarId();
    }

    public Candidatura(long id, String cpfCandidato, long idVaga, String status, LocalDate data) {
        this.id = id;
        this.candidato = null;
        this.vaga = null;
        this.status = status;
        this.dataCandidatura = data;
        this.cpfCandidatoDoArquivo = cpfCandidato;
        this.idVagaDoArquivo = idVaga;
    }

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDataCandidatura() {
        return dataCandidatura;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public String getCpfCandidatoDoArquivo() {
        return cpfCandidatoDoArquivo;
    }

    public long getIdVagaDoArquivo() {
        return idVagaDoArquivo;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String toLinhaArquivo() {
        return String.join(";",
                String.valueOf(this.id),
                this.cpfCandidatoDoArquivo,
                String.valueOf(this.idVagaDoArquivo),
                this.status,
                this.dataCandidatura.toString()
        );
    }

    @Override
    public String toString() {
        return toLinhaArquivo();
    }

    private static long inicializarId() {
        long maxId = 99;
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_CANDIDATURAS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length > 0) {
                    try {
                        long id = Long.parseLong(dados[0]);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } catch (IOException e) {
        }
        return maxId + 1;
    }

    public static long getProximoId() {
        return proximoId++;
    }
}