package com.example.demo.service;

import com.example.demo.entity.BookEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private static final Long ISBN = 1234567890L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BibliothequeService bibliothequeService;

    @InjectMocks
    private BookService bookService;

    // Le livre existe en DB et save() renvoie l'entité qu'on lui donne
    private BookEntity givenBookExists() {
        BookEntity book = BookEntity.builder().isbn(ISBN).name("Ancien nom").build();
        when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(BookEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        return book;
    }

    // null, vide, uniquement des espaces, trop long (101 caractères)
    static Stream<String> invalidNames() {
        return Stream.of(null, "", "   ", "\t\n", "a".repeat(101));
    }

    // ===== Fonctionnel =====

    @Test
    void validName_isUpdated() {
        givenBookExists();

        BookEntity result = bookService.updateBookName(ISBN, "Nouveau nom");

        assertThat(result.getName()).isEqualTo("Nouveau nom");
    }

    @Test
    void nameOfExactly100Chars_isAccepted() {
        givenBookExists();
        String name = "a".repeat(100);

        BookEntity result = bookService.updateBookName(ISBN, name);

        assertThat(result.getName()).isEqualTo(name);
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    void invalidName_isRejected(String name) {
        assertThatThrownBy(() -> bookService.updateBookName(ISBN, name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unknownIsbn_throwsNotFound() {
        when(bookRepository.findById(ISBN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBookName(ISBN, "Nouveau nom"))
                .isInstanceOf(NotFoundException.class);
    }

    // ===== Performance : nombre d'appels à la DB =====

    @Test
    void validName_makesOneReadAndOneWrite() {
        BookEntity book = givenBookExists();

        bookService.updateBookName(ISBN, "Nouveau nom");

        verify(bookRepository, times(1)).findById(ISBN);
        verify(bookRepository, times(1)).save(book);
        verifyNoMoreInteractions(bookRepository);
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    void invalidName_neverTouchesTheDb(String name) {
        assertThatThrownBy(() -> bookService.updateBookName(ISBN, name))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(bookRepository);
    }

    @Test
    void unknownIsbn_neverSaves() {
        when(bookRepository.findById(ISBN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBookName(ISBN, "Nouveau nom"))
                .isInstanceOf(NotFoundException.class);

        verify(bookRepository, times(1)).findById(ISBN);
        verify(bookRepository, never()).save(any());
    }
}
