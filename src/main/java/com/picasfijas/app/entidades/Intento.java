package com.picasfijas.app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "intentos")
public class Intento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @Column(nullable = false, length = 4)
    private String numero;

    @Column(nullable = false)
    private int picas;

    @Column(nullable = false)
    private int fijas;

    @Column(nullable = false)
    private LocalDateTime fecha;
}