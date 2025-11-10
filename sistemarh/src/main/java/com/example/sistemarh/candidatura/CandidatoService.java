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
        // Validação removida para permitir edição (a lógica de CPF único é tratada no repo)
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