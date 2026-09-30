package br.com.agrovisionti.api.repository;

import br.com.agrovisionti.api.entity.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Integer> {

    List<Colaborador> findByAtivoTrueOrderByNome();

    List<Colaborador> findAllByOrderByNome();

    @Query("""
            SELECT c FROM Colaborador c
            WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(c.cpf) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(c.cargo) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(c.setor) LIKE LOWER(CONCAT('%', :termo, '%'))
            ORDER BY c.nome
            """)
    List<Colaborador> pesquisar(@Param("termo") String termo);
}
