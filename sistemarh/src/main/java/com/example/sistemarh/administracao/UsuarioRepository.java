package com.example.sistemarh.administracao;

import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UsuarioRepository {

    private static final String ARQUIVO_USUARIOS = "usuarios.txt";
    private static final String SEPARADOR = ";";

    public UsuarioRepository() {
        try {
            if (!Files.exists(Paths.get(ARQUIVO_USUARIOS))) {
                Files.createFile(Paths.get(ARQUIVO_USUARIOS));

                Administrador admin = new Administrador(
                        "Administrador",
                        "00000000000",
                        "admin",
                        "admin123",
                        "ADM001",
                        java.time.LocalDate.now(),
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
        if (dados.length < 9) return null;

        String nome = dados[0];
        String cpf = dados[1];
        String login = dados[2];
        String senha = dados[3];
        String matricula = dados[4];
        java.time.LocalDate dataAdmissao = java.time.LocalDate.parse(dados[5]);
        Double baseSalario = Double.parseDouble(dados[6]);
        String status = dados[7];
        String perfil = dados[8];

        // CORREÇÃO AQUI (if/else if)
        if ("ADMIN".equals(perfil)) {
            return new Administrador(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
        } else if ("GESTOR".equals(perfil)) {
            return new Gestor(nome, cpf, login, senha, matricula, dataAdmissao, baseSalario, status);
        } else {
            return new Usuario(nome, cpf, login, senha);
        }
    }

    private String usuarioParaLinha(Usuario usuario) {
        String perfil = "USUARIO";
        String matricula = "N/A";
        java.time.LocalDate dataAdmissao = java.time.LocalDate.now();
        Double baseSalario = 0.0;
        String status = "Ativo";

        if (usuario instanceof Administrador) {
            perfil = "ADMIN";
            Administrador admin = (Administrador) usuario;
            matricula = admin.getMatricula();
            dataAdmissao = admin.getDataAdmissao();
            baseSalario = admin.getBaseSalario();
            status = admin.getStatus();
        } else if (usuario instanceof Gestor) {
            perfil = "GESTOR";
            Gestor gestor = (Gestor) usuario;
            matricula = gestor.getMatricula();
            dataAdmissao = gestor.getDataAdmissao();
            baseSalario = gestor.getBaseSalario();
            status = gestor.getStatus();
        }

        return String.join(SEPARADOR,
                usuario.getNome(),
                usuario.getCpf(),
                usuario.login,
                usuario.senha,
                matricula,
                dataAdmissao.toString(),
                baseSalario.toString(),
                status,
                perfil
        );
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
                .filter(u -> u.login.equals(usuario.login))
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
                .filter(u -> u.login != null && u.login.equals(login))
                .findFirst();
    }

    // NOVO MÉTODO ADICIONADO
    public Optional<Usuario> buscarPorCpf(String cpf) {
        return buscarTodos().stream()
                .filter(u -> u.getCpf() != null && u.getCpf().equals(cpf))
                .findFirst();
    }

    public void excluirPorLogin(String login) {
        List<Usuario> usuarios = buscarTodos();

        List<Usuario> usuariosFiltrados = usuarios.stream()
                .filter(u -> u.login != null && !u.login.equals(login))
                .collect(Collectors.toList());

        salvarListaNoArquivo(usuariosFiltrados);
    }
}