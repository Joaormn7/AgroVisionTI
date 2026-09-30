package br.com.agrovisionti.api.service;

import br.com.agrovisionti.api.dto.colaborador.ColaboradorRequest;
import br.com.agrovisionti.api.dto.colaborador.ColaboradorResponse;
import br.com.agrovisionti.api.entity.Colaborador;
import br.com.agrovisionti.api.entity.Unidade;
import br.com.agrovisionti.api.exception.ResourceNotFoundException;
import br.com.agrovisionti.api.repository.ColaboradorRepository;
import br.com.agrovisionti.api.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional(readOnly = true)
    public List<ColaboradorResponse> listar(boolean somenteAtivos) {
        List<Colaborador> colaboradores = somenteAtivos
                ? colaboradorRepository.findByAtivoTrueOrderByNome()
                : colaboradorRepository.findAllByOrderByNome();
        return colaboradores.stream().map(ColaboradorResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<ColaboradorResponse> pesquisar(String termo) {
        return colaboradorRepository.pesquisar(termo).stream().map(ColaboradorResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ColaboradorResponse buscarPorId(Integer id) {
        return ColaboradorResponse.de(buscarEntidade(id));
    }

    public ColaboradorResponse criar(ColaboradorRequest request) {
        Colaborador colaborador = new Colaborador();
        aplicar(colaborador, request);
        return ColaboradorResponse.de(colaboradorRepository.save(colaborador));
    }

    public ColaboradorResponse atualizar(Integer id, ColaboradorRequest request) {
        Colaborador colaborador = buscarEntidade(id);
        aplicar(colaborador, request);
        return ColaboradorResponse.de(colaboradorRepository.save(colaborador));
    }

    public void desativar(Integer id) {
        alterarStatus(id, false);
    }

    public void reativar(Integer id) {
        alterarStatus(id, true);
    }

    private void alterarStatus(Integer id, boolean ativo) {
        Colaborador colaborador = buscarEntidade(id);
        colaborador.setAtivo(ativo);
        colaboradorRepository.save(colaborador);
    }

    private void aplicar(Colaborador colaborador, ColaboradorRequest request) {
        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada: " + request.unidadeId()));

        colaborador.setNome(request.nome());
        colaborador.setCpf(request.cpf());
        colaborador.setCargo(request.cargo());
        colaborador.setSetor(request.setor());
        colaborador.setUnidade(unidade);
    }

    private Colaborador buscarEntidade(Integer id) {
        return colaboradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador nao encontrado: " + id));
    }
}
