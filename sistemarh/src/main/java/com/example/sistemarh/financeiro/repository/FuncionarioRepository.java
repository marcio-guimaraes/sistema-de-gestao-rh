package com.example.sistemarh.financeiro.repository;

import com.example.sistemarh.financeiro.model.Funcionario;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FuncionarioRepository {

    private static final String ARQUIVO_FUNCIONARIOS = "funcionarios.txt";

    public void salvar(Funcionario f) {
        // CORREÇÃO: Estava faltando "f.toString()"
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_FUNCIONARIOS, true))) {
            writer.write(f.toString()); // <-- Corrigido
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar funcionário: " + e.getMessage());
        }
    }

    /**
     * Converte uma linha do arquivo em um objeto Funcionario.
     */
    private Funcionario parseFuncionario(String linha) {
        try {
            String[] campos = linha.split(";");
            if (campos.length < 10) {
                System.err.println("Linha de funcionário incompleta, pulando: " + linha);
                return null; // Pula linha mal formatada
            }

            // A ordem DEVE ser a mesma do toString()
            String nome = campos[0];
            String cpf = campos[1];
            String login = campos[2];
            String senha = campos[3]; // Lembre-se: NUNCA salve senhas em texto puro em apps reais
            String matricula = campos[4];
            LocalDate dataAdmissao = LocalDate.parse(campos[5]);
            Double baseSalario = Double.parseDouble(campos[6]);
            String status = campos[7];
            String departamento = campos[8];
            String cargo = campos[9];

            return new Funcionario(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status, departamento, cargo);

        } catch (Exception e) {
            System.err.println("Erro ao parsear linha do funcionário: " + linha + " -> " + e.getMessage());
            return null;
        }
    }


    public List<Funcionario> buscarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_FUNCIONARIOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                // CORREÇÃO: Agora faz o parse da linha para Funcionario
                Funcionario f = parseFuncionario(linha);
                if (f != null) {
                    funcionarios.add(f); // Adiciona o OBJETO Funcionario
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler funcionários: " + e.getMessage());
        }
        return funcionarios; // Retorna a lista de OBJETOS
    }

    public List<Funcionario> filtrar(String cargo, String tipoContratacao, String status, String departamento) {
        // OTIMIZAÇÃO: Reutiliza o buscarTodos() que já funciona
        List<Funcionario> todos = buscarTodos();

        return todos.stream()
                .filter(f -> cargo == null || cargo.isEmpty() || cargo.equalsIgnoreCase(f.getCargo()))
                .filter(f -> tipoContratacao == null || tipoContratacao.isEmpty() ||
                        (f.getRegraSalario() != null && tipoContratacao.equalsIgnoreCase(f.getRegraSalario().getNomeRegra())))
                .filter(f -> status == null || status.isEmpty() || status.equalsIgnoreCase(f.getStatus()))
                .filter(f -> departamento == null || departamento.isEmpty() || departamento.equalsIgnoreCase(f.getDepartamento()))
                .collect(Collectors.toList());
    }
}


/*package com.example.sistemarh.financeiro.repository;

import com.example.sistemarh.financeiro.model.Funcionario;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FuncionarioRepository {

    private static final String ARQUIVO_FUNCIONARIOS = "funcionarios.txt";

    public void salvar(Funcionario f) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_FUNCIONARIOS, true))) {
            writer.write(f.toString());;
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erro ao salvar funcionário: " + e.getMessage());
        }
    }

    public List<Funcionario> buscarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_FUNCIONARIOS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                funcionarios.add(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler funcionários: " + e.getMessage());
        }
        return funcionarios;
    }

    public List<Funcionario> filtrar(String cargo,String tipoContratacao,String status,String departamento) {
        List<Funcionario> lista = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("funcionarios.txt"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] campos = linha.split(";");
                if (campos.length < 9) continue; // pula linhas incompletas
                String matricula = campos[0];
                String nome = campos[1];
                String cpf = campos[2];
                String login = campos[3];
                LocalDate dataAdmissao = LocalDate.parse(campos[4]);
                Double baseSalario = Double.parseDouble(campos[5]);
                String statusFuncionario = campos[6];
                String dept = campos[7];
                String cargoFunc = campos[8];
                String tipo = campos.length > 9 ? campos[9] : "";

                Funcionario f = new Funcionario(nome, cpf, login, "senha", matricula, dataAdmissao, baseSalario, statusFuncionario, dept, cargoFunc);

                lista.add(f);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de funcionários: " + e.getMessage());
        }

        return lista.stream()
                .filter(f -> cargo == null || cargo.isEmpty() || cargo.equalsIgnoreCase(f.getCargo()))
                .filter(f -> tipoContratacao == null || tipoContratacao.isEmpty() ||
                        (f.getRegraSalario() != null && tipoContratacao.equalsIgnoreCase(f.getRegraSalario().getNomeRegra())))
                .filter(f -> status == null || status.isEmpty() || status.equalsIgnoreCase(f.getStatus()))
                .filter(f -> departamento == null || departamento.isEmpty() || departamento.equalsIgnoreCase(f.getDepartamento()))
                .collect(Collectors.toList());
    }

}
*/