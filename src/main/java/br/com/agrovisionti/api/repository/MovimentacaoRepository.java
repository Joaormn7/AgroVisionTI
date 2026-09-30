package br.com.agrovisionti.api.repository;

import br.com.agrovisionti.api.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {

    List<Movimentacao> findAllByOrderByDataMovimentacaoDesc();

    List<Movimentacao> findByAtivoIdOrderByDataMovimentacaoDesc(Integer ativoId);

    @Query("""
            SELECT m FROM Movimentacao m
            WHERE LOWER(m.ativo.tipo) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(m.ativo.marca) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(m.ativo.modelo) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(m.unidadeOrigem.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(m.unidadeDestino.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(m.responsavelOrigem.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(m.responsavelDestino.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(m.observacoes) LIKE LOWER(CONCAT('%', :termo, '%'))
            ORDER BY m.dataMovimentacao DESC
            """)
    List<Movimentacao> pesquisar(@Param("termo") String termo);
}
