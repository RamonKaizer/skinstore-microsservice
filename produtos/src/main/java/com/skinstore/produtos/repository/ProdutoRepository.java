package com.skinstore.produtos.repository;

import com.skinstore.produtos.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Modifying
    @Transactional
    @Query("update Produto p set p.estoque = p.estoque - :qtd where p.id = :id and p.estoque >= :qtd")
    int debitarEstoque(@Param("id") Long id, @Param("qtd") int qtd);

    /** Repor/estornar unidades ao estoque. */
    @Modifying
    @Transactional
    @Query("update Produto p set p.estoque = p.estoque + :qtd where p.id = :id")
    int reporEstoque(@Param("id") Long id, @Param("qtd") int qtd);
}
