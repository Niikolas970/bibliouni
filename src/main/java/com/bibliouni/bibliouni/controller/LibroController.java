package com.bibliouni.bibliouni.controller;

import com.bibliouni.bibliouni.model.Libro;
import com.bibliouni.bibliouni.service.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public List<Libro> listarLibros() {
        return libroService.listarLibros();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro registrarLibro(@RequestBody Libro libro) {
        return libroService.registrarLibro(libro);
    }
}