# Casos de prueba formales — Unidad 2

Identificador BU-U2-CP, versión 1.0. Responsable individual: Nicolás Pirajan. Fecha de ejecución: ver metadatos de la ejecución final. Los identificadores U2-CP no sustituyen los CP de Unidad 1. El CSV casos_unidad2.csv es la fuente editable; este documento se genera y contrasta con XML reales de Surefire.

## Condiciones y procedimiento común

Para cada caso unitario se crea un servicio con MockitoExtension, repositorios nuevos y los datos indicados. Las pruebas H2 usan perfil test y preparan libro/estudiante persistidos; cada prueba de préstamos elimina únicamente sus propios datos. Las pruebas HTTP usan MockMvc, el contexto Spring y rollback del caso. Son pruebas locales sin datos personales reales ni un servidor externo.

Preparación: disponer de JDK 21 y dependencias del proyecto. Ejecución: .mvnw.cmd clean verify. Alternativa con conservación: .scriptsejecutar-pruebas.ps1 -Nombre nueva-ejecucion -Objetivos clean,verify. Consultar la clase y método indicados para repetir un caso individual. En parametrizados, todas sus invocaciones deben aprobar. La prioridad es alta para integridad, validación y multa; media para consultas y estado.

## U2-CP-001 — Registro válido de libro

Requisito: RF-01. Nivel: Unitario. Técnica: Partición válida.

- Precondiciones y entradas: Clean Code, Robert C. Martin, ISBN 9780132350884, cantidad 5; ISBN no registrado.
- Pasos: Invocar registrarLibro y verificar retorno y escritura.
- Resultado esperado: Mismo libro retornado; repositorio save invocado una vez.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeRegistrarLibroValido; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-002 — Libro sin campos obligatorios

Requisito: RF-01,RF-10. Nivel: Unitario. Técnica: Particiones inválidas.

- Precondiciones y entradas: Libro null o título/autor/ISBN null o blanco; siete variantes.
- Pasos: Invocar registrarLibro con cada variante.
- Resultado esperado: IllegalArgumentException; ninguna interacción con repositorio.
- Resultado obtenido: aprobado; 7 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeRechazarLibroIncompleto; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-003 — Cantidad negativa

Requisito: RF-01. Nivel: Unitario. Técnica: Partición inválida.

- Precondiciones y entradas: Libro completo con cantidad -5.
- Pasos: Invocar registrarLibro.
- Resultado esperado: IllegalArgumentException con mensaje de cantidad negativa; no guardar.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeRechazarCantidadNegativa; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-004 — Cantidad cero permitida

Requisito: RF-01. Nivel: Unitario. Técnica: Valor límite.

- Precondiciones y entradas: Libro completo, cantidad 0, ISBN nuevo.
- Pasos: Registrar y consultar cantidad del retorno.
- Resultado esperado: Registro aceptado con cantidad 0; guardar una vez.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeRegistrarLibroConCeroEjemplares; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-005 — ISBN duplicado

Requisito: RF-01. Nivel: Unitario. Técnica: Partición inválida.

- Precondiciones y entradas: Libro completo; existsByIsbn retorna true.
- Pasos: Intentar registrar y observar excepción.
- Resultado esperado: IllegalStateException con mensaje de duplicado; no guardar.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeRechazarIsbnDuplicado; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-006 — Búsqueda de libro existente

Requisito: RF-02. Nivel: Unitario. Técnica: Partición válida.

- Precondiciones y entradas: ID 1; repositorio contiene el libro.
- Pasos: Invocar buscarPorId(1).
- Resultado esperado: Optional presente con el libro esperado.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeBuscarLibroPorIdExistente; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-007 — Búsqueda de libro inexistente

Requisito: RF-02. Nivel: Unitario. Técnica: Partición válida sin coincidencia.

- Precondiciones y entradas: ID 99; repositorio devuelve vacío.
- Pasos: Invocar buscarPorId(99).
- Resultado esperado: Optional.empty sin excepción.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeDevolverVacioCuandoIdNoExiste; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-008 — ID de libro inválido

