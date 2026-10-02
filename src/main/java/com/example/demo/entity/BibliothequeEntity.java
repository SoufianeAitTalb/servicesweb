package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="bibliotheque")
public class BibliothequeEntity {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "nom")
    private String nom;
}
