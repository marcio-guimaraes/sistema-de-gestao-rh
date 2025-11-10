package com.example.sistemarh.recrutamento.service;

import com.example.sistemarh.candidatura.Candidatura;
import com.example.sistemarh.candidatura.CandidaturaService;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.repository.ContratacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ContratacaoService {

    @Autowired
    private ContratacaoRepository contratacaoRepository;

    @Autowired
    private CandidaturaService candidaturaService;

    public Contratacao solicitarContratacao(long candidaturaId) {
        Candidatura candidatura = candidaturaService.buscarPorId(candidaturaId)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

        if (!"Aprovado".equalsIgnoreCase(candidatura.getStatus())) {
            throw new RuntimeException("Somente candidaturas Aprovadas podem ser solicitadas.");
        }

        Contratacao contratacao = new Contratacao.Builder(candidatura.getVaga(), candidatura.getCandidato())
                .status("Pendente de aprovação do Gestor")
                .build();

        return contratacaoRepository.salvar(contratacao);
    }

    public List<Contratacao> listarTodas() {
        List<Contratacao> contratacoes = contratacaoRepository.buscarTodas();
        for (Contratacao c : contratacoes) {
            if (c.getCandidato() == null || c.getVaga() == null) {
                // Preenche os objetos Candidato e Vaga que vêm nulos do repositório
                candidaturaService.buscarEPreencher(c.getCandidaturaObj()); // Supondo que você tenha um getCandidatura
            }
        }
        return contratacoes;
    }

    public Optional<Contratacao> buscarPorId(long id) {
        // Busca a contratação e preenche os dados faltantes
        Optional<Contratacao> cOpt = contratacaoRepository.buscarPorId(id);
        if (cOpt.isPresent()) {
            Contratacao c = cOpt.get();
            if (c.getCandidato() == null || c.getVaga() == null) {
                candidaturaService.buscarEPreencher(c.getCandidaturaObj()); // Supondo que você tenha um getCandidatura
            }
        }
        return cOpt;
    }


    public Contratacao aprovarContratacao(long id) {
        Contratacao c = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        c.setStatus("Aprovada pelo Gestor");
        c.setDataAprovacaoGestor(LocalDate.now());

        return contratacaoRepository.salvar(c);
    }

    public Contratacao rejeitarContratacao(long id) {
        Contratacao c = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        c.setStatus("Rejeitada pelo Gestor");

        return contratacaoRepository.salvar(c);
    }

    // MÉTODO ADICIONADO QUE ESTAVA FALTANDO
    public Contratacao salvar(Contratacao contratacao) {
        return contratacaoRepository.salvar(contratacao);
    }
}