Requisito: RF-10. Nivel: Unitario. Técnica: Límites y nulos.

- Precondiciones y entradas: ID null, 0 y -1.
- Pasos: Buscar cada identificador.
- Resultado esperado: IllegalArgumentException; sin acceder al repositorio.
- Resultado obtenido: aprobado; 3 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeRechazarIdentificadorInvalido; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-009 — Listado con registros

Requisito: RF-02. Nivel: Unitario. Técnica: Partición válida.

- Precondiciones y entradas: Repositorio retorna una lista con un libro.
- Pasos: Invocar listarLibros.
- Resultado esperado: Lista de tamaño 1 con el mismo libro.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeListarLibrosRegistrados; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-010 — Catálogo vacío

Requisito: RF-02. Nivel: Unitario. Técnica: Valor límite.

- Precondiciones y entradas: Repositorio retorna lista vacía.
- Pasos: Invocar listarLibros.
- Resultado esperado: Lista vacía sin error.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: LibroServiceTest.debeListarCatalogoVacio; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.LibroServiceTest.xml.

## U2-CP-011 — Préstamo con disponibilidad

Requisito: RF-04. Nivel: Unitario. Técnica: Partición válida.

- Precondiciones y entradas: Libro con 2; estudiante; inicio 2026-11-14; vencimiento 2026-11-21.
- Pasos: Registrar préstamo y verificar ambos repositorios.
- Resultado esperado: Préstamo retornado; disponibilidad 1; libro y préstamo guardados una vez.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRegistrarPrestamoCuandoHayDisponibilidad; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-012 — Límite del último ejemplar

Requisito: RF-04,RF-05. Nivel: Unitario. Técnica: Valores límite.

- Precondiciones y entradas: Disponibilidad inicial 1 y 2; préstamo válido.
- Pasos: Registrar una vez en cada variante.
- Resultado esperado: Disponibilidad inicial menos 1; una escritura por repositorio.
- Resultado obtenido: aprobado; 2 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeDescontarUnSoloEjemplar; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-013 — Préstamo con stock cero

Requisito: RF-05. Nivel: Unitario. Técnica: Valor límite negativo.

- Precondiciones y entradas: Libro con 0; demás datos válidos.
- Pasos: Intentar registrar.
- Resultado esperado: IllegalStateException: No hay ejemplares disponibles; ningún save.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarPrestamoSinDisponibilidad; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-014 — Préstamo con stock negativo

Requisito: RF-05. Nivel: Unitario. Técnica: Partición inválida.

- Precondiciones y entradas: Libro con -1; demás datos válidos.
- Pasos: Intentar registrar.
- Resultado esperado: IllegalStateException; sin interacción con repositorios.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarDisponibilidadNegativa; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-015 — Préstamo incompleto o fechas invertidas

Requisito: RF-04,RF-10. Nivel: Unitario. Técnica: Particiones inválidas.

- Precondiciones y entradas: Préstamo null; libro/estudiante null; inicio/vencimiento null; vencimiento anterior al inicio.
- Pasos: Invocar registrarPrestamo con cada variante.
- Resultado esperado: IllegalArgumentException; ninguna escritura ni consulta.
- Resultado obtenido: aprobado; 6 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarPrestamoInvalido; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-016 — Vencimiento igual al inicio

Requisito: RF-04,RF-10. Nivel: Unitario. Técnica: Valor límite.

- Precondiciones y entradas: Inicio y vencimiento 2026-11-01; stock 2.
- Pasos: Registrar préstamo.
- Resultado esperado: Registro permitido; stock 1.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeAceptarVencimientoElMismoDia; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-017 — Rechazo de registro ya devuelto

Requisito: RF-04,RF-06. Nivel: Unitario. Técnica: Transición inválida.

- Precondiciones y entradas: Préstamo con fechaDevolucion 2026-11-08.
- Pasos: Intentar registrar como nuevo.
- Resultado esperado: IllegalStateException; ninguna interacción con repositorios.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarRegistroYaDevuelto; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-018 — Rechazo de registro con ID existente

