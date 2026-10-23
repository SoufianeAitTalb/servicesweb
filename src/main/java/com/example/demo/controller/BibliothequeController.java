package com.example.demo.controller;

import com.example.demo.dto.BibliothequeDto;
import com.example.demo.dto.LivreDto;
import com.example.demo.service.BibliothequeService;
import com.example.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class BibliothequeController {

    private final BibliothequeService bibliothequeService;
    private final BookService bookService;





    @GetMapping("/bibliotheques")
    public Collection<BibliothequeDto> getBibliotheques() {
        return bibliothequeService.getBibliotheques();
    }

    @GetMapping("/bibliotheques/{id}")
    public BibliothequeDto getBibliothequeById(@PathVariable(name = "id") String id) {
        return bibliothequeService.getBibliotheque(id);   // 404 handled by CustomExceptionHandler
    }
    // GET /bibliotheques/{id}/livres?isbn=123123&isbn=456456&isbn=789789
    // Sans paramètre isbn : retourne tous les livres de la bibliothèque
    @GetMapping("/bibliotheques/{id}/livres")
    public List<LivreDto> getLivres(@PathVariable(name = "id") String id,
                                    @RequestParam(name = "isbn", required = false) List<String> isbns) {
        return bookService.getLivres(id, isbns);
    }
}