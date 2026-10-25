package com.bibliouni.bibliouni.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LibroTest {

    @Test
    void debeGuardarLaCantidadDisponible() {

        // Preparación
        Libro libro = new Libro(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                5
        );

        // Ejecución
        int cantidad = libro.getCantidadDisponible();

        // Verificación
        assertEquals(5, cantidad);
    }
}