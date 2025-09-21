package com.example.sistemarh;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InterfaceController {

    @GetMapping("/")
    public String paginaInicial(Model model) {
        return "index";
    }

    // == ROTAS DE ADMINISTRAÇÃO E GESTÃO (ALUNO 1) ==
    @GetMapping("/Administração")
    public String menuAdministracao() {
        return "adm/menu";
    }

    @GetMapping("/Administração/Gestão")
    public String gestaoDeUsuarios() {
        return "adm/admGestao";
    }

    @GetMapping("/Administração/Relatório")
    public String gerarRelatorioAdm() {return "adm/admRelatorio";}

    @GetMapping("/Login")
    public String realizaLogin() {
        return "adm/Login";
    }

    @GetMapping("/Cadastro")
    public String cadastro() {
        return "adm/cadastroNovoUsuario";
    }

    // == ROTAS DE CADASTRO (ALUNO 2) ==
    @GetMapping("/cadastro")
    public String menuCadastro() {return "cadastro/menu";
    }

    @GetMapping("/cadastro/candidato")
    public String cadastroCandidato() {return "cadastro/cadastroCandidato";
    }

    @GetMapping("/cadastro/candidatura")
    public String candidaturaVaga() {return "cadastro/candidaturaVaga";
    }

    @GetMapping("/cadastro/status")
    public String statusCandidatura() {return "cadastro/statusCandidatura";
    }

     // == ROTAS DE RECRUTAMENTO (ALUNO 3) ==
    @GetMapping("/recrutamento")
    public String menuRecrutamento() {
        return "recrutamento/menu";
    }

    @GetMapping("/recrutamento/cadastrar-candidato")
    public String cadastrarCandidato() {
        return "recrutamento/cadastro-candidato";
    }

    @GetMapping("/recrutamento/realizar-candidatura")
    public String realizarCandidatura() {
        return "recrutamento/realizar-candidatura";
    }

    @GetMapping("/recrutamento/marcar-entrevista")
    public String marcarEntrevista() {
        return "recrutamento/marcar-entrevista";
    }

    @GetMapping("/recrutamento/solicitar-contratacao")
    public String solicitarContratacao() {
        return "recrutamento/solicitar-contratacao";
    }

    @GetMapping("/recrutamento/consultar-contratacoes")
    public String consultarContratacoes() {
        return "recrutamento/consultar-contratacoes";
    }

    // == ROTAS FINANCEIRO (ALUNO 4) ==
    @GetMapping("/financeiro")
    public String menuFinanceiro() {
        return "financeiro/menu";
    }

    @GetMapping("/financeiro/cadastrar-funcionario")
    public String cadastrarFuncionario() {
        return "financeiro/cadastro-funcionario";
    }

    @GetMapping("/financeiro/configurar-regras")
    public String configurarRegras() {
        return "financeiro/configurar-regras";
    }

    @GetMapping("/financeiro/gerar-folha")
    public String gerarFolha() {
        return "financeiro/gerar-folha";
    }

    @GetMapping("/financeiro/relatorio")
    public String relatorioFinanceiro() {
        return "financeiro/relatorio";
    }

    @GetMapping("/financeiro/contracheques")
    public String contracheques() {
        return "financeiro/contracheques";
    }
}