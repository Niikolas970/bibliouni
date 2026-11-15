package com.bibliouni.bibliouni.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegresionApiTest {
    @Autowired private WebApplicationContext contexto;
    private MockMvc mvc;

    @BeforeEach
    void configurarCliente() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
    }

    @Test
    void debeConservarConsultaEstado() throws Exception {
        mvc.perform(get("/api/estado")).andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacion").value("BiblioUni"))
                .andExpect(jsonPath("$.estado").value("Disponible"));
    }

    @Test
    void debeRegistrarListarYRechazarDuplicado() throws Exception {
        String cuerpo = """
                {"titulo":"HTTP U2","autor":"Autor","isbn":"HTTP-U2","cantidadDisponible":1}
                """;
        mvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber());
        mvc.perform(get("/api/libros")).andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.isbn == 'HTTP-U2')]").isNotEmpty());
        mvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ya existe un libro registrado con ese ISBN"));
    }

    @Test
    void debeResponder400ParaLibroIncompleto() throws Exception {
        mvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo":" ","autor":"Autor","isbn":"HTTP-INVALIDO","cantidadDisponible":1}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El libro debe tener titulo, autor e ISBN"));
    }

    @Test
    void debeResponder400ParaCuerpoNoDeserializable() throws Exception {
        mvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El cuerpo de la solicitud no es valido"));
    }
}
