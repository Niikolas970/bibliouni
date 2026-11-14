
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
        return libroRepository.findById(id);
    }

    public Libro registrarLibro(Libro libro) {

        if (libro == null || libro.getTitulo() == null || libro.getTitulo().isBlank()
                || libro.getAutor() == null || libro.getAutor().isBlank()
                || libro.getIsbn() == null || libro.getIsbn().isBlank()) {
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
}
