package br.com.agrovisionti.api.controller;

import br.com.agrovisionti.api.dto.unidade.UnidadeRequest;
import br.com.agrovisionti.api.dto.unidade.UnidadeResponse;
import br.com.agrovisionti.api.service.UnidadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    public List<UnidadeResponse> listar(@RequestParam(defaultValue = "false") boolean somenteAtivas) {
        return unidadeService.listar(somenteAtivas);
    }

    @GetMapping("/{id}")
    public UnidadeResponse buscarPorId(@PathVariable Integer id) {
        return unidadeService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnidadeResponse criar(@Valid @RequestBody UnidadeRequest request) {
        return unidadeService.criar(request);
    }

    @PutMapping("/{id}")
    public UnidadeResponse atualizar(@PathVariable Integer id, @Valid @RequestBody UnidadeRequest request) {
        return unidadeService.atualizar(id, request);
    }

    @PatchMapping("/{id}/desativar")
    public void desativar(@PathVariable Integer id) {
        unidadeService.desativar(id);
    }

    @PatchMapping("/{id}/reativar")
    public void reativar(@PathVariable Integer id) {
        unidadeService.reativar(id);
    }
}
