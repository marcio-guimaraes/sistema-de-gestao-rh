package com.example.sistemarh.candidatura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    @Autowired
    private CandidatoRepository candidatoRepository;


    public Candidato salvarCandidato(Candidato candidato, boolean isEditMode) {
        //trata o cpf tanto tirando tanto quanto . e - quanto fazendo a verificação de 11 digitos
        String cpf = candidato.getCpf();
        if (cpf != null) {
            cpf = cpf.replaceAll("[^0-9]", "");
        }
        if (cpf == null || cpf.trim().isEmpty() || cpf.length() != 11) {
            throw new RuntimeException("CPF inválido. Deve conter exatamente 11 dígitos.");
        }

        candidato.setCpf(cpf);
        //verifica se o cpf ja esta cadastrado no sistema
        Optional<Candidato> existente = candidatoRepository.buscarPorCpf(candidato.getCpf());
        if (existente.isPresent() && !isEditMode) {
            throw new RuntimeException("CPF já cadastrado.");
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