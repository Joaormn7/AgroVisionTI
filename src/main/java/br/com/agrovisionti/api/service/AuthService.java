package br.com.agrovisionti.api.service;

import br.com.agrovisionti.api.dto.auth.LoginRequest;
import br.com.agrovisionti.api.dto.auth.LoginResponse;
import br.com.agrovisionti.api.entity.Usuario;
import br.com.agrovisionti.api.repository.UsuarioRepository;
import br.com.agrovisionti.api.security.JwtService;
import br.com.agrovisionti.api.security.SenhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaService senhaService;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(request.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha invalidos."));

        boolean senhaValida = senhaService.isHashLegado(usuario.getSenha())
                ? senhaService.verificarLegado(request.senha(), usuario.getSenha(), usuario.getSalt())
                : senhaService.verificarBcrypt(request.senha(), usuario.getSenha());

        if (!senhaValida) {
            throw new BadCredentialsException("E-mail ou senha invalidos.");
        }

        // Upgrade silencioso do hash legado (SHA-256+salt) para BCrypt no
        // primeiro login apos a migracao - o usuario nao percebe nada.
        if (senhaService.isHashLegado(usuario.getSenha())) {
            usuario.setSenha(senhaService.gerarHashBcrypt(request.senha()));
            usuario.setSalt(null);
            usuarioRepository.save(usuario);
        }

        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }
}
