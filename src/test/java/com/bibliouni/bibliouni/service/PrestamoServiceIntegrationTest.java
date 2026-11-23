package com.bibliouni.bibliouni.service;

import com.bibliouni.bibliouni.model.Estudiante;
import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.model.Prestamo;
import com.bibliouni.bibliouni.repository.LibroRepository;
import com.bibliouni.bibliouni.repository.PrestamoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PrestamoServiceIntegrationTest {
    @Autowired private PrestamoService servicio;
    @Autowired private LibroRepository libros;
    @Autowired private PrestamoRepository prestamos;
    @Autowired private EntityManager entityManager;
    @Autowired private PlatformTransactionManager transactionManager;

    private Libro libro;
    private Estudiante estudiante;
    private TransactionTemplate transaccion;
    private static final LocalDate INICIO = LocalDate.of(2026, 11, 1);

    @BeforeEach
    void prepararDatosPersistidos() {
        transaccion = new TransactionTemplate(transactionManager);
        transaccion.executeWithoutResult(estado -> {
            libro = libros.save(new Libro("Integracion", "Autor", "INTEGRACION-U2", 1));
            estudiante = new Estudiante("INTEGRACION-U2", "Ana", "integracion@example.com");
            entityManager.persist(estudiante);
        });
    }

    @AfterEach
    void limpiarSoloDatosDeEstaPrueba() {
        transaccion.executeWithoutResult(estado -> {
            prestamos.findAll().stream().filter(p -> p.getLibro().getId().equals(libro.getId()))
                    .forEach(prestamos::delete);
            prestamos.flush();
            libros.deleteById(libro.getId());
            entityManager.remove(entityManager.find(Estudiante.class, estudiante.getId()));
        });
    }

    private Prestamo nuevoPrestamo() {
        return new Prestamo(libro, estudiante, INICIO, INICIO.plusDays(7));
    }

    @Test
    void debePersistirPrestamoDevolucionYMulta() {
        Prestamo prestamo = servicio.registrarPrestamo(nuevoPrestamo());
        assertNotNull(prestamo.getId());
        assertEquals(0, libros.findById(libro.getId()).orElseThrow().getCantidadDisponible());

        servicio.registrarDevolucion(prestamo.getId(), INICIO.plusDays(10));

        Prestamo guardado = prestamos.findById(prestamo.getId()).orElseThrow();
        assertEquals(INICIO.plusDays(10), guardado.getFechaDevolucion());
        assertEquals(6000L, servicio.calcularMulta(guardado, guardado.getFechaDevolucion()));
        assertEquals(1, libros.findById(libro.getId()).orElseThrow().getCantidadDisponible());
        assertThrows(IllegalStateException.class, () ->
                servicio.registrarDevolucion(prestamo.getId(), INICIO.plusDays(11)));
        assertEquals(1, libros.findById(libro.getId()).orElseThrow().getCantidadDisponible());
    }

    @Test
    void debeRevertirDescuentoSiFallaPersistenciaDelPrestamo() {
        // El estudiante transitorio incumple la relacion persistida requerida por JPA.
        Prestamo invalido = new Prestamo(libro, new Estudiante("sin-id", "Ana", "otra@example.com"),
                INICIO, INICIO.plusDays(7));
        assertThrows(RuntimeException.class, () -> servicio.registrarPrestamo(invalido));
        assertEquals(1, libros.findById(libro.getId()).orElseThrow().getCantidadDisponible());
        assertTrue(prestamos.findAll().stream().noneMatch(p -> p.getLibro().getId().equals(libro.getId())));
    }

    @Test
    void debeConservarEstadoAnteDevolucionConFechaInvalida() {
        Prestamo prestamo = servicio.registrarPrestamo(nuevoPrestamo());
        assertThrows(IllegalArgumentException.class, () ->
                servicio.registrarDevolucion(prestamo.getId(), INICIO.minusDays(1)));
        assertNull(prestamos.findById(prestamo.getId()).orElseThrow().getFechaDevolucion());
        assertEquals(0, libros.findById(libro.getId()).orElseThrow().getCantidadDisponible());
    }

    @Test
    void debeImpedirRegistrarOtraVezUnPrestamoPersistido() {
        Prestamo prestamo = servicio.registrarPrestamo(nuevoPrestamo());
        assertThrows(IllegalStateException.class, () -> servicio.registrarPrestamo(prestamo));
        assertEquals(0, libros.findById(libro.getId()).orElseThrow().getCantidadDisponible());
    }

    @Test
    void debeRechazarSegundoPrestamoAunqueReferenciaTengaStockAnterior() {
        servicio.registrarPrestamo(nuevoPrestamo());
        assertEquals(1, libro.getCantidadDisponible()); // Objeto separado conserva el valor anterior.
        assertThrows(IllegalStateException.class, () -> servicio.registrarPrestamo(nuevoPrestamo()));
        assertEquals(0, libros.findById(libro.getId()).orElseThrow().getCantidadDisponible());
    }
}
