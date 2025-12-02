package com.skinstore.carrinho.service;

import com.skinstore.carrinho.domain.Carrinho;
import com.skinstore.carrinho.domain.ItemCarrinho;
import com.skinstore.carrinho.dto.CriarCarrinhoRequest;
import com.skinstore.carrinho.repository.CarrinhoRepository;
import com.skinstore.carrinho.repository.ItemCarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepo;
    private final ItemCarrinhoRepository itemRepo;

    public CarrinhoService(CarrinhoRepository carrinhoRepo, ItemCarrinhoRepository itemRepo) {
        this.carrinhoRepo = carrinhoRepo;
        this.itemRepo = itemRepo;
    }

    @Transactional
    public Carrinho abrirOuObter(Long usuarioId) {
        return carrinhoRepo.findFirstByUsuarioIdAndStatusOrderByIdDesc(usuarioId, Carrinho.Status.ABERTO)
                .orElseGet(() -> {
                    Carrinho c = new Carrinho();
                    c.setUsuarioId(usuarioId);
                    return carrinhoRepo.save(c);
                });
    }

    @Transactional
    public Carrinho adicionarItem(Long carrinhoId, ItemCarrinho item) {
        Carrinho carrinho = carrinhoRepo.findById(carrinhoId).orElseThrow();
        item.setCarrinho(carrinho);
        itemRepo.save(item);
        return carrinhoRepo.findById(carrinhoId).orElseThrow(); // reload
    }

    @Transactional
    public Carrinho atualizarQuantidade(Long carrinhoId, Long itemId, Integer qtd) {
        ItemCarrinho it = itemRepo.findById(itemId).orElseThrow();
        if (!it.getCarrinho().getId().equals(carrinhoId)) throw new IllegalArgumentException("Item não pertence ao carrinho");
        it.setQuantidade(qtd);
        itemRepo.save(it);
        return carrinhoRepo.findById(carrinhoId).orElseThrow();
    }

    @Transactional
    public void removerItem(Long carrinhoId, Long itemId) {
        ItemCarrinho it = itemRepo.findById(itemId).orElseThrow();
        if (!it.getCarrinho().getId().equals(carrinhoId)) throw new IllegalArgumentException("Item não pertence ao carrinho");
        itemRepo.deleteById(itemId);
    }

    @Transactional
    public Carrinho alterarStatusCarrinho(Long carrinhoId, String status) {
        Carrinho c = carrinhoRepo.findById(carrinhoId).orElseThrow();
        c.setStatus(Carrinho.Status.valueOf(status.toUpperCase()));
        return carrinhoRepo.save(c);
    }

    public Carrinho obter(Long id) { return carrinhoRepo.findById(id).orElse(null); }

    public Carrinho criarCarrinho(CriarCarrinhoRequest request) {
        Carrinho carrinho = new Carrinho();
        carrinho.setUsuarioId(request.getUsuarioId());
        return carrinhoRepo.save(carrinho);
    }
}
