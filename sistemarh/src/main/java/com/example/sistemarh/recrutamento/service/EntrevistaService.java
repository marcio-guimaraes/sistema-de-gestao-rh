package com.example.sistemarh.recrutamento.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioService;
import com.example.sistemarh.candidatura.CandidatoService;
import com.example.sistemarh.candidatura.Candidatura;
import com.example.sistemarh.candidatura.CandidaturaService;
import com.example.sistemarh.recrutamento.model.Entrevista;
import com.example.sistemarh.recrutamento.model.Recrutador;
import com.example.sistemarh.recrutamento.repository.EntrevistaRepository;

@Service
public class EntrevistaService {

    // Repositório responsável por persistir entrevistas
    @Autowired
    private EntrevistaRepository entrevistaRepository;

    // Serviços auxiliares usados para buscar dados relacionados à entrevista
    @Autowired
    private CandidaturaService candidaturaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CandidatoService candidatoService;

    @Autowired
    private VagaService vagaService;

    /**
     * Agenda uma nova entrevista para uma candidatura existente.
     *
     * @param candidaturaId  ID da candidatura associada
     * @param recrutadorLogin login do recrutador que realizará a entrevista
     * @param dataHora        data e hora da entrevista
     * @param local           local onde a entrevista ocorrerá
     * @return Entrevista criada e salva no repositório
     */
    public Entrevista agendarEntrevista(long candidaturaId, String recrutadorLogin, LocalDateTime dataHora, String local) {

        // Busca a candidatura associada
        Candidatura candidatura = candidaturaService.buscarPorId(candidaturaId)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

        // Preenche os dados faltantes da candidatura (como candidato e vaga)
        candidaturaService.buscarEPreencher(candidatura);

        // Busca o usuário do recrutador pelo login informado
        Usuario usuarioRecrutador = usuarioService.buscarPorLogin(recrutadorLogin)
                .orElseThrow(() -> new RuntimeException("Recrutador não encontrado."));

        // Cria o objeto Recrutador a partir dos dados do usuário
        Recrutador recrutador = new Recrutador.Builder(
                0,
                usuarioRecrutador.getNome(),
                usuarioRecrutador.getCpf()
        ).build();

        // Constrói a nova entrevista, associando recrutador, candidato e vaga
        Entrevista entrevista = new Entrevista.Builder(
                dataHora,
                recrutador,
                candidatura.getCandidato(),
                candidatura.getVaga()
        ).local(local).build();

        // Atualiza o status da candidatura para indicar que está em análise
        candidaturaService.atualizarStatus(candidaturaId, "Em Análise");

        // Persiste a entrevista e retorna o objeto salvo
        return entrevistaRepository.salvar(entrevista);
    }

    /**
     * Salva o resultado de uma entrevista (nota e feedback).
     *
     * Busca a entrevista mais recente de um candidato em determinada vaga
     * e registra o resultado da avaliação.
     */
    public Entrevista salvarResultadoEntrevista(String cpfCandidato, long idVaga, Double nota, String feedback) {
        // Busca todas as entrevistas e filtra pela combinação candidato + vaga
        Optional<Entrevista> entrevistaOpt = entrevistaRepository.buscarTodas().stream()
                .filter(e -> e.getCpfCandidatoDoArquivo().equals(cpfCandidato) && e.getIdVagaDoArquivo() == idVaga)
                .max(Comparator.comparing(Entrevista::getDataHora)); // Seleciona a mais recente

        if (!entrevistaOpt.isPresent()) {
            throw new RuntimeException("Nenhuma entrevista agendada encontrada para esta candidatura.");
        }

        // Atualiza os dados da entrevista existente
        Entrevista entrevista = entrevistaOpt.get();
        entrevista.setNota(nota);
        entrevista.setFeedback(feedback);

        // Salva novamente no repositório
        return entrevistaRepository.salvar(entrevista);
    }

    /**
     * Retorna todas as entrevistas salvas, preenchendo os objetos relacionados
     * (candidato, vaga e recrutador) com base nas informações dos arquivos.
     */
    public List<Entrevista> listarTodas() {
        List<Entrevista> entrevistas = entrevistaRepository.buscarTodas();
        for (Entrevista e : entrevistas) {
            // Busca e associa o candidato correspondente
            candidatoService.buscarPorCpf(e.getCpfCandidatoDoArquivo())
                    .ifPresent(e::setCandidato);

            // Busca e associa a vaga correspondente
            vagaService.buscarVagaPorId(e.getIdVagaDoArquivo())
                    .ifPresent(e::setVaga);

            // Busca e associa o recrutador com base no CPF
            usuarioService.buscarPorCpf(e.getCpfRecrutadorDoArquivo())
                    .ifPresent(u -> e.setRecrutador(new Recrutador.Builder(0, u.getNome(), u.getCpf()).build()));
        }
        return entrevistas;
    }

    /**
     * Verifica se já existe uma entrevista marcada para determinado candidato e vaga.
     *
     * @return true se existir entrevista para essa combinação, false caso contrário.
     */
    public boolean existeEntrevistaParaCandidatura(String cpfCandidato, long idVaga) {
        return entrevistaRepository.buscarTodas().stream()
                .anyMatch(e -> e.getCpfCandidatoDoArquivo().equals(cpfCandidato) && e.getIdVagaDoArquivo() == idVaga);
    }
}
