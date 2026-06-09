package com.projeto.produto.repository;

import com.projeto.produto.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByTipoProdutoId(Long tipoProdutoId);
}
