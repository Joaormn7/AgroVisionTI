package br.com.agrovisionti.api.repository;

import br.com.agrovisionti.api.entity.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AtivoRepository extends JpaRepository<Ativo, Integer> {

    List<Ativo> findByExcluidoFalseOrderByIdDesc();

    Optional<Ativo> findByIdAndExcluidoFalse(Integer id);

    @Query("""
            SELECT a FROM Ativo a
            WHERE a.excluido = false
              AND (LOWER(a.tipo) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(a.marca) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(a.modelo) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(a.unidade.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(a.status) LIKE LOWER(CONCAT('%', :termo, '%')))
            ORDER BY a.id DESC
            """)
    List<Ativo> pesquisar(@Param("termo") String termo);
}
