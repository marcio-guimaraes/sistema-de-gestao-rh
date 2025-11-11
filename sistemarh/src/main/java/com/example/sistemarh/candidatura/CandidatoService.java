package com.example.sistemarh.candidatura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    @Autowired
    private CandidatoRepository candidatoRepository;

    /**
     * ATUALIZADO:
     * 1. Adiciona validação de 11 dígitos para o CPF.
     * 2. Limpa o CPF (remove pontos/traços) antes de salvar.
     */
    public Candidato salvarCandidato(Candidato candidato, boolean isEditMode) {

        // --- INÍCIO DA NOVA VALIDAÇÃO DE CPF ---
        String cpf = candidato.getCpf();

        // 1. Limpa o CPF de caracteres não numéricos (pontos, traços, etc.)
        if (cpf != null) {
            cpf = cpf.replaceAll("[^0-9]", "");
        }

        // 2. Verifica se o CPF limpo tem exatamente 11 dígitos
        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 11) {
            throw new RuntimeException("CPF inválido. Deve conter exatamente 11 dígitos.");
        }

        // 3. Atualiza o objeto candidato com o CPF limpo
        candidato.setCpf(cpf);
        // --- FIM DA NOVA VALIDAÇÃO ---

        Optional<Candidato> existente = candidatoRepository.buscarPorCpf(candidato.getCpf());

        // Validação de duplicidade (que fizemos antes)
        if (existente.isPresent() && !isEditMode) {
            throw new RuntimeException("CPF já cadastrado.");
        }

        // Salva (atualiza ou cria)
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