package com.example.sistemarh.candidatura.service;

import com.example.sistemarh.candidatura.model.Candidato;
import com.example.sistemarh.candidatura.model.Candidatura;
import com.example.sistemarh.candidatura.repository.CandidatoRepository;
import com.example.sistemarh.candidatura.repository.CandidaturaRepository;
import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final CandidatoRepository candidatoRepository;
    private final VagaRepository vagaRepository;

    public CandidaturaService(CandidaturaRepository candidaturaRepository,
                              CandidatoRepository candidatoRepository,
                              VagaRepository vagaRepository) {
        this.candidaturaRepository = candidaturaRepository;
        this.candidatoRepository = candidatoRepository;
        this.vagaRepository = vagaRepository;
    }

    public Candidatura registrarCandidatura(String cpfCandidato, long idVaga) {
        Optional<Candidato> optCandidato = candidatoRepository.buscarPorCpf(cpfCandidato);
        Optional<Vaga> optVaga = vagaRepository.buscarPorId(idVaga);

        if (optCandidato.isEmpty()) {
            throw new IllegalArgumentException("Erro: Candidato com CPF " + cpfCandidato + " não encontrado.");
        }
        if (optVaga.isEmpty()) {
            throw new IllegalArgumentException("Erro: Vaga com ID " + idVaga + " não encontrada.");
        }

        Candidatura novaCandidatura = new Candidatura.Builder(0, optCandidato.get(), optVaga.get())
                .status("Pendente")
                .dataCandidatura(LocalDate.now())
                .build();

        return candidaturaRepository.salvar(novaCandidatura);
    }

    public void excluirCandidatura(long id) throws ExclusaoInvalidaException {
        Optional<Candidatura> optCandidatura = candidaturaRepository.buscarPorId(id);

        if (optCandidatura.isEmpty()) {
            throw new IllegalArgumentException("Erro: Candidatura com ID " + id + " não encontrada.");
        }

        Candidatura candidatura = optCandidatura.get();

        if (!candidatura.getStatus().equalsIgnoreCase("Pendente")) {
            throw new ExclusaoInvalidaException("Erro: Somente candidaturas com status 'Pendente' podem ser excluídas.");
        }

        candidaturaRepository.excluirPorId(id);
    }

    public Optional<Candidatura> atualizarStatus(long id, String novoStatus) {
        Optional<Candidatura> optCandidatura = candidaturaRepository.buscarPorId(id);

        if (optCandidatura.isEmpty()) {
            return Optional.empty();
        }

        Candidatura candidatura = optCandidatura.get();
        candidatura.setStatus(novoStatus);
        candidaturaRepository.salvar(candidatura);

        return Optional.of(candidatura);
    }

    public List<Candidatura> listarTodasCandidaturas() {
        return candidaturaRepository.buscarTodos();
    }

    public Optional<Candidatura> buscarCandidaturaPorId(long id) {
        return candidaturaRepository.buscarPorId(id);
    }
}
