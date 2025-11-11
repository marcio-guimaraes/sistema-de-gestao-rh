package com.example.sistemarh.candidatura.repository;

import com.example.sistemarh.candidatura.model.Candidato;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
public class CandidatoRepository {


    private static final String NOME_ARQUIVO = "candidatos.txt";
    private static final String SEPARADOR = ";"; /

    private Candidato linhaParaCandidato(String linha) {
        String[] dados = linha.split(SEPARADOR);

        if (dados.length < 8) {
            System.err.println("Linha CSV inválida (colunas insuficientes): " + linha);
            return null;
        }

        try {

            return new Candidato.Builder(dados[0], dados[1]) // nome, cpf
                    .formacao(dados[2])
                    .experiencia(dados[3])
                    .pretensaoSalario(Double.parseDouble(dados[4]))
                    .disponibilidade(dados[5])
                    .dataCadastro(LocalDate.parse(dados[6]))
                    .documentosAdicionais(dados[7])
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha do candidato: " + linha + " -> " + e.getMessage());
            return null;
        }
    }


    private String candidatoParaLinha(Candidato candidato) {
        return candidato.toString();
    }


    public Candidato salvar(Candidato candidato) {
        List<Candidato> todosCandidatos = buscarTodos();
        boolean atualizou = false;

        //tenta encontrar pelo CPF
        for (int i = 0; i < todosCandidatos.size(); i++) {
            if (todosCandidatos.get(i).getCpf().equals(candidato.getCpf())) {
                todosCandidatos.set(i, candidato); //substitui o antigo pelo novo
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            todosCandidatos.add(candidato); //adiciona o novo no fim da lista
        }

        //lista atualizada
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO, false))) { // false = sobrescrever
            for (Candidato c : todosCandidatos) {
                writer.println(candidatoParaLinha(c));
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar candidato no arquivo: " + e.getMessage());
        }
        return candidato;
    }

    public List<Candidato> buscarTodos() {
        List<Candidato> candidatos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                Candidato c = linhaParaCandidato(linha);
                if (c != null) {
                    candidatos.add(c);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de candidatos: " + e.getMessage());
        }
        return candidatos;
    }

    public Optional<Candidato> buscarPorCpf(String cpf) {

        List<Candidato> todos = buscarTodos();

        for (Candidato c : todos) {
            if (c.getCpf().equals(cpf)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public boolean excluirPorCpf(String cpf) {
        List<Candidato> todosCandidatos = buscarTodos();


        boolean removeu = todosCandidatos.removeIf(c -> c.getCpf().equals(cpf));


        if (removeu) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO, false))) {
                for (Candidato c : todosCandidatos) {
                    writer.println(candidatoParaLinha(c));
                }
            } catch (IOException e) {
                System.err.println("Erro ao excluir candidato do arquivo: " + e.getMessage());
                return false;
            }
        }
        return removeu;
    }
}