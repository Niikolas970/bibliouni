
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
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    private Libro libro;

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void debeRechazarIdentificadorInvalido(Long id) {
        assertThrows(IllegalArgumentException.class, () -> libroService.buscarPorId(id));
        verifyNoInteractions(libroRepository);
    }

    @Test
    void debeRegistrarLibroConCeroEjemplares() {
        libro.setCantidadDisponible(0);
        when(libroRepository.save(libro)).thenReturn(libro);
        assertEquals(0, libroService.registrarLibro(libro).getCantidadDisponible());
        verify(libroRepository).save(libro);
    }

    @Test
    void debeListarCatalogoVacio() {
        when(libroRepository.findAll()).thenReturn(List.of());
        assertTrue(libroService.listarLibros().isEmpty());
    }

    static Stream<Libro> librosIncompletos() {
        return Stream.of(null,
                new Libro(null, "Autor", "ISBN", 1),
                new Libro("  ", "Autor", "ISBN", 1),
                new Libro("Titulo", null, "ISBN", 1),
                new Libro("Titulo", "\t", "ISBN", 1),
                new Libro("Titulo", "Autor", null, 1),
                new Libro("Titulo", "Autor", " ", 1));
    }

    @ParameterizedTest
    @MethodSource("librosIncompletos")
    void debeRechazarLibroIncompleto(Libro incompleto) {
        assertThrows(IllegalArgumentException.class,
                () -> libroService.registrarLibro(incompleto));
        verifyNoInteractions(libroRepository);
    }

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
