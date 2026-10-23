package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.entity.CommentEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private static final Long ISBN = 1234567890L;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CommentService commentService;

    // ===== Fonctionnel =====

    @Test
    void bookWithComments_returnsThem() {
        LocalDateTime date = LocalDateTime.of(2026, 10, 1, 12, 0);
        CommentEntity comment = CommentEntity.builder()
                .id(1L).author("Lecteur").content("Super livre").createdAt(date).build();
        when(commentRepository.findByBookIsbnOrderByCreatedAtDesc(ISBN)).thenReturn(List.of(comment));

        List<CommentDto> result = commentService.getComments(ISBN);

        assertThat(result).singleElement().satisfies(c -> {
            assertThat(c.getId()).isEqualTo(1L);
            assertThat(c.getAuthor()).isEqualTo("Lecteur");
            assertThat(c.getContent()).isEqualTo("Super livre");
            assertThat(c.getCreatedAt()).isEqualTo(date);
        });
    }

    @Test
    void bookWithoutComments_returnsEmptyList() {
        when(commentRepository.findByBookIsbnOrderByCreatedAtDesc(ISBN)).thenReturn(List.of());
        when(bookRepository.existsById(ISBN)).thenReturn(true);

        assertThat(commentService.getComments(ISBN)).isEmpty();
    }

    @Test
    void unknownIsbn_throwsNotFound() {
        when(commentRepository.findByBookIsbnOrderByCreatedAtDesc(ISBN)).thenReturn(List.of());
        when(bookRepository.existsById(ISBN)).thenReturn(false);

        assertThatThrownBy(() -> commentService.getComments(ISBN))
                .isInstanceOf(NotFoundException.class);
    }

    // ===== Performance : nombre d'appels à la DB =====

    @Test
    void bookWithComments_makesOneDbCall() {
        CommentEntity comment = CommentEntity.builder().id(1L).author("Lecteur").content("Super livre").build();
        when(commentRepository.findByBookIsbnOrderByCreatedAtDesc(ISBN)).thenReturn(List.of(comment));

        commentService.getComments(ISBN);

        verify(commentRepository, times(1)).findByBookIsbnOrderByCreatedAtDesc(ISBN);
        verifyNoInteractions(bookRepository);
    }
}
