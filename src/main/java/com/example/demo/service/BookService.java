package com.example.demo.service;

import com.example.demo.dto.LivreDto;
import com.example.demo.entity.BookEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.S3Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private final Map<String, LivreDto> livres = new HashMap<>(); // catalogue, clé = ISBN
    private final BibliothequeService bibliothequeService;
    private final BookRepository bookRepository;
    private final S3Repository s3Repository;

    // Injection par constructeur
    public BookService(BibliothequeService bibliothequeService, BookRepository bookRepository,
                       S3Repository s3Repository) {
        this.bibliothequeService = bibliothequeService;
        this.bookRepository = bookRepository;
        this.s3Repository = s3Repository;
    }

    // Clé de la couverture d'un livre dans le bucket S3 : déduite de l'ISBN, rien à stocker en base
    private String bookCoverKey(String isbn) {
        return "bookcover/" + isbn;
    }

    // Le backend dépose la couverture dans S3, puis renvoie le lien S3 pour la voir
    public String uploadBookCover(String isbn, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("La couverture doit être une image.");
        }
        s3Repository.uploadFile(bookCoverKey(isbn), file.getBytes(), file.getContentType());
        return s3Repository.getFileUrl(bookCoverKey(isbn));
    }

    // Lien pour afficher la couverture directement depuis S3
    public String getBookCoverUrl(String isbn) {
        return s3Repository.getFileUrl(bookCoverKey(isbn));
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
    public LivreDto addBook(LivreDto livre) {
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

    // Modifier le nom d'un livre
    // La validation se fait AVANT tout accès à la DB : un nom invalide ne coûte aucun appel
    public BookEntity updateBookName(Long isbn, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Le nom du livre ne peut pas être vide.");
        }
        if (newName.length() > 100) {
            throw new IllegalArgumentException("Le nom du livre ne doit pas dépasser 100 caractères.");
        }
        BookEntity book = bookRepository.findById(isbn)
                .orElseThrow(() -> new NotFoundException("Livre " + isbn));
        book.setName(newName);
        return bookRepository.save(book);
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