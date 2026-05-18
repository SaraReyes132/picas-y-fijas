package com.picasfijas.app.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartidaDto {
    private Long id;
    private String username;
    private String avatar;
    private Integer intentos;
    private Integer tiempo;
    private LocalDateTime fechaInicio;
}