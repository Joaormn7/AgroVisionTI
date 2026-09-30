package br.com.agrovisionti.api.service;

import br.com.agrovisionti.api.dto.usuario.UsuarioRequest;
import br.com.agrovisionti.api.dto.usuario.UsuarioResponse;
import br.com.agrovisionti.api.entity.Usuario;
import br.com.agrovisionti.api.exception.BusinessException;
import br.com.agrovisionti.api.exception.ResourceNotFoundException;
import br.com.agrovisionti.api.repository.UsuarioRepository;
import br.com.agrovisionti.api.security.SenhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByOrderByNome().stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Integer id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    public UsuarioResponse criar(UsuarioRequest request) {
        if (request.senha() == null || request.senha().isBlank()) {
            throw new BusinessException("Senha e obrigatoria ao cadastrar um novo usuario.");
        }
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Ja existe um usuario com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPerfil(request.perfil());
        usuario.setSenha(senhaService.gerarHashBcrypt(request.senha()));
        usuario.setAtivo(true);

        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    public UsuarioResponse atualizar(Integer id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPerfil(request.perfil());

        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(senhaService.gerarHashBcrypt(request.senha()));
            usuario.setSalt(null);
        }

        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    public void desativar(Integer id) {
        alterarStatus(id, false);
    }

    public void reativar(Integer id) {
        alterarStatus(id, true);
    }

    private void alterarStatus(Integer id, boolean ativo) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(ativo);
        usuarioRepository.save(usuario);
    }

    private Usuario buscarEntidade(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + id));
    }
}
