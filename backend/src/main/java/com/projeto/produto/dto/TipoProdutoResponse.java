package com.projeto.produto.dto;

import com.projeto.produto.domain.TipoProduto;

public class TipoProdutoResponse {

    private Long id;
    private String nome;

    public TipoProdutoResponse() {}

    public TipoProdutoResponse(TipoProduto tipoProduto) {
        this.id = tipoProduto.getId();
        this.nome = tipoProduto.getNome();
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
}
