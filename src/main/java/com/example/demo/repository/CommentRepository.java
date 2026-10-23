package com.example.demo.repository;

import com.example.demo.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    // Commentaires d'un livre, du plus récent au plus ancien
    List<CommentEntity> findByBookIsbnOrderByCreatedAtDesc(Long isbn);
}
