package com.projeto.produto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProdutoRequest {

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    private String nome;

    @NotNull(message = "O preço é obrigatório.")
    @Positive(message = "O preço deve ser um valor positivo.")
    private BigDecimal preco;

    @NotNull(message = "O tipo de produto é obrigatório.")
    private Long tipoProdutoId;

    public ProdutoRequest() {}

    public ProdutoRequest(String nome, BigDecimal preco, Long tipoProdutoId) {
        this.nome = nome;
        this.preco = preco;
        this.tipoProdutoId = tipoProdutoId;
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
}
