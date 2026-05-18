package com.picasfijas.app.repositorios;

import com.picasfijas.app.entidades.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioPartida extends JpaRepository<Partida, Long> {
    @Query("SELECT p FROM Partida p " +
            "WHERE p.tiempo > 0 " +
            "AND p.intentos = (SELECT MIN(p2.intentos) FROM Partida p2 WHERE p2.jugador.id = p.jugador.id AND p2.tiempo > 0) " +
            "AND p.tiempo = (SELECT MIN(p3.tiempo) FROM Partida p3 WHERE p3.jugador.id = p.jugador.id AND p3.intentos = p.intentos AND p3.tiempo > 0) " +
            "ORDER BY p.intentos ASC, p.tiempo ASC")
    List<Partida> findMejoresPartidas();
}