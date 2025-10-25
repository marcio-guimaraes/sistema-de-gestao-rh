package com.example.sistemarh.recrutamento.controller;

import com.example.sistemarh.candidatura.model.Candidato;
import com.example.sistemarh.recrutamento.model.Contratacao;
import com.example.sistemarh.recrutamento.model.Entrevista;
import com.example.sistemarh.recrutamento.model.Recrutador;
import com.example.sistemarh.recrutamento.model.Vaga;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recrutamento")
public class RecrutamentoController {

    private Recrutador criarRecrutadorFicticio() {
        return new Recrutador.Builder(1L, "Márcio Guimarães", "111.222.333-44").build();
    }

    private List<Vaga> criarVagasFicticias() {
        List<Vaga> vagas = new ArrayList<>();
        vagas.add(new Vaga.Builder(1L, "Desenvolvedor Frontend Senior")
                .descricao("Desenvolver interfaces web responsivas e performáticas.")
                .salario(8000, 12000)
                .regime("CLT")
                .status("Aberta")
                .departamento("Tecnologia")
                .requisitos("React, Vue, TypeScript")
                .build());
        vagas.add(new Vaga.Builder(2L, "Designer UX/UI Pleno")
                .descricao("Criar protótipos e fluxos de usuário intuitivos.")
                .salario(6000, 9000)
                .regime("PJ")
                .status("Aberta")
                .departamento("Design")
                .build());
        vagas.add(new Vaga.Builder(3L, "Analista de Marketing Digital")
                .salario(5000, 7000)
                .regime("CLT")
                .status("Pausada")
                .departamento("Marketing")
                .build());
        vagas.add(new Vaga.Builder(4L, "Estágio em Desenvolvimento")
                .salario(1500, 1500)
                .regime("Estágio")
                .status("Fechada")
                .departamento("Tecnologia")
                .build());
        return vagas;
    }

    private List<Candidato> criarCandidatosFicticios() {
        List<Candidato> candidatos = new ArrayList<>();

        // try {
        //     candidatos.add(new Candidato.Builder(1L, "Ana Silva", "111.222.333-44")
        //         .formacao("Ciência da Computação")
        //         .experiencia("5 anos com Java e Spring")
        //         .pretensaoSalarial(9000.0)
        //         .build());
        //     candidatos.add(new Candidato.Builder(2L, "Carlos Santos", "222.333.444-55")
        //         .formacao("Design Gráfico")
        //         .experiencia("3 anos com Figma e Adobe XD")
        //         .pretensaoSalarial(7000.0)
        //         .build());
        //      candidatos.add(new Candidato.Builder(3L, "Beatriz Costa", "333.444.555-66")
        //         .formacao("Engenharia de Software")
        //          .experiencia("1 ano de estágio")
        //         .pretensaoSalarial(4000.0)
        //         .build());
        // } catch (IllegalArgumentException e) {
        //     System.err.println("Erro ao criar candidatos fictícios: " + e.getMessage());
        //     // Trate o erro como preferir
        // }

        return candidatos;
    }


    @GetMapping
    public String menuRecrutamento(Model model) {
        long totalVagasAbertas = criarVagasFicticias().stream()
                .filter(v -> "Aberta".equalsIgnoreCase(v.getStatus()))
                .count();
        model.addAttribute("totalVagasAbertas", totalVagasAbertas);
        return "recrutamento/menu";
    }

    @GetMapping("/vagas")
    public String listarVagas(Model model) {
        model.addAttribute("vagas", criarVagasFicticias());
        return "recrutamento/listar-vagas";
    }


    @GetMapping("/cadastrar-candidato")
    public String cadastrarCandidato(Model model) {
        return "recrutamento/cadastro-candidato";
    }

    @GetMapping("/realizar-candidatura")
    public String realizarCandidatura(Model model) {
        List<Vaga> vagasAbertas = criarVagasFicticias().stream()
                .filter(v -> "Aberta".equalsIgnoreCase(v.getStatus()))
                .toList();
        model.addAttribute("candidatos", criarCandidatosFicticios());
        model.addAttribute("vagas", vagasAbertas);
        return "recrutamento/realizar-candidatura";
    }

