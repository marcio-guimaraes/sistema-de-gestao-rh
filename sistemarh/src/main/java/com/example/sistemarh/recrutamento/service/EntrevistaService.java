package com.example.sistemarh.recrutamento.service;

import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioService;
import com.example.sistemarh.candidatura.Candidato;
import com.example.sistemarh.candidatura.CandidatoService;
import com.example.sistemarh.candidatura.Candidatura;
import com.example.sistemarh.candidatura.CandidaturaService;
import com.example.sistemarh.recrutamento.model.Entrevista;
import com.example.sistemarh.recrutamento.model.Recrutador;
import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.repository.EntrevistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EntrevistaService {

    @Autowired
    private EntrevistaRepository entrevistaRepository;

    @Autowired
    private CandidaturaService candidaturaService;

    @Autowired
    private UsuarioService usuarioService;

    public Entrevista agendarEntrevista(long candidaturaId, String recrutadorLogin, LocalDateTime dataHora, String local) {

        Candidatura candidatura = candidaturaService.buscarPorId(candidaturaId)
                .orElseThrow(() -> new RuntimeException("Candidatura não encontrada."));

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
        return entrevistaRepository.buscarTodas();
    }
}