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

        if (contratacao.getCandidato() == null || contratacao.getVaga() == null) {
            contratacaoService.buscarEPreencher(contratacao);
        }

        Candidato candidato = contratacao.getCandidato();
        if (candidato == null) {
            throw new RuntimeException("Falha ao carregar dados do candidato para a contratação.");
        }

        Optional<Funcionario> existenteFunc = funcionarioRepository.buscarPorCpf(candidato.getCpf());
        if (existenteFunc.isPresent()) {
            throw new RuntimeException("Funcionário já cadastrado com este CPF no funcionarios.txt.");
        }

        Optional<Usuario> existenteUser = usuarioService.buscarPorCpf(candidato.getCpf());
        Usuario usuario = existenteUser.orElseGet(() -> usuarioService.criarUsuarioCandidato(candidato));

        String matricula = "FUNC" + (this.listarTodos().size() + 100);

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
        usuarioService.salvar(novoFuncionario);
        return funcionarioRepository.salvar(novoFuncionario);
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.buscarTodos();
    }

    public List<Funcionario> listarAtivos() {
        return this.listarTodos().stream()
                .filter(f -> "Ativo".equalsIgnoreCase(f.getStatus()))
                .collect(Collectors.toList());
    }

    public Optional<Funcionario> buscarPorCpf(String cpf) {
        return funcionarioRepository.buscarPorCpf(cpf);
    }

    public Funcionario atualizarFuncionario(String cpf, String cargo, String departamento, double salarioBase, long regraSalarialId) {

        Funcionario funcionario = funcionarioRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado no repositorio de funcionários."));

        funcionario.setCargo(cargo);
        funcionario.setDepartamento(departamento);
        funcionario.setBaseSalario(salarioBase);
        funcionario.setRegraSalarialId(regraSalarialId);

        funcionarioRepository.salvar(funcionario);

        Usuario usuario = usuarioService.buscarPorCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado no repositorio de usuários."));

        if (usuario instanceof Funcionario) {
            Funcionario userFunc = (Funcionario) usuario;
            userFunc.setCargo(cargo);
            userFunc.setDepartamento(departamento);
            userFunc.setBaseSalario(salarioBase);
            userFunc.setRegraSalarialId(regraSalarialId);

            usuarioService.salvar(userFunc);
        } else {
            throw new RuntimeException("O CPF " + cpf + " pertence a um usuário, mas não a um funcionário no arquivo de usuários.");
        }

        return funcionario;
    }
}