    @GetMapping("/marcar-entrevista")
    public String marcarEntrevista(Model model) {
        List<Map<String, Object>> candidaturasSimuladas = new ArrayList<>();
        List<Candidato> candidatos = criarCandidatosFicticios();
        List<Vaga> vagas = criarVagasFicticias();

        // if (!candidatos.isEmpty() && !vagas.isEmpty()) {
        //     candidaturasSimuladas.add(Map.of("candidato", candidatos.get(0), "vaga", vagas.get(0), "id", 1L)); // ID fictício da candidatura
        //     if (candidatos.size() > 1) {
        //        candidaturasSimuladas.add(Map.of("candidato", candidatos.get(1), "vaga", vagas.get(0), "id", 2L));
        //     }
        // }

        model.addAttribute("candidaturas", candidaturasSimuladas);
        model.addAttribute("recrutadores", List.of(criarRecrutadorFicticio()));
        return "recrutamento/marcar-entrevista";
    }

    @GetMapping("/solicitar-contratacao")
    public String solicitarContratacao(Model model) {
        List<Map<String, Object>> candidaturasAprovadas = new ArrayList<>();
        List<Candidato> candidatos = criarCandidatosFicticios();
        List<Vaga> vagas = criarVagasFicticias();

        // if (!candidatos.isEmpty() && !vagas.isEmpty()) {
        //      // Assumindo que o primeiro candidato foi aprovado para a primeira vaga
        //     candidaturasAprovadas.add(Map.of("candidato", candidatos.get(0), "vaga", vagas.get(0)));
        //
        //      // Assumindo que o terceiro candidato (se existir) foi aprovado para a segunda vaga
        //     if (candidatos.size() > 2 && vagas.size() > 1) {
        //         candidaturasAprovadas.add(Map.of("candidato", candidatos.get(2), "vaga", vagas.get(1)));
        //     }
        // }

        model.addAttribute("candidaturasAprovadas", candidaturasAprovadas);
        return "recrutamento/solicitar-contratacao";
    }

    @GetMapping("/consultar-contratacoes")
    public String consultarContratacoes(Model model) {
        List<Contratacao> contratacoes = new ArrayList<>();
        List<Candidato> candidatos = criarCandidatosFicticios();
        List<Vaga> vagas = criarVagasFicticias();

        // if (!candidatos.isEmpty() && !vagas.isEmpty()) {
        //     try {
        //         contratacoes.add(new Contratacao.Builder(vagas.get(0), candidatos.get(0))
        //                 .dataSolicitacao(LocalDate.of(2025, 9, 20))
        //                 .status("Pendente de aprovação do Gestor")
        //                 .build());
        //
        //          if (candidatos.size() > 1 && vagas.size() > 1) {
        //              contratacoes.add(new Contratacao.Builder(vagas.get(1), candidatos.get(1))
        //                      .dataSolicitacao(LocalDate.of(2025, 9, 15))
        //                      .dataAprovacaoGestor(LocalDate.of(2025, 9, 18))
        //                      .status("Aprovada pelo Gestor")
        //                      .build());
        //          }
        //
        //          if (candidatos.size() > 2 && vagas.size() > 2) { // Exemplo Rejeitada
        //              contratacoes.add(new Contratacao.Builder(vagas.get(2), candidatos.get(2))
        //                      .dataSolicitacao(LocalDate.of(2025, 9, 12))
        //                      .status("Rejeitada pelo Gestor") // Supondo que o gestor rejeitou sem data explícita
        //                      .build());
        //          }
        //     } catch (IllegalArgumentException e) {
        //         System.err.println("Erro ao criar contratações fictícias: " + e.getMessage());
        //     }
        // }

        model.addAttribute("contratacoes", contratacoes);
        return "recrutamento/consultar-contratacoes";
    }
}