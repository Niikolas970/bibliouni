# Escenarios de Prueba — BiblioUni

**Proyecto:** Sistema de Gestión de Biblioteca Universitaria  
**Asignatura:** Pruebas de Software  
**Unidad:** 1  
**Fecha:** 25 de octubre de 2026  
**Estado:** Escenarios diseñados, pendientes de ejecución salvo las pruebas iniciales documentadas por separado.

## 1. Objetivo

Diseñar escenarios de prueba para verificar las funcionalidades principales de BiblioUni, detectar defectos y comprobar el cumplimiento de los requisitos funcionales y no funcionales.

Los escenarios se ejecutarán progresivamente mediante JUnit, Spring Boot Test y Postman, según el componente evaluado.

## 2. Requisitos y criterios generales

Los requisitos funcionales considerados son RF-01 a RF-08, definidos en la estrategia de pruebas.

Para las futuras pruebas de API se proponen las siguientes convenciones HTTP:

- `200 OK`: consulta u operación exitosa.
- `201 Created`: recurso creado.
- `400 Bad Request`: datos inválidos.
- `404 Not Found`: recurso inexistente.
- `409 Conflict`: operación incompatible con el estado actual.

Estas respuestas constituyen un contrato propuesto y deberán implementarse y verificarse.

## 3. Escenarios funcionales

### CP-01 — Registrar un libro válido

- **Requisito:** RF-01.
- **Componente:** Libro / controlador REST.
- **Nivel:** Sistema.
- **Tipo:** Funcional, positivo.
- **Técnica:** Partición de equivalencia.
- **Prioridad:** Alta.
- **Precondición:** API disponible y ISBN no registrado.
- **Datos:** título `Clean Code`, autor `Robert C. Martin`, ISBN `9780132350884`, cantidad `5`.
- **Pasos:** enviar una solicitud POST con los datos del libro.
- **Resultado esperado:** respuesta `201`, libro registrado con identificador y disponibilidad de cinco ejemplares.
- **Herramienta prevista:** Postman.
- **Estado:** Pendiente.

### CP-02 — Rechazar ISBN duplicado

- **Requisito:** RF-01.
- **Componente:** Libro / persistencia.
- **Nivel:** Integración y sistema.
- **Tipo:** Funcional, negativo.
- **Técnica:** Predicción de errores.
- **Prioridad:** Alta.
- **Precondición:** existe un libro con el ISBN indicado.
- **Pasos:** intentar registrar otro libro con el mismo ISBN.
- **Resultado esperado:** respuesta `409`; no se crea un registro duplicado.
- **Herramientas previstas:** JUnit y Postman.
- **Estado:** Pendiente.

### CP-03 — Rechazar cantidad negativa

- **Requisito:** RF-01.
- **Componente:** Validación de Libro.
- **Nivel:** Unitario y sistema.
- **Tipo:** Funcional, negativo.
- **Técnica:** Análisis de valores límite.
- **Prioridad:** Alta.
- **Datos:** cantidad `-1`.
- **Pasos:** intentar registrar un libro con cantidad negativa.
- **Resultado esperado:** respuesta `400`; no se registra el libro.
- **Herramientas previstas:** JUnit y Postman.
- **Estado:** Pendiente.

### CP-04 — Consultar el catálogo

- **Requisito:** RF-02.
- **Componente:** Consulta de libros.
- **Nivel:** Sistema.
- **Tipo:** Funcional, positivo.
- **Prioridad:** Media.
- **Precondición:** existen libros registrados.
- **Pasos:** enviar una solicitud GET al catálogo.
- **Resultado esperado:** respuesta `200` y lista JSON con los libros disponibles en el catálogo.
- **Herramienta prevista:** Postman.
- **Estado:** Pendiente.

### CP-05 — Registrar un estudiante válido

- **Requisito:** RF-03.
- **Componente:** Estudiante.
- **Nivel:** Sistema.
- **Tipo:** Funcional, positivo.
- **Prioridad:** Alta.
- **Datos:** identificación `1000123456`, nombre `Ana Pérez`, correo `ana@example.com`.
- **Pasos:** enviar una solicitud POST para registrar al estudiante.
- **Resultado esperado:** respuesta `201` y registro creado.
- **Herramienta prevista:** Postman.
- **Estado:** Pendiente.

### CP-06 — Rechazar estudiante con correo duplicado

