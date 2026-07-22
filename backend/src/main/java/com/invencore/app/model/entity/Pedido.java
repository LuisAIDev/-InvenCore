package com.invencore.app.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clienteNombre;

    @Column(nullable = false)
    private String clienteEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<PedidoItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Pago pago;

    @Column(nullable = false, unique = true, length = 36)
    @Builder.Default
    private String tokenConfirmacion = java.util.UUID.randomUUID().toString();

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (estado == null) estado = EstadoPedido.PENDIENTE;
    }
}
