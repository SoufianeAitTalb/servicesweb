package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.entity.CommentEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    // Commentaires d'un livre, du plus récent au plus ancien
    // On ne vérifie l'existence du livre que si la liste est vide (livre sans commentaire → [], livre inconnu → 404) :
    // un livre qui a des commentaires ne coûte qu'un seul appel à la DB
    public List<CommentDto> getComments(Long isbn) {
        List<CommentEntity> comments = commentRepository.findByBookIsbnOrderByCreatedAtDesc(isbn);
        if (comments.isEmpty() && !bookRepository.existsById(isbn)) {
            throw new NotFoundException("Livre " + isbn);
        }
        return comments.stream()
                .map(CommentDto::fromEntity)
                .toList();
    }
}
