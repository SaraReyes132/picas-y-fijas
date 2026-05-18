package com.picasfijas.app.recursos;

import com.picasfijas.app.servicios.ServicioPartida;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class RecursoRanking {

    private final ServicioPartida servicioPartida;

    @GetMapping("/ranking")
    public String mostrarRanking(Model model) {
        model.addAttribute("ranking", servicioPartida.obtenerRanking());
        return "ranking";
    }
}