package br.com.agrovisionti.api.service;

import br.com.agrovisionti.api.dto.ativo.AtivoRequest;
import br.com.agrovisionti.api.dto.ativo.AtivoResponse;
import br.com.agrovisionti.api.entity.Ativo;
import br.com.agrovisionti.api.entity.Colaborador;
import br.com.agrovisionti.api.entity.Unidade;
import br.com.agrovisionti.api.exception.ResourceNotFoundException;
import br.com.agrovisionti.api.repository.AtivoRepository;
import br.com.agrovisionti.api.repository.ColaboradorRepository;
import br.com.agrovisionti.api.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AtivoService {

    private final AtivoRepository ativoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ColaboradorRepository colaboradorRepository;

    @Transactional(readOnly = true)
    public List<AtivoResponse> listar() {
        return ativoRepository.findByExcluidoFalseOrderByIdDesc().stream().map(AtivoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<AtivoResponse> pesquisar(String termo) {
        return ativoRepository.pesquisar(termo).stream().map(AtivoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public AtivoResponse buscarPorId(Integer id) {
        return AtivoResponse.de(buscarEntidade(id));
    }

    public AtivoResponse criar(AtivoRequest request) {
        Ativo ativo = new Ativo();
        aplicar(ativo, request);
        return AtivoResponse.de(ativoRepository.save(ativo));
    }

    public AtivoResponse atualizar(Integer id, AtivoRequest request) {
        Ativo ativo = buscarEntidade(id);
        aplicar(ativo, request);
        return AtivoResponse.de(ativoRepository.save(ativo));
    }

    // Soft delete - equivalente ao "excluido = true" do AtivoDAO original.
    public void excluir(Integer id) {
        Ativo ativo = buscarEntidade(id);
        ativo.setExcluido(true);
        ativoRepository.save(ativo);
    }

    private void aplicar(Ativo ativo, AtivoRequest request) {
        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada: " + request.unidadeId()));

        Colaborador responsavel = null;
        if (request.responsavelId() != null) {
            responsavel = colaboradorRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Colaborador nao encontrado: " + request.responsavelId()));
        }

        ativo.setTipo(request.tipo());
        ativo.setMarca(request.marca());
        ativo.setModelo(request.modelo());
        ativo.setNumeroSerie(request.numeroSerie());
        ativo.setUnidade(unidade);
        ativo.setResponsavel(responsavel);
        ativo.setStatus(request.status());
        ativo.setObservacoes(request.observacoes());
    }

    Ativo buscarEntidade(Integer id) {
        return ativoRepository.findByIdAndExcluidoFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ativo nao encontrado: " + id));
    }
}