- **Requisito:** RF-03.
- **Componente:** Estudiante / persistencia.
- **Nivel:** Integración y sistema.
- **Tipo:** Funcional, negativo.
- **Prioridad:** Alta.
- **Precondición:** existe un estudiante con el correo indicado.
- **Pasos:** registrar otro estudiante con el mismo correo.
- **Resultado esperado:** respuesta `409` y ausencia de duplicados.
- **Herramientas previstas:** JUnit y Postman.
- **Estado:** Pendiente.

### CP-07 — Registrar préstamo con disponibilidad

- **Requisito:** RF-04.
- **Componente:** Servicio de préstamos.
- **Nivel:** Integración y sistema.
- **Tipo:** Funcional, positivo.
- **Prioridad:** Crítica.
- **Precondición:** estudiante registrado y libro con dos ejemplares disponibles.
- **Pasos:** solicitar un préstamo válido.
- **Resultado esperado:** préstamo registrado, fecha de vencimiento asociada y disponibilidad reducida de dos a uno.
- **Herramientas previstas:** JUnit y Postman.
- **Estado:** Pendiente.

### CP-08 — Impedir préstamo sin disponibilidad

- **Requisito:** RF-05.
- **Componente:** Servicio de préstamos.
- **Nivel:** Unitario y sistema.
- **Tipo:** Funcional, negativo.
- **Técnica:** Valor límite cero.
- **Prioridad:** Crítica.
- **Precondición:** libro con cero ejemplares disponibles.
- **Pasos:** solicitar el préstamo.
- **Resultado esperado:** respuesta `409`; no se registra préstamo ni se modifica la disponibilidad.
- **Herramientas previstas:** JUnit y Postman.
- **Estado:** Pendiente.

### CP-09 — Prestar el último ejemplar

- **Requisitos:** RF-04 y RF-05.
- **Componente:** Servicio de préstamos.
- **Nivel:** Unitario e integración.
- **Tipo:** Funcional, límite.
- **Técnica:** Análisis de valores límite.
- **Prioridad:** Crítica.
- **Precondición:** libro con exactamente un ejemplar disponible.
- **Pasos:** registrar un préstamo válido.
- **Resultado esperado:** préstamo creado y disponibilidad igual a cero, nunca negativa.
- **Herramienta prevista:** JUnit.
- **Estado:** Pendiente.

### CP-10 — Registrar devolución

- **Requisito:** RF-06.
- **Componente:** Servicio de devoluciones.
- **Nivel:** Integración y sistema.
- **Tipo:** Funcional, positivo.
- **Prioridad:** Crítica.
- **Precondición:** préstamo activo y libro con cero ejemplares disponibles.
- **Pasos:** registrar la devolución.
- **Resultado esperado:** se registra la fecha de devolución y la disponibilidad aumenta a uno.
- **Herramientas previstas:** JUnit y Postman.
- **Estado:** Pendiente.

### CP-11 — Impedir devolución duplicada

- **Requisito:** RF-06.
- **Componente:** Servicio de devoluciones.
- **Nivel:** Unitario y sistema.
- **Tipo:** Funcional, negativo.
- **Técnica:** Transición de estados.
- **Prioridad:** Crítica.
- **Precondición:** préstamo ya devuelto.
- **Pasos:** intentar devolverlo nuevamente.
- **Resultado esperado:** respuesta `409`; la disponibilidad no aumenta por segunda vez.
- **Herramientas previstas:** JUnit y Postman.
- **Estado:** Pendiente.

### CP-12 — Consultar préstamos activos de un estudiante

- **Requisito:** RF-07.
- **Componente:** Consulta de préstamos.
- **Nivel:** Sistema.
- **Tipo:** Funcional, positivo.
- **Prioridad:** Media.
- **Precondición:** estudiante con un préstamo activo y uno devuelto.
- **Pasos:** consultar sus préstamos activos.
- **Resultado esperado:** respuesta `200` con únicamente el préstamo activo.
- **Herramienta prevista:** Postman.
- **Estado:** Pendiente.

## 4. Escenarios del componente innovador

### CP-13 — Identificar préstamo próximo a vencer

