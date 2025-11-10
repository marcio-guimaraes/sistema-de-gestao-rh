package com.example.sistemarh.financeiro;

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
import java.util.stream.Collectors;

@Repository
public class FuncionarioRepository {

    private static final String ARQUIVO_FUNCIONARIOS = "funcionarios.txt";
    private static final String SEPARADOR = ";";

    private Funcionario linhaParaFuncionario(String linha) {
        String[] dados = linha.split(SEPARADOR);
        // Um funcionário sempre terá 10 campos
        if (dados.length < 10) return null;

        try {
            String nome = dados[0];
            String cpf = dados[1];
            String login = dados[2];
            String senha = dados[3];
            String matricula = dados[4];
            LocalDate dataAdmissao = LocalDate.parse(dados[5]);
            Double baseSalario = Double.parseDouble(dados[6]);
            String status = dados[7];
            String departamento = dados[8];
            String cargo = dados[9]; // Perfil (ADMIN, GESTOR) ou Cargo (RECRUTADOR, FUNCIONARIO)

            // Instancia a classe correta com base no cargo/perfil
            // (Usamos o construtor de Funcionario, mas a lógica de usuário diferencia)
            return new Funcionario(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status, departamento, cargo);

        } catch (Exception e) {
            System.err.println("Erro ao parsear linha do funcionário: " + linha + " -> " + e.getMessage());
            return null;
        }
    }

    private String funcionarioParaLinha(Funcionario f) {
        return String.join(SEPARADOR,
                f.getNome(),
                f.getCpf(),
                f.getLogin(),
                f.getSenha(),
                f.getMatricula(),
                f.getDataAdmissao().toString(),
                f.getBaseSalario().toString(),
                f.getStatus(),
                f.getDepartamento(),
                f.getCargo()
        );
    }

    private void salvarListaNoArquivo(List<Funcionario> funcionarios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_FUNCIONARIOS, false))) {
            for (Funcionario f : funcionarios) {
                writer.write(funcionarioParaLinha(f));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar lista de funcionários no arquivo: " + e.getMessage());
        }
    }

    public Funcionario salvar(Funcionario funcionario) {
        List<Funcionario> funcionarios = buscarTodos();
        Optional<Funcionario> existente = funcionarios.stream()
                .filter(f -> f.getCpf().equals(funcionario.getCpf()))
                .findFirst();

        if (existente.isPresent()) {
            int index = funcionarios.indexOf(existente.get());
            funcionarios.set(index, funcionario);
        } else {
            funcionarios.add(funcionario);
        }

        salvarListaNoArquivo(funcionarios);
        return funcionario;
    }

    public List<Funcionario> buscarTodos() {
        List<Funcionario> funcionarios = new ArrayList<>();
        if (!Files.exists(Paths.get(ARQUIVO_FUNCIONARIOS))) {
            return funcionarios;
        }
        try {
            // Filtra apenas linhas que são de Funcionários (e não usuários simples)
            funcionarios = Files.lines(Paths.get(ARQUIVO_FUNCIONARIOS))
                    .filter(linha -> linha.split(SEPARADOR).length >= 10)
                    .map(this::linhaParaFuncionario)
                    .filter(f -> f != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de funcionários: " + e.getMessage());
        }
        return funcionarios;
    }

    public Optional<Funcionario> buscarPorCpf(String cpf) {
        return buscarTodos().stream()
                .filter(f -> f.getCpf().equals(cpf))
                .findFirst();
    }
}