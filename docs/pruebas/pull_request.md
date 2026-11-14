# Borrador de Pull Request

Título: Completar pruebas, devoluciones y entregable de Unidad 2

Base: develop. Origen: feature/plan-tests. Estado: preparado localmente; sin push ni PR creado.

El archivo PrestamoServiceTest tenía declaraciones duplicadas que impedían compilar. Se conserva su contenido válido y se completan las reglas de préstamos y devoluciones: stock persistido, fechas válidas, recuperación de ejemplares una sola vez y multa de 2.000 COP por día de retraso. Se conservan las pruebas de Unidad 1 y se añade regresión HTTP y transaccional con H2.

Se incluyen dos ciclos TDD con commits Red, Green y Refactor y evidencia original; plan con estructura IEEE 829-1998, 40 casos formales con trazabilidad, informe Word y scripts de reproducción. JaCoCo queda ligado a test y comprueba al menos 80 % de líneas de servicios durante verify.

Validación: .\mvnw.cmd clean test y .\mvnw.cmd clean verify, ambos BUILD SUCCESS. Suite final: 70 pruebas, 0 fallos, 0 errores, 0 omitidas. Cobertura de servicios: 64/64 líneas y 60/60 ramas (100 %); cobertura global: 122/139 líneas (87,77 %). Evidencia final en docs/pruebas/evidencias/unidad2/11-final. El código probado corresponde a 6eaba68; los commits posteriores son documentación y evidencias.

Limitaciones: H2 y MockMvc locales; no se afirman concurrencia segura, rendimiento, seguridad, MySQL ni aceptación de usuarios. Las operaciones de préstamos/devoluciones se entregan en la capa de servicio. Spring Boot 4.1.2 administra Jupiter 6.0.3; la diferencia con JUnit 5 indicado inicialmente queda documentada y respaldada por la compatibilidad de Spring Framework 7.

La referencia develop local (41980a6) es antecesora de la rama; no hay divergencia local que requiera resolver conflictos. No se consultó ni actualizó develop remoto. Antes de integrar, actualizar referencias remotas y repetir la comprobación de conflictos y pruebas. La aprobación de publicación y creación del PR corresponde al propietario.

Commits TDD revisables:

- Libro: 8f4a04f (Red), 825b425 (Green), 262142b (Refactor).
- Devolución: 75f8f47 (Red), 8016651 (Green), 27ccb71 (Refactor).
