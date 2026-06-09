package com.projeto.produto.repository;

import com.projeto.produto.domain.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoProdutoRepository extends JpaRepository<TipoProduto, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
