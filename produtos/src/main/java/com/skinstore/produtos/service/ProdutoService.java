package com.skinstore.produtos.service;

import com.skinstore.produtos.domain.Produto;
import com.skinstore.produtos.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Produto atualizar(Long id, Produto produto) {
        Produto existente = buscarPorId(id);
        if (existente != null) {
            existente.setNome(produto.getNome());
            existente.setDescricao(produto.getDescricao());
            existente.setPreco(produto.getPreco());
            existente.setEstoque(produto.getEstoque());
            return repository.save(existente);
        }
        return null;
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Produto buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Produto " + id + " não encontrado"));
    }

    @Transactional
    public Produto debitarEstoque(Long id, int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade deve ser > 0");
        int updated = repository.debitarEstoque(id, qtd);
        if (updated == 0) {
            throw new IllegalStateException("Estoque insuficiente para o produto " + id);
        }
        return buscar(id);
    }

    @Transactional
    public Produto reporEstoque(Long id, int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade deve ser > 0");
        int updated = repository.reporEstoque(id, qtd);
        if (updated == 0) {
            throw new EntityNotFoundException("Produto " + id + " não encontrado");
        }
        return buscar(id);
    }
}
