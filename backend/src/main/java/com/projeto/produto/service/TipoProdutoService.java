package com.projeto.produto.service;

import com.projeto.produto.domain.TipoProduto;
import com.projeto.produto.exception.RegraNegocioException;
import com.projeto.produto.repository.ProdutoRepository;
import com.projeto.produto.repository.TipoProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TipoProdutoService {

    private final TipoProdutoRepository tipoProdutoRepository;
    private final ProdutoRepository produtoRepository;

    public TipoProdutoService(TipoProdutoRepository tipoProdutoRepository,
                               ProdutoRepository produtoRepository) {
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<TipoProduto> listar() {
        return tipoProdutoRepository.findAll();
    }

    public TipoProduto buscarPorId(Long id) {
        return tipoProdutoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Tipo de produto não encontrado com id: " + id));
    }

    @Transactional
    public TipoProduto salvar(TipoProduto tipoProduto) {
        if (tipoProdutoRepository.existsByNomeIgnoreCase(tipoProduto.getNome())) {
            throw new RegraNegocioException("Já existe um tipo de produto com o nome: " + tipoProduto.getNome());
        }
        return tipoProdutoRepository.save(tipoProduto);
    }

    @Transactional
    public void remover(Long id) {
        buscarPorId(id);
        if (produtoRepository.existsByTipoProdutoId(id)) {
            throw new RegraNegocioException(
                    "Não é possível remover o tipo de produto pois existem produtos vinculados a ele.");
        }
        tipoProdutoRepository.deleteById(id);
    }
}
