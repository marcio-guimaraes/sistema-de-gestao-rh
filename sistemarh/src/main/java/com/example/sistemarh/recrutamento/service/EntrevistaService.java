package com.example.sistemarh.recrutamento.service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private EntrevistaRepository entrevistaRepository;

    @Autowired
    private CandidaturaService candidaturaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CandidatoService candidatoService;

    @Autowired
    private VagaService vagaService;

    public Entrevista agendarEntrevista(long candidaturaId, String recrutadorLogin, LocalDateTime dataHora, String local) {

        Candidatura candidatura = candidaturaService.buscarPorId(candidaturaId)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

        candidaturaService.buscarEPreencher(candidatura);

        Usuario usuarioRecrutador = usuarioService.buscarPorLogin(recrutadorLogin)
                .orElseThrow(() -> new RuntimeException("Recrutador não encontrado."));

        Recrutador recrutador = new Recrutador.Builder(
                0,
                usuarioRecrutador.getNome(),
                usuarioRecrutador.getCpf()
        ).build();

        Entrevista entrevista = new Entrevista.Builder(
                dataHora,
                recrutador,
                candidatura.getCandidato(),
                candidatura.getVaga()
        ).local(local).build();

        candidaturaService.atualizarStatus(candidaturaId, "Em Análise");

        return entrevistaRepository.salvar(entrevista);
    }

    public List<Entrevista> listarTodas() {
        List<Entrevista> entrevistas = entrevistaRepository.buscarTodas();
        for (Entrevista e : entrevistas) {
            candidatoService.buscarPorCpf(e.getCpfCandidatoDoArquivo())
                    .ifPresent(e::setCandidato);
            vagaService.buscarVagaPorId(e.getIdVagaDoArquivo())
                    .ifPresent(e::setVaga);

            // CORREÇÃO AQUI
            usuarioService.buscarPorLogin(e.getCpfRecrutadorDoArquivo()) // Supondo que o login do recrutador seja o CPF
                    .ifPresent(u -> e.setRecrutador(new Recrutador.Builder(0, u.getNome(), u.getCpf()).build()));
        }
        return entrevistas;
    }
}
