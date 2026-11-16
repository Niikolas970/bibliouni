# Informe final — Pruebas de Software, Unidad 2

Proyecto: BiblioUni. Modalidad: trabajo individual. Responsable: Nicolás Pirajan, según identidad local de Git. Fecha de la ejecución final: 2026-11-14T12:30:33.0680403-05:00. Rama: feature/plan-tests. Código verificado: 6eaba6812721980b32a4be5d73bcd8baa1768de3. Documento listo para revisión académica; no contiene firmas ni aprobaciones atribuidas al docente.

## 1. Introducción

En esta unidad se amplió la verificación del sistema de biblioteca BiblioUni. Se conservaron las pruebas y documentos de Unidad 1, se corrigió la compilación de la prueba de préstamos y se completaron las reglas necesarias para devolver libros y calcular multas. El trabajo incluye pruebas automatizadas, dos ciclos TDD con resultados reales y documentación que permite repetir la ejecución.

## 2. Objetivos y alcance

El objetivo general fue comprobar que las operaciones de libros, préstamos, devoluciones y multas responden correctamente a datos válidos e inválidos. Se verificaron registro y búsqueda, disponibilidad, fechas, duplicados, recuperación de ejemplares y multa diaria. Se añadieron comprobaciones con H2 y regresión HTTP de la API existente. Las nuevas operaciones de préstamos permanecen en el servicio Java; no se implementó una interfaz web ni una API nueva para esas operaciones.

## 3. Ambiente y metodología

Se utilizó Windows, Java Temurin 21.0.12, Maven Wrapper 3.10.0, Spring Boot 4.1.2, Mockito 5.23.0, H2 2.4.240 y JaCoCo 0.8.14. El classpath real utiliza JUnit Jupiter 6.0.3, administrado por Spring Boot, aunque el planteamiento inicial indicaba JUnit 5. Se conserva esa versión porque Spring Framework 7 establece JUnit 6 como mínimo. Las pruebas mantienen el modelo Jupiter y sus anotaciones habituales. Esta diferencia queda declarada para revisión de la rúbrica.

Se aplicaron particiones de equivalencia, valores límite, tablas de decisión y transición de estados. Mockito permitió aislar reglas y verificar la ausencia de escrituras en escenarios negativos. H2 permitió comprobar persistencia y reversión de cambios. MockMvc comprobó el contrato HTTP dentro del contexto Spring. No se realizaron capturas nuevas: la evidencia consiste en logs completos, XML y reportes generados.

El plan adjunto sigue las 16 secciones de IEEE 829-1998 como estructura académica. El catálogo contiene 40 casos formales trazados a métodos ejecutados. Los identificadores de Unidad 1 se conservan y los casos nuevos usan el prefijo U2-CP.

## 4. Reglas de negocio y cambios

- Libro: título, autor e ISBN obligatorios; cantidad no negativa; ISBN único. Se admite cantidad cero y no se añade una restricción de formato ISBN ajena al alcance original.
- Búsquedas: ID positivo; ausencia expresada con Optional vacío en libros y préstamos.
- Préstamo: libro y estudiante obligatorios; fechas completas; vencimiento igual o posterior al inicio; solo registros nuevos y activos. Debe haber al menos un ejemplar y se descuenta exactamente uno. Si el libro tiene ID, se lee su disponibilidad persistida para evitar confiar en una referencia desactualizada.
- Devolución: préstamo existente y activo; fecha no anterior al inicio; registro de fecha e incremento de un ejemplar. Se rechaza repetición y desbordamiento del entero antes de modificar datos.
- Multa: max(0, días calendario entre vencimiento y devolución) por 2.000 COP. Un día tarde produce 2.000 COP y tres días producen 6.000 COP. Devolver antes o el día del vencimiento produce cero. La tarifa es la propuesta académica del proyecto, no una norma legal.
- Persistencia: préstamo y cambio de disponibilidad participan en una transacción. La prueba de fallo de persistencia comprobó que el descuento no queda guardado si el estudiante no está persistido.
- API previa: se conservan estado, registro y listado de libros; se incorpora mensaje JSON 400 para cuerpos no deserializables.

