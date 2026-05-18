package com.picasfijas.app.repositorios;

import com.picasfijas.app.entidades.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioJugador extends JpaRepository<Jugador, Long> {

    boolean existsByUsername(String username);

    Jugador findByUsername(String username);

}