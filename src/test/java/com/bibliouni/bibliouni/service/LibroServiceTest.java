
package com.bibliouni.bibliouni.service;

import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    private Libro libro;

    @BeforeEach
    void prepararDatos() {
        libro = new Libro(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                5
        );
    }

    @Test
    void debeRegistrarLibroValido() {
        // Arrange
        when(libroRepository.save(libro)).thenReturn(libro);

        // Act
        Libro resultado = libroService.registrarLibro(libro);

        // Assert
        assertSame(libro, resultado);
        verify(libroRepository).save(libro);
    }

    @Test
    void debeBuscarLibroPorIdExistente() {
        // Arrange
        when(libroRepository.findById(1L))
                .thenReturn(Optional.of(libro));

        // Act
        Optional<Libro> resultado = libroService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertSame(libro, resultado.get());
    }

    @Test
    void debeDevolverVacioCuandoIdNoExiste() {
        // Arrange
        when(libroRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act
        Optional<Libro> resultado = libroService.buscarPorId(99L);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeListarLibrosRegistrados() {
        // Arrange
        when(libroRepository.findAll()).thenReturn(List.of(libro));

        // Act
        List<Libro> resultado = libroService.listarLibros();

        // Assert
        assertEquals(1, resultado.size());
        assertSame(libro, resultado.get(0));
    }

    @Test
    void debeRechazarCantidadNegativa() {
        // Arrange
        libro.setCantidadDisponible(-5);

        // Act y Assert
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> libroService.registrarLibro(libro)
        );

        assertEquals(
                "La cantidad disponible no puede ser negativa",
                excepcion.getMessage()
        );

        verify(libroRepository, never()).save(any());
    }

    @Test
    void debeRechazarIsbnDuplicado() {
        // Arrange
        when(libroRepository.existsByIsbn(libro.getIsbn()))
                .thenReturn(true);

        // Act y Assert
        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> libroService.registrarLibro(libro)
        );

        assertEquals(
                "Ya existe un libro registrado con ese ISBN",
                excepcion.getMessage()
        );

        verify(libroRepository, never()).save(any());
    }
}
