package com.bibliouni.bibliouni.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class EstadoController {

    @GetMapping("/estado")
    public Map<String, String> consultarEstado() {
        return Map.of(
                "aplicacion", "BiblioUni",
                "estado", "Disponible"
        );
    }
}