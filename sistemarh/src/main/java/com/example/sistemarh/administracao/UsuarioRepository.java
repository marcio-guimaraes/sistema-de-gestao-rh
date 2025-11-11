package com.example.sistemarh.administracao;

// Importe o Funcionario, pois ele agora é usado aqui
import com.example.sistemarh.financeiro.model.Funcionario;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UsuarioRepository {

    private static final String ARQUIVO_USUARIOS = "usuarios.txt";
    private static final String SEPARADOR = ";";

    public UsuarioRepository() {
        // Cria um usuário admin padrão se o arquivo não existir
        try {
            if (!Files.exists(Paths.get(ARQUIVO_USUARIOS))) {
                Files.createFile(Paths.get(ARQUIVO_USUARIOS));

                Administrador admin = new Administrador(
                        "Administrador",
                        "00000000000",
                        "admin",
                        "admin123",
                        "ADM001",
                        LocalDate.now(),
                        0.0,
                        "Ativo"
                );
                salvar(admin);
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo de usuário padrão: " + e.getMessage());
        }
    }

    private Usuario linhaParaUsuario(String linha) {
        String[] dados = linha.split(SEPARADOR);
        // Um funcionário (Admin, Gestor, Func) tem 10 campos. Um usuário simples tem 4.
        if (dados.length < 4) return null;

        String nome = dados[0];
        String cpf = dados[1];
        String login = dados[2];
        String senha = dados[3];

        // Se não tiver mais dados, é um usuário simples
        if (dados.length < 10) {
            return new Usuario(nome, cpf, login, senha);
        }

        try {
            // Se tem mais dados, é um Funcionario ou subclasse
            String matricula = dados[4];
            LocalDate dataAdmissao = LocalDate.parse(dados[5]);
            Double baseSalario = Double.parseDouble(dados[6]);
            String status = dados[7];
            String departamento = dados[8];
            String cargoOuPerfil = dados[9]; // "ADMIN", "GESTOR", "RECRUTADOR", "FUNCIONÁRIO"
            long regraId = 1;
            if (dados.length > 10 && !dados[10].isEmpty()) {
                regraId = Long.parseLong(dados[10]);
            }

            switch (cargoOuPerfil) {
                case "ADMIN":
                    return new Administrador(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
                case "GESTOR":
                    return new Gestor(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
                case "RECRUTADOR":
                case "FUNCIONÁRIO":
                    return new Funcionario(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status, departamento, cargoOuPerfil, regraId);
                default:
                    return new Funcionario(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status, departamento, cargoOuPerfil, regraId);
            }
        } catch (Exception e) {
            System.err.println("Erro ao parsear linha de usuário/funcionário: " + linha);
            return null;
        }
    }

    private String usuarioParaLinha(Usuario usuario) {
        // Campos base
        String linhaBase = String.join(SEPARADOR,
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getLogin(),
                usuario.getSenha()
        );

        // Se for um Funcionario (ou subclasse), adiciona os campos extras
        if (usuario instanceof Funcionario) {
            Funcionario f = (Funcionario) usuario;
            String perfil;

            // Define o perfil baseado na classe
            if (f instanceof Administrador) {
                perfil = "ADMIN";
            } else if (f instanceof Gestor) {
                perfil = "GESTOR";
            } else {
                perfil = f.getCargo();
            }

            return String.join(SEPARADOR,
                    linhaBase,
                    f.getMatricula(),
                    f.getDataAdmissao().toString(),
                    f.getBaseSalario().toString(),
                    f.getStatus(),
                    f.getDepartamento(),
                    perfil, // Salva o perfil/cargo
                    String.valueOf(f.getRegraSalarialId())
            );
        }

        // Se for só Usuario, retorna a linha base
        return linhaBase;
    }

    private void salvarListaNoArquivo(List<Usuario> usuarios) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_USUARIOS, false))) {
            for (Usuario u : usuarios) {
                writer.write(usuarioParaLinha(u));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuário no arquivo: " + e.getMessage());
        }
    }

    public Usuario salvar(Usuario usuario) {
        List<Usuario> usuarios = buscarTodos();

        Optional<Usuario> existente = usuarios.stream()
                .filter(u -> u.getLogin().equals(usuario.getLogin()))
                .findFirst();

        if (existente.isPresent()) {
            int index = usuarios.indexOf(existente.get());
            usuarios.set(index, usuario);
        } else {
            usuarios.add(usuario);
        }

        salvarListaNoArquivo(usuarios);
        return usuario;
    }

    public List<Usuario> buscarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        if (!Files.exists(Paths.get(ARQUIVO_USUARIOS))) {
            return usuarios;
        }
        try {
            List<String> linhas = Files.readAllLines(Paths.get(ARQUIVO_USUARIOS));
            for (String linha : linhas) {
                Usuario u = linhaParaUsuario(linha);
                if (u != null) {
                    usuarios.add(u);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de usuários: " + e.getMessage());
        }
        return usuarios;
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return buscarTodos().stream()
                .filter(u -> u.getLogin() != null && u.getLogin().equals(login))
                .findFirst();
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        return buscarTodos().stream()
                .filter(u -> u.getCpf() != null && u.getCpf().equals(cpf))
                .findFirst();
    }

    public void excluirPorLogin(String login) {
        List<Usuario> usuarios = buscarTodos();

        List<Usuario> usuariosFiltrados = usuarios.stream()
                .filter(u -> u.getLogin() != null && !u.getLogin().equals(login))
                .collect(Collectors.toList());

        salvarListaNoArquivo(usuariosFiltrados);
    }
}