package com.example.sistemarh.administracao.controller;

import com.example.sistemarh.administracao.Usuario;
import com.example.sistemarh.administracao.UsuarioDTO;
import com.example.sistemarh.administracao.UsuarioService;
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
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNome(usuario.getNome());
            dto.setCpf(usuario.getCpf());
            dto.setLogin(usuario.getLogin()); // CORREÇÃO AQUI

            model.addAttribute("usuarioDTO", dto);
            model.addAttribute("editMode", true);
            model.addAttribute("loginOriginal", login);
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