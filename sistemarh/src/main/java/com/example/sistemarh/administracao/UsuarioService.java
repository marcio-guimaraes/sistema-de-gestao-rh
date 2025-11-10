package com.example.sistemarh.administracao;

import com.example.sistemarh.candidatura.Candidato;
import com.example.sistemarh.financeiro.Funcionario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public boolean validarLogin(String login, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorLogin(login);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Validação segura (evita null)
            return usuario.getSenha() != null && usuario.getSenha().equals(senha);
        }
        return false;
    }

    public Usuario criarUsuario(UsuarioDTO usuarioDTO) {

        Optional<Usuario> existenteLogin = usuarioRepository.buscarPorLogin(usuarioDTO.getLogin());
        if (existenteLogin.isPresent()) {
            throw new RuntimeException("Login já cadastrado.");
        }

        Optional<Usuario> existenteCpf = usuarioRepository.buscarPorCpf(usuarioDTO.getCpf());
        if (existenteCpf.isPresent()) {
            throw new RuntimeException("CPF já cadastrado.");
        }

        String matricula = "M" + (usuarioRepository.buscarTodos().size() + 1);
        LocalDate dataAdmissao = LocalDate.now();

        Usuario novoUsuario;
        String perfil = usuarioDTO.getPerfil().toUpperCase();

        switch (perfil) {
            case "ADMINISTRADOR":
                novoUsuario = new Administrador(
                        usuarioDTO.getNome(), usuarioDTO.getCpf(), usuarioDTO.getLogin(),
                        usuarioDTO.getSenha(), matricula, dataAdmissao, 0.0, "Ativo"
                );
                break;

            case "GESTOR":
                novoUsuario = new Gestor(
                        usuarioDTO.getNome(), usuarioDTO.getCpf(), usuarioDTO.getLogin(),
                        usuarioDTO.getSenha(), matricula, dataAdmissao, 0.0, "Ativo"
                );
                break;

            case "RECRUTADOR":
            case "FUNCIONÁRIO":
                novoUsuario = new Funcionario(
                        usuarioDTO.getNome(), usuarioDTO.getCpf(), usuarioDTO.getLogin(),
                        usuarioDTO.getSenha(), matricula, dataAdmissao, 0.0, "Ativo",
                        usuarioDTO.getDepartamento(), perfil // Salva "RECRUTADOR" ou "FUNCIONÁRIO" como cargo
                );
                break;

            default: // Usuário simples
                novoUsuario = new Usuario(
                        usuarioDTO.getNome(), usuarioDTO.getCpf(),
                        usuarioDTO.getLogin(), usuarioDTO.getSenha()
                );
        }

        return usuarioRepository.salvar(novoUsuario);
    }

    public Usuario criarUsuarioCandidato(Candidato candidato) {
        Optional<Usuario> existente = usuarioRepository.buscarPorCpf(candidato.getCpf());
        if (existente.isPresent()) {
            return existente.get();
        }

        Usuario novoUsuario = new Usuario(
                candidato.getNome(),
                candidato.getCpf(),
                candidato.getCpf(), // Login é o CPF
                "senhaPadrao123" // Senha padrão
        );
        return usuarioRepository.salvar(novoUsuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.buscarTodos();
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.buscarPorLogin(login);
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarioRepository.buscarPorCpf(cpf);
    }

    public void excluirUsuario(String login) {
        usuarioRepository.excluirPorLogin(login);
    }

    public void atualizarUsuario(String loginOriginal, UsuarioDTO usuarioDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorLogin(loginOriginal);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado para atualizar.");
        }

        Usuario usuario = usuarioOpt.get();

        // Atualiza dados básicos
        usuario.setNome(usuarioDTO.getNome());
        usuario.setCpf(usuarioDTO.getCpf());

        // Só atualiza a senha se uma nova foi fornecida
        if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
            usuario.setSenha(usuarioDTO.getSenha());
        }

        // Se for um funcionário, atualiza os campos de funcionário
        if (usuario instanceof Funcionario) {
            Funcionario f = (Funcionario) usuario;
            f.setDepartamento(usuarioDTO.getDepartamento());

            // Lógica para atualizar o "Cargo" (Perfil)
            String perfil = usuarioDTO.getPerfil().toUpperCase();
            if (perfil.equals("RECRUTADOR") || perfil.equals("FUNCIONÁRIO")) {
                f.setCargo(perfil);
            }
            // OBS: Não estamos tratando a mudança de perfil de/para Admin/Gestor aqui
        }

        // Se o login mudou, precisa remover o antigo e salvar o novo
        if (!loginOriginal.equals(usuarioDTO.getLogin())) {
            // Verifica se o novo login já existe
            Optional<Usuario> existenteLogin = usuarioRepository.buscarPorLogin(usuarioDTO.getLogin());
            if (existenteLogin.isPresent()) {
                throw new RuntimeException("O novo login já está em uso.");
            }

            usuario.setLogin(usuarioDTO.getLogin());
            usuarioRepository.excluirPorLogin(loginOriginal);
        }

        usuarioRepository.salvar(usuario);
    }
}