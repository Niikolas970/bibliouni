# Análisis de Calidad ISO/IEC 25010 — BiblioUni

**Proyecto:** BiblioUni — Sistema de Gestión de Biblioteca Universitaria  
**Asignatura:** Pruebas de Software  
**Unidad:** 1  
**Fecha:** 25 de octubre de 2026  
**Estado:** Modelo de calidad y criterios de evaluación propuestos

## 1. Introducción

La norma ISO/IEC 25010 proporciona un modelo para analizar la calidad de los productos de software mediante características y subcaracterísticas.

Para esta actividad se utiliza el modelo de ocho características estudiado en la unidad académica. Este análisis permite identificar atributos de calidad relevantes para BiblioUni, establecer criterios medibles y priorizar los riesgos asociados al sistema.

Los criterios presentados son objetivos de calidad propuestos. No representan resultados obtenidos mediante pruebas, salvo cuando se indique expresamente.

## 2. Características de calidad

### 2.1 Adecuación funcional

**Definición:** capacidad del software para proporcionar funciones que satisfagan las necesidades establecidas.

**Subcaracterísticas:**
- Completitud funcional.
- Corrección funcional.
- Pertinencia funcional.

**Aplicación en BiblioUni:**

El sistema debe permitir registrar libros y estudiantes, administrar préstamos y devoluciones, consultar información e identificar préstamos próximos a vencer.

**Indicadores propuestos:**
- 100 % de los requisitos funcionales críticos cubiertos por escenarios de prueba.
- 100 % de los escenarios críticos aprobados antes de la aceptación.
- Cero préstamos registrados cuando la disponibilidad es cero.

**Prioridad:** Crítica.

**Pruebas relacionadas:** CP-01 a CP-14.

### 2.2 Eficiencia de desempeño

**Definición:** capacidad del sistema para ofrecer un desempeño adecuado en relación con los recursos utilizados.

**Subcaracterísticas:**
- Comportamiento temporal.
- Utilización de recursos.
- Capacidad.

**Aplicación en BiblioUni:**

Las consultas de libros y préstamos deben responder en un tiempo razonable, incluso cuando varios usuarios utilizan el sistema.

**Indicadores propuestos:**
- Percentil 95 de tiempo de respuesta inferior a dos segundos bajo una carga de 50 usuarios virtuales.
- Tasa de errores del servidor inferior al 1 % durante la prueba de carga.
- Utilización de recursos registrada durante la ejecución.

**Prioridad:** Media.

**Prueba relacionada:** CP-15.

**Limitación:** todavía no se han ejecutado pruebas de carga.

### 2.3 Compatibilidad

**Definición:** capacidad del producto para compartir un entorno e intercambiar información con otros sistemas.

**Subcaracterísticas:**
- Coexistencia.
- Interoperabilidad.

**Aplicación en BiblioUni:**

La API debe intercambiar información mediante HTTP y JSON, permitiendo su consumo desde herramientas como Postman y, posteriormente, una interfaz web.

**Indicadores propuestos:**
- 100 % de las respuestas JSON evaluadas cumplen el contrato definido para cada endpoint.
- Uso consistente de códigos de estado HTTP.
- Ausencia de conflictos de configuración con otros servicios del ambiente de pruebas.

**Prioridad:** Media.

**Pruebas relacionadas:** CP-01, CP-04, CP-05 y CP-12.

### 2.4 Usabilidad

**Definición:** capacidad del software para ser comprendido, aprendido y utilizado eficazmente por sus usuarios.

**Subcaracterísticas:**
- Reconocimiento de la adecuación.
- Capacidad de aprendizaje.
- Operabilidad.
- Protección contra errores del usuario.
- Estética de la interfaz de usuario.
- Accesibilidad.

**Aplicación en BiblioUni:**

Las respuestas de la API deben comunicar claramente el resultado de las operaciones y los errores de validación.

Cuando se desarrolle una interfaz gráfica, se evaluarán también su navegación, claridad visual y accesibilidad.

**Indicadores propuestos:**
- 100 % de los errores de validación evaluados presentan mensajes comprensibles.
- Las operaciones fallidas no alteran indebidamente los registros.
- Las operaciones principales pueden completarse sin pasos innecesarios.

