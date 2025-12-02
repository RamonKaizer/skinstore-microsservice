package com.skinstore.pedido.service;

import com.skinstore.pedido.entity.ItemPedido;
import com.skinstore.pedido.entity.Pedido;
import com.skinstore.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository repo;

    public PedidoService(PedidoRepository repo) { this.repo = repo; }

    @Transactional
    public Pedido criar(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;

        if (pedido.getItens() != null) {
            for (ItemPedido it : pedido.getItens()) {
                it.setPedido(pedido);
                BigDecimal preco = it.getPreco() != null ? it.getPreco() : BigDecimal.ZERO;
                int qtd = it.getQuantidade() != null ? it.getQuantidade() : 0;
                total = total.add(preco.multiply(BigDecimal.valueOf(qtd)));
            }
        }
        pedido.setValorTotal(total);
        return repo.save(pedido);
    }

    public List<Pedido> listar() { return repo.findAll(); }

    public Pedido buscar(Long id) { return repo.findById(id).orElse(null); }

    @Transactional
    public Pedido atualizarStatus(Long id, Pedido.Status status) {
        Pedido p = buscar(id);
        if (p == null) return null;
        p.setStatus(status);
        return repo.save(p);
    }

    public void deletar(Long id) { repo.deleteById(id); }
}