## 5. Resultados verificables

Comando final: .\mvnw.cmd clean verify. Resultado: BUILD SUCCESS; 70 pruebas, 0 fallos, 0 errores y 0 omitidas. El umbral de cobertura de servicios fue comprobado por jacoco:check durante verify. La ejecución anterior clean test también aprobó las mismas 70 pruebas; se conserva como 10-stock-persistido.

| Clase | Ejecutadas | Fallos | Errores | Omitidas |
|---|---|---|---|---|
| BibliouniApplicationTests | 1 | 0 | 0 | 0 |
| RegresionApiTest | 4 | 0 | 0 | 0 |
| LibroTest | 1 | 0 | 0 | 0 |
| LibroRepositoryTest | 2 | 0 | 0 | 0 |
| LibroServiceTest | 18 | 0 | 0 | 0 |
| PrestamoServiceIntegrationTest | 5 | 0 | 0 | 0 |
| PrestamoServiceTest | 39 | 0 | 0 | 0 |

Las pruebas originales de LibroService, LibroRepository, Libro y carga de contexto permanecen. Las tres pruebas de préstamos recibidas se conservaron al eliminar únicamente la declaración duplicada vacía que impedía compilar. El archivo de configuración de prueba se trasladó de src/test/java/resources a src/test/resources, donde Maven sí lo copia.

## 6. Cobertura

| Ámbito | Líneas cubiertas/total | Ramas cubiertas/total |
|---|---|---|
| LibroService | 16/16 (100,00 %) | 20/20 (100,00 %) |
| PrestamoService | 48/48 (100,00 %) | 40/40 (100,00 %) |
| Paquete de servicios | 64/64 (100,00 %) | 60/60 (100,00 %) |
| Código de producción completo | 122/139 (87,77 %) | 60/60 (100,00 %) |

La meta era al menos 80 % de líneas de servicios. Se alcanzó sin excluir clases de producción. La cobertura global incluye modelos, controladores, manejo de excepciones y arranque; algunas líneas no ejecutadas corresponden a accesores y al método main. El 100 % de ramas no significa probar todas las combinaciones de entrada ni demostrar seguridad frente a concurrencia.

El reporte navegable se genera en target/site/jacoco/index.html. Se conserva una copia HTML, XML y CSV en evidencias/unidad2/11-final/cobertura junto con jacoco.exec. Las métricas de esta tabla se calcularon desde el CSV real, no se escribieron como valores supuestos.

## 7. Aplicación de TDD

Se completaron dos funcionalidades mediante Red, Green y Refactor: validación de datos obligatorios del libro y devolución única con recuperación de disponibilidad. Cada fase tuvo una ejecución real y un commit separado. El anexo Registro TDD incluye hashes, comandos, cantidades e incidentes.

En el primer ciclo fallaron siete variantes antes de implementar la validación; después aprobaron las 13 pruebas del servicio en Green y Refactor. En el segundo ciclo, el método nuevo tuvo un contrato sin implementación para permitir compilar: sus dos pruebas nuevas fallaron en Red (un fallo y un error) y las cinco del servicio aprobaron en Green y Refactor. No se presenta como TDD el código de préstamo y multa que ya existía ni las ampliaciones posteriores que no siguieron ese ciclo completo.

## 8. Incidentes y correcciones

