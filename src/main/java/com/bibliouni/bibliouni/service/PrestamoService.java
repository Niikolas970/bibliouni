
package com.bibliouni.bibliouni.service;

import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.model.Prestamo;
import com.bibliouni.bibliouni.repository.LibroRepository;
import com.bibliouni.bibliouni.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PrestamoService {

    private static final long MULTA_DIARIA = 2000L;

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(
            PrestamoRepository prestamoRepository,
            LibroRepository libroRepository) {

        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional
    public Prestamo registrarPrestamo(Prestamo prestamo) {

        if (prestamo == null || prestamo.getLibro() == null
                || prestamo.getEstudiante() == null) {
            throw new IllegalArgumentException(
                    "El prestamo debe tener libro y estudiante"
            );
        }

        if (prestamo.getFechaPrestamo() == null
                || prestamo.getFechaVencimiento() == null
                || prestamo.getFechaVencimiento()
                .isBefore(prestamo.getFechaPrestamo())) {
            throw new IllegalArgumentException(
                    "Las fechas del prestamo no son validas"
            );
        }

        Libro libro = prestamo.getLibro();

        if (libro.getCantidadDisponible() <= 0) {
            throw new IllegalStateException(
                    "No hay ejemplares disponibles"
            );
        }

        libro.setCantidadDisponible(
                libro.getCantidadDisponible() - 1
        );

        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }

    public long calcularMulta(
            Prestamo prestamo,
            LocalDate fechaDevolucion) {

        if (prestamo == null
                || prestamo.getFechaVencimiento() == null
                || fechaDevolucion == null) {
            throw new IllegalArgumentException(
                    "Los datos de la devolucion no son validos"
            );
        }

        long diasRetraso = ChronoUnit.DAYS.between(
                prestamo.getFechaVencimiento(),
                fechaDevolucion
        );

        return Math.max(0, diasRetraso) * MULTA_DIARIA;
    }

    @Transactional
    public Prestamo registrarDevolucion(Long id, LocalDate fechaDevolucion) {
        // Contrato inicial para ejecutar las pruebas Red de la funcionalidad nueva.
        throw new UnsupportedOperationException("Devolucion pendiente de implementar");
    }
}
