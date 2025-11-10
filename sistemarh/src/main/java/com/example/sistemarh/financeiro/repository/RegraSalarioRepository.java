package com.example.sistemarh.financeiro.repository;

import com.example.sistemarh.financeiro.model.RegraSalario;
import org.springframework.stereotype.Repository;

import java.io.*;

@Repository
public class RegraSalarioRepository {

    private static final String ARQUIVO_REGRAS = "regras_salariais.txt";

    /**
     * Salva o objeto de regras no arquivo.
     * Ele sempre sobrescreve o arquivo, pois só há um conjunto de regras.
     */
    public void salvar(RegraSalario regra) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_REGRAS, false))) { // false = sobrescrever

            // Salva os campos separados por ponto e vírgula
            String linha = String.join(";",
                    String.valueOf(regra.getId()),
                    regra.getNomeRegra(),
                    String.valueOf(regra.getValorValeTransporte()),
                    String.valueOf(regra.getPercentualDescVT()),
                    String.valueOf(regra.getValorValeAlimentacao()),
                    String.valueOf(regra.getPercentualINSS()),
                    String.valueOf(regra.getPercentualRRF())
            );
            writer.write(linha);
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Erro ao salvar regras salariais: " + e.getMessage());
        }
    }

    /**
     * Carrega o objeto de regras do arquivo.
     * Lê apenas a primeira linha válida do arquivo.
     */
    public RegraSalario carregar() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_REGRAS))) {
            String linha = reader.readLine(); // Lê a primeira (e única) linha
            if (linha == null || linha.isEmpty()) {
                return null;
            }

            String[] campos = linha.split(";");
            if (campos.length < 7) {
                System.err.println("Arquivo de regras corrompido.");
                return null;
            }

            // Faz o "parse" dos campos na ordem em que foram salvos
            long id = Long.parseLong(campos[0]);
            String nomeRegra = campos[1];
            double valorVT = Double.parseDouble(campos[2]);
            double descVT = Double.parseDouble(campos[3]);
            double valorVA = Double.parseDouble(campos[4]);
            double percINSS = Double.parseDouble(campos[5]);
            double percIRRF = Double.parseDouble(campos[6]);

            return new RegraSalario(id, nomeRegra, valorVT, descVT, valorVA, percINSS, percIRRF);

        } catch (FileNotFoundException e) {
            System.err.println("Arquivo de regras ainda não foi criado. Salve as regras primeiro.");
            return null; // Arquivo não existe ainda
        } catch (IOException e) {
            System.err.println("Erro ao carregar regras salariais: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao fazer parse das regras: " + e.getMessage());
            return null;
        }
    }
}