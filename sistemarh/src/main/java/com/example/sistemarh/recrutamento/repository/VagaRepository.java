package com.example.sistemarh.recrutamento.repository;

import com.example.sistemarh.recrutamento.model.Vaga;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class VagaRepository {


    private static final String NOME_ARQUIVO = "dados_vagas.csv";
    private static final String SEPARADOR = ";";
    private AtomicLong contadorId = new AtomicLong(0);

    public VagaRepository() {
        carregarContadorId();
    }

    private void carregarContadorId() {
        try {
            if (Files.exists(Paths.get(NOME_ARQUIVO))) {
                Optional<String> ultimaLinha = Files.lines(Paths.get(NOME_ARQUIVO)).reduce((primeira, segunda) -> segunda);
                ultimaLinha.ifPresent(linha -> {
                    String[] dados = linha.split(SEPARADOR);
                    if (dados.length > 0) {
                        try {
                            contadorId.set(Long.parseLong(dados[0]));
                        } catch (NumberFormatException e) {
                            System.err.println("Erro ao ler ID da última vaga no arquivo: " + e.getMessage());
                        }
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar contador de ID do arquivo de vagas: " + e.getMessage());
        }
    }

    private long gerarNovoId() {
        return contadorId.incrementAndGet();
    }

    private String vagaParaLinhaCsv(Vaga vaga) {
        return String.join(SEPARADOR, String.valueOf(vaga.getId()), vaga.getTitulo(), vaga.getDescricao(), String.valueOf(vaga.getSalarioMin()), String.valueOf(vaga.getSalarioMax()), vaga.getRegime(), vaga.getStatus(), vaga.getDepartamento(), vaga.getRequisitos(), vaga.getDataCriacao().toString());
    }

    private Vaga linhaCsvParaVaga(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 10) {
            System.err.println("Linha CSV inválida (colunas insuficientes): " + linha);
            return null;
        }
        try {
            long id = Long.parseLong(dados[0]);
            String titulo = dados[1];
            String descricao = dados[2];
            double salarioMin = Double.parseDouble(dados[3]);
            double salarioMax = Double.parseDouble(dados[4]);
            String regime = dados[5];
            String status = dados[6];
            String departamento = dados[7];
            String requisitos = dados[8];
            LocalDate dataCriacao = LocalDate.parse(dados[9]);

            return new Vaga.Builder(id, titulo).descricao(descricao).salario(salarioMin, salarioMax).regime(regime).status(status).departamento(departamento).requisitos(requisitos).dataCriacao(dataCriacao).build();
        } catch (Exception e) {
            System.err.println("Erro ao converter linha CSV para Vaga: '" + linha + "'. Erro: " + e.getMessage());
            return null;
        }
    }

    public Vaga salvar(Vaga vaga) {
        List<Vaga> todasVagas = buscarTodas();
        boolean atualizou = false;

        for (int i = 0; i < todasVagas.size(); i++) {
            if (todasVagas.get(i).getId() == vaga.getId() && vaga.getId() != 0) {
                todasVagas.set(i, vaga);
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            if (vaga.getId() == 0) {

                vaga = new Vaga.Builder(gerarNovoId(), vaga.getTitulo()).descricao(vaga.getDescricao()).salario(vaga.getSalarioMin(), vaga.getSalarioMax()).regime(vaga.getRegime()).status(vaga.getStatus()).departamento(vaga.getDepartamento()).requisitos(vaga.getRequisitos()).dataCriacao(vaga.getDataCriacao()).build();
            }
            todasVagas.add(vaga);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO, false))) {
            for (Vaga v : todasVagas) {
                writer.println(vagaParaLinhaCsv(v));
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar vaga no arquivo: " + e.getMessage());
        }
        return vaga;
    }

    public List<Vaga> buscarTodas() {
        List<Vaga> vagas = new ArrayList<>();
        if (!Files.exists(Paths.get(NOME_ARQUIVO))) {
            return vagas;
        }
        try {
            vagas = Files.lines(Paths.get(NOME_ARQUIVO)).map(this::linhaCsvParaVaga).filter(vaga -> vaga != null).collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de vagas: " + e.getMessage());
        }
        return vagas;
    }

    public Optional<Vaga> buscarPorId(long id) {
        return buscarTodas().stream().filter(vaga -> vaga.getId() == id).findFirst();
    }

    public boolean excluirPorId(long id) {
        List<Vaga> todasVagas = buscarTodas();
        boolean removeu = todasVagas.removeIf(vaga -> vaga.getId() == id);

        if (removeu) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(NOME_ARQUIVO, false))) {
                for (Vaga v : todasVagas) {
                    writer.println(vagaParaLinhaCsv(v));
                }
            } catch (IOException e) {
                System.err.println("Erro ao excluir vaga do arquivo: " + e.getMessage());
                return false;
            }
        }
        return removeu;
    }


    public List<Vaga> buscarPorStatus(String status) {
        return buscarTodas().stream().filter(vaga -> vaga.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
    }

    public List<Vaga> buscarPorDepartamento(String departamento) {
        return buscarTodas().stream().filter(vaga -> vaga.getDepartamento().equalsIgnoreCase(departamento)).collect(Collectors.toList());
    }
}