package com.bibliouni.bibliouni.service;

import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.repository.LibroRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LibroServiceTest {

    @Test
    void debeRechazarCantidadNegativa() {


        LibroRepository repositorio = mock(LibroRepository.class);
        LibroService servicio = new LibroService(repositorio);

        Libro libro = new Libro(
                "Libro de prueba",
                "Autor de prueba",
                "9780000000002",
                -5
        );


        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarLibro(libro)
        );


        assertEquals(
                "La cantidad disponible no puede ser negativa",
                excepcion.getMessage()
        );

        verify(repositorio, never()).save(any(Libro.class));
    }

    @Test
    void debeRechazarIsbnDuplicado() {

        LibroRepository repositorio = mock(LibroRepository.class);
        LibroService servicio = new LibroService(repositorio);

        Libro libro = new Libro(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                5
        );

        when(repositorio.existsByIsbn("9780132350884"))
                .thenReturn(true);

        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> servicio.registrarLibro(libro)
        );

        assertEquals(
                "Ya existe un libro registrado con ese ISBN",
                excepcion.getMessage()
        );

        verify(repositorio, never()).save(any(Libro.class));
    }
}