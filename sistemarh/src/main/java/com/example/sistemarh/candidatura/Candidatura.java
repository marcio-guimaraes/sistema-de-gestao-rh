package com.example.sistemarh.candidatura;

import com.example.sistemarh.recrutamento.model.Vaga;



import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;



class ExclusaoInvalidaException extends Exception {
    public ExclusaoInvalidaException(String message) {
        super(message);
    }
}


public class Candidatura {


    private long id;
    private String status;
    private LocalDate dataCandidatura;
    private Candidato candidato;
    private Vaga vaga;



    private static final String ARQUIVO_CANDIDATURAS = "candidaturas.txt";
    private static final AtomicLong contadorId = new AtomicLong(System.currentTimeMillis());


    private Candidatura(Candidato candidato, Vaga vaga) {
        this.id = contadorId.getAndIncrement();
        this.dataCandidatura = LocalDate.now();
        this.candidato = candidato;
        this.vaga = vaga;
        this.status = "Pendente";
    }

   -
    public long getId() { return id; }
    public String getStatus() { return status; }
    public Candidato getCandidato() { return candidato; }
    public Vaga getVaga() { return vaga; }
    public LocalDate getDataCandidatura() { return dataCandidatura; }


     //Recrutador vai atualizar, regras de negocio

    public void setStatus(String status) {
        this.status = status;
        // NOTA: Isso só muda na memória. Você precisaria de um método
        // estático "editarCandidatura" para salvar essa mudança no arquivo.
    }

/**
     * Converte o objeto em linha de texto para o arquivo.
     */
    @Override
    public String toString() {
        return String.join(";",
                String.valueOf(this.id),
                this.candidato.getCpf(), // Salva a CHAVE (CPF) do candidato
                String.valueOf(this.vaga.getId()), // Salva a CHAVE (ID) da Vaga
                this.status,
                this.dataCandidatura.toString()
        );
    }

    // --- 4. "FUNÇÕES" (MÉTODOS ESTÁTICOS DE PERSISTÊNCIA) ---
    // (Esta é a lógica que deveria estar em um Repositório)

    /**
     * Função para registrar (criar e salvar) uma nova candidatura.
     * Atende ao requisito "Associar candidatos a vagas".
     */
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

        // 2. Lógica de exclusão do arquivo (com arquivo temporário)
        File arquivoOriginal = new File(ARQUIVO_CANDIDATURAS);
        File arquivoTemp = new File("temp_candidaturas.txt");
        String idParaExcluir = String.valueOf(candidaturaParaExcluir.getId());

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoOriginal));
             BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoTemp))) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                // Se a linha NÃO for a da candidatura, nós a copiamos.
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
        System.out.println("Candidatura excluída com sucesso.");
    }


    public static List<Candidatura> listarCandidaturas() {
        List<Candidatura> candidaturas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_CANDIDATURAS))) {
            String linha;
            while ((linha = reader.readLine()) != null) { // Converte a linha de volta em um objeto
                Candidatura c = parseCandidatura(linha);
                if (c != null) {
                    candidaturas.add(c);
                }
            }
        } catch (IOException e) {
            System.err.println("Info: Arquivo de candidaturas ainda nao existe ou erro ao ler.");
        }
        return candidaturas;
    }


    private static Candidatura parseCandidatura(String linha) {
        String[] dados = linha.split(";");
        if (dados.length < 5) return null; // Linha inválida

        try {
            long id = Long.parseLong(dados[0]);
            String cpfCandidato = dados[1];
            long idVaga = Long.parseLong(dados[2]);
            String status = dados[3];
            LocalDate data = LocalDate.parse(dados[4]);

            // --- Acoplamento Forte (Ruim) ---
            // Para "remontar" o objeto, precisamos buscar os objetos reais
            // nos seus repositórios.
            CandidatoRepositorio candidatoRepo = new CandidatoRepositorio();
            // VagaRepositorio vagaRepo = new VagaRepositorio(); // (Do Aluno 3)

            Candidato candidato = candidatoRepo.buscarPorCpf(cpfCandidato);
            Vaga vaga = null; // vagaRepo.buscarPorId(idVaga);
            // ---------------------------------

            if (candidato == null || vaga == null) {
                System.err.println("Erro: Candidato (" + cpfCandidato + ") ou Vaga (" + idVaga + ") não encontrado ao parsear candidatura " + id);
                return null;
            }

            // Remonta o objeto
            Candidatura candidatura = new Candidatura(candidato, vaga);
            // Corrige os dados que o construtor definiu como padrão
            candidatura.id = id;
            candidatura.status = status;
            candidatura.dataCandidatura = data;

            return candidatura;

        } catch (Exception e) {
            System.err.println("Erro ao parsear linha da candidatura: " + linha);
            return null;
        }
    }
}