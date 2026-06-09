package com.projeto.produto.controller;

import com.projeto.produto.domain.Produto;
import com.projeto.produto.domain.TipoProduto;
import com.projeto.produto.dto.ProdutoRequest;
import com.projeto.produto.dto.ProdutoResponse;
import com.projeto.produto.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar() {
        List<ProdutoResponse> lista = produtoService.listar()
                .stream()
                .map(ProdutoResponse::new)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(new ProdutoResponse(produto));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> salvar(@Valid @RequestBody ProdutoRequest request) {
        TipoProduto tipoProduto = new TipoProduto();
        tipoProduto.setId(request.getTipoProdutoId());

        Produto produto = new Produto(request.getNome(), request.getPreco(), tipoProduto);
        Produto salvo = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProdutoResponse(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
