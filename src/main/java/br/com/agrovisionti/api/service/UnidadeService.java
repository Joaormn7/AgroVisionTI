package br.com.agrovisionti.api.service;

import br.com.agrovisionti.api.dto.unidade.UnidadeRequest;
import br.com.agrovisionti.api.dto.unidade.UnidadeResponse;
import br.com.agrovisionti.api.entity.Unidade;
import br.com.agrovisionti.api.exception.ResourceNotFoundException;
import br.com.agrovisionti.api.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    @Transactional(readOnly = true)
    public List<UnidadeResponse> listar(boolean somenteAtivas) {
        List<Unidade> unidades = somenteAtivas
                ? unidadeRepository.findByAtivoTrueOrderByNome()
                : unidadeRepository.findAllByOrderByNome();
        return unidades.stream().map(UnidadeResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(Integer id) {
        return UnidadeResponse.de(buscarEntidade(id));
    }

    public UnidadeResponse criar(UnidadeRequest request) {
        Unidade unidade = new Unidade();
        aplicar(unidade, request);
        return UnidadeResponse.de(unidadeRepository.save(unidade));
    }

    public UnidadeResponse atualizar(Integer id, UnidadeRequest request) {
        Unidade unidade = buscarEntidade(id);
        aplicar(unidade, request);
        return UnidadeResponse.de(unidadeRepository.save(unidade));
    }

    public void desativar(Integer id) {
        alterarStatus(id, false);
    }

    public void reativar(Integer id) {
        alterarStatus(id, true);
    }

    private void alterarStatus(Integer id, boolean ativo) {
        Unidade unidade = buscarEntidade(id);
        unidade.setAtivo(ativo);
        unidadeRepository.save(unidade);
    }

    private void aplicar(Unidade unidade, UnidadeRequest request) {
        unidade.setNome(request.nome());
        unidade.setTipo(request.tipo());
        unidade.setCnpj(request.cnpj());
    }

    private Unidade buscarEntidade(Integer id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada: " + id));
    }
}
