package com.example.sistemarh.financeiro;

// IMPORTS ADICIONADOS PARA CORRIGIR 'Cannot resolve symbol'
import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioService;
import com.example.sistemarh.candidatura.Candidato;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.service.ContratacaoService;
// FIM DOS IMPORTS ADICIONADOS

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ContratacaoService contratacaoService;

    @Autowired
    private UsuarioService usuarioService;

    public Funcionario admitirFuncionario(long contratacaoId, double salarioBase, String cargo, String departamento) {

        Contratacao contratacao = contratacaoService.buscarPorId(contratacaoId)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        if (!"Aprovada pelo Gestor".equalsIgnoreCase(contratacao.getStatus())) {
            throw new RuntimeException("Contratação não foi aprovada pelo gestor.");
        }

        Candidato candidato = contratacao.getCandidato();
        Vaga vaga = contratacao.getVaga();

        Optional<Funcionario> existente = funcionarioRepository.buscarPorCpf(candidato.getCpf());
        if (existente.isPresent()) {
            throw new RuntimeException("Funcionário já cadastrado com este CPF.");
        }

        Usuario usuario = usuarioService.buscarPorLogin(candidato.getCpf())
                .orElse(new Usuario(candidato.getNome(), candidato.getCpf(), candidato.getCpf(), "senhaPadrao123"));

        String matricula = "FUNC" + (funcionarioRepository.buscarTodos().size() + 100);

        Funcionario novoFuncionario = new Funcionario(
                candidato.getNome(),
                candidato.getCpf(),
                usuario.getLogin(), // Correção de acesso
                usuario.getSenha(), // Correção de acesso
                matricula,
                LocalDate.now(),
                salarioBase,
                "Ativo",
                departamento,
                cargo
        );

        contratacao.setStatus("Efetivada");
        contratacao.setDataEfetivacao(LocalDate.now());
        contratacaoService.salvar(contratacao); // Esta linha agora vai funcionar

        return funcionarioRepository.salvar(novoFuncionario);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.buscarTodos();
    }

    public List<Funcionario> listarAtivos() {
        return funcionarioRepository.buscarTodos().stream()
                .filter(f -> "Ativo".equalsIgnoreCase(f.getStatus()))
                .collect(Collectors.toList());
    }

    public Optional<Funcionario> buscarPorCpf(String cpf) {
        return funcionarioRepository.buscarPorCpf(cpf);
    }
}