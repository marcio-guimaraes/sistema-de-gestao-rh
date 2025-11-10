package com.example.sistemarh.recrutamento.repository;

import com.example.sistemarh.candidatura.Candidatura;
import com.example.sistemarh.recrutamento.model.Entrevista;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class EntrevistaRepository {

    private static final String NOME_ARQUIVO = "entrevistas.txt";
    private static final String SEPARADOR = ";";
    private AtomicLong contadorId = new AtomicLong(0);

    public EntrevistaRepository() {
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
            System.err.println("Erro ao carregar contador de ID de entrevistas: " + e.getMessage());
        }
    }

    private long gerarNovoId() {
        return contadorId.incrementAndGet();
    }

    private String entrevistaParaLinha(Entrevista entrevista) {
        return String.join(SEPARADOR,
                String.valueOf(entrevista.getId()),
                entrevista.getCpfCandidatoDoArquivo(),
                String.valueOf(entrevista.getIdVagaDoArquivo()),
                entrevista.getCpfRecrutadorDoArquivo(),
                entrevista.getDataHora().toString(),
                entrevista.getLocal(),
                entrevista.getFeedback() != null ? entrevista.getFeedback() : "",
                entrevista.getNota() != null ? String.valueOf(entrevista.getNota()) : ""
        );
    }

    private Entrevista linhaParaEntrevista(String linha) {
        String[] dados = linha.split(SEPARADOR);
        if (dados.length < 8) return null;

        try {
            long id = Long.parseLong(dados[0]);
            String cpfCandidato = dados[1];
            long idVaga = Long.parseLong(dados[2]);
            String cpfRecrutador = dados[3];
            LocalDateTime dataHora = LocalDateTime.parse(dados[4]);
            String local = dados[5];
            String feedback = dados[6];
            Double nota = dados[7].isEmpty() ? null : Double.parseDouble(dados[7]);

            return Entrevista.fromRepository(id, cpfCandidato, idVaga, cpfRecrutador, dataHora, local, feedback, nota);
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha de entrevista: " + linha);
            return null;
        }
    }

    public Entrevista salvar(Entrevista entrevista) {
        if (entrevista.getId() == 0) {
            entrevista.setId(gerarNovoId());
        }

        List<Entrevista> entrevistas = buscarTodas();
        boolean atualizou = false;

        for (int i = 0; i < entrevistas.size(); i++) {
            if (entrevistas.get(i).getId() == entrevista.getId()) {
                entrevistas.set(i, entrevista);
                atualizou = true;
                break;
            }
        }

        if (!atualizou) {
            entrevistas.add(entrevista);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO, false))) {
            for (Entrevista e : entrevistas) {
                writer.write(entrevistaParaLinha(e));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar entrevista no arquivo: " + e.getMessage());
        }
        return entrevista;
    }


    public List<Entrevista> buscarTodas() {
        List<Entrevista> entrevistas = new ArrayList<>();
        if (!Files.exists(Paths.get(NOME_ARQUIVO))) {
            return entrevistas;
        }
        try {
            entrevistas = Files.lines(Paths.get(NOME_ARQUIVO))
                    .map(this::linhaParaEntrevista)
                    .filter(e -> e != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de entrevistas: " + e.getMessage());
        }
        return entrevistas;
    }
}