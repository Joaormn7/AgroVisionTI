package br.com.agrovisionti.api.repository;

import br.com.agrovisionti.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAllByOrderByNome();
}
