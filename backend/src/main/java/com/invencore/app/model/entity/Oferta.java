package com.invencore.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ofertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @ToString.Include
    private String nombre;

    @NotNull
    @Column(nullable = false, precision = 5, scale = 2)
    @ToString.Include
    private BigDecimal porcentajeDescuento;

    @NotNull
    @Column(nullable = false)
    @ToString.Include
    private LocalDateTime fechaInicio;

    @NotNull
    @Column(nullable = false)
    @ToString.Include
    private LocalDateTime fechaFin;

    @Builder.Default
    @ToString.Include
    private Boolean activa = true;

    @ManyToMany
    @JoinTable(
            name = "ofertas_productos",
            joinColumns = @JoinColumn(name = "oferta_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();
}
