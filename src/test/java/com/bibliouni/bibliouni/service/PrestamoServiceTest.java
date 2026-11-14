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
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    private Prestamo prestamoValido() {
        return new Prestamo(libro, estudiante, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 8));
    }

    @Test
    void debeBuscarPrestamoExistente() {
        Prestamo prestamo = prestamoValido();
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        assertSame(prestamo, prestamoService.buscarPorId(1L).orElseThrow());
    }

    @Test
    void debeDevolverVacioParaPrestamoInexistente() {
        when(prestamoRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(prestamoService.buscarPorId(99L).isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void debeRechazarIdentificadorInvalido(Long id) {
        assertThrows(IllegalArgumentException.class, () -> prestamoService.buscarPorId(id));
        assertThrows(IllegalArgumentException.class, () ->
                prestamoService.registrarDevolucion(id, LocalDate.of(2026, 11, 8)));
        verifyNoInteractions(prestamoRepository, libroRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void debeDescontarUnSoloEjemplar(int cantidad) {
        libro.setCantidadDisponible(cantidad);
        Prestamo prestamo = prestamoValido();
        when(prestamoRepository.save(prestamo)).thenReturn(prestamo);
        prestamoService.registrarPrestamo(prestamo);
        assertEquals(cantidad - 1, libro.getCantidadDisponible());
        verify(libroRepository).save(libro);
        verify(prestamoRepository).save(prestamo);
    }

    @Test
    void debeRechazarDisponibilidadNegativa() {
        libro.setCantidadDisponible(-1);
        assertThrows(IllegalStateException.class, () -> prestamoService.registrarPrestamo(prestamoValido()));
        verifyNoInteractions(prestamoRepository, libroRepository);
    }

    static Stream<Prestamo> prestamosInvalidos() {
        Libro libro = new Libro("Titulo", "Autor", "ISBN", 1);
        Estudiante estudiante = new Estudiante("123", "Ana", "ana@example.com");
        LocalDate inicio = LocalDate.of(2026, 11, 1);
        return Stream.of(null, new Prestamo(null, estudiante, inicio, inicio),
                new Prestamo(libro, null, inicio, inicio), new Prestamo(libro, estudiante, null, inicio),
                new Prestamo(libro, estudiante, inicio, null),
                new Prestamo(libro, estudiante, inicio, inicio.minusDays(1)));
    }

    @ParameterizedTest
    @MethodSource("prestamosInvalidos")
    void debeRechazarPrestamoInvalido(Prestamo prestamo) {
        assertThrows(IllegalArgumentException.class, () -> prestamoService.registrarPrestamo(prestamo));
        verifyNoInteractions(prestamoRepository, libroRepository);
    }

    @Test
    void debeAceptarVencimientoElMismoDia() {
        LocalDate fecha = LocalDate.of(2026, 11, 1);
        Prestamo prestamo = new Prestamo(libro, estudiante, fecha, fecha);
        when(prestamoRepository.save(prestamo)).thenReturn(prestamo);
        assertSame(prestamo, prestamoService.registrarPrestamo(prestamo));
        assertEquals(1, libro.getCantidadDisponible());
    }

    @Test
    void debeRechazarRegistroYaDevuelto() {
        Prestamo prestamo = prestamoValido();
        prestamo.setFechaDevolucion(LocalDate.of(2026, 11, 8));
        assertThrows(IllegalStateException.class, () -> prestamoService.registrarPrestamo(prestamo));
        verifyNoInteractions(prestamoRepository, libroRepository);
    }

    @Test
    void debeRechazarRegistroConIdExistente() {
        Prestamo persistido = mock(Prestamo.class);
        when(persistido.getLibro()).thenReturn(libro);
        when(persistido.getEstudiante()).thenReturn(estudiante);
        when(persistido.getId()).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> prestamoService.registrarPrestamo(persistido));
        verifyNoInteractions(prestamoRepository, libroRepository);
    }

    @ParameterizedTest
    @CsvSource({"-1,0", "0,0", "1,2000", "3,6000", "30,60000"})
    void debeCalcularMultaEnLimites(int dias, long esperado) {
        Prestamo prestamo = prestamoValido();
        assertEquals(esperado, prestamoService.calcularMulta(prestamo,
                prestamo.getFechaVencimiento().plusDays(dias)));
    }

    static Stream<Arguments> multasInvalidas() {
        LocalDate fecha = LocalDate.of(2026, 11, 1);
        return Stream.of(Arguments.of(null, fecha),
                Arguments.of(new Prestamo(null, null, null, fecha), fecha),
                Arguments.of(new Prestamo(null, null, fecha, null), fecha),
                Arguments.of(new Prestamo(null, null, fecha, fecha), null),
                Arguments.of(new Prestamo(null, null, fecha, fecha.minusDays(1)), fecha),
                Arguments.of(new Prestamo(null, null, fecha, fecha), fecha.minusDays(1)));
    }

    @ParameterizedTest
    @MethodSource("multasInvalidas")
    void debeRechazarDatosInvalidosDeMulta(Prestamo prestamo, LocalDate fecha) {
        assertThrows(IllegalArgumentException.class, () -> prestamoService.calcularMulta(prestamo, fecha));
        verifyNoInteractions(prestamoRepository, libroRepository);
    }

    @Test
    void debeRechazarDevolucionInexistente() {
        when(prestamoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                prestamoService.registrarDevolucion(99L, LocalDate.of(2026, 11, 8)));
        verify(libroRepository, never()).save(any());
        verify(prestamoRepository, never()).save(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"2026-10-31"})
    void debeRechazarFechaDevolucionInvalida(String fecha) {
        Prestamo prestamo = prestamoValido();
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        assertThrows(IllegalArgumentException.class, () ->
                prestamoService.registrarDevolucion(1L, fecha == null ? null : LocalDate.parse(fecha)));
        assertNull(prestamo.getFechaDevolucion());
        assertEquals(2, libro.getCantidadDisponible());
        verify(libroRepository, never()).save(any());
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    void debeRechazarDesbordamientoDeDisponibilidad() {
        libro.setCantidadDisponible(Integer.MAX_VALUE);
        Prestamo prestamo = prestamoValido();
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        assertThrows(IllegalStateException.class, () ->
                prestamoService.registrarDevolucion(1L, LocalDate.of(2026, 11, 8)));
        assertNull(prestamo.getFechaDevolucion());
        assertEquals(Integer.MAX_VALUE, libro.getCantidadDisponible());
        verify(libroRepository, never()).save(any());
        verify(prestamoRepository, never()).save(any());
    }

    @Test
    void debeRegistrarDevolucionYRecuperarDisponibilidad() {
        libro.setCantidadDisponible(0);
        Prestamo prestamo = new Prestamo(libro, estudiante,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 8));
        LocalDate devolucion = LocalDate.of(2026, 11, 11);
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(prestamoRepository.save(prestamo)).thenReturn(prestamo);

        Prestamo resultado = prestamoService.registrarDevolucion(1L, devolucion);

        assertSame(prestamo, resultado);
        assertEquals(devolucion, resultado.getFechaDevolucion());
        assertEquals(1, libro.getCantidadDisponible());
        assertEquals(6000L, prestamoService.calcularMulta(resultado, devolucion));
        verify(libroRepository).save(libro);
        verify(prestamoRepository).save(prestamo);
    }

    @Test
    void debeRechazarDevolucionDuplicada() {
        Prestamo prestamo = new Prestamo(libro, estudiante,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 8));
        prestamo.setFechaDevolucion(LocalDate.of(2026, 11, 8));
        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));

        assertThrows(IllegalStateException.class, () ->
                prestamoService.registrarDevolucion(1L, LocalDate.of(2026, 11, 9)));

        assertEquals(2, libro.getCantidadDisponible());
        assertEquals(LocalDate.of(2026, 11, 8), prestamo.getFechaDevolucion());
        verify(libroRepository, never()).save(any());
        verify(prestamoRepository, never()).save(any());
    }

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
