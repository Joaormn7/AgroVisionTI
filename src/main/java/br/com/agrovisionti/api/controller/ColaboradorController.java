package br.com.agrovisionti.api.controller;

import br.com.agrovisionti.api.dto.colaborador.ColaboradorRequest;
import br.com.agrovisionti.api.dto.colaborador.ColaboradorResponse;
import br.com.agrovisionti.api.service.ColaboradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @GetMapping
    public List<ColaboradorResponse> listar(
            @RequestParam(required = false) String termo,
            @RequestParam(defaultValue = "false") boolean somenteAtivos
    ) {
        if (termo != null && !termo.isBlank()) {
            return colaboradorService.pesquisar(termo);
        }
        return colaboradorService.listar(somenteAtivos);
    }

    @GetMapping("/{id}")
    public ColaboradorResponse buscarPorId(@PathVariable Integer id) {
        return colaboradorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColaboradorResponse criar(@Valid @RequestBody ColaboradorRequest request) {
        return colaboradorService.criar(request);
    }

    @PutMapping("/{id}")
    public ColaboradorResponse atualizar(@PathVariable Integer id, @Valid @RequestBody ColaboradorRequest request) {
        return colaboradorService.atualizar(id, request);
    }

    @PatchMapping("/{id}/desativar")
    public void desativar(@PathVariable Integer id) {
        colaboradorService.desativar(id);
    }

    @PatchMapping("/{id}/reativar")
    public void reativar(@PathVariable Integer id) {
        colaboradorService.reativar(id);
    }
}
