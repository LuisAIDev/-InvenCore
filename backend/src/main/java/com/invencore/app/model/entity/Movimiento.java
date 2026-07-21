package com.invencore.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoMovimiento tipo;

    @NotNull
    @Min(1)
    private Integer cantidad;

    private String descripcion;

    private String motivo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @NotNull
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @NotNull
    private Usuario usuario;
}
