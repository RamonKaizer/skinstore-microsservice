package com.skinstore.pagamento.controller;

import com.skinstore.pagamento.entity.Pagamento;
import com.skinstore.pagamento.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Pagamento> criarEProcessar(@RequestBody Pagamento body) {
        return ResponseEntity.ok(service.criarEProcessar(body));
    }

    @GetMapping
    public ResponseEntity<List<Pagamento>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscar(@PathVariable Long id) {
        Pagamento p = service.buscar(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/reprocessar")
    public ResponseEntity<Pagamento> reprocessar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reprocessar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
