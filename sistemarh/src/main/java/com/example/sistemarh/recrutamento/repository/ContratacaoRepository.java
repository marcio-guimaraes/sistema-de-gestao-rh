package com.example.sistemarh.recrutamento.repository;

import com.example.sistemarh.recrutamento.model.Contratacao;
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
public class ContratacaoRepository {

    private static final String NOME_ARQUIVO = "arquivos/contratacoes.txt";
    private static final String SEPARADOR = ";";
    private AtomicLong contadorId = new AtomicLong(0);

    public ContratacaoRepository() {
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
                        } catch (NumberFormatException e) {}
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar contador de ID de contratações: " + e.getMessage());
        }
    }

    private long gerarNovoId() {
        return contadorId.incrementAndGet();
    }

    private String contratacaoParaLinha(Contratacao c) {
        return String.join(SEPARADOR,
                String.valueOf(c.getId()),
                c.getCpfCandidatoDoArquivo(),
                String.valueOf(c.getIdVagaDoArquivo()),
                c.getStatus(),
                c.getDataSolicitacao().toString(),
                c.getDataAprovacaoGestor() != null ? c.getDataAprovacaoGestor().toString() : "",
                c.getDataEfetivacao() != null ? c.getDataEfetivacao().toString() : "",
                c.getRegimeContratacao()
        );
    }

    private Contratacao linhaParaContratacao(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 8) return null;

        try {
            long id = Long.parseLong(dados[0]);
            String cpfCandidato = dados[1];
            long idVaga = Long.parseLong(dados[2]);
            String status = dados[3];
            LocalDate dataSolic = LocalDate.parse(dados[4]);
            LocalDate dataAprov = dados[5].isEmpty() ? null : LocalDate.parse(dados[5]);
            LocalDate dataEfetiv = dados[6].isEmpty() ? null : LocalDate.parse(dados[6]);
            String regime = dados[7];

            return Contratacao.fromRepository(id, cpfCandidato, idVaga, status, dataSolic, dataAprov, dataEfetiv, regime);
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha de contratação: " + linha);
            return null;
        }
    }

    public Contratacao salvar(Contratacao contratacao) {
        if (contratacao.getId() == 0) {
            contratacao.setId(gerarNovoId());
        }

        List<Contratacao> contratacoes = buscarTodas();
        boolean atualizou = false;

        for (int i = 0; i < contratacoes.size(); i++) {
            if (contratacoes.get(i).getId() == contratacao.getId()) {
                contratacoes.set(i, contratacao);
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            contratacoes.add(contratacao);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO, false))) {
            for (Contratacao c : contratacoes) {
                writer.write(contratacaoParaLinha(c));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar contratação no arquivo: " + e.getMessage());
        }
        return contratacao;
    }

    public List<Contratacao> buscarTodas() {
        List<Contratacao> contratacoes = new ArrayList<>();
        if (!Files.exists(Paths.get(NOME_ARQUIVO))) {
            return contratacoes;
        }
        try {
            contratacoes = Files.lines(Paths.get(NOME_ARQUIVO))
                    .map(this::linhaParaContratacao)
                    .filter(c -> c != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de contratações: " + e.getMessage());
        }
        return contratacoes;
    }

    public Optional<Contratacao> buscarPorId(long id) {
        return buscarTodas().stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }
}