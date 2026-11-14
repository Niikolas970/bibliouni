
package com.bibliouni.bibliouni.service;

import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.model.Prestamo;
import com.bibliouni.bibliouni.repository.LibroRepository;
import com.bibliouni.bibliouni.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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

        if (prestamo.getId() != null || prestamo.getFechaDevolucion() != null) {
            throw new IllegalStateException("Solo se pueden registrar prestamos nuevos y activos");
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

        ajustarDisponibilidad(libro, -1);

        return prestamoRepository.save(prestamo);
    }

    public long calcularMulta(
            Prestamo prestamo,
            LocalDate fechaDevolucion) {

        if (prestamo == null || prestamo.getFechaPrestamo() == null
                || prestamo.getFechaVencimiento() == null || fechaDevolucion == null
                || prestamo.getFechaVencimiento().isBefore(prestamo.getFechaPrestamo())
                || fechaDevolucion.isBefore(prestamo.getFechaPrestamo())) {
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
        Prestamo prestamo = buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("El prestamo no existe"));
        if (prestamo.getFechaDevolucion() != null) {
            throw new IllegalStateException("El prestamo ya fue devuelto");
        }
        // Valida las fechas antes de modificar el estado o la disponibilidad.
        calcularMulta(prestamo, fechaDevolucion);
        Libro libro = prestamo.getLibro();
        if (libro.getCantidadDisponible() == Integer.MAX_VALUE) {
            throw new IllegalStateException("La disponibilidad excede el limite permitido");
        }
        prestamo.setFechaDevolucion(fechaDevolucion);
        ajustarDisponibilidad(libro, 1);
        return prestamoRepository.save(prestamo);
    }

    private void ajustarDisponibilidad(Libro libro, int diferencia) {
        libro.setCantidadDisponible(libro.getCantidadDisponible() + diferencia);
        libroRepository.save(libro);
    }

    public Optional<Prestamo> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El identificador debe ser positivo");
        }
        return prestamoRepository.findById(id);
    }
}
