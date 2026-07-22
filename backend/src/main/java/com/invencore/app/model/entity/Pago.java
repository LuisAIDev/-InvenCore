package com.invencore.app.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Pedido pedido;

    @Column(nullable = false, length = 100)
    private String paymentIntentId;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(columnDefinition = "TEXT")
    private String stripeResponseJson;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
    }
}
