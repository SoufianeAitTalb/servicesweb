package com.example.demo.service;

import com.example.demo.dto.BibliothequeDto;
import com.example.demo.dto.LivreDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.BibliothequeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BibliothequeService {

    private final BibliothequeRepository bibliothequeRepository;

    // new ArrayList<>(...) because List.of(...) is immutable
    private final List<BibliothequeDto> bibliotheques = new ArrayList<>(List.of(
            BibliothequeDto.builder().id("1").name("Biblio").build()
    ));

    public Collection<BibliothequeDto> getBibliotheques() {
        return bibliotheques;
    }

    public BibliothequeDto getBibliotheque(String id) {
        return bibliotheques.stream()
                .filter(bibliotheque -> bibliotheque.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Bibliothèque " + id));
    }

    // Ajouter un livre à une bibliothèque
    public BibliothequeDto ajouterLivre(String bibliothequeId, LivreDto livre) {
        BibliothequeDto bibliotheque = getBibliotheque(bibliothequeId);
        boolean dejaPresent = bibliotheque.getLivres().stream()
                .anyMatch(l -> l.getISBN().equals(livre.getISBN()));
        if (!dejaPresent) {
            bibliotheque.getLivres().add(livre);
        }
        return bibliotheque;
    }

    // Supprimer un livre d'une bibliothèque
    public void supprimerLivre(String bibliothequeId, String isbn) {
        BibliothequeDto bibliotheque = getBibliotheque(bibliothequeId);
        boolean supprime = bibliotheque.getLivres().removeIf(l -> l.getISBN().equals(isbn));
        if (!supprime) {
            throw new NotFoundException("Livre " + isbn + " dans la bibliothèque " + bibliothequeId);
        }
    }
}
