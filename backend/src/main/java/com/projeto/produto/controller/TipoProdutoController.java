package com.projeto.produto.controller;

import com.projeto.produto.domain.TipoProduto;
import com.projeto.produto.dto.TipoProdutoRequest;
import com.projeto.produto.dto.TipoProdutoResponse;
import com.projeto.produto.service.TipoProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-produto")
@CrossOrigin(origins = "*")
public class TipoProdutoController {

    private final TipoProdutoService tipoProdutoService;

    public TipoProdutoController(TipoProdutoService tipoProdutoService) {
        this.tipoProdutoService = tipoProdutoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoProdutoResponse>> listar() {
        List<TipoProdutoResponse> lista = tipoProdutoService.listar()
                .stream()
                .map(TipoProdutoResponse::new)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoProdutoResponse> buscarPorId(@PathVariable Long id) {
        TipoProduto tipo = tipoProdutoService.buscarPorId(id);
        return ResponseEntity.ok(new TipoProdutoResponse(tipo));
    }

    @PostMapping
    public ResponseEntity<TipoProdutoResponse> salvar(@Valid @RequestBody TipoProdutoRequest request) {
        TipoProduto tipo = new TipoProduto(request.getNome());
        TipoProduto salvo = tipoProdutoService.salvar(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TipoProdutoResponse(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        tipoProdutoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
