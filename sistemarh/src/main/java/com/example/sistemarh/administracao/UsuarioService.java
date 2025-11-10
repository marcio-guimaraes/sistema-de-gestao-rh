package com.example.sistemarh.administracao;

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
            return usuario.senha.equals(senha);
        }
        return false;
    }

    public Usuario criarUsuario(UsuarioDTO usuarioDTO) {

        Optional<Usuario> existente = usuarioRepository.buscarPorLogin(usuarioDTO.getLogin());
        if (existente.isPresent()) {
            throw new RuntimeException("Login já cadastrado.");
        }

        String matricula = "M" + (usuarioRepository.buscarTodos().size() + 1);
        LocalDate dataAdmissao = LocalDate.now();

        Usuario novoUsuario;

        switch (usuarioDTO.getPerfil().toUpperCase()) {
            case "ADMINISTRADOR":
                novoUsuario = new Administrador(
                        usuarioDTO.getNome(),
                        usuarioDTO.getCpf(),
                        usuarioDTO.getLogin(),
                        usuarioDTO.getSenha(),
                        matricula,
                        dataAdmissao,
                        0.0,
                        "Ativo"
                );
                break;

            default:
                novoUsuario = new Usuario(
                        usuarioDTO.getNome(),
                        usuarioDTO.getCpf(),
                        usuarioDTO.getLogin(),
                        usuarioDTO.getSenha()
                );
        }

        return usuarioRepository.salvar(novoUsuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.buscarTodos();
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.buscarPorLogin(login);
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

        Usuario usuarioAtualizado = new Usuario(
                usuarioDTO.getNome(),
                usuarioDTO.getCpf(),
                usuarioDTO.getLogin(),
                usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty() ? usuarioDTO.getSenha() : usuario.senha
        );

        if (!loginOriginal.equals(usuarioDTO.getLogin())) {
            usuarioRepository.excluirPorLogin(loginOriginal);
        }

        usuarioRepository.salvar(usuarioAtualizado);
    }
}