package com.example.sistemarh.recrutamento.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sistemarh.candidatura.Candidatura;
import com.example.sistemarh.candidatura.CandidaturaService;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.repository.ContratacaoRepository;

@Service
public class ContratacaoService {

    @Autowired
    private ContratacaoRepository contratacaoRepository;

    @Autowired
    private CandidaturaService candidaturaService;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private EntrevistaService entrevistaService;

    public void buscarEPreencher(Contratacao contratacao) {
        if (contratacao == null) {
            return;
        }
        candidaturaService.buscarEPreencher(contratacao);
    }

    public Contratacao solicitarContratacao(long candidaturaId) {
        Candidatura candidatura = candidaturaService.buscarPorId(candidaturaId)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

        if (!"Aprovado".equalsIgnoreCase(candidatura.getStatus())) {
            throw new RuntimeException("Somente candidaturas Aprovadas podem ser solicitadas.");
        }

        candidaturaService.buscarEPreencher(candidatura);


        Contratacao contratacao = new Contratacao.Builder(candidatura.getVaga(), candidatura.getCandidato())
                .status("Pendente de aprovação do Gestor")
                .build();

        candidaturaService.atualizarStatus(candidaturaId, "Pendente de Aprovação");

        return contratacaoRepository.salvar(contratacao);
    }

    public List<Contratacao> listarTodas() {
        List<Contratacao> contratacoes = contratacaoRepository.buscarTodas();
        for (Contratacao c : contratacoes) {
            this.buscarEPreencher(c);
        }
        return contratacoes;
    }

    public Optional<Contratacao> buscarPorId(long id) {
        Optional<Contratacao> cOpt = contratacaoRepository.buscarPorId(id);
        if (cOpt.isPresent()) {
            this.buscarEPreencher(cOpt.get());
        }
        return cOpt;
    }

    public Contratacao aprovarContratacao(long id) {
        Contratacao c = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        c.setStatus("Aprovada pelo Gestor");
        c.setDataAprovacaoGestor(LocalDate.now());

        Contratacao contratacaoSalva = contratacaoRepository.salvar(c);


        try {
            candidaturaService.listarTodas().stream()
                    .filter(cand -> cand.getCpfCandidatoDoArquivo().equals(c.getCpfCandidatoDoArquivo()) &&
                            cand.getIdVagaDoArquivo() == c.getIdVagaDoArquivo())
                    .findFirst()
                    .ifPresent(candidaturaOriginal -> {
                        // Atualiza o status da candidatura original
                        candidaturaService.atualizarStatus(candidaturaOriginal.getId(), "Aprovado pelo Gestor");
                    });
        } catch (Exception e) {
            System.err.println("Aviso: Contratação " + c.getId() + " aprovada, mas falha ao atualizar status da candidatura original: " + e.getMessage());
        }

        return contratacaoSalva;
    }


    public Contratacao rejeitarContratacao(long id) {
        Contratacao c = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        c.setStatus("Rejeitada pelo Gestor");

        Contratacao contratacaoSalva = contratacaoRepository.salvar(c);


        try {
            candidaturaService.listarTodas().stream()
                    .filter(cand -> cand.getCpfCandidatoDoArquivo().equals(c.getCpfCandidatoDoArquivo()) &&
                            cand.getIdVagaDoArquivo() == c.getIdVagaDoArquivo())
                    .findFirst()
                    .ifPresent(candidaturaOriginal -> {
                        // Atualiza o status da candidatura para "Rejeitado" (ou "Rejeitado pelo Gestor")
                        candidaturaService.atualizarStatus(candidaturaOriginal.getId(), "Rejeitado pelo Gestor");
                    });
        } catch (Exception e) {
            System.err.println("Aviso: Contratação " + c.getId() + " rejeitada, mas falha ao atualizar status da candidatura original: " + e.getMessage());
        }

        return contratacaoSalva;
    }

    public Contratacao salvar(Contratacao contratacao) {
        return contratacaoRepository.salvar(contratacao);
    }
}