package com.bibliouni.bibliouni.repository;

import com.bibliouni.bibliouni.model.Libro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LibroRepositoryTest {

    @Autowired
    private LibroRepository libroRepository;

    @Test
    void debeDetectarIsbnExistente() {

        Libro libro = new Libro(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                5
        );

        libroRepository.save(libro);

        boolean existe = libroRepository.existsByIsbn(
                "9780132350884"
        );

        assertTrue(existe);
    }

    @Test
    void debeDetectarIsbnInexistente() {

        boolean existe = libroRepository.existsByIsbn(
                "9789999999999"
        );

        assertFalse(existe);
    }
}