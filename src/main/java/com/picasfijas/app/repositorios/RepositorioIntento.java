package com.picasfijas.app.repositorios;

import com.picasfijas.app.entidades.Intento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepositorioIntento extends JpaRepository<Intento, Long> {
    List<Intento> findByPartidaIdOrderByFechaAsc(Long partidaId);
}