package com.example.sistemarh.candidatura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    @Autowired
    private CandidatoRepository candidatoRepository;

    public Candidato salvarCandidato(Candidato candidato) {
        Optional<Candidato> existente = candidatoRepository.buscarPorCpf(candidato.getCpf());
        if (existente.isPresent() && !existente.get().getNome().equals(candidato.getNome())) {
            throw new RuntimeException("CPF já cadastrado para outro candidato.");
        }
        return candidatoRepository.salvar(candidato);
    }

    public List<Candidato> listarTodos() {
        return candidatoRepository.buscarTodos();
    }

    public Optional<Candidato> buscarPorCpf(String cpf) {
        return candidatoRepository.buscarPorCpf(cpf);
    }

    public void excluirCandidato(String cpf) {
        candidatoRepository.excluirPorCpf(cpf);
    }
}