- **Requisito:** RF-08.
- **Componente:** Alertas de vencimiento.
- **Nivel:** Unitario.
- **Tipo:** Funcional, positivo.
- **Técnica:** Análisis de valores límite.
- **Prioridad:** Media.
- **Precondición:** préstamo activo con vencimiento dentro de tres días.
- **Pasos:** ejecutar la consulta de préstamos próximos a vencer.
- **Resultado esperado:** el préstamo se identifica como próximo a vencer.
- **Herramienta prevista:** JUnit.
- **Estado:** Pendiente.

### CP-14 — Excluir préstamo devuelto de alertas

- **Requisito:** RF-08.
- **Componente:** Alertas de vencimiento.
- **Nivel:** Unitario.
- **Tipo:** Funcional, negativo.
- **Técnica:** Transición de estados.
- **Prioridad:** Media.
- **Precondición:** préstamo devuelto cuya fecha de vencimiento está dentro de tres días.
- **Pasos:** ejecutar la consulta de préstamos próximos a vencer.
- **Resultado esperado:** el préstamo no aparece entre los pendientes de vencimiento.
- **Herramienta prevista:** JUnit.
- **Estado:** Pendiente.

## 5. Escenarios no funcionales

### CP-15 — Rendimiento de consultas

- **Requisito:** RNF-01.
- **Característica:** Eficiencia de desempeño.
- **Nivel:** Sistema.
- **Tipo:** No funcional.
- **Prioridad:** Media.
- **Precondición:** API disponible y conjunto de datos de prueba definido.
- **Pasos:** ejecutar consultas concurrentes con 50 usuarios virtuales.
- **Resultado esperado propuesto:** percentil 95 de tiempo de respuesta inferior a dos segundos, sin errores de servidor.
- **Herramienta prevista:** herramienta de carga, como JMeter.
- **Estado:** Pendiente. No se ha realizado medición de rendimiento.

### CP-16 — Acceso no autorizado

- **Requisito:** RNF-02.
- **Característica:** Seguridad.
- **Nivel:** Sistema.
- **Tipo:** No funcional, negativo.
- **Prioridad:** Alta.
- **Precondición:** existe un endpoint protegido y se ha implementado autenticación.
- **Pasos:** enviar una petición sin credenciales válidas.
- **Resultado esperado propuesto:** acceso rechazado mediante `401` o `403`, según corresponda.
- **Herramienta prevista:** Postman.
- **Estado:** Pendiente. El mecanismo de autenticación todavía no está implementado.

### CP-17 — Consistencia ante préstamos simultáneos

- **Requisito:** RNF-03.
- **Característica:** Fiabilidad e integridad de datos.
- **Nivel:** Integración.
- **Tipo:** No funcional.
- **Prioridad:** Crítica.
- **Precondición:** libro con un único ejemplar disponible.
- **Pasos:** ejecutar dos solicitudes de préstamo simultáneas.
- **Resultado esperado:** solamente una operación tiene éxito; la disponibilidad final es cero y no se generan dos préstamos activos sobre el mismo ejemplar disponible.
- **Herramienta prevista:** prueba automatizada de concurrencia.
- **Estado:** Pendiente.

## 6. Matriz de trazabilidad

| Requisito | Escenarios asociados |
|---|---|
| RF-01 | CP-01, CP-02, CP-03 |
| RF-02 | CP-04 |
| RF-03 | CP-05, CP-06 |
| RF-04 | CP-07, CP-09 |
| RF-05 | CP-08, CP-09 |
| RF-06 | CP-10, CP-11 |
| RF-07 | CP-12 |
| RF-08 | CP-13, CP-14 |
| RNF-01 | CP-15 |
| RNF-02 | CP-16 |
| RNF-03 | CP-17 |

## 7. Registro de ejecución

Cada escenario deberá registrar:

- Fecha de ejecución.
- Responsable.
- Ambiente utilizado.
- Datos de entrada.
- Resultado esperado.
- Resultado obtenido.
- Estado: aprobado, fallido, bloqueado o pendiente.
- Evidencia, cuando corresponda.
- Identificador del defecto detectado, si existe.

Los escenarios anteriores representan el diseño de las pruebas. No deben considerarse aprobados hasta contar con su ejecución y evidencia correspondiente.

## 8. Conclusión

Los 17 escenarios propuestos permiten cubrir las operaciones principales del sistema, condiciones negativas, valores límite, estados de los préstamos, riesgos de integridad, características no funcionales y el componente innovador.

La ejecución progresiva de estas pruebas permitirá evaluar la calidad de BiblioUni y detectar problemas antes de su uso.