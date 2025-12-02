package com.skinstore.carrinho.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "itens_carrinho")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemCarrinho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "carrinho_id")
    @JsonBackReference
    private Carrinho carrinho;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(name = "preco_unitario")
    private Double precoUnitario;

}
