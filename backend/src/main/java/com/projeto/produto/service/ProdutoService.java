package com.projeto.produto.service;

import com.projeto.produto.domain.Produto;
import com.projeto.produto.domain.TipoProduto;
import com.projeto.produto.exception.RegraNegocioException;
import com.projeto.produto.repository.ProdutoRepository;
import com.projeto.produto.repository.TipoProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final TipoProdutoRepository tipoProdutoRepository;

    public ProdutoService(ProdutoRepository produtoRepository,
                          TipoProdutoRepository tipoProdutoRepository) {
        this.produtoRepository = produtoRepository;
        this.tipoProdutoRepository = tipoProdutoRepository;
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado com id: " + id));
    }

    @Transactional
    public Produto salvar(Produto produto) {
        TipoProduto tipo = tipoProdutoRepository.findById(produto.getTipoProduto().getId())
                .orElseThrow(() -> new RegraNegocioException(
                        "Tipo de produto não encontrado com id: " + produto.getTipoProduto().getId()));
        produto.setTipoProduto(tipo);
        return produtoRepository.save(produto);
    }

    @Transactional
    public void remover(Long id) {
        buscarPorId(id);
        produtoRepository.deleteById(id);
    }
}
