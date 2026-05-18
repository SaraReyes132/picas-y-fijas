package com.picasfijas.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadorDto {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String username;

    @NotBlank(message = "Debes seleccionar un avatar")
    private String avatar;
}