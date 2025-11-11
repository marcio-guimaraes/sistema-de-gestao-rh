package com.example.sistemarh.financeiro.repository;

import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import com.example.sistemarh.financeiro.model.RegraSalario;

@Repository
public class RegraSalarialRepository {

    private static final String NOME_ARQUIVO = "regras_salariais.txt"; // Mudei o nome para o plural
    private static final String SEPARADOR = ";";
    private AtomicLong contadorId = new AtomicLong(0);

    public RegraSalarialRepository() {
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
                        }
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar contador de ID de regras salariais: " + e.getMessage());
        }
    }

    private long gerarNovoId() {
        return contadorId.incrementAndGet();
    }

    private String regraParaLinha(RegraSalario regra) {
        return String.join(SEPARADOR,
                String.valueOf(regra.getId()),
                regra.getNomeRegra(),
                String.valueOf(regra.getValorValeTransporte()),
                String.valueOf(regra.getPercentualDescVT()),
                String.valueOf(regra.getValorValeAlimentacao()),
                String.valueOf(regra.getPercentualINSS()),
                String.valueOf(regra.getPercentualRRF())
        );
    }

    private RegraSalario linhaParaRegra(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 7) return null;

        try {
            return new RegraSalario(
                    Long.parseLong(dados[0]),
                    dados[1],
                    Double.parseDouble(dados[2]),
                    Double.parseDouble(dados[3]),
                    Double.parseDouble(dados[4]),
                    Double.parseDouble(dados[5]),
                    Double.parseDouble(dados[6])
            );
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha da regra salarial: " + linha);
            return null;
        }
    }

    private void salvarListaNoArquivo(List<RegraSalario> regras) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO, false))) {
            for (RegraSalario r : regras) {
                writer.write(regraParaLinha(r));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar lista de regras no arquivo: " + e.getMessage());
        }
    }

    public RegraSalario salvar(RegraSalario regra) {
        List<RegraSalario> regras = buscarTodas();
        boolean atualizou = false;

        if (regra.getId() == 0) {
            regra.setId(gerarNovoId());
        }

        for (int i = 0; i < regras.size(); i++) {
            if (regras.get(i).getId() == regra.getId()) {
                regras.set(i, regra); // Atualiza a regra existente
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            regras.add(regra); // Adiciona nova regra
        }

        salvarListaNoArquivo(regras);
        return regra;
    }

    public List<RegraSalario> buscarTodas() {
        List<RegraSalario> regras = new ArrayList<>();
        if (!Files.exists(Paths.get(NOME_ARQUIVO))) {
            return regras;
        }
        try {
            regras = Files.lines(Paths.get(NOME_ARQUIVO))
                    .map(this::linhaParaRegra)
                    .filter(r -> r != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de regras salariais: " + e.getMessage());
        }
        return regras;
    }

    public Optional<RegraSalario> buscarPorId(long id) {
        return buscarTodas().stream()
                .filter(r -> r.getId() == id)
                .findFirst();
    }

    public void excluirPorId(long id) {
        List<RegraSalario> regras = buscarTodas();
        boolean removeu = regras.removeIf(r -> r.getId() == id);
        if (removeu) {
            salvarListaNoArquivo(regras);
        }
    }

}