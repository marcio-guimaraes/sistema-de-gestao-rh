package com.example.sistemarh.financeiro.service;

import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioService;
import com.example.sistemarh.candidatura.Candidato;
import com.example.sistemarh.financeiro.model.Funcionario;
import com.example.sistemarh.financeiro.repository.FuncionarioRepository;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.service.ContratacaoService;

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

    public Funcionario admitirFuncionario(long contratacaoId, double salarioBase, String cargo, String departamento, long regraSalarialId) {

        Contratacao contratacao = contratacaoService.buscarPorId(contratacaoId)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        if (!"Aprovada pelo Gestor".equalsIgnoreCase(contratacao.getStatus())) {
            throw new RuntimeException("Contratação não foi aprovada pelo gestor.");
        }

        // Garante que os dados de candidato e vaga estão carregados
        if (contratacao.getCandidato() == null || contratacao.getVaga() == null) {
            contratacaoService.buscarEPreencher(contratacao);
        }

        Candidato candidato = contratacao.getCandidato();
        Vaga vaga = contratacao.getVaga();

        if (candidato == null) {
            throw new RuntimeException("Falha ao carregar dados do candidato para a contratação.");
        }

        Optional<Usuario> existente = usuarioService.buscarPorCpf(candidato.getCpf());
        if (existente.isPresent() && existente.get() instanceof Funcionario) {
            throw new RuntimeException("Funcionário já cadastrado com este CPF.");
        }

        Usuario usuario = usuarioService.buscarPorCpf(candidato.getCpf())
                .orElseGet(() -> usuarioService.criarUsuarioCandidato(candidato));

        String matricula = "FUNC" + (funcionarioRepository.buscarTodos().size() + 100);

        Funcionario novoFuncionario = new Funcionario(
                candidato.getNome(),
                candidato.getCpf(),
                usuario.getLogin(),
                usuario.getSenha(),
                matricula,
                LocalDate.now(),
                salarioBase,
                "Ativo",
                departamento,
                cargo,
                regraSalarialId
        );

        contratacao.setStatus("Efetivada");
        contratacao.setDataEfetivacao(LocalDate.now());
        contratacaoService.salvar(contratacao);

        return (Funcionario) usuarioService.salvar(novoFuncionario);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.buscarTodos();
    }

    public List<Funcionario> listarAtivos() {
        return usuarioService.listarTodos().stream()
                .filter(u -> u instanceof Funcionario)
                .map(u -> (Funcionario) u)
                .collect(Collectors.toList());
    }

    public Optional<Funcionario> buscarPorCpf(String cpf) {
        return usuarioService.buscarPorCpf(cpf)
                .filter(u -> u instanceof Funcionario)
                .map(u -> (Funcionario) u);
    }
}