package com.picasfijas.app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;

    @Column(name = "numero_secreto", length = 4, nullable = false)
    private String numeroSecreto;

    @Column(nullable = false)
    private Integer intentos;

    @Column(nullable = false)
    private Integer tiempo;

    @Column(nullable = false)
    private LocalDateTime fecha;

}