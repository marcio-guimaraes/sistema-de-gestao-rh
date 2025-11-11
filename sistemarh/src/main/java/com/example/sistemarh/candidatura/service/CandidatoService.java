package com.example.sistemarh.candidatura.service;

import com.example.sistemarh.candidatura.model.Candidato;
import com.example.sistemarh.candidatura.repository.CandidatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;

    public CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public Candidato criarCandidato(Candidato candidato) {
        Optional<Candidato> existente = candidatoRepository.buscarPorCpf(candidato.getCpf());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Erro: Já existe um candidato com o CPF: " + candidato.getCpf());
        }
        return candidatoRepository.salvar(candidato);
    }

    public Optional<Candidato> atualizarCandidato(String cpf, Candidato candidatoAtualizado) {
        Optional<Candidato> existente = candidatoRepository.buscarPorCpf(cpf);
        if (existente.isEmpty()) {
            return Optional.empty();
        }
        if (!cpf.equals(candidatoAtualizado.getCpf())) {
            throw new IllegalArgumentException("Erro: O CPF não pode ser alterado.");
        }
        candidatoRepository.salvar(candidatoAtualizado);
        return Optional.of(candidatoAtualizado);
    }

    public List<Candidato> listarTodosCandidatos() {
        return candidatoRepository.buscarTodos();
    }

    public Optional<Candidato> buscarCandidatoPorCpf(String cpf) {
        return candidatoRepository.buscarPorCpf(cpf);
    }

    public boolean excluirCandidato(String cpf) {
        return candidatoRepository.excluirPorCpf(cpf);
    }
}
