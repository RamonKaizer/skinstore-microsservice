package com.skinstore.carrinho.controller;

import com.skinstore.carrinho.domain.Carrinho;
import com.skinstore.carrinho.domain.ItemCarrinho;
import com.skinstore.carrinho.dto.CriarCarrinhoRequest;
import com.skinstore.carrinho.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/carrinhos")
public class CarrinhoController {
    private final CarrinhoService service;
    public CarrinhoController(CarrinhoService service) { this.service = service; }

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<Carrinho> abrir(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.abrirOuObter(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> obter(@PathVariable Long id) {
        Carrinho c = service.obter(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{carrinhoId}/itens")
    public ResponseEntity<Carrinho> addItem(@PathVariable Long carrinhoId, @RequestBody ItemCarrinho body) {
        return ResponseEntity.ok(service.adicionarItem(carrinhoId, body));
    }

    @PatchMapping("/{carrinhoId}/itens/{itemId}")
    public ResponseEntity<Carrinho> atualizarQtd(@PathVariable Long carrinhoId, @PathVariable Long itemId, @RequestParam Integer qtd) {
        return ResponseEntity.ok(service.atualizarQuantidade(carrinhoId, itemId, qtd));
    }

    @DeleteMapping("/{carrinhoId}/itens/{itemId}")
    public ResponseEntity<Void> remover(@PathVariable Long carrinhoId, @PathVariable Long itemId) {
        service.removerItem(carrinhoId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{carrinhoId}/status/{status}")
    public ResponseEntity<Carrinho> alterarStatusCarrinho(@PathVariable Long carrinhoId, @PathVariable String status) {
        return ResponseEntity.ok(service.alterarStatusCarrinho(carrinhoId, status));
    }

    @PostMapping
    public ResponseEntity<Carrinho> criarCarrinho(@RequestBody CriarCarrinhoRequest request) {
        Carrinho carrinhoCriado = service.criarCarrinho(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(carrinhoCriado);
    }
}
