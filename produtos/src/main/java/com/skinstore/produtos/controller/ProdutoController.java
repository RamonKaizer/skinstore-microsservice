package com.skinstore.produtos.controller;

import com.skinstore.produtos.domain.Produto;
import com.skinstore.produtos.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        return ResponseEntity.ok(service.salvar(produto));
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Produto produto = service.buscarPorId(id);
        return produto != null ? ResponseEntity.ok(produto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto) {
        Produto atualizado = service.atualizar(id, produto);
        return atualizado != null ? ResponseEntity.ok(atualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estoque/debitar")
    public ResponseEntity<Produto> debitar(@PathVariable Long id, @RequestBody Map<String,Integer> body) {
        int qtd = body.getOrDefault("quantidade", 0);
        Produto p = service.debitarEstoque(id, qtd);
        return ResponseEntity.ok(p);
    }

    @PatchMapping("/{id}/estoque/repor")
    public ResponseEntity<Produto> repor(@PathVariable Long id, @RequestBody Map<String,Integer> body) {
        int qtd = body.getOrDefault("quantidade", 0);
        Produto p = service.reporEstoque(id, qtd);
        return ResponseEntity.ok(p);
    }
}