Requisito: RF-04. Nivel: Unitario. Técnica: Partición inválida.

- Precondiciones y entradas: Préstamo con ID 1 y relaciones completas.
- Pasos: Intentar registrar como nuevo.
- Resultado esperado: IllegalStateException; ninguna interacción con repositorios.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarRegistroConIdExistente; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-019 — Búsqueda de préstamo existente

Requisito: RF-04. Nivel: Unitario. Técnica: Partición válida.

- Precondiciones y entradas: ID 1; préstamo presente.
- Pasos: Invocar buscarPorId(1).
- Resultado esperado: Optional presente con el préstamo esperado.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeBuscarPrestamoExistente; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-020 — Búsqueda de préstamo inexistente

Requisito: RF-04. Nivel: Unitario. Técnica: Partición sin coincidencia.

- Precondiciones y entradas: ID 99 sin préstamo.
- Pasos: Invocar buscarPorId(99).
- Resultado esperado: Optional vacío.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeDevolverVacioParaPrestamoInexistente; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-021 — ID de préstamo inválido

Requisito: RF-10. Nivel: Unitario. Técnica: Límites y nulos.

- Precondiciones y entradas: ID null, 0 y -1.
- Pasos: Intentar búsqueda y devolución para cada ID.
- Resultado esperado: IllegalArgumentException; ninguna interacción con repositorios.
- Resultado obtenido: aprobado; 3 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarIdentificadorInvalido; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-022 — Multa de tres días

Requisito: RF-09. Nivel: Unitario. Técnica: Partición de retraso.

- Precondiciones y entradas: Vencimiento 2026-11-08; devolución 2026-11-11.
- Pasos: Invocar calcularMulta.
- Resultado esperado: 6000 COP.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeCalcularMultaPorDevolucionTardia; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-023 — Límites de multa

Requisito: RF-09. Nivel: Unitario. Técnica: Valores límite y particiones.

- Precondiciones y entradas: Devolución a -1, 0, 1, 3 y 30 días del vencimiento; inicio anterior.
- Pasos: Calcular para cada diferencia.
- Resultado esperado: 0, 0, 2000, 6000 y 60000 COP respectivamente.
- Resultado obtenido: aprobado; 5 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeCalcularMultaEnLimites; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-024 — Datos de multa inválidos

Requisito: RF-09,RF-10. Nivel: Unitario. Técnica: Particiones inválidas.

- Precondiciones y entradas: Préstamo/inicio/vencimiento/devolución null; fechas invertidas; devolución antes del inicio.
- Pasos: Calcular para seis variantes.
- Resultado esperado: IllegalArgumentException; ninguna interacción con repositorios.
- Resultado obtenido: aprobado; 6 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarDatosInvalidosDeMulta; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-025 — Devolución exitosa con multa

Requisito: RF-06,RF-09. Nivel: Unitario. Técnica: Transición de estado.

- Precondiciones y entradas: Préstamo activo; stock 0; vencimiento 2026-11-08; devolución 2026-11-11.
- Pasos: Registrar devolución; verificar fecha, stock, persistencia y multa.
- Resultado esperado: Fecha guardada; stock 1; ambos saves una vez; multa 6000 COP.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRegistrarDevolucionYRecuperarDisponibilidad; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-026 — Devolución duplicada

Requisito: RF-06. Nivel: Unitario. Técnica: Transición inválida.

- Precondiciones y entradas: Préstamo devuelto el 2026-11-08; stock 2; nueva devolución el día 9.
- Pasos: Intentar devolver otra vez.
- Resultado esperado: IllegalStateException; fecha original y stock conservados; ningún save.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarDevolucionDuplicada; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-027 — Devolución de préstamo inexistente

Requisito: RF-06. Nivel: Unitario. Técnica: Partición inválida.

- Precondiciones y entradas: ID 99; repositorio vacío.
- Pasos: Intentar devolución el 2026-11-08.
- Resultado esperado: IllegalArgumentException; ningún save.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarDevolucionInexistente; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-028 — Fecha de devolución inválida

Requisito: RF-06,RF-10. Nivel: Unitario. Técnica: Límite y nulo.

