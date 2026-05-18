package com.picasfijas.app.servicios;

import com.picasfijas.app.dto.IntentoDto;
import com.picasfijas.app.dto.PartidaDto;
import com.picasfijas.app.dto.RankingDto;
import com.picasfijas.app.entidades.Intento;
import com.picasfijas.app.entidades.Jugador;
import com.picasfijas.app.entidades.Partida;
import com.picasfijas.app.exception.ResourceNotFoundException;
import com.picasfijas.app.repositorios.RepositorioIntento;
import com.picasfijas.app.repositorios.RepositorioJugador;
import com.picasfijas.app.repositorios.RepositorioPartida;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ServicioPartida {

    private final RepositorioPartida repositorioPartida;
    private final RepositorioJugador repositorioJugador;
    private final RepositorioIntento repositorioIntento;
    private final ModelMapper modelMapper;

    public PartidaDto crearPartida(String username) {
        Jugador jugador = repositorioJugador.findByUsername(username);
        if (jugador == null) {
            throw new ResourceNotFoundException("Jugador no encontrado: " + username);
        }

        Partida partida = Partida.builder()
                .jugador(jugador)
                .numeroSecreto(generarNumeroSecreto())
                .intentos(0)
                .tiempo(0)
                .fecha(LocalDateTime.now())
                .build();

        partida = repositorioPartida.save(partida);

        PartidaDto dto = new PartidaDto();
        dto.setId(partida.getId());
        dto.setUsername(jugador.getUsername());
        dto.setAvatar(jugador.getAvatar());
        dto.setIntentos(0);
        dto.setTiempo(0);
        dto.setFechaInicio(partida.getFecha());
        return dto;
    }

    private String generarNumeroSecreto() {
        List<Integer> digitos = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(digitos);
        return digitos.subList(0, 4)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public Map<String, Object> verificarIntento(Long partidaId, String intento) {
        Partida partida = repositorioPartida.findById(partidaId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada"));

        if (partida.getTiempo() > 0 && partida.getIntentos() > 0) {
            throw new RuntimeException("Esta partida ya ha terminado");
        }

        // Validación estricta de formato
        if (intento == null || intento.length() != 4 || !intento.matches("\\d{4}")) {
            throw new RuntimeException("El intento debe ser exactamente 4 dígitos numéricos");
        }

        // Validar dígitos no repetidos (no detiene la partida, solo lanza error)
        if (tieneDigitosRepetidos(intento)) {
            throw new RuntimeException("Los 4 dígitos deben ser diferentes entre sí");
        }

        int[] resultado = calcularPicasFijas(partida.getNumeroSecreto(), intento);
        int picas = resultado[0];
        int fijas = resultado[1];
        boolean gano = (fijas == 4);

        // Incrementar intentos
        partida.setIntentos(partida.getIntentos() + 1);
        repositorioPartida.save(partida);

        // Guardar el intento en el historial (siempre, aunque no sea ganador)
        Intento intentoEntity = Intento.builder()
                .partida(partida)
                .numero(intento)
                .picas(picas)
                .fijas(fijas)
                .fecha(LocalDateTime.now())
                .build();
        repositorioIntento.save(intentoEntity);

        // Si ganó, calcular tiempo
        if (gano) {
            long segundos = ChronoUnit.SECONDS.between(partida.getFecha(), LocalDateTime.now());
            partida.setTiempo((int) segundos);
            repositorioPartida.save(partida);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("picas", picas);
        response.put("fijas", fijas);
        response.put("gano", gano);
        response.put("intentos", partida.getIntentos());
        response.put("tiempo", partida.getTiempo());
        response.put("partidaId", partidaId);
        response.put("username", partida.getJugador().getUsername());
        response.put("avatar", partida.getJugador().getAvatar());
        response.put("numeroSecreto", gano ? partida.getNumeroSecreto() : null);

        return response;
    }

    private boolean tieneDigitosRepetidos(String numero) {
        return numero.chars().distinct().count() != numero.length();
    }

    private int[] calcularPicasFijas(String secreto, String intento) {
        int picas = 0;
        int fijas = 0;
        boolean[] secretoUsado = new boolean[4];
        boolean[] intentoUsado = new boolean[4];

        for (int i = 0; i < 4; i++) {
            if (intento.charAt(i) == secreto.charAt(i)) {
                fijas++;
                secretoUsado[i] = true;
                intentoUsado[i] = true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (!intentoUsado[i]) {
                for (int j = 0; j < 4; j++) {
                    if (!secretoUsado[j] && intento.charAt(i) == secreto.charAt(j)) {
                        picas++;
                        secretoUsado[j] = true;
                        break;
                    }
                }
            }
        }
        return new int[]{picas, fijas};
    }

    @Transactional(readOnly = true)
    public List<IntentoDto> obtenerHistorial(Long partidaId) {
        return repositorioIntento.findByPartidaIdOrderByFechaAsc(partidaId)
                .stream()
                .map(i -> {
                    IntentoDto dto = new IntentoDto();
                    dto.setNumero(i.getNumero());
                    dto.setPicas(i.getPicas());
                    dto.setFijas(i.getFijas());
                    dto.setFecha(i.getFecha());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PartidaDto obtenerPartidaDtoPorId(Long id) {
        Partida p = repositorioPartida.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada"));
        PartidaDto dto = new PartidaDto();
        dto.setId(p.getId());
        dto.setUsername(p.getJugador().getUsername());
        dto.setAvatar(p.getJugador().getAvatar());
        dto.setIntentos(p.getIntentos());
        dto.setTiempo(p.getTiempo());
        dto.setFechaInicio(p.getFecha());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<RankingDto> obtenerRanking() {
        List<Partida> partidas = repositorioPartida.findMejoresPartidas();
        Map<String, RankingDto> mapa = new LinkedHashMap<>();

        for (Partida p : partidas) {
            String username = p.getJugador().getUsername();
            if (!mapa.containsKey(username)) {
                RankingDto dto = new RankingDto();
                dto.setPosicion(mapa.size() + 1);
                dto.setAvatar(p.getJugador().getAvatar());
                dto.setUsername(username);
                dto.setIntentos(p.getIntentos());
                dto.setTiempo(p.getTiempo());
                mapa.put(username, dto);
            }
        }
        return new ArrayList<>(mapa.values());
    }
}