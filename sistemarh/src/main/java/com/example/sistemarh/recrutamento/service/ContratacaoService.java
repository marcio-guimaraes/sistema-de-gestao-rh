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

    // ADICIONADO (necessário para o buscarEPreencher)
    @Autowired
    private VagaService vagaService;

    // NOVO MÉTODO HELPER ADICIONADO
    public void buscarEPreencher(Contratacao contratacao) {
        if (contratacao == null) {
            return;
        }
        // Usa o serviço de candidatura para preencher os dados
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