- Precondiciones y entradas: Préstamo inicia 2026-11-01; devolución null o 2026-10-31.
- Pasos: Intentar registrar devolución.
- Resultado esperado: IllegalArgumentException; fechaDevolucion null; stock 2; ningún save.
- Resultado obtenido: aprobado; 2 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarFechaDevolucionInvalida; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-029 — Desbordamiento de disponibilidad

Requisito: RF-06. Nivel: Unitario. Técnica: Valor límite.

- Precondiciones y entradas: Préstamo activo; cantidad Integer.MAX_VALUE.
- Pasos: Intentar devolver el 2026-11-08.
- Resultado esperado: IllegalStateException; fecha y cantidad sin cambios; ningún save.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarDesbordamientoDeDisponibilidad; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-030 — Disponibilidad persistida autorizada

Requisito: RF-04,RF-05. Nivel: Unitario. Técnica: Partición válida.

- Precondiciones y entradas: Referencia Libro con ID 1; repositorio retorna libro con stock 2.
- Pasos: Registrar con la referencia separada.
- Resultado esperado: Se asocia el libro del repositorio y su stock disminuye a 1.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeUsarDisponibilidadPersistida; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-031 — Libro referenciado inexistente

Requisito: RF-04,RF-10. Nivel: Unitario. Técnica: Partición inválida.

- Precondiciones y entradas: Referencia Libro con ID 99; búsqueda vacía.
- Pasos: Intentar préstamo.
- Resultado esperado: IllegalArgumentException; no guardar libro ni préstamo.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceTest.debeRechazarLibroReferenciadoInexistente; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceTest.xml.

## U2-CP-032 — Persistencia del ciclo completo

Requisito: RF-04,RF-06,RF-09. Nivel: Integración H2. Técnica: Transición de estado.

- Precondiciones y entradas: Libro y estudiante persistidos; stock 1; inicio 2026-11-01; vence día 8.
- Pasos: Prestar, leer stock, devolver día 11, releer fecha y stock, intentar devolución repetida.
- Resultado esperado: ID asignado; stock 0 luego 1; fecha persistida; multa 6000; repetición rechazada sin incrementar.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceIntegrationTest.debePersistirPrestamoDevolucionYMulta; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceIntegrationTest.xml.

## U2-CP-033 — Rollback de préstamo inválido en JPA

Requisito: RNF-U2-01. Nivel: Integración H2. Técnica: Inyección de fallo de persistencia.

- Precondiciones y entradas: Libro persistido con stock 1; estudiante transitorio sin ID.
- Pasos: Intentar préstamo y leer de nuevo base de datos tras la excepción.
- Resultado esperado: Excepción de persistencia; stock permanece 1 y no hay préstamo guardado.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceIntegrationTest.debeRevertirDescuentoSiFallaPersistenciaDelPrestamo; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceIntegrationTest.xml.

## U2-CP-034 — Devolución inválida conserva datos H2

Requisito: RF-06,RNF-U2-01. Nivel: Integración H2. Técnica: Escenario negativo.

- Precondiciones y entradas: Préstamo activo persistido; devolución un día antes del inicio.
- Pasos: Intentar devolución y releer entidades.
- Resultado esperado: IllegalArgumentException; fecha null; stock 0.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceIntegrationTest.debeConservarEstadoAnteDevolucionConFechaInvalida; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceIntegrationTest.xml.

## U2-CP-035 — Préstamo persistido no se registra dos veces

Requisito: RF-04. Nivel: Integración H2. Técnica: Transición inválida.

- Precondiciones y entradas: Préstamo ya registrado con ID; stock 0.
- Pasos: Volver a enviar el mismo préstamo al registro.
- Resultado esperado: IllegalStateException; stock guardado sigue en 0.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceIntegrationTest.debeImpedirRegistrarOtraVezUnPrestamoPersistido; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceIntegrationTest.xml.

## U2-CP-036 — Referencia con stock anterior

Requisito: RF-05. Nivel: Integración H2. Técnica: Escenario negativo.

