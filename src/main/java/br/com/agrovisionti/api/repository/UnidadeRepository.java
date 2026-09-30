package br.com.agrovisionti.api.repository;

import br.com.agrovisionti.api.entity.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnidadeRepository extends JpaRepository<Unidade, Integer> {

    List<Unidade> findByAtivoTrueOrderByNome();

    List<Unidade> findAllByOrderByNome();
}
