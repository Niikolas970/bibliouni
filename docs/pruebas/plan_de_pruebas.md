# Plan de pruebas — BiblioUni, Unidad 2

## 1. Identificador del plan

BU-U2-PP-001, versión 1.0. Fecha de elaboración: 14 de noviembre de 2026. Rama: feature/plan-tests. Responsable individual: Nicolás Pirajan (identidad configurada en Git). Estado: preparado para revisión académica. Este documento complementa los artefactos de Unidad 1 sin reemplazarlos.

## 2. Introducción y referencias

El propósito es verificar las reglas de registro y consulta de libros, préstamos, devoluciones y multas, conservando la regresión de Unidad 1. Se adopta la estructura de plan de IEEE 829-1998 para el ejercicio universitario; no se afirma certificación ni cumplimiento de una edición distinta.

Referencias: estrategia.md, escenarios.md, calidad_iso25010.md (Unidad 1); casos_de_prueba.md, tdd.md e informe_unidad2.md (Unidad 2); pom.xml y fuentes versionadas. Estructura de referencia: [IEEE 829-1998, sección 4, copia del documento IEEE alojada por University of Kansas](https://people.eecs.ku.edu/~saiedian/Teaching/Stds/829-1998.pdf). Instrumentación: [documentación oficial JaCoCo Maven](https://www.jacoco.org/jacoco/trunk/doc/maven.html).

## 3. Elementos de prueba

LibroService, PrestamoService, entidades Libro/Estudiante/Prestamo, LibroRepository y PrestamoRepository; API existente de estado y libros, GlobalExceptionHandler; configuración Maven y perfil test. Los repositorios se verifican con H2; los servicios tienen pruebas aisladas con Mockito y pruebas de integración. El objeto libro y el estudiante de los préstamos de integración se crean previamente en la base de datos.

## 4. Características que se probarán

| Requisito | Comportamiento verificable |
|---|---|
| RF-01 | Registrar libro completo, rechazar cantidad negativa e ISBN duplicado; permitir cantidad cero. |
| RF-02 | Listar y buscar libros por ID, con Optional vacío si no existe. |
| RF-04 | Registrar préstamo nuevo con libro, estudiante y fechas válidas; descontar uno; consultar por ID. |
| RF-05 | Rechazar disponibilidad cero o negativa, consultando el stock persistido para libros con ID. |
| RF-06 | Registrar devolución, recuperar uno y rechazar devolución repetida, fecha inválida o desbordamiento. |
| RF-09 | Calcular max(0, días calendario de retraso) × 2.000 COP. Es una regla académica propuesta. |
| RF-10 | Validar ID positivo y datos obligatorios antes de mutar disponibilidad. |
| RNF-U2-01 | Revertir el descuento cuando falla la persistencia del préstamo (transacción). |
| REG-U1 | Conservar pruebas anteriores y contratos HTTP 200, 201, 400 y 409 de estado/libros. |

RF-09, RF-10 y RNF-U2-01 amplían el catálogo de Unidad 1. No se renumeran RF-03, RF-07 ni RF-08.

## 5. Características excluidas

Registro de estudiantes mediante API (RF-03), consulta por estudiante (RF-07), alertas (RF-08), pagos de multas, interfaz gráfica, autenticación, carga y despliegue. Los préstamos y devoluciones se prueban en la capa de servicio; no se añadieron endpoints nuevos de préstamos. Las capturas históricas de Unidad 1 no demuestran ejecuciones de Unidad 2. La concurrencia de solicitudes simultáneas no se da por resuelta por utilizar transacciones: requiere control de bloqueo y pruebas específicas posteriores.

## 6. Enfoque

Pruebas de caja negra por particiones de equivalencia y valores límite; tabla de decisión para préstamos/devoluciones y transición activo→devuelto. Las pruebas unitarias usan Arrange–Act–Assert, datos deterministas y verifican que no se escriba ante entradas inválidas. Las integraciones comprueban estado real persistido y rollback. MockMvc ejercita los controladores y el manejo de excepciones con servicios y H2 reales, sin levantar un puerto externo. JaCoCo mide líneas y ramas del código de producción, sin excluir clases para mejorar el porcentaje.

Se realizan dos ciclos TDD separados (libros incompletos y devoluciones) con fallos reales, corrección mínima y refactorización, cada fase con ejecución y commit. Las pruebas anteriores se ejecutan en cada regresión completa. La multa usa LocalDate y días calendario, sin horas ni zonas horarias; no hay multa al devolver el día del vencimiento. Se permite un préstamo que vence el mismo día de inicio. El ISBN se valida como obligatorio y único, sin imponer un formato o dígito de control que no estuviera en los requisitos.

## 7. Criterios de aprobación y fallo

Un caso aprueba si todas sus aserciones pasan y no hay error u omisión en su resultado XML. La salida exige 100 % de las pruebas automatizadas aprobadas, cero fallos y errores, conservación de la Unidad 1 y al menos 80 % de líneas en el paquete de servicios. `clean test` genera el reporte; `clean verify` además aplica el umbral de JaCoCo. La cobertura no demuestra ausencia de defectos ni concurrencia segura. Un caso no ejecutado se informa como pendiente.

## 8. Suspensión y reanudación

Suspender una ejecución si falta JDK/dependencias, no compila o el ambiente no permite cargar H2. Guardar el log, identificar el incidente y corregirlo antes de reanudar. Un fallo Red es esperado exclusivamente en su fase TDD. Un fallo de integración suspende la aceptación, no el trabajo independiente de documentación. La reanudación requiere ejecutar nuevamente el caso fallido y la suite completa; no se borran evidencias anteriores.

## 9. Entregables

Código y pruebas; plan; catálogo formal y matriz de trazabilidad; registro TDD; logs completos, metadatos y XML originales; copia de cobertura HTML/XML/CSV; informe Word y fuente Markdown; scripts locales para reproducción; descripción de Pull Request. No se requiere publicación ni servicios de pago.

## 10. Tareas de prueba

Inspeccionar código y cambios locales; corregir compilación; establecer base verde; ejecutar dos ciclos TDD; completar negativos y límites; verificar H2 y HTTP; configurar y medir JaCoCo; analizar incidentes; documentar casos y resultados; generar y validar Word; revisar diferencia con develop y preparar commits locales.

## 11. Necesidades ambientales

Windows, JDK Temurin 21.0.12 y Maven Wrapper 3.10.0, Spring Boot 4.1.2, H2 2.4.240, Mockito 5.23.0 y JaCoCo 0.8.14. El perfil test se encuentra en src/test/resources/application-test.properties y usa jdbc:h2:mem:bibliouni_test con create-drop. No se necesita MySQL. La descarga inicial de dependencias requiere red; las siguientes ejecuciones pueden reutilizar la caché local.

El contexto inicial mencionaba JUnit 5. El classpath real administrado por Spring Boot contiene JUnit Jupiter 6.0.3, conservando las anotaciones org.junit.jupiter.api. Se mantiene esa resolución para respetar Spring Boot 4.1.2: [Spring Framework 7 declara JUnit 6 como requisito mínimo](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes). Si la rúbrica exige exclusivamente la versión mayor 5, queda pendiente acordar una combinación de versiones compatible; no se afirma haber ejecutado JUnit 5.

## 12. Responsabilidades

El estudiante es responsable del análisis, diseño, implementación, ejecución, análisis de resultados y entrega. No se atribuye participación a un equipo inexistente. El docente evalúa el entregable y define su aceptación académica. La preparación asistida se conserva en cambios y resultados revisables.

## 13. Personal y formación

Trabajo individual. Se requieren conocimientos de Java, Maven, pruebas JUnit Jupiter, Mockito, transacciones JPA y lectura de cobertura. La reproducción está descrita con comandos locales, sin necesidad de capacitación o herramientas de pago adicionales.

## 14. Cronograma e hitos

| Hito | Secuencia | Evidencia de realización |
|---|---|---|
| Corrección y base | 1 | 00-base |
| TDD libro | 2 | 01-libro-red, 02-libro-green, 03-libro-refactor |
| TDD devolución | 3 | 04-devolucion-red, 05-devolucion-green, 06-devolucion-refactor |
| Ampliación e integración | 4 | 07-ampliacion a 10-stock-persistido |
| Aceptación técnica | 5 | 11-final y copia de cobertura |
| Documentación y revisión local | 6 | Informe, validación documental y commits |

Los tiempos reales están en cada ejecucion.json; la secuencia no supone duraciones planificadas retrospectivamente. No se inventan horas trabajadas ni fechas de aprobación docente.

## 15. Riesgos y contingencias

| Riesgo | Impacto y respuesta |
|---|---|
| Stock desactualizado en un objeto separado de JPA | Se vuelve a leer el libro por ID antes del préstamo; existe caso de regresión. |
| Escritura parcial | Transacción de servicio y caso real de rollback en H2. |
| Solicitudes simultáneas | Pendiente bloqueo optimista/pesimista y prueba concurrente; no apto para afirmar garantía multiusuario. |
| Diferencias H2 frente a MySQL | Limitar conclusiones a H2 y verificar el motor objetivo antes de desplegar. |
| Confusión JUnit 5/6 | Declarar versiones resueltas y referencia de compatibilidad, sin cambiar silenciosamente el framework. |
| Pérdida de evidencias por clean | Copiar XML y logs fuera de target antes de la siguiente ejecución. |
| Cambios en develop remoto | Revisión con develop local; volver a comprobar contra remoto actualizado antes de integrar. |
| Documento Word | Generar OOXML reproducible y comprobar estructura y apertura local cuando el entorno lo permita. |

## 16. Aprobaciones

Preparado para revisión técnica y académica. La aprobación docente y la integración a develop están pendientes. No se incluyen firmas inventadas. Los commits son locales; no se hace push ni se crea Pull Request sin autorización del propietario.