- Precondiciones y entradas: Libro separado de JPA conserva stock 1 después del primer préstamo.
- Pasos: Crear otro préstamo con esa referencia y releer stock.
- Resultado esperado: Segundo préstamo rechazado; stock real 0.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: PrestamoServiceIntegrationTest.debeRechazarSegundoPrestamoAunqueReferenciaTengaStockAnterior; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.service.PrestamoServiceIntegrationTest.xml.

## U2-CP-037 — Estado de la API anterior

Requisito: REG-U1. Nivel: Integración HTTP/H2. Técnica: Regresión positiva.

- Precondiciones y entradas: Contexto web activo; GET /api/estado.
- Pasos: Realizar GET con MockMvc.
- Resultado esperado: 200; aplicacion BiblioUni y estado Disponible.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: RegresionApiTest.debeConservarConsultaEstado; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.controller.RegresionApiTest.xml.

## U2-CP-038 — Registro listado y duplicado por API

Requisito: REG-U1,RF-01,RF-02. Nivel: Integración HTTP/H2. Técnica: Tabla de decisión.

- Precondiciones y entradas: JSON completo ISBN HTTP-U2 y cantidad 1; sin ISBN duplicado inicialmente.
- Pasos: POST, GET libros, repetir POST.
- Resultado esperado: 201 con ID numérico; 200 con el libro; 409 con mensaje de ISBN duplicado.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: RegresionApiTest.debeRegistrarListarYRechazarDuplicado; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.controller.RegresionApiTest.xml.

## U2-CP-039 — Validación semántica por API

Requisito: REG-U1,RF-10. Nivel: Integración HTTP/H2. Técnica: Partición inválida.

- Precondiciones y entradas: JSON con título blanco y demás campos completos.
- Pasos: POST /api/libros.
- Resultado esperado: 400 y error El libro debe tener titulo, autor e ISBN.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: RegresionApiTest.debeResponder400ParaLibroIncompleto; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.controller.RegresionApiTest.xml.

## U2-CP-040 — Cuerpo no deserializable por API

Requisito: REG-U1,RF-10. Nivel: Integración HTTP/H2. Técnica: Partición inválida.

- Precondiciones y entradas: Cuerpo JSON vacío de propiedades: {}; falta cantidad primitiva exigida por deserialización.
- Pasos: POST /api/libros.
- Resultado esperado: 400 y error El cuerpo de la solicitud no es valido.
- Resultado obtenido: aprobado; 1 invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.
- Automatización y trazabilidad: RegresionApiTest.debeResponder400ParaCuerpoNoDeserializable; evidencia evidencias/unidad2/11-final/TEST-com.bibliouni.bibliouni.controller.RegresionApiTest.xml.

## Particiones de equivalencia y valores límite

| Variable | Particiones | Representantes y límites ejecutados |
|---|---|---|
| Libro y textos | Completo / null / blanco | Libro válido; libro null; título, autor e ISBN null o blancos |
| Cantidad de registro | Negativa / cero / positiva | -5, 0, 5 |
| Disponibilidad de préstamo | No disponible / disponible | -1, 0, 1, 2; límite operativo entre 0 y 1 |
| Identificador | Inválido / existente / ausente | null, -1, 0; 1; 99 |
| Fechas del préstamo | Faltantes / invertidas / iguales / ordenadas | null; vencimiento inicio-1; mismo día; inicio+7 |
| Retraso de devolución | Anticipado / puntual / tardío | Vencimiento-1, vencimiento, vencimiento+1, +3 y +30 |
| Estado del préstamo | Nuevo / persistido / devuelto | Sin ID ni devolución; con ID; fecha de devolución presente |
| Incremento de stock | Entero admitido / desbordamiento | 0→1; Integer.MAX_VALUE rechazado antes del incremento |

El caso -1 día respecto al vencimiento conserva una fecha posterior al inicio. La devolución anterior al inicio pertenece a otra partición inválida. Estos datos son representantes, no una afirmación de haber ensayado todos los enteros o fechas posibles.

## Tabla de decisión: registro de préstamo

Se aplica de arriba abajo. Guion significa condición irrelevante para esa regla.

| Condición o acción | P1 | P2 | P3 | P4 | P5 |
|---|---|---|---|---|---|
| Libro/estudiante/objeto presentes | No | Sí | Sí | Sí | Sí |
| Préstamo nuevo y activo | - | No | Sí | Sí | Sí |
| Fechas completas y ordenadas | - | - | No | Sí | Sí |
| Stock real mayor a cero | - | - | - | No | Sí |
| Resultado | Argumento inválido | Conflicto | Argumento inválido | Conflicto | Guardar, descontar 1 |
| Casos | 015 | 017,018 | 015 | 013,014,036 | 011,012,016,030 |

Además, un libro con ID no encontrado se rechaza como argumento inválido (031). Una referencia con ID existente se reemplaza por el libro leído del repositorio (030 y 036).

## Tabla de decisión: devolución y multa

| Regla | ID/préstamo | Estado | Fecha | Stock | Resultado y caso |
|---|---|---|---|---|---|
| D1 | ID inválido | - | - | - | Argumento inválido; 021 |
| D2 | Préstamo ausente | - | - | - | Argumento inválido; 027 |
| D3 | Existe | Devuelto | - | - | Conflicto, no incrementar; 026 |
| D4 | Existe | Activo | null o anterior al inicio | - | Argumento inválido, no modificar; 028 |
| D5 | Existe | Activo | Válida | Integer.MAX_VALUE | Conflicto, no desbordar; 029 |
| D6 | Existe | Activo | Hasta vencimiento | Admite incremento | Incrementar 1; multa 0 (regla de cálculo probada en 023) |
| D7 | Existe | Activo | Vencimiento+3 | Admite incremento | Incrementar 1; multa 6000; 025,032 |

La combinación D6 de devolución y multa puntual se cubre por las unidades de ambas reglas; la integración persistida completa se ejecutó con devolución tardía (032). No se inventa una ejecución integrada puntual.

## Matriz de trazabilidad y resultados