**Prioridad:** Media.

**Pruebas relacionadas:** CP-02, CP-03, CP-06, CP-08 y CP-11.

**Limitación:** la estética y accesibilidad visual no pueden evaluarse completamente mientras no exista una interfaz gráfica.

### 2.5 Fiabilidad

**Definición:** capacidad del sistema para mantener su funcionamiento correcto bajo condiciones determinadas.

**Subcaracterísticas:**
- Madurez.
- Disponibilidad.
- Tolerancia a fallos.
- Capacidad de recuperación.

**Aplicación en BiblioUni:**

El sistema debe conservar la integridad de los préstamos y la disponibilidad de libros, incluso cuando se producen errores o solicitudes simultáneas.

**Indicadores propuestos:**
- Cero disponibilidades negativas.
- Cero incrementos duplicados por una misma devolución.
- Cero préstamos duplicados sobre el último ejemplar disponible.
- Las operaciones fallidas no dejan modificaciones parciales inconsistentes.

**Prioridad:** Crítica.

**Pruebas relacionadas:** CP-07 a CP-11 y CP-17.

### 2.6 Seguridad

**Definición:** capacidad del software para proteger información y controlar el acceso a sus funcionalidades.

**Subcaracterísticas:**
- Confidencialidad.
- Integridad.
- No repudio.
- Responsabilidad o trazabilidad.
- Autenticidad.

**Aplicación en BiblioUni:**

Los datos de los estudiantes deben protegerse y las operaciones administrativas deben estar disponibles únicamente para usuarios autorizados.

**Indicadores propuestos:**
- 100 % de los endpoints definidos como protegidos rechazan solicitudes sin autorización.
- No se exponen contraseñas ni credenciales en las respuestas.
- Los cambios críticos pueden asociarse con el usuario responsable cuando se implemente auditoría.

**Prioridad:** Alta.

**Prueba relacionada:** CP-16.

**Limitación:** la autenticación, autorización y auditoría todavía no están implementadas.

### 2.7 Mantenibilidad

**Definición:** capacidad del software para ser analizado, modificado y probado de forma eficiente.

**Subcaracterísticas:**
- Modularidad.
- Reusabilidad.
- Analizabilidad.
- Modificabilidad.
- Capacidad de prueba.

**Aplicación en BiblioUni:**

El proyecto utiliza una estructura organizada en paquetes como `model`, `repository`, `service`, `controller`, `dto` y `exception`.

Esta separación busca facilitar la comprensión y las pruebas de cada componente.

**Indicadores propuestos:**
- Cobertura mínima objetivo del 80 % de líneas en la capa de servicios.
- Ejecución automatizada de las pruebas mediante Maven.
- Ausencia de defectos críticos de regresión después de cambios.
- Reglas de negocio concentradas en la capa de servicios.

**Prioridad:** Alta.

**Evidencia inicial:**
- Dos pruebas automatizadas ejecutadas.
- Cero fallos y cero errores.
- Resultado `BUILD SUCCESS`.

**Limitación:** todavía no se ha medido cobertura de código.

### 2.8 Portabilidad

**Definición:** capacidad del software para adaptarse a diferentes entornos de ejecución.

**Subcaracterísticas:**
- Adaptabilidad.
- Instalabilidad.
- Reemplazabilidad.

**Aplicación en BiblioUni:**

Al utilizar Java y Maven, se busca que el proyecto pueda compilarse y ejecutarse en diferentes sistemas operativos compatibles.

La base de datos H2 permite realizar pruebas automatizadas sin depender de un servidor MySQL externo.

**Indicadores propuestos:**
- Compilación exitosa mediante Maven en los entornos compatibles seleccionados.
- Configuración externa de conexiones y credenciales.
- Ejecución reproducible de pruebas en un entorno limpio.

**Prioridad:** Media.

**Limitación:** la ejecución solo ha sido comprobada en el ambiente Windows utilizado durante el desarrollo.

## 3. Matriz comparativa

| Característica | Prioridad | Indicador principal | Estado |
|---|---|---|---|
| Adecuación funcional | Crítica | 100 % de escenarios críticos aprobados | Pendiente |
| Eficiencia de desempeño | Media | P95 menor de 2 s con 50 usuarios | Pendiente |
| Compatibilidad | Media | Respuestas HTTP/JSON conformes | Pendiente |
| Usabilidad | Media | Mensajes de error comprensibles | Pendiente |
| Fiabilidad | Crítica | Cero inconsistencias en préstamos | Pendiente |
| Seguridad | Alta | Accesos protegidos correctamente | Pendiente |
| Mantenibilidad | Alta | Cobertura objetivo del 80 % en servicios | Parcial |
| Portabilidad | Media | Compilación reproducible en distintos entornos | Parcial |

Los estados parciales corresponden únicamente a la evidencia técnica inicial y no significan que toda la característica de calidad haya sido validada.

## 4. Análisis de prioridades

Las características más importantes para BiblioUni son la adecuación funcional y la fiabilidad.

Un error en la disponibilidad puede permitir préstamos imposibles o generar inconsistencias en el inventario.

La seguridad también tiene alta prioridad debido al tratamiento de información de estudiantes.

La mantenibilidad es relevante porque el proyecto se encuentra en desarrollo y sus funcionalidades pueden ampliarse.

Las características de rendimiento, compatibilidad, usabilidad y portabilidad deben verificarse progresivamente conforme avance la implementación.

## 5. Compensaciones entre características de calidad

### 5.1 Seguridad frente a usabilidad

Solicitar autenticación aumenta la protección de los datos, pero también introduce pasos adicionales para los usuarios.

**Decisión propuesta:** priorizar el acceso seguro a las operaciones administrativas y mantener mensajes de error comprensibles.

### 5.2 Fiabilidad frente a rendimiento

Los mecanismos de control de concurrencia pueden aumentar el tiempo de procesamiento de una solicitud.

**Decisión propuesta:** priorizar la integridad de los préstamos sobre pequeñas mejoras de velocidad.

### 5.3 Mantenibilidad frente a rapidez de implementación

Separar el sistema en controladores, servicios y repositorios requiere más estructura que concentrar toda la lógica en una sola clase.

**Decisión propuesta:** conservar una arquitectura modular para facilitar futuras modificaciones y pruebas.

### 5.4 Portabilidad frente a particularidades de la base de datos

H2 facilita las pruebas locales, pero puede presentar diferencias respecto de MySQL.

**Decisión propuesta:** utilizar H2 para pruebas automatizadas iniciales y planificar pruebas de integración adicionales con MySQL antes de un despliegue real.

## 6. Plan de evaluación

1. Ejecutar pruebas unitarias con JUnit.
2. Comprobar la persistencia y las relaciones mediante pruebas de integración.
3. Verificar endpoints REST y códigos HTTP utilizando Postman.
4. Evaluar escenarios negativos y valores límite.
5. Medir cobertura mediante una herramienta como JaCoCo.
6. Realizar pruebas de rendimiento y seguridad cuando los componentes necesarios estén implementados.
7. Registrar los resultados y compararlos con los indicadores propuestos.

## 7. Conclusión

El modelo ISO/IEC 25010 permite evaluar BiblioUni desde una perspectiva más amplia que la simple ejecución correcta de sus funcionalidades.

La estrategia propuesta prioriza la integridad de los préstamos, el cumplimiento de los requisitos, la protección de los datos y la facilidad de mantenimiento.

Aunque el proyecto cuenta con una configuración inicial de pruebas automatizadas, la mayoría de los indicadores todavía requieren implementación, medición y evidencia.

Este análisis proporciona criterios verificables para orientar las siguientes etapas de desarrollo y pruebas.

## 8. Referencias

- ISO/IEC 25010:2011. *Systems and software engineering — Systems and software Quality Requirements and Evaluation (SQuaRE) — System and software quality models*.
- ISTQB. *Certified Tester Foundation Level Syllabus*.
- JUnit. Documentación oficial: https://junit.org/
- Spring Boot. Documentación oficial: https://docs.spring.io/spring-boot/
- Apache Maven. Documentación oficial: https://maven.apache.org/