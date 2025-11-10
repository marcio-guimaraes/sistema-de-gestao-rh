package com.example.sistemarh.administracao.controller;

import com.example.sistemarh.administracao.*;
import com.example.sistemarh.financeiro.Funcionario; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Administração")
public class AdministracaoController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String menuAdministracao() {
        return "adm/menu";
    }

    @GetMapping("/Gestão")
    public String gestaoDeUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "adm/admGestao";
    }

    @GetMapping("/Relatório")
    public String gerarRelatorioAdm() {
        return "adm/admRelatorio";
    }

    @GetMapping("/Login")
    public String realizaLoginGet() {
        return "adm/login";
    }

    @PostMapping("/Login")
    public String realizaLoginPost(@RequestParam String username, @RequestParam String password) {
        // Correção: Adicionado tratamento para senha nula/vazia
        if (password == null || password.isEmpty()) {
            return "redirect:/Administração/Login?error=true";
        }

        boolean loginValido = usuarioService.validarLogin(username, password);

        if (loginValido) {
            return "redirect:/";
        } else {
            return "redirect:/Administração/Login?error=true";
        }
    }

    @GetMapping("/Cadastro")
    public String cadastroGet(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("editMode", false);
        return "adm/cadastroNovoUsuario";
    }

    @PostMapping("/Cadastro/Salvar")
    public String cadastroPost(@ModelAttribute UsuarioDTO usuarioDTO) {
        try {
            usuarioService.criarUsuario(usuarioDTO);
        } catch (RuntimeException e) {
            return "redirect:/Administração/Cadastro?error=" + e.getMessage();
        }
        return "redirect:/Administração/Gestão";
    }

    @GetMapping("/Gestão/Excluir/{login}")
    public String excluirUsuario(@PathVariable("login") String login) {
        if (!login.equals("admin")) {
            usuarioService.excluirUsuario(login);
        }
        return "redirect:/Administração/Gestão";
    }

    @GetMapping("/Gestão/Editar/{login}")
    public String editarUsuarioGet(@PathVariable("login") String login, Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorLogin(login);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // CORREÇÃO: Preencher o DTO completamente
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNome(usuario.getNome());
            dto.setCpf(usuario.getCpf());
            dto.setLogin(usuario.getLogin());
            // Não enviamos a senha para o formulário

            if (usuario instanceof Funcionario) {
                Funcionario f = (Funcionario) usuario;
                dto.setDepartamento(f.getDepartamento());

                if (f instanceof Administrador) {
                    dto.setPerfil("Administrador");
                } else if (f instanceof Gestor) {
                    dto.setPerfil("Gestor");
                } else {
                    dto.setPerfil(f.getCargo()); // "RECRUTADOR" ou "FUNCIONÁRIO"
                }
            } else {
                dto.setPerfil("Usuario"); // Perfil genérico
                dto.setDepartamento("N/A");
            }

            model.addAttribute("usuarioDTO", dto);
            model.addAttribute("editMode", true);
            return "adm/cadastroNovoUsuario";
        }
        return "redirect:/Administração/Gestão";
    }

    @PostMapping("/Gestão/Editar/Salvar")
    public String editarUsuarioPost(@RequestParam("loginOriginal") String loginOriginal, @ModelAttribute UsuarioDTO usuarioDTO) {
        try {
            usuarioService.atualizarUsuario(loginOriginal, usuarioDTO);
        } catch (RuntimeException e) {
            return "redirect:/Administração/Gestão/Editar/" + loginOriginal + "?error=" + e.getMessage();
        }
        return "redirect:/Administração/Gestão";
    }
}