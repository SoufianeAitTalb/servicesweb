package com.example.demo.dto;

import com.example.demo.entity.BookEntity;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBookResponseDTO {

    @Getter
    @Setter
    public Long isbn;

    public static AddBookResponseDTO fromEntity(BookEntity bookEntity) {
        return AddBookResponseDTO.builder()
                .isbn(bookEntity.getIsbn())
                .build();
    }
}
 