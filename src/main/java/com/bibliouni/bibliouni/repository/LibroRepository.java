package com.bibliouni.bibliouni.repository;

import com.bibliouni.bibliouni.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    boolean existsByIsbn(String isbn);
}