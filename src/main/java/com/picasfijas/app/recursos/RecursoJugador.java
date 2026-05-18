package com.picasfijas.app.recursos;

import com.picasfijas.app.dto.JugadorDto;
import com.picasfijas.app.servicios.ServicioJugador;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class RecursoJugador {

    private final ServicioJugador servicioJugador;

    @GetMapping({"/", "/registro"})
    public String mostrarRegistro(Model model) {
        model.addAttribute("jugador", new JugadorDto());
        model.addAttribute("avatares", new String[]{"avatar1.png", "avatar2.png", "avatar3.png", "avatar4.png"});
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("jugador") JugadorDto dto,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("avatares", new String[]{"avatar1.png", "avatar2.png", "avatar3.png", "avatar4.png"});
            return "registro";
        }

        if (servicioJugador.existeUsername(dto.getUsername())) {
            model.addAttribute("error", "El nombre de usuario ya existe. ¡Elige otro!");
            model.addAttribute("avatares", new String[]{"avatar1.png", "avatar2.png", "avatar3.png", "avatar4.png"});
            return "registro";
        }

        try {
            JugadorDto jugador = servicioJugador.registrar(dto);
            return "redirect:/juego?username=" + jugador.getUsername();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("avatares", new String[]{"avatar1.png", "avatar2.png", "avatar3.png", "avatar4.png"});
            return "registro";
        }
    }
}