package com.example.sistemarh.financeiro.repository;

import com.example.sistemarh.financeiro.model.FolhaPagamento;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FolhaPagamentoRepository {

    private static final String ARQUIVO_FOLHAS = "arquivos/folhas_pagamento.txt";
    private static final String SEPARADOR = ";";
    private AtomicLong contadorId = new AtomicLong(0);

    public FolhaPagamentoRepository() {
        carregarContadorId();
    }

    private void carregarContadorId() {
        try {
            if (Files.exists(Paths.get(ARQUIVO_FOLHAS))) {
                Optional<String> ultimaLinha = Files.lines(Paths.get(ARQUIVO_FOLHAS)).reduce((primeira, segunda) -> segunda);
                ultimaLinha.ifPresent(linha -> {
                    String[] dados = linha.split(SEPARADOR);
                    if (dados.length > 0) {
                        try {
                            contadorId.set(Long.parseLong(dados[0]));
                        } catch (NumberFormatException e) {}
                    }
                });
            }
        } catch (IOException e) {}
    }

    private long gerarNovoId() {
        return contadorId.incrementAndGet();
    }

    private String folhaParaLinha(FolhaPagamento folha) {
        return String.join(SEPARADOR,
                String.valueOf(folha.getId()),
                String.valueOf(folha.getMesReferencia()),
                String.valueOf(folha.getAnoReferencia()),
                folha.getDataProcessamento().toString(),
                String.valueOf(folha.getValorTotalBruto()),
                String.valueOf(folha.getValorTotalDescontos()),
                String.valueOf(folha.getValorTotalLiquido())
        );
    }

    private FolhaPagamento linhaParaFolha(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 7) return null;
        try {
            return new FolhaPagamento.Builder(
                    Long.parseLong(dados[0]),
                    Integer.parseInt(dados[1]),
                    Integer.parseInt(dados[2]),
                    LocalDate.parse(dados[3]),
                    null
            )
                    .valorTotalBruto(Double.parseDouble(dados[4]))
                    .valorTotalDescontos(Double.parseDouble(dados[5]))
                    .valorTotalLiquido(Double.parseDouble(dados[6]))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public FolhaPagamento salvar(FolhaPagamento folha) {
        long id = folha.getId();
        if (id == 0) {
            id = gerarNovoId();
        }

        folha = new FolhaPagamento.Builder(id, folha.getMesReferencia(), folha.getAnoReferencia(), folha.getDataProcessamento(), null)
                .valorTotalBruto(folha.getValorTotalBruto())
                .valorTotalDescontos(folha.getValorTotalDescontos())
                .valorTotalLiquido(folha.getValorTotalLiquido())
                .build();


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_FOLHAS, true))) {
            writer.write(folhaParaLinha(folha));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar folha de pagamento no arquivo: " + e.getMessage());
        }
        return folha;
    }

    public List<FolhaPagamento> buscarTodas() {
        List<FolhaPagamento> folhas = new ArrayList<>();
        if (!Files.exists(Paths.get(ARQUIVO_FOLHAS))) {
            return folhas;
        }
        try {
            folhas = Files.lines(Paths.get(ARQUIVO_FOLHAS))
                    .map(this::linhaParaFolha)
                    .filter(f -> f != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de folhas: " + e.getMessage());
        }
        return folhas;
    }
}