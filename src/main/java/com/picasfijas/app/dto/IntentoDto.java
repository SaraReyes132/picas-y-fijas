package com.picasfijas.app.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentoDto {
    private String numero;
    private int picas;
    private int fijas;
    private LocalDateTime fecha;
}