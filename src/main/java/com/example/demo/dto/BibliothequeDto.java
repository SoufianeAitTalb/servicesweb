package com.example.demo.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class BibliothequeDto {
    private String id;
    private String name;


    @Builder.Default
    private List<LivreDto> livres= new ArrayList<>();




}
