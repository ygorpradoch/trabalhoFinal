package com.projeto.produto.dto;

import com.projeto.produto.domain.Produto;

import java.math.BigDecimal;

public class ProdutoResponse {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private Long tipoProdutoId;
    private String tipoProdutoNome;

    public ProdutoResponse() {}

    public ProdutoResponse(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.preco = produto.getPreco();
        this.tipoProdutoId = produto.getTipoProduto().getId();
        this.tipoProdutoNome = produto.getTipoProduto().getNome();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Long getTipoProdutoId() {
        return tipoProdutoId;
    }

    public void setTipoProdutoId(Long tipoProdutoId) {
        this.tipoProdutoId = tipoProdutoId;
    }

    public String getTipoProdutoNome() {
        return tipoProdutoNome;
    }

    public void setTipoProdutoNome(String tipoProdutoNome) {
        this.tipoProdutoNome = tipoProdutoNome;
    }
}
