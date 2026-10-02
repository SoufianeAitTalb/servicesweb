package com.example.demo.repository;

import com.example.demo.entity.BibliothequeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliothequeRepository extends JpaRepository<BibliothequeEntity, Integer> {
}
