package com.example.demo.dto;

import com.example.demo.entity.CommentEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentDto {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;

    public static CommentDto fromEntity(CommentEntity comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(comment.getAuthor())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
