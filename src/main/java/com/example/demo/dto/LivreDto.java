package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivreDto {
    @Min(0000000000L)
    private String ISBN;
    @NotBlank
    private String name;
}
