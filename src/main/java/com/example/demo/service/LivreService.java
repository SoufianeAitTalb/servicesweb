package com.example.demo.service;

import com.example.demo.dto.LivreDto;
import com.example.demo.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LivreService {

    private final Map<String, LivreDto> livres = new HashMap<>(); // catalogue, clé = ISBN
    private final BibliothequeService bibliothequeService;

    // Injection par constructeur
    public LivreService(BibliothequeService bibliothequeService) {
        this.bibliothequeService = bibliothequeService;
    }

    // Récupérer un ou plusieurs livres par bibliothèque (isbns == null → tous)
    public List<LivreDto> getLivres(String bibliothequeId, List<String> isbns) {
        return bibliothequeService.getBibliotheque(bibliothequeId).getLivres().stream()
                .filter(livre -> isbns == null || isbns.contains(livre.getISBN()))
                .toList();
    }

    public LivreDto getLivre(String isbn) {
        LivreDto livre = livres.get(isbn);
        if (livre == null) {
            throw new NotFoundException("Livre " + isbn);
        }
        return livre;
    }

    // Créer un livre (the validation moved here from the controller)
    public LivreDto creerLivre(LivreDto livre) {
        if (livre.getISBN() == null || !livre.getISBN().matches("\\d{10,13}")) {
            throw new IllegalArgumentException("ISBN invalide : il doit contenir entre 10 et 13 chiffres.");
        }
        if (livre.getName() == null || livre.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom du livre ne peut pas être vide.");
        }
        if (livres.containsKey(livre.getISBN())) {
            throw new IllegalArgumentException("Un livre avec cet ISBN existe déjà.");
        }
        livres.put(livre.getISBN(), livre);
        return livre;
    }

    // Supprimer un livre
    public void supprimerLivre(String isbn) {
        if (livres.remove(isbn) == null) {
            throw new NotFoundException("Livre " + isbn);
        }
        // also remove it from every library, so no library keeps a deleted book
        bibliothequeService.getBibliotheques()
                .forEach(b -> b.getLivres().removeIf(l -> l.getISBN().equals(isbn)));
    }
}