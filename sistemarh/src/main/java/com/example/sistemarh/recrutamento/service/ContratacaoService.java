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

    // Repositório responsável por salvar e buscar contratações
    @Autowired
    private ContratacaoRepository contratacaoRepository;

    // Serviços auxiliares usados para acessar dados relacionados
    @Autowired
    private CandidaturaService candidaturaService;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private EntrevistaService entrevistaService;

    /**
     * Preenche os dados relacionados a uma contratação (vaga e candidato)
     * reutilizando a lógica do CandidaturaService.
     */
    public void buscarEPreencher(Contratacao contratacao) {
        if (contratacao == null) {
            return;
        }
        candidaturaService.buscarEPreencher(contratacao);
    }

    /**
     * Cria uma nova solicitação de contratação a partir de uma candidatura aprovada.
     *
     * @param candidaturaId ID da candidatura aprovada
     * @return Contratação criada e salva
     */
    public Contratacao solicitarContratacao(long candidaturaId) {
        // Busca a candidatura informada
        Candidatura candidatura = candidaturaService.buscarPorId(candidaturaId)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

        // Apenas candidaturas com status "Aprovado" podem gerar uma contratação
        if (!"Aprovado".equalsIgnoreCase(candidatura.getStatus())) {
            throw new RuntimeException("Somente candidaturas Aprovadas podem ser solicitadas.");
        }

        // Preenche os dados faltantes da candidatura
        candidaturaService.buscarEPreencher(candidatura);

        // Cria a nova contratação com status inicial "Pendente de aprovação do Gestor"
        Contratacao contratacao = new Contratacao.Builder(candidatura.getVaga(), candidatura.getCandidato())
                .status("Pendente de aprovação do Gestor")
                .build();

        // Atualiza o status da candidatura original
        candidaturaService.atualizarStatus(candidaturaId, "Pendente de Aprovação");

        // Persiste e retorna a contratação
        return contratacaoRepository.salvar(contratacao);
    }

    /**
     * Lista todas as contratações registradas, preenchendo os vínculos
     * com vaga e candidato correspondentes.
     */
    public List<Contratacao> listarTodas() {
        List<Contratacao> contratacoes = contratacaoRepository.buscarTodas();
        for (Contratacao c : contratacoes) {
            this.buscarEPreencher(c);
        }
        return contratacoes;
    }

    /**
     * Busca uma contratação pelo ID, preenchendo as informações associadas.
     */
    public Optional<Contratacao> buscarPorId(long id) {
        Optional<Contratacao> cOpt = contratacaoRepository.buscarPorId(id);
        if (cOpt.isPresent()) {
            this.buscarEPreencher(cOpt.get());
        }
        return cOpt;
    }

    /**
     * Aprova uma contratação — muda o status e registra a data da aprovação.
     * <p>
     * Também atualiza a candidatura correspondente para "Aprovado pelo Gestor".
     */
    public Contratacao aprovarContratacao(long id) {
        // Busca a contratação a ser aprovada
        Contratacao c = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        // Atualiza status e data de aprovação
        c.setStatus("Aprovada pelo Gestor");
        c.setDataAprovacaoGestor(LocalDate.now());

        // Persiste a alteração
        Contratacao contratacaoSalva = contratacaoRepository.salvar(c);

        // Tenta sincronizar o status da candidatura correspondente
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
            // Caso a atualização da candidatura falhe, apenas registra aviso
            System.err.println("Aviso: Contratação " + c.getId() +
                    " aprovada, mas falha ao atualizar status da candidatura original: " + e.getMessage());
        }

        return contratacaoSalva;
    }

    /**
     * Rejeita uma contratação — atualiza o status e sincroniza a candidatura associada.
     */
    public Contratacao rejeitarContratacao(long id) {
        // Busca a contratação a ser rejeitada
        Contratacao c = buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contratação não encontrada."));

        // Atualiza status
        c.setStatus("Rejeitada pelo Gestor");

        // Persiste a alteração
        Contratacao contratacaoSalva = contratacaoRepository.salvar(c);

        // Atualiza o status da candidatura correspondente
        try {
            candidaturaService.listarTodas().stream()
                    .filter(cand -> cand.getCpfCandidatoDoArquivo().equals(c.getCpfCandidatoDoArquivo()) &&
                            cand.getIdVagaDoArquivo() == c.getIdVagaDoArquivo())
                    .findFirst()
                    .ifPresent(candidaturaOriginal -> {
                        // Marca a candidatura como rejeitada
                        candidaturaService.atualizarStatus(candidaturaOriginal.getId(), "Rejeitado pelo Gestor");
                    });
        } catch (Exception e) {
            System.err.println("Aviso: Contratação " + c.getId() +
                    " rejeitada, mas falha ao atualizar status da candidatura original: " + e.getMessage());
        }

        return contratacaoSalva;
    }

    /**
     * Persiste uma contratação genérica no repositório.
     */
    public Contratacao salvar(Contratacao contratacao) {
        return contratacaoRepository.salvar(contratacao);
    }
}
