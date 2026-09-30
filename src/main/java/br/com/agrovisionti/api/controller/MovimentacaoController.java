package br.com.agrovisionti.api.controller;

import br.com.agrovisionti.api.dto.movimentacao.MovimentacaoRequest;
import br.com.agrovisionti.api.dto.movimentacao.MovimentacaoResponse;
import br.com.agrovisionti.api.service.MovimentacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    @GetMapping
    public List<MovimentacaoResponse> listar(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) Integer ativoId
    ) {
        if (termo != null && !termo.isBlank()) {
            return movimentacaoService.pesquisar(termo);
        }
        if (ativoId != null) {
            return movimentacaoService.listarPorAtivo(ativoId);
        }
        return movimentacaoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoResponse registrar(@Valid @RequestBody MovimentacaoRequest request) {
        return movimentacaoService.registrar(request);
    }
}
