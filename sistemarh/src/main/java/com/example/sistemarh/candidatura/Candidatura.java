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

//atributos
    private long id;
    private String status;
    private LocalDate dataCandidatura;

//objeto
    private Candidato candidato;
    private Vaga vaga;

    //chaves
    private String cpfCandidatoDoArquivo;
    private long idVagaDoArquivo;

    //arquivo
    private static final String ARQUIVO_CANDIDATURAS = "candidaturas.txt";
    private static long proximoId;

    static {
        proximoId = inicializarId();
    }

    private Candidatura(Candidato candidato, Vaga vaga) {
        this.id = proximoId++;
        this.dataCandidatura = LocalDate.now();
        this.candidato = candidato;
        this.vaga = vaga;
        this.status = "Pendente";
        this.cpfCandidatoDoArquivo = (candidato != null) ? candidato.getCpf() : null;
        this.idVagaDoArquivo = (vaga != null) ? vaga.getId() : 0;
    }


    private Candidatura(long id, String cpfCandidato, long idVaga, String status, LocalDate data) {
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


    public String getCpfCandidatoDoArquivo() {
        return cpfCandidatoDoArquivo;
    }

    public long getIdVagaDoArquivo() {
        return idVagaDoArquivo;
    }


    public void setStatus(String status) {
        this.status = status;
        // depois um metodo editarStatus para salvar no arquivo
    }

    @Override
    public String toString() {

        return String.join(";",
                String.valueOf(this.id),       //0
                this.cpfCandidatoDoArquivo,    //1
                String.valueOf(this.idVagaDoArquivo), //2
                this.status,                   //3
                this.dataCandidatura.toString() //4
        );
    }


    public static Candidatura registrarCandidatura(Candidato candidato, Vaga vaga) {
        Candidatura novaCandidatura = new Candidatura(candidato, vaga);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_CANDIDATURAS, true))) {
            writer.write(novaCandidatura.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar Candidatura: " + e.getMessage());
        }
        return novaCandidatura;
    }


    public static void excluirCandidatura(Candidatura candidaturaParaExcluir) throws ExclusaoInvalidaException {

        if (!candidaturaParaExcluir.getStatus().equalsIgnoreCase("Pendente")) {
            throw new ExclusaoInvalidaException("Erro: Somente candidaturas com status 'Pendente' podem ser excluídas.");
        }
        File arquivoOriginal = new File(ARQUIVO_CANDIDATURAS);
        File arquivoTemp = new File("temp_candidaturas.txt");
        String idParaExcluir = String.valueOf(candidaturaParaExcluir.getId());
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.startsWith(idParaExcluir + ";")) {
                    writer.write(linha);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao excluir Candidatura: " + e.getMessage());
            return;
        }
        arquivoOriginal.delete();
        arquivoTemp.renameTo(arquivoOriginal);
        System.out.println("Candidatura excluída.");
    }


    public static List<Candidatura> listarCandidaturas() {
        List<Candidatura> candidaturas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_CANDIDATURAS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {

                Candidatura c = parseCandidatura(linha); //
                if (c != null) {
                    candidaturas.add(c);
                }
            }
        } catch (IOException e) {
            System.err.println("Arquivo de candidaturas nao existe ou erro ao ler.");
        }
        return candidaturas;
    }


    private static Candidatura parseCandidatura(String linha) {
        String[] dados = linha.split(";");
        if (dados.length < 5) return null;

        try {
            long id = Long.parseLong(dados[0]);
            String cpfCandidato = dados[1];
            long idVaga = Long.parseLong(dados[2]);
            String status = dados[3];
            LocalDate data = LocalDate.parse(dados[4]);

            Candidatura candidatura = new Candidatura(id, cpfCandidato, idVaga, status, data);

            return candidatura;

        } catch (Exception e) {
            System.err.println("Erro ao parsear linha da candidatura: " + linha + " -> " + e.getMessage());
            return null;
        }
    }

    private static long inicializarId() {
        long maxId = 99; //pro prox comecar em 100
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
                    } catch (NumberFormatException e) { // ignora
                    }

                }
            }
        } catch (IOException e) { //ignora
        }
        return maxId + 1;
    }
}