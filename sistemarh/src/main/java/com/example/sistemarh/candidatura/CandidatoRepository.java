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
public class CandidatoRepository {

    private static final String ARQUIVO_CANDIDATOS = "candidatos.txt";
    private static final String SEPARADOR = ";";

    private Candidato linhaParaCandidato(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 8) return null;

        try {
            String nome = dados[0];
            String cpf = dados[1];
            String formacao = dados[2];
            String experiencia = dados[3];
            double pretensao = Double.parseDouble(dados[4]);
            String disponibilidade = dados[5];
            LocalDate dataCadastro = LocalDate.parse(dados[6]);
            String documentos = dados[7];

            return new Candidato.Builder(nome, cpf)
                    .formacao(formacao)
                    .experiencia(experiencia)
                    .pretensaoSalario(pretensao)
                    .disponibilidade(disponibilidade)
                    .dataCadastro(dataCadastro)
                    .documentosAdicionais(documentos)
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha do candidato: " + linha);
            return null;
        }
    }

    private void salvarListaNoArquivo(List<Candidato> candidatos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_CANDIDATOS, false))) {
            for (Candidato c : candidatos) {
                writer.write(c.toLinhaArquivo());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar lista de candidatos no arquivo: " + e.getMessage());
        }
    }

    public Candidato salvar(Candidato candidato) {
        List<Candidato> candidatos = buscarTodos();
        Optional<Candidato> existente = candidatos.stream()
                .filter(c -> c.getCpf().equals(candidato.getCpf()))
                .findFirst();

        if (existente.isPresent()) {
            int index = candidatos.indexOf(existente.get());
            candidatos.set(index, candidato);
        } else {
            candidatos.add(candidato);
        }

        salvarListaNoArquivo(candidatos);
        return candidato;
    }

    public List<Candidato> buscarTodos() {
        List<Candidato> candidatos = new ArrayList<>();
        if (!Files.exists(Paths.get(ARQUIVO_CANDIDATOS))) {
            return candidatos;
        }
        try {
            candidatos = Files.lines(Paths.get(ARQUIVO_CANDIDATOS))
                    .map(this::linhaParaCandidato)
                    .filter(c -> c != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de candidatos: " + e.getMessage());
        }
        return candidatos;
    }

    public Optional<Candidato> buscarPorCpf(String cpf) {
        return buscarTodos().stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst();
    }

    public void excluirPorCpf(String cpf) {
        List<Candidato> candidatos = buscarTodos();
        List<Candidato> filtrados = candidatos.stream()
                .filter(c -> !c.getCpf().equals(cpf))
                .collect(Collectors.toList());
        salvarListaNoArquivo(filtrados);
    }
}