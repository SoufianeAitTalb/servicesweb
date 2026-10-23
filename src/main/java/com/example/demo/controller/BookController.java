package com.example.demo.controller;

import com.example.demo.dto.CommentDto;
import com.example.demo.service.BookService;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class BookController {
    private final BookService bookService;
    private final CommentService commentService;

    public BookController(
            BookService bookService,
            CommentService commentService,
            @Value("${spring.datasource.url}") String datasourceUrl,
            @Value("${PATH:${Path}}") String path    // PATH sous Linux/macOS, Path sous Windows
    ) {
        this.bookService = bookService;
        this.commentService = commentService;
        System.out.println(datasourceUrl);
        System.out.println(path);
    }

    // Envoyer la couverture au backend (form-data, clé "file"), qui la dépose dans S3.
    // La réponse est le lien S3 pour voir l'image (valable 10 minutes)
    @PutMapping("/livres/{isbn}/bookcover")
    public String uploadBookCover(@PathVariable(name = "isbn") String isbn,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        return bookService.uploadBookCover(isbn, file);
    }

    // Obtenir un nouveau lien S3 pour voir la couverture (quand le précédent a expiré)
    @GetMapping("/livres/{isbn}/bookcover")
    public String getBookCoverUrl(@PathVariable(name = "isbn") String isbn) {
        return bookService.getBookCoverUrl(isbn);
    }

    // Commentaires d'un livre, du plus récent au plus ancien (404 si le livre n'existe pas)
    @GetMapping("/livres/{isbn}/commentaires")
    public List<CommentDto> getComments(@PathVariable(name = "isbn") Long isbn) {
        return commentService.getComments(isbn);
    }
}
