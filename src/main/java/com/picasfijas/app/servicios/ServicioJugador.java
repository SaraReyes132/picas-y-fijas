package com.picasfijas.app.servicios;

import com.picasfijas.app.dto.JugadorDto;
import com.picasfijas.app.entidades.Jugador;
import com.picasfijas.app.exception.ResourceNotFoundException;
import com.picasfijas.app.repositorios.RepositorioJugador;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class ServicioJugador {

    private final RepositorioJugador repositorioJugador;
    private final ModelMapper modelMapper;

    public JugadorDto registrar(JugadorDto dto) {
        if (repositorioJugador.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        Jugador jugador = new Jugador();
        jugador.setUsername(dto.getUsername().trim());
        jugador.setAvatar(dto.getAvatar());   // usa el elegido por el usuario

        jugador = repositorioJugador.save(jugador);
        return modelMapper.map(jugador, JugadorDto.class);
    }

    @Transactional(readOnly = true)
    public JugadorDto obtenerPorUsername(String username) {
        Jugador jugador = repositorioJugador.findByUsername(username);
        if (jugador == null) {
            throw new ResourceNotFoundException("Jugador no encontrado: " + username);
        }
        return modelMapper.map(jugador, JugadorDto.class);
    }

    public boolean existeUsername(String username) {
        return repositorioJugador.existsByUsername(username);
    }
}