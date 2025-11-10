package com.example.sistemarh.candidatura;

import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CandidaturaRepository {

    private static final String ARQUIVO_CANDIDATURAS = "candidaturas.txt";
    private static final String SEPARADOR = ";";

    private Candidatura linhaParaCandidatura(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 5) return null;

        try {
            long id = Long.parseLong(dados[0]);
            String cpfCandidato = dados[1];
            long idVaga = Long.parseLong(dados[2]);
            String status = dados[3];
            LocalDate data = LocalDate.parse(dados[4]);

            return new Candidatura(id, cpfCandidato, idVaga, status, data);

        } catch (Exception e) {
            System.err.println("Erro ao parsear linha da candidatura: " + linha + " -> " + e.getMessage());
            return null;
        }
    }

    private void salvarListaNoArquivo(List<Candidatura> candidaturas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_CANDIDATURAS, false))) {
            for (Candidatura c : candidaturas) {
                writer.write(c.toLinhaArquivo());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar lista de candidaturas no arquivo: " + e.getMessage());
        }
    }

    public Candidatura salvar(Candidatura candidatura) {
        List<Candidatura> candidaturas = buscarTodas();

        Optional<Candidatura> existente = candidaturas.stream()
                .filter(c -> c.getId() == candidatura.getId())
                .findFirst();

        if (existente.isPresent()) {
            int index = candidaturas.indexOf(existente.get());
            candidaturas.set(index, candidatura);
        } else {
            candidaturas.add(candidatura);
        }

        salvarListaNoArquivo(candidaturas);
        return candidatura;
    }

    public List<Candidatura> buscarTodas() {
        List<Candidatura> candidaturas = new ArrayList<>();
        if (!Files.exists(Paths.get(ARQUIVO_CANDIDATURAS))) {
            return candidaturas;
        }
        try {
            candidaturas = Files.lines(Paths.get(ARQUIVO_CANDIDATURAS))
                    .map(this::linhaParaCandidatura)
                    .filter(c -> c != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de candidaturas: " + e.getMessage());
        }
        return candidaturas;
    }

    public Optional<Candidatura> buscarPorId(long id) {
        return buscarTodas().stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    public List<Candidatura> buscarPorCpf(String cpf) {
        return buscarTodas().stream()
                .filter(c -> c.getCpfCandidatoDoArquivo().equals(cpf))
                .collect(Collectors.toList());
    }

    public List<Candidatura> buscarPorVagaId(long vagaId) {
        return buscarTodas().stream()
                .filter(c -> c.getIdVagaDoArquivo() == vagaId)
                .collect(Collectors.toList());
    }

    public void excluirPorId(long id) {
        List<Candidatura> candidaturas = buscarTodas();
        List<Candidatura> filtradas = candidaturas.stream()
                .filter(c -> c.getId() != id)
                .collect(Collectors.toList());
        salvarListaNoArquivo(filtradas);
    }
}