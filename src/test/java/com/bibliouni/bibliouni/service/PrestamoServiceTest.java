package com.bibliouni.bibliouni.service;

import com.bibliouni.bibliouni.model.Estudiante;
import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.model.Prestamo;
import com.bibliouni.bibliouni.repository.LibroRepository;
import com.bibliouni.bibliouni.repository.PrestamoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    private Libro libro;
    private Estudiante estudiante;

    @BeforeEach
    void prepararDatos() {
        libro = new Libro(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                2
        );

        estudiante = new Estudiante(
                "1000123456",
                "Ana Perez",
                "ana@example.com"
        );
    }

    @Test
    void debeRegistrarPrestamoCuandoHayDisponibilidad() {

        LocalDate inicio = LocalDate.of(2026, 11, 14);
        LocalDate vencimiento = LocalDate.of(2026, 11, 21);

        Prestamo prestamo = new Prestamo(
                libro, estudiante, inicio, vencimiento
        );

        when(prestamoRepository.save(prestamo))
                .thenReturn(prestamo);


        Prestamo resultado =
                prestamoService.registrarPrestamo(prestamo);


        assertSame(prestamo, resultado);
        assertEquals(1, libro.getCantidadDisponible());

        verify(prestamoRepository).save(prestamo);
        verify(libroRepository).save(libro);
    }

    @Test
    void debeRechazarPrestamoSinDisponibilidad() {
        libro.setCantidadDisponible(0);

        Prestamo prestamo = new Prestamo(
                libro,
                estudiante,
                LocalDate.of(2026, 11, 14),
                LocalDate.of(2026, 11, 21)
        );


        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> prestamoService.registrarPrestamo(prestamo)
        );

        assertEquals(
                "No hay ejemplares disponibles",
                excepcion.getMessage()
        );

        verify(prestamoRepository, never()).save(any());
        verify(libroRepository, never()).save(any());
    }

    @Test
    void debeCalcularMultaPorDevolucionTardia() {

        Prestamo prestamo = new Prestamo(
                libro,
                estudiante,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 8)
        );

        LocalDate fechaDevolucion =
                LocalDate.of(2026, 11, 11);


        long multa = prestamoService.calcularMulta(
                prestamo, fechaDevolucion
        );

        assertEquals(6000L, multa);
    }
}
