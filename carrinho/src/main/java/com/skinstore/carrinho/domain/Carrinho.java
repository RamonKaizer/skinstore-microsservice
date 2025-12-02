package com.skinstore.carrinho.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "carrinhos")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Carrinho {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ABERTO;

    @Column(name = "criado_em")
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemCarrinho> itens = new ArrayList<>();

    public enum Status { ABERTO, FECHADO }

    @PrePersist
    public void prePersist() {
        this.criadoEm = OffsetDateTime.now();
        this.status = Status.ABERTO;
    }
}
