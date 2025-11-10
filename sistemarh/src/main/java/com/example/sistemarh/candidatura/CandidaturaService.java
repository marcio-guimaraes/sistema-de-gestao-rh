package com.example.sistemarh.candidatura;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.service.VagaService;

@Service
public class CandidaturaService {

    @Autowired
    private CandidaturaRepository candidaturaRepository;

    @Autowired
    private CandidatoService candidatoService;

    @Autowired
    private VagaService vagaService;

    public void buscarEPreencher(Candidatura candidatura) {
        if (candidatura == null) {
            return;
        }

        if (candidatura.getCandidato() == null) {
            candidatoService.buscarPorCpf(candidatura.getCpfCandidatoDoArquivo())
                    .ifPresent(candidatura::setCandidato);
        }
        if (candidatura.getVaga() == null) {
            vagaService.buscarVagaPorId(candidatura.getIdVagaDoArquivo())
                    .ifPresent(candidatura::setVaga);
        }
    }

    public Candidatura registrarCandidatura(String cpf, long vagaId, String status) {

        Optional<Candidato> candidatoOpt = candidatoService.buscarPorCpf(cpf);
        if (!candidatoOpt.isPresent()) {
            throw new RuntimeException("Candidato não encontrado.");
        }

        Optional<Vaga> vagaOpt = vagaService.buscarVagaPorId(vagaId);
        if (!vagaOpt.isPresent()) {
            throw new RuntimeException("Vaga não encontrada.");
        }

        boolean jaCandidatado = candidaturaRepository.buscarTodas().stream()
                .anyMatch(c -> c.getCpfCandidatoDoArquivo().equals(cpf) && c.getIdVagaDoArquivo() == vagaId);

        if (jaCandidatado) {
            throw new RuntimeException("Candidato já aplicado para esta vaga.");
        }

        long novoId = Candidatura.getProximoId();
        LocalDate dataHoje = LocalDate.now();

        Candidatura novaCandidatura = new Candidatura(novoId, cpf, vagaId, status, dataHoje);

        return candidaturaRepository.salvar(novaCandidatura);
    }

    public List<Candidatura> listarTodas() {
        List<Candidatura> candidaturas = candidaturaRepository.buscarTodas();
        for (Candidatura c : candidaturas) {
            buscarEPreencher(c);
        }
        return candidaturas;
    }

    public List<Candidatura> listarComFiltros(Long vagaId, String status) {
        List<Candidatura> candidaturas = listarTodas();
        return candidaturas.stream()
                .filter(c -> vagaId == null || c.getIdVagaDoArquivo() == vagaId)
                .filter(c -> status == null || status.isEmpty() || c.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public Optional<Candidatura> buscarPorId(long id) {
        Optional<Candidatura> candidaturaOpt = candidaturaRepository.buscarPorId(id);

        if (candidaturaOpt.isPresent()) {
            buscarEPreencher(candidaturaOpt.get());
        }
        return candidaturaOpt;
    }

    public Candidatura atualizarStatus(long id, String novoStatus) {
        Optional<Candidatura> candidaturaOpt = candidaturaRepository.buscarPorId(id);
        if (!candidaturaOpt.isPresent()) {
            throw new RuntimeException("Candidatura não encontrada.");
        }

        Candidatura candidatura = candidaturaOpt.get();
        candidatura.setStatus(novoStatus);

        return candidaturaRepository.salvar(candidatura);
    }

    public void excluirCandidatura(long id) {
        Candidatura candidatura = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

        if (!"Pendente".equalsIgnoreCase(candidatura.getStatus())) {
            throw new RuntimeException("Somente candidaturas com status 'Pendente' podem ser excluídas.");
        }

        candidaturaRepository.excluirPorId(id);
    }

    public void excluirCandidaturaSePendente(long id) {
        buscarPorId(id).ifPresent(candidatura -> {
            if ("Pendente".equalsIgnoreCase(candidatura.getStatus())) {
                candidaturaRepository.excluirPorId(id);
            }
        });
    }
}
