package com.example.sistemarh.candidatura.repository;

import com.example.sistemarh.candidatura.model.Candidato;
import com.example.sistemarh.candidatura.model.Candidatura;
import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.repository.VagaRepository;
import org.springframework.stereotype.Repository; // <-- ANOTAÇÃO OBRIGATÓRIA

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Repository
public class CandidaturaRepository {

    private static final String CANDIDATURAS_TXT = "candidaturas.txt";
    private static final String SEPARADOR = ";";
    private AtomicLong contadorId = new AtomicLong(0);


    private final CandidatoRepository candidatoRepository;
    private final VagaRepository vagaRepository;


    public CandidaturaRepository(CandidatoRepository candidatoRepository, VagaRepository vagaRepository) {
        this.candidatoRepository = candidatoRepository;
        this.vagaRepository = vagaRepository;
        carregarContadorId(); // inicializa o contador de ID
    }

    private void carregarContadorId() {
        long maxId = 99;
        try {
            if (Files.exists(Paths.get(CANDIDATURAS_TXT))) {

                Optional<String> ultimaLinha = Files.lines(Paths.get(CANDIDATURAS_TXT)).reduce((primeira, segunda) -> segunda);
                ultimaLinha.ifPresent(linha -> {
                    String[] dados = linha.split(SEPARADOR);
                    if (dados.length > 0) {
                        try {
                            contadorId.set(Long.parseLong(dados[0]));
                        } catch (NumberFormatException e) {
                            contadorId.set(maxId);
                        }
                    }
                });
            } else {
                contadorId.set(maxId);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar contador de ID de candidaturas: " + e.getMessage());
            contadorId.set(maxId);
        }
    }

    private long gerarNovoId() {
        return contadorId.incrementAndGet();
    }


    private String candidaturaParaLinha(Candidatura candidatura) {

        return candidatura.toString();
    }


    private Candidatura linhaParaCandidatura(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 5) return null;
        try {
            long id = Long.parseLong(dados[0]);
            String cpfCandidato = dados[1];
            long idVaga = Long.parseLong(dados[2]);
            String status = dados[3];
            LocalDate data = LocalDate.parse(dados[4]);


            // 1. Busca o Candidato real
            Optional<Candidato> optCandidato = candidatoRepository.buscarPorCpf(cpfCandidato);
            // 2. Busca a Vaga real
            Optional<Vaga> optVaga = vagaRepository.buscarPorId(idVaga);


            if (optCandidato.isPresent() && optVaga.isPresent()) {

                return new Candidatura.Builder(id, optCandidato.get(), optVaga.get())
                        .status(status)
                        .dataCandidatura(data)
                        .build();
            } else {
                System.err.println("Erro: Candidato (" + cpfCandidato + ") ou Vaga (" + idVaga + ") não encontrado ao parsear candidatura " + id);
                return null;
            }

        } catch (Exception e) {
            System.err.println("Erro ao parsear linha da candidatura: " + linha + " -> " + e.getMessage());
            return null;
        }
    }


     // Salva (cria ou atualiza) uma candidatura.

    public Candidatura salvar(Candidatura candidatura) {
        List<Candidatura> todasCandidaturas = buscarTodos();
        boolean atualizou = false;

        // Se o ID for 0, é uma nova candidatura, precisa gerar ID
        if (candidatura.getId() == 0) {
            long novoId = gerarNovoId();
            //recriando objeto
            candidatura = new Candidatura.Builder(novoId, candidatura.getCandidato(), candidatura.getVaga())
                    .status(candidatura.getStatus())
                    .dataCandidatura(candidatura.getDataCandidatura())
                    .build();
        }

        for (int i = 0; i < todasCandidaturas.size(); i++) {
            if (todasCandidaturas.get(i).getId() == candidatura.getId()) {
                todasCandidaturas.set(i, candidatura); // Substitui
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            todasCandidaturas.add(candidatura); // Adiciona
        }

        // Reescreve o arquivo
        try (PrintWriter writer = new PrintWriter(new FileWriter(CANDIDATURAS_TXT, false))) {
            for (Candidatura c : todasCandidaturas) {
                writer.println(candidaturaParaLinha(c));
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar candidatura no arquivo: " + e.getMessage());
        }
        return candidatura;
    }

    public List<Candidatura> buscarTodos() {
        if (!Files.exists(Paths.get(CANDIDATURAS_TXT))) {
            return new ArrayList<>();
        }
        try {

            return Files.lines(Paths.get(CANDIDATURAS_TXT))
                    .map(this::linhaParaCandidatura) //converte cada linha em objeto
                    .filter(c -> c != null)           //filtra linhas inválidas
                    .collect(Collectors.toList());    //coleta em uma lista
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de candidaturas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<Candidatura> buscarPorId(long id) {
        return buscarTodos().stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }


    public boolean excluirPorId(long id) {
        List<Candidatura> todasCandidaturas = buscarTodos();

        boolean removeu = todasCandidaturas.removeIf(c -> c.getId() == id);

        if (removeu) {

            try (PrintWriter writer = new PrintWriter(new FileWriter(CANDIDATURAS_TXT, false))) {
                for (Candidatura c : todasCandidaturas) {
                    writer.println(candidaturaParaLinha(c));
                }
            } catch (IOException e) {
                System.err.println("Erro ao excluir candidatura do arquivo: " + e.getMessage());
                return false;
            }
        }
        return removeu;
    }
}