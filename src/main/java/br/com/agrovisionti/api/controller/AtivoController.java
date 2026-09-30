package br.com.agrovisionti.api.controller;

import br.com.agrovisionti.api.dto.ativo.AtivoRequest;
import br.com.agrovisionti.api.dto.ativo.AtivoResponse;
import br.com.agrovisionti.api.service.AtivoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ativos")
@RequiredArgsConstructor
public class AtivoController {

    private final AtivoService ativoService;

    @GetMapping
    public List<AtivoResponse> listar(@RequestParam(required = false) String termo) {
        if (termo != null && !termo.isBlank()) {
            return ativoService.pesquisar(termo);
        }
        return ativoService.listar();
    }

    @GetMapping("/{id}")
    public AtivoResponse buscarPorId(@PathVariable Integer id) {
        return ativoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtivoResponse criar(@Valid @RequestBody AtivoRequest request) {
        return ativoService.criar(request);
    }

    @PutMapping("/{id}")
    public AtivoResponse atualizar(@PathVariable Integer id, @Valid @RequestBody AtivoRequest request) {
        return ativoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        ativoService.excluir(id);
    }
}
