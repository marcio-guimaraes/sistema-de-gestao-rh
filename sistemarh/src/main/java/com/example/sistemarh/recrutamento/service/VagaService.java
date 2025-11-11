package com.example.sistemarh.recrutamento.service;

import com.example.sistemarh.recrutamento.model.Vaga;
import com.example.sistemarh.recrutamento.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VagaService {

    @Autowired
    private VagaRepository vagaRepository;

    public List<Vaga> listarTodasVagas() {
        return vagaRepository.buscarTodas();
    }

    public Optional<Vaga> buscarVagaPorId(long id) {
        return vagaRepository.buscarPorId(id);
    }

    public Vaga criarVaga(Vaga vaga) {
        Vaga vagaParaSalvar = new Vaga.Builder(0, vaga.getTitulo())
                .descricao(vaga.getDescricao())
                .salario(vaga.getSalarioMin(), vaga.getSalarioMax())
                .regime(vaga.getRegime())
                .status(vaga.getStatus())
                .departamento(vaga.getDepartamento())
                .requisitos(vaga.getRequisitos())
                .dataCriacao(vaga.getDataCriacao())
                .build();

        return vagaRepository.salvar(vagaParaSalvar);
    }

    public Optional<Vaga> atualizarVaga(long id, Vaga vagaAtualizada) {
        Optional<Vaga> vagaExistenteOpt = vagaRepository.buscarPorId(id);

        if (vagaExistenteOpt.isPresent()) {
            Vaga vagaExistente = vagaExistenteOpt.get();

            Vaga vagaParaSalvar = new Vaga.Builder(vagaExistente.getId(), vagaAtualizada.getTitulo())
                    .descricao(vagaAtualizada.getDescricao())
                    .salario(vagaAtualizada.getSalarioMin(), vagaAtualizada.getSalarioMax())
                    .regime(vagaAtualizada.getRegime())
                    .status(vagaAtualizada.getStatus())
                    .departamento(vagaAtualizada.getDepartamento())
                    .requisitos(vagaAtualizada.getRequisitos())
                    .dataCriacao(vagaExistente.getDataCriacao())
                    .build();

            return Optional.of(vagaRepository.salvar(vagaParaSalvar));
        } else {
            return Optional.empty();
        }
    }

    public boolean excluirVaga(long id) {
        return vagaRepository.excluirPorId(id);
    }

    public List<Vaga> filtrarVagas(String status, String departamento, Double salarioMin) {
        List<Vaga> todas = vagaRepository.buscarTodas();

        return todas.stream()
                .filter(v -> status == null || status.isEmpty() ||
                        (v.getStatus() != null && v.getStatus().equalsIgnoreCase(status)))
                .filter(v -> departamento == null || departamento.isEmpty() ||
                        (v.getDepartamento() != null && v.getDepartamento().equalsIgnoreCase(departamento)))
                .filter(v -> salarioMin == null || v.getSalarioMin() >= salarioMin)
                .collect(Collectors.toList());
    }
}