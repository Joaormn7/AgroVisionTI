package br.com.agrovisionti.api.controller;

import br.com.agrovisionti.api.dto.usuario.UsuarioRequest;
import br.com.agrovisionti.api.dto.usuario.UsuarioResponse;
import br.com.agrovisionti.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Todo endpoint aqui exige perfil Administrador - ver SecurityConfig
// (.requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")).
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest request) {
        return usuarioService.criar(request);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    @PatchMapping("/{id}/desativar")
    public void desativar(@PathVariable Integer id) {
        usuarioService.desativar(id);
    }

    @PatchMapping("/{id}/reativar")
    public void reativar(@PathVariable Integer id) {
        usuarioService.reativar(id);
    }
}
