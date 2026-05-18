package com.picasfijas.app.recursos;

import com.picasfijas.app.dto.IntentoDto;
import com.picasfijas.app.dto.JugadorDto;
import com.picasfijas.app.dto.PartidaDto;
import com.picasfijas.app.servicios.ServicioJugador;
import com.picasfijas.app.servicios.ServicioPartida;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class RecursoJuego {

    private final ServicioJugador servicioJugador;
    private final ServicioPartida servicioPartida;

    @GetMapping("/juego")
    public String jugar(@RequestParam String username, Model model) {
        try {
            JugadorDto jugador = servicioJugador.obtenerPorUsername(username);
            PartidaDto partida = servicioPartida.crearPartida(username);

            model.addAttribute("jugador", jugador);
            model.addAttribute("partidaId", partida.getId());
            model.addAttribute("intentos", 0);
            model.addAttribute("tiempo", 0);
            model.addAttribute("fechaInicio", partida.getFechaInicio());
            model.addAttribute("historial", List.of());   // historial vacío al inicio
            model.addAttribute("partidaTerminada", false);
            return "juego";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/registro";
        }
    }

    @PostMapping("/juego/intento")
    public String intento(@RequestParam Long partidaId,
                          @RequestParam String intento,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> resultado = servicioPartida.verificarIntento(partidaId, intento);

            if ((boolean) resultado.get("gano")) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "¡Felicidades! Has ganado con " + resultado.get("intentos") +
                                " intentos en " + resultado.get("tiempo") + " segundos.");
                return "redirect:/ranking";
            }

            // Obtener datos para la vista
            PartidaDto partida = servicioPartida.obtenerPartidaDtoPorId(partidaId);
            String username = (String) resultado.get("username");
            JugadorDto jugador = servicioJugador.obtenerPorUsername(username);
            List<IntentoDto> historial = servicioPartida.obtenerHistorial(partidaId);

            model.addAttribute("jugador", jugador);
            model.addAttribute("partidaId", partidaId);
            model.addAttribute("intentos", resultado.get("intentos"));
            model.addAttribute("tiempo", resultado.get("tiempo"));
            model.addAttribute("picas", resultado.get("picas"));
            model.addAttribute("fijas", resultado.get("fijas"));
            model.addAttribute("ultimoIntento", intento);
            model.addAttribute("fechaInicio", partida.getFechaInicio());
            model.addAttribute("historial", historial);
            model.addAttribute("partidaTerminada", false);

            return "juego";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            // Intentar cargar datos mínimos para no romper la vista
            try {
                PartidaDto partida = servicioPartida.obtenerPartidaDtoPorId(partidaId);
                JugadorDto jugador = servicioJugador.obtenerPorUsername(partida.getUsername());
                model.addAttribute("jugador", jugador);
                model.addAttribute("partidaId", partidaId);
                model.addAttribute("fechaInicio", partida.getFechaInicio());
                model.addAttribute("historial", servicioPartida.obtenerHistorial(partidaId));
                model.addAttribute("partidaTerminada", false);
            } catch (Exception ex) {
                model.addAttribute("partidaId", partidaId);
            }
            return "juego";
        }
    }

    @GetMapping("/juego/nueva")
    public String nuevaPartida(@RequestParam String username) {
        return "redirect:/juego?username=" + username;
    }
}