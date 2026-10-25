# Estrategia de Pruebas de Software — BiblioUni

**Proyecto:** BiblioUni — Sistema de Gestión de Biblioteca Universitaria  
**Asignatura:** Pruebas de Software  
**Unidad:** 1  
**Fecha:** 25 de octubre de 2026  
**Estado:** Estrategia propuesta con ejecución inicial de pruebas

## 1. Introducción

BiblioUni es un proyecto académico orientado a gestionar libros, estudiantes y préstamos de una biblioteca universitaria.

Este documento define una estrategia de pruebas para identificar defectos, verificar el cumplimiento de los requisitos y reducir el riesgo de fallos antes de utilizar el sistema.

La estrategia distingue las funcionalidades actualmente implementadas de aquellas que todavía se encuentran en fase de diseño.

## 2. Objetivos

**Objetivo general:** Definir una estrategia de pruebas que permita evaluar la calidad del sistema BiblioUni mediante técnicas, niveles y tipos de prueba apropiados.

**Objetivos específicos:**

- Identificar los componentes que requieren verificación.
- Definir pruebas funcionales y no funcionales.
- Aplicar técnicas de caja negra y caja blanca.
- Priorizar escenarios según su riesgo e impacto.
- Establecer criterios de entrada y salida.
- Documentar resultados y evidencias reproducibles.

## 3. Alcance

| Componente | Funcionalidad | Estado |
|---|---|---|
| Modelo Libro | Representación de libros y disponibilidad | Implementado |
| Modelo Estudiante | Representación de estudiantes | Implementado |
| Modelo Prestamo | Relación entre estudiantes y libros | Implementado |
| Servicios | Reglas de préstamos, devoluciones y disponibilidad | Pendiente |
| Repositorios | Persistencia y consultas | Pendiente |
| Controladores REST | Exposición de operaciones HTTP | Pendiente |
| Base de datos | Integridad y persistencia | Configuración de pruebas disponible |
| Alertas de vencimiento | Identificación de préstamos próximos a vencer | Propuesta |

Los componentes pendientes forman parte del alcance previsto de pruebas, pero no se reportan como funcionalidades verificadas.

## 4. Requisitos considerados

| Código | Requisito |
|---|---|
| RF-01 | Registrar libros |
| RF-02 | Consultar libros |
| RF-03 | Registrar estudiantes |
| RF-04 | Registrar préstamos |
| RF-05 | Impedir préstamos sin disponibilidad |
| RF-06 | Registrar devoluciones y recuperar disponibilidad |
| RF-07 | Consultar préstamos activos por estudiante |
| RF-08 | Identificar préstamos próximos a vencer |

RF-08 es una funcionalidad adicional propuesta como elemento innovador del proyecto.

## 5. Niveles de prueba

### 5.1 Pruebas unitarias

Comprueban unidades individuales de código, como métodos y reglas de negocio.

**Ejemplo:** verificar que un libro conserva correctamente su cantidad disponible.

**Herramienta:** JUnit.

### 5.2 Pruebas de integración

Verifican la interacción entre componentes y la persistencia de información.

**Ejemplo:** comprobar que un préstamo se almacena correctamente y queda relacionado con un estudiante y un libro.

**Herramientas previstas:** Spring Boot Test, Spring Data JPA y H2.

### 5.3 Pruebas de sistema

Evalúan el comportamiento completo del sistema mediante sus interfaces disponibles.

**Ejemplo:** registrar un préstamo mediante una petición HTTP y comprobar su respuesta.

**Herramientas previstas:** Spring Boot Test y cliente HTTP.

### 5.4 Pruebas de aceptación

Comprueban si el sistema satisface las necesidades de sus usuarios.

**Ejemplo:** un bibliotecario intenta prestar un libro sin ejemplares disponibles y recibe una respuesta que impide completar la operación.

## 6. Tipos de prueba

**Funcionales:** registro de libros, estudiantes, préstamos, devoluciones y consultas.

**No funcionales:** rendimiento, seguridad, confiabilidad, mantenibilidad y compatibilidad de la API.

**Regresión:** repetición de pruebas existentes después de modificar el código.

**Negativas:** entradas inválidas, identificadores inexistentes, duplicados y préstamos sin disponibilidad.

## 7. Técnicas de diseño

### Caja negra

Se evalúan entradas y salidas sin depender de la implementación interna.

- **Partición de equivalencia:** cantidades negativas, cero y positivas.
- **Análisis de valores límite:** disponibilidad de 0 y 1 ejemplares.
- **Transición de estados:** préstamo activo → préstamo devuelto.

### Caja blanca

Se revisa el comportamiento interno del código.

- Cobertura de instrucciones.
- Cobertura de decisiones.
- Verificación de ramas que aceptan o rechazan préstamos.

### Basadas en la experiencia

- Predicción de errores frecuentes.
- Exploración de datos faltantes o duplicados.
- Comprobación de devoluciones repetidas.

## 8. Priorización basada en riesgos

| Riesgo | Impacto | Prioridad |
|---|---|---|
| Prestar libros sin disponibilidad | Alto | Alta |
| Registrar dos devoluciones del mismo préstamo | Alto | Alta |
| Perder información de préstamos | Alto | Alta |
| Registrar ISBN duplicados | Medio | Alta |
| Consultar incorrectamente préstamos activos | Medio | Media |
| Generar alertas incorrectas de vencimiento | Medio | Media |

Las reglas que modifican disponibilidad y estado de préstamos tendrán prioridad sobre las consultas informativas.

## 9. Ambiente de pruebas

- Lenguaje: Java 21.
- Framework: Spring Boot 4.1.2.
- Gestor de dependencias: Maven.
- Pruebas automatizadas: JUnit.
- Persistencia: Spring Data JPA.
- Base de datos para pruebas: H2 en memoria.
- Entorno de desarrollo: IntelliJ IDEA.
- Sistema operativo: Windows.

**Comando de ejecución:**

```powershell
.\mvnw.cmd clean test
```

El perfil `test` utiliza H2 para evitar depender de una instalación externa de MySQL durante las pruebas automatizadas.

## 10. Criterios de entrada y salida

### Criterios de entrada

- Requisitos identificados.
- Código compilable.
- Dependencias disponibles.
- Datos de prueba definidos.
- Ambiente de pruebas configurado.

### Criterios de salida

- Todas las pruebas críticas ejecutadas y aprobadas.
- Ningún defecto crítico conocido pendiente de resolución.
- Evidencias registradas.
- Resultados reproducibles mediante Maven.
- Cobertura objetivo de al menos 80 % de líneas en la capa de servicios, cuando esté implementada y se configure su medición.

El porcentaje de cobertura es una meta propuesta, no un resultado actualmente medido.

## 11. Exclusiones y limitaciones

Esta primera estrategia no contempla pruebas sobre aplicaciones móviles, integraciones con pasarelas de pago, notificaciones por correo electrónico ni despliegues productivos.

Las pruebas de carga y seguridad se plantean como objetivos de evaluación futura; no se consideran ejecutadas.

## 12. Componente innovador

Se propone identificar préstamos cuya fecha de vencimiento se encuentre dentro de los próximos tres días.

Los escenarios deberán verificar:

- Identificación correcta de préstamos próximos a vencer.
- Exclusión de préstamos ya devueltos.
- Comportamiento en fechas límite.
- Prevención de alertas duplicadas, si posteriormente se implementa un mecanismo de notificación.

Esta funcionalidad permite incorporar pruebas basadas en fechas y estados del préstamo.

## 13. Evidencia inicial

Se ejecutó el comando:

```powershell
.\mvnw.cmd clean test
```

**Resultado observado:**

- Pruebas ejecutadas: 2.
- Fallos: 0.
- Errores: 0.
- Pruebas omitidas: 0.
- Compilación: BUILD SUCCESS.

Las pruebas corresponden al inicio del contexto de Spring Boot y a la consulta de cantidad disponible en la entidad Libro.

Estos resultados no demuestran todavía el cumplimiento de todos los requisitos funcionales.

## 14. Seguimiento y mejora

Cada escenario deberá documentar su identificador, requisito asociado, prioridad, condiciones previas, pasos, resultado esperado y resultado real cuando se ejecute.

Los defectos encontrados deberán registrarse y volver a comprobarse después de su corrección.

La estrategia se actualizará conforme se implementen nuevas funcionalidades y se amplíe la cobertura de pruebas.

## 15. Referencias

- ISTQB. *Certified Tester Foundation Level Syllabus*.
- ISO/IEC 25010. *Systems and software quality models*.
- Documentación oficial de JUnit.
- Documentación oficial de Spring Boot Testing.