
package com.bibliouni.bibliouni.service;

import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    public Optional<Libro> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El identificador debe ser positivo");
        }
        return libroRepository.findById(id);
    }

    public Libro registrarLibro(Libro libro) {

        if (libro == null || estaVacio(libro.getTitulo())
                || estaVacio(libro.getAutor()) || estaVacio(libro.getIsbn())) {
            throw new IllegalArgumentException("El libro debe tener titulo, autor e ISBN");
        }

        if (libro.getCantidadDisponible() < 0) {
            throw new IllegalArgumentException(
                    "La cantidad disponible no puede ser negativa"
            );
        }

        if (libroRepository.existsByIsbn(libro.getIsbn())) {
            throw new IllegalStateException(
                    "Ya existe un libro registrado con ese ISBN"
            );
        }

        return libroRepository.save(libro);
    }

    private static boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