| Incidente | Evidencia y solución |
|---|---|
| Declaraciones duplicadas en PrestamoServiceTest | Inspección inicial: dos package y dos clases. Se eliminó el bloque vacío y se ejecutó una base de 13 pruebas aprobadas. No existe un log de compilación fallida inicial; no se fabrica. |
| Configuración test fuera de recursos Maven | En la primera ejecución se observó bibliouni_local; se movió el archivo y 00-base verifica bibliouni_test. |
| Falta de validación de datos del libro | 01-libro-red: siete fallos reales; resuelto en Green. |
| Devolución sin implementar | 04-devolucion-red: un fallo y un error reales; resuelto en Green. |
| Cuerpo HTTP no convertible sin mensaje JSON | 08-integracion: un fallo real entre 66 pruebas. Se añadió manejo de HttpMessageNotReadableException; 09-integracion-corregida aprobó 67. |
| Stock de referencia separado desactualizado | Se reforzó el servicio para consultar stock persistido y se añadieron pruebas aisladas y H2; 10-stock-persistido y la ejecución final aprobaron 70 pruebas. |

No se borraron ejecuciones fallidas. Cada carpeta identifica cuándo y con qué comando se obtuvieron sus resultados. Las advertencias de carga dinámica del agente Mockito en Java 21 no impidieron la ejecución y no se presentan como fallos de pruebas.

## 9. Limitaciones y pendientes

Los resultados cubren un ambiente local con H2. Quedan pendientes pruebas de concurrencia real y control de bloqueos, carga, seguridad, MySQL, aceptación con usuarios, API de estudiantes, consulta de préstamos por estudiante y alertas. No se evaluaron pagos ni almacenamiento contable de multas: el importe se calcula a partir de fechas. No existe garantía de comportamiento multiusuario simultáneo por haber probado transacciones secuenciales.

La devolución y el stock persistido se probaron con datos deterministas, sin depender de la fecha del reloj. Las pruebas HTTP usan MockMvc y no equivalen a una prueba de red desplegada. La aprobación académica y la eventual exigencia de JUnit 5 exacto deben ser revisadas por el docente. No se inventan capturas, resultados previos, firmas ni una revisión visual de Word que no se haya realizado.

## 10. Reproducción y entrega

Ejecutar .\mvnw.cmd clean test para pruebas y reporte. Ejecutar .\mvnw.cmd clean verify para incluir el control de cobertura. Para conservar un ensayo nuevo, ejecutar scripts/ejecutar-pruebas.ps1 con un nombre distinto; no sobrescribir las carpetas de evidencia entregadas. El script generar-documentacion.ps1 valida que los métodos formales aparezcan aprobados en XML y genera este informe y el catálogo desde datos reales.

La rama está preparada con commits locales y un borrador de descripción en pull_request.md para destino develop. No se realizó push ni se creó un Pull Request. La base develop local es 41980a6; el estado remoto debe actualizarse y revisarse antes de una integración autorizada.

## 11. Conclusiones

La ejecución final permitió aceptar técnicamente el alcance automatizado de esta unidad: no se encontraron fallos en las 70 pruebas y se superó la meta de cobertura de servicios. Los casos negativos ayudaron a descubrir validaciones y respuestas incompletas que no se observaban en los caminos exitosos. El registro por fases de TDD y la evidencia guardada permiten revisar tanto los defectos como su corrección. El sistema requiere las verificaciones pendientes antes de afirmar condiciones de producción.

## 12. Referencias

- IEEE. IEEE Std 829-1998, sección 4, estructura del plan. Copia institucional: https://people.eecs.ku.edu/~saiedian/Teaching/Stds/829-1998.pdf
- JaCoCo. Maven Plug-in: https://www.jacoco.org/jacoco/trunk/doc/maven.html
- Spring Framework. 7.0 Release Notes, Baseline Upgrades: https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes
- Evidencia primaria del proyecto: archivos TEST-*.xml, maven.log, ejecucion.json y cobertura/jacoco.csv de evidencias/unidad2/11-final.

Los anexos del documento Word contienen el plan de pruebas, el registro TDD y el catálogo completo de casos. Los documentos de Unidad 1 conservan sus resultados históricos y no deben interpretarse como el resumen actualizado de esta ejecución.
