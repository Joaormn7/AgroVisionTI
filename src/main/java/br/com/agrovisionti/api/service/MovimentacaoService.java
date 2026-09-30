package br.com.agrovisionti.api.service;

import br.com.agrovisionti.api.dto.movimentacao.MovimentacaoRequest;
import br.com.agrovisionti.api.dto.movimentacao.MovimentacaoResponse;
import br.com.agrovisionti.api.entity.Ativo;
import br.com.agrovisionti.api.entity.Colaborador;
import br.com.agrovisionti.api.entity.Movimentacao;
import br.com.agrovisionti.api.entity.Unidade;
import br.com.agrovisionti.api.exception.ResourceNotFoundException;
import br.com.agrovisionti.api.repository.ColaboradorRepository;
import br.com.agrovisionti.api.repository.MovimentacaoRepository;
import br.com.agrovisionti.api.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final AtivoService ativoService;
    private final UnidadeRepository unidadeRepository;
    private final ColaboradorRepository colaboradorRepository;

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listar() {
        return movimentacaoRepository.findAllByOrderByDataMovimentacaoDesc().stream()
                .map(MovimentacaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listarPorAtivo(Integer ativoId) {
        return movimentacaoRepository.findByAtivoIdOrderByDataMovimentacaoDesc(ativoId).stream()
                .map(MovimentacaoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> pesquisar(String termo) {
        return movimentacaoRepository.pesquisar(termo).stream().map(MovimentacaoResponse::de).toList();
    }

    // Equivalente ao MovimentacaoDAO.cadastrar original: grava o historico
    // com a localizacao ATUAL do ativo como origem, e so depois move o ativo
    // para o destino. As duas escritas ficam na mesma transacao (a anotacao
    // @Transactional da classe cobre o metodo) - se uma falhar, a outra
    // tambem e desfeita, o que o codigo desktop fazia manualmente com
    // setAutoCommit(false)/commit().
    public MovimentacaoResponse registrar(MovimentacaoRequest request) {
        Ativo ativo = ativoService.buscarEntidade(request.ativoId());

        Unidade unidadeDestino = unidadeRepository.findById(request.unidadeDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidade de destino nao encontrada: " + request.unidadeDestinoId()));

        Colaborador responsavelDestino = colaboradorRepository.findById(request.responsavelDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador de destino nao encontrado: " + request.responsavelDestinoId()));

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setAtivo(ativo);
        movimentacao.setUnidadeOrigem(ativo.getUnidade());
        movimentacao.setUnidadeDestino(unidadeDestino);
        movimentacao.setResponsavelOrigem(ativo.getResponsavel());
        movimentacao.setResponsavelDestino(responsavelDestino);
        movimentacao.setObservacoes(request.observacoes());

        Movimentacao salva = movimentacaoRepository.save(movimentacao);

        ativo.setUnidade(unidadeDestino);
        ativo.setResponsavel(responsavelDestino);
        // no-op explicito: o Hibernate ja detecta a mudanca (dirty checking)
        // dentro da transacao, mas deixamos claro que o ativo e persistido aqui.

        return MovimentacaoResponse.de(salva);
    }
}