| Caso | Requisito | Clase y método verificables | Estado |
|---|---|---|---|
| U2-CP-001 | RF-01 | LibroServiceTest.debeRegistrarLibroValido | Aprobado |
| U2-CP-002 | RF-01,RF-10 | LibroServiceTest.debeRechazarLibroIncompleto | Aprobado |
| U2-CP-003 | RF-01 | LibroServiceTest.debeRechazarCantidadNegativa | Aprobado |
| U2-CP-004 | RF-01 | LibroServiceTest.debeRegistrarLibroConCeroEjemplares | Aprobado |
| U2-CP-005 | RF-01 | LibroServiceTest.debeRechazarIsbnDuplicado | Aprobado |
| U2-CP-006 | RF-02 | LibroServiceTest.debeBuscarLibroPorIdExistente | Aprobado |
| U2-CP-007 | RF-02 | LibroServiceTest.debeDevolverVacioCuandoIdNoExiste | Aprobado |
| U2-CP-008 | RF-10 | LibroServiceTest.debeRechazarIdentificadorInvalido | Aprobado |
| U2-CP-009 | RF-02 | LibroServiceTest.debeListarLibrosRegistrados | Aprobado |
| U2-CP-010 | RF-02 | LibroServiceTest.debeListarCatalogoVacio | Aprobado |
| U2-CP-011 | RF-04 | PrestamoServiceTest.debeRegistrarPrestamoCuandoHayDisponibilidad | Aprobado |
| U2-CP-012 | RF-04,RF-05 | PrestamoServiceTest.debeDescontarUnSoloEjemplar | Aprobado |
| U2-CP-013 | RF-05 | PrestamoServiceTest.debeRechazarPrestamoSinDisponibilidad | Aprobado |
| U2-CP-014 | RF-05 | PrestamoServiceTest.debeRechazarDisponibilidadNegativa | Aprobado |
| U2-CP-015 | RF-04,RF-10 | PrestamoServiceTest.debeRechazarPrestamoInvalido | Aprobado |
| U2-CP-016 | RF-04,RF-10 | PrestamoServiceTest.debeAceptarVencimientoElMismoDia | Aprobado |
| U2-CP-017 | RF-04,RF-06 | PrestamoServiceTest.debeRechazarRegistroYaDevuelto | Aprobado |
| U2-CP-018 | RF-04 | PrestamoServiceTest.debeRechazarRegistroConIdExistente | Aprobado |
| U2-CP-019 | RF-04 | PrestamoServiceTest.debeBuscarPrestamoExistente | Aprobado |
| U2-CP-020 | RF-04 | PrestamoServiceTest.debeDevolverVacioParaPrestamoInexistente | Aprobado |
| U2-CP-021 | RF-10 | PrestamoServiceTest.debeRechazarIdentificadorInvalido | Aprobado |
| U2-CP-022 | RF-09 | PrestamoServiceTest.debeCalcularMultaPorDevolucionTardia | Aprobado |
| U2-CP-023 | RF-09 | PrestamoServiceTest.debeCalcularMultaEnLimites | Aprobado |
| U2-CP-024 | RF-09,RF-10 | PrestamoServiceTest.debeRechazarDatosInvalidosDeMulta | Aprobado |
| U2-CP-025 | RF-06,RF-09 | PrestamoServiceTest.debeRegistrarDevolucionYRecuperarDisponibilidad | Aprobado |
| U2-CP-026 | RF-06 | PrestamoServiceTest.debeRechazarDevolucionDuplicada | Aprobado |
| U2-CP-027 | RF-06 | PrestamoServiceTest.debeRechazarDevolucionInexistente | Aprobado |
| U2-CP-028 | RF-06,RF-10 | PrestamoServiceTest.debeRechazarFechaDevolucionInvalida | Aprobado |
| U2-CP-029 | RF-06 | PrestamoServiceTest.debeRechazarDesbordamientoDeDisponibilidad | Aprobado |
| U2-CP-030 | RF-04,RF-05 | PrestamoServiceTest.debeUsarDisponibilidadPersistida | Aprobado |
| U2-CP-031 | RF-04,RF-10 | PrestamoServiceTest.debeRechazarLibroReferenciadoInexistente | Aprobado |
| U2-CP-032 | RF-04,RF-06,RF-09 | PrestamoServiceIntegrationTest.debePersistirPrestamoDevolucionYMulta | Aprobado |
| U2-CP-033 | RNF-U2-01 | PrestamoServiceIntegrationTest.debeRevertirDescuentoSiFallaPersistenciaDelPrestamo | Aprobado |
| U2-CP-034 | RF-06,RNF-U2-01 | PrestamoServiceIntegrationTest.debeConservarEstadoAnteDevolucionConFechaInvalida | Aprobado |
| U2-CP-035 | RF-04 | PrestamoServiceIntegrationTest.debeImpedirRegistrarOtraVezUnPrestamoPersistido | Aprobado |
| U2-CP-036 | RF-05 | PrestamoServiceIntegrationTest.debeRechazarSegundoPrestamoAunqueReferenciaTengaStockAnterior | Aprobado |
| U2-CP-037 | REG-U1 | RegresionApiTest.debeConservarConsultaEstado | Aprobado |
| U2-CP-038 | REG-U1,RF-01,RF-02 | RegresionApiTest.debeRegistrarListarYRechazarDuplicado | Aprobado |
| U2-CP-039 | REG-U1,RF-10 | RegresionApiTest.debeResponder400ParaLibroIncompleto | Aprobado |
| U2-CP-040 | REG-U1,RF-10 | RegresionApiTest.debeResponder400ParaCuerpoNoDeserializable | Aprobado |

Total: 40 casos formales; la suite completa ejecutó 70 invocaciones. Los casos parametrizados representan varias invocaciones y algunos métodos originales de regresión no tienen un caso formal adicional. No se suman estos dos conteos como si fueran pruebas diferentes.

Fuera de alcance y pendientes: RF-03, RF-07, RF-08, carga, seguridad y concurrencia de Unidad 1 conservan su estado en escenarios.md. No se declaran aprobados por estos resultados.
