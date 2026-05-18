package com.picasfijas.app.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingDto {
    private int posicion;
    private String avatar;
    private String username;
    private Integer intentos;
    private Integer tiempo;
}