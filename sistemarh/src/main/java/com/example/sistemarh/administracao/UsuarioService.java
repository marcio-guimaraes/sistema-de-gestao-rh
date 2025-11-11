package com.example.sistemarh.administracao;

import com.example.sistemarh.candidatura.Candidato;
import com.example.sistemarh.financeiro.model.Funcionario;
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

    public Usuario salvarDto(UsuarioDTO dto) {
        // Pega a senha: ou a nova (se digitada) ou a antiga (se editando)
        String senha = dto.getSenha();
        if (senha == null || senha.isEmpty()) {
            // Se a senha veio vazia (em modo de edição), busca a original
            usuarioRepository.buscarPorCpf(dto.getCpf())
                    .ifPresent(user -> dto.setSenha(user.getSenha()));
        }

        Usuario usuarioParaSalvar;
        String perfil = dto.getPerfil();

        // Cria a Entidade correta com base no Perfil
        // Esta lógica usa TODOS os campos do DTO
        switch (perfil) {
            case "Administrador":
                usuarioParaSalvar = new Administrador(dto.getNome(), dto.getCpf(), dto.getLogin(), dto.getSenha(), dto.getMatricula(), dto.getDataAdmissao(), dto.getBaseSalario(), dto.getStatus());
                break;
            case "Gestor":
                usuarioParaSalvar = new Gestor(dto.getNome(), dto.getCpf(), dto.getLogin(), dto.getSenha(), dto.getMatricula(), dto.getDataAdmissao(), dto.getBaseSalario(), dto.getStatus());
                break;
            default: // Recrutador, Funcionário, etc.
                usuarioParaSalvar = new Funcionario(dto.getNome(), dto.getCpf(), dto.getLogin(), dto.getSenha(), dto.getMatricula(), dto.getDataAdmissao(), dto.getBaseSalario(), dto.getStatus(), dto.getDepartamento(), perfil, 1L /* regraId default */);
        }

        // O seu 'salvar' do Repository já lida com criar OU atualizar
        return usuarioRepository.salvar(usuarioParaSalvar);
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

    public void excluirUsuario(String cpf) {
        usuarioRepository.excluirPorCpf(cpf);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.salvar(usuario);
    }
}