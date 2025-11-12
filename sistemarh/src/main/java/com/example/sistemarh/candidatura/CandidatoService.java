package com.example.sistemarh.candidatura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    @Autowired
    private CandidatoRepository candidatoRepository;

    // A injeção do CandidaturaRepository foi removida daqui

    /**
     * Valida o CPF (11 dígitos, duplicidade) e salva o candidato.
     */
    public Candidato salvarCandidato(Candidato candidato, boolean isEditMode) {

        String cpf = candidato.getCpf();

        if (cpf != null) {
            cpf = cpf.replaceAll("[^0-9]", "");
        }

        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 11) {
            throw new RuntimeException("CPF inválido. Deve conter exatamente 11 dígitos.");
        }

        candidato.setCpf(cpf);

        Optional<Candidato> existente = candidatoRepository.buscarPorCpf(candidato.getCpf());

        if (existente.isPresent() && !isEditMode) {
            throw new RuntimeException("CPF já cadastrado.");
        }

        return candidatoRepository.salvar(candidato);
    }

    /**
     * REVERTIDO:
     * Este método agora simplesmente lista todos os candidatos, sem filtros.
     */
    public List<Candidato> listarTodos() {
        return candidatoRepository.buscarTodos();
    }

    // O método listarComFiltros(...) foi removido daqui.

    public Optional<Candidato> buscarPorCpf(String cpf) {
        return candidatoRepository.buscarPorCpf(cpf);
    }

    public void excluirCandidato(String cpf) {
        candidatoRepository.excluirPorCpf(cpf);
    }
}