param([string]$Evidencia = '11-final')
$ErrorActionPreference = 'Stop'
$raiz = Split-Path $PSScriptRoot -Parent
$docs = Join-Path $raiz 'docs/pruebas'
$fuente = Join-Path $docs "evidencias/unidad2/$Evidencia"
$meta = Get-Content "$fuente/ejecucion.json" -Raw -Encoding UTF8 | ConvertFrom-Json
if ($meta.codigoSalida -ne 0) { throw 'No se puede generar un informe aprobado desde una ejecucion fallida.' }
$suites = @(Get-ChildItem "$fuente/TEST-*.xml" | ForEach-Object {
    ([xml](Get-Content $_.FullName -Raw -Encoding UTF8)).testsuite
})
$total = 0; $fallos = 0; $errores = 0; $omitidas = 0
foreach ($suite in $suites) {
    $total += [int]$suite.tests; $fallos += [int]$suite.failures
    $errores += [int]$suite.errors; $omitidas += [int]$suite.skipped
}
if ($fallos + $errores + $omitidas -ne 0) { throw 'Los resultados no cumplen el criterio de salida.' }
$casos = @(Import-Csv "$docs/casos_unidad2.csv" -Delimiter '|' -Encoding UTF8)
if ($casos.Count -lt 15) { throw 'Faltan casos formales.' }
$texto = [System.Text.StringBuilder]::new()
[void]$texto.AppendLine(@'
# Casos de prueba formales — Unidad 2

Identificador BU-U2-CP, versión 1.0. Responsable individual: Nicolás Pirajan. Fecha de ejecución: ver metadatos de la ejecución final. Los identificadores U2-CP no sustituyen los CP de Unidad 1. El CSV casos_unidad2.csv es la fuente editable; este documento se genera y contrasta con XML reales de Surefire.

## Condiciones y procedimiento común

Para cada caso unitario se crea un servicio con MockitoExtension, repositorios nuevos y los datos indicados. Las pruebas H2 usan perfil test y preparan libro/estudiante persistidos; cada prueba de préstamos elimina únicamente sus propios datos. Las pruebas HTTP usan MockMvc, el contexto Spring y rollback del caso. Son pruebas locales sin datos personales reales ni un servidor externo.

Preparación: disponer de JDK 21 y dependencias del proyecto. Ejecución: .mvnw.cmd clean verify. Alternativa con conservación: .scriptsejecutar-pruebas.ps1 -Nombre nueva-ejecucion -Objetivos clean,verify. Consultar la clase y método indicados para repetir un caso individual. En parametrizados, todas sus invocaciones deben aprobar. La prioridad es alta para integridad, validación y multa; media para consultas y estado.

'@)
$matriz = [System.Collections.Generic.List[string]]::new()
foreach ($caso in $casos) {
    $suiteCaso = @($suites | Where-Object { $_.name.EndsWith('.' + $caso.clase) })
    $invocaciones = @($suiteCaso.testcase | Where-Object { $_.name -match ('^' + [regex]::Escape($caso.metodo) + '(\(|\[|$)') })
    if ($invocaciones.Count -eq 0) { throw "Caso sin evidencia ejecutada: $($caso.id), $($caso.metodo)" }
    foreach ($invocacion in $invocaciones) {
        if ($invocacion.failure -or $invocacion.error -or $invocacion.skipped) { throw "Caso no aprobado: $($caso.id)" }
    }
    [void]$texto.AppendLine("## $($caso.id) — $($caso.titulo)")
    [void]$texto.AppendLine("")
    [void]$texto.AppendLine("Requisito: $($caso.requisito). Nivel: $($caso.nivel). Técnica: $($caso.tecnica).")
    [void]$texto.AppendLine("")
    [void]$texto.AppendLine("- Precondiciones y entradas: $($caso.datos).")
    [void]$texto.AppendLine("- Pasos: $($caso.pasos).")
    [void]$texto.AppendLine("- Resultado esperado: $($caso.esperado).")
    [void]$texto.AppendLine("- Resultado obtenido: aprobado; $($invocaciones.Count) invocación(es), sin fallos, errores ni omisiones. Las aserciones del método verifican el resultado esperado.")
    [void]$texto.AppendLine("- Automatización y trazabilidad: $($caso.clase).$($caso.metodo); evidencia evidencias/unidad2/$Evidencia/TEST-$($suiteCaso[0].name).xml.")
    [void]$texto.AppendLine("")
    $matriz.Add("| $($caso.id) | $($caso.requisito) | $($caso.clase).$($caso.metodo) | Aprobado |")
}
[void]$texto.AppendLine(@'
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
'@)
foreach ($fila in $matriz) { [void]$texto.AppendLine($fila) }
[void]$texto.AppendLine("")
[void]$texto.AppendLine("Total: $($casos.Count) casos formales; la suite completa ejecutó $total invocaciones. Los casos parametrizados representan varias invocaciones y algunos métodos originales de regresión no tienen un caso formal adicional. No se suman estos dos conteos como si fueran pruebas diferentes.")
[void]$texto.AppendLine("")
[void]$texto.AppendLine("Fuera de alcance y pendientes: RF-03, RF-07, RF-08, carga, seguridad y concurrencia de Unidad 1 conservan su estado en escenarios.md. No se declaran aprobados por estos resultados.")
$texto.ToString() | Set-Content "$docs/casos_de_prueba.md" -Encoding UTF8

$filas = @(Import-Csv "$fuente/cobertura/jacoco.csv")
function Cobertura($conjunto, $tipo) {
    $cubierto = ($conjunto | Measure-Object -Property ($tipo + '_COVERED') -Sum).Sum
    $perdido = ($conjunto | Measure-Object -Property ($tipo + '_MISSED') -Sum).Sum
    return ('{0}/{1} ({2:F2} %)' -f $cubierto, ($cubierto + $perdido), (100 * $cubierto / ($cubierto + $perdido)))
}
$resumen = @{
    ejecucion=$Evidencia; fecha=$meta.fin; commitCodigo=$meta.commitBase; pruebas=$total
    fallos=$fallos; errores=$errores; omitidas=$omitidas; casosFormales=$casos.Count
    lineasGlobal=(Cobertura $filas 'LINE'); ramasGlobal=(Cobertura $filas 'BRANCH')
    lineasServicios=(Cobertura @($filas | Where-Object PACKAGE -eq 'com.bibliouni.bibliouni.service') 'LINE')
    ramasServicios=(Cobertura @($filas | Where-Object PACKAGE -eq 'com.bibliouni.bibliouni.service') 'BRANCH')
}
$resumen | ConvertTo-Json | Set-Content "$docs/resultados_unidad2.json" -Encoding UTF8
$informe = [System.Text.StringBuilder]::new()
[void]$informe.AppendLine(@"
# Informe final — Pruebas de Software, Unidad 2

Proyecto: BiblioUni. Modalidad: trabajo individual. Responsable: Nicolás Pirajan, según identidad local de Git. Fecha de la ejecución final: $($meta.fin). Rama: feature/plan-tests. Código verificado: $($meta.commitBase). Documento listo para revisión académica; no contiene firmas ni aprobaciones atribuidas al docente.

## 1. Introducción

En esta unidad se amplió la verificación del sistema de biblioteca BiblioUni. Se conservaron las pruebas y documentos de Unidad 1, se corrigió la compilación de la prueba de préstamos y se completaron las reglas necesarias para devolver libros y calcular multas. El trabajo incluye pruebas automatizadas, dos ciclos TDD con resultados reales y documentación que permite repetir la ejecución.

## 2. Objetivos y alcance

El objetivo general fue comprobar que las operaciones de libros, préstamos, devoluciones y multas responden correctamente a datos válidos e inválidos. Se verificaron registro y búsqueda, disponibilidad, fechas, duplicados, recuperación de ejemplares y multa diaria. Se añadieron comprobaciones con H2 y regresión HTTP de la API existente. Las nuevas operaciones de préstamos permanecen en el servicio Java; no se implementó una interfaz web ni una API nueva para esas operaciones.

## 3. Ambiente y metodología

Se utilizó Windows, Java Temurin 21.0.12, Maven Wrapper 3.10.0, Spring Boot 4.1.2, Mockito 5.23.0, H2 2.4.240 y JaCoCo 0.8.14. El classpath real utiliza JUnit Jupiter 6.0.3, administrado por Spring Boot, aunque el planteamiento inicial indicaba JUnit 5. Se conserva esa versión porque Spring Framework 7 establece JUnit 6 como mínimo. Las pruebas mantienen el modelo Jupiter y sus anotaciones habituales. Esta diferencia queda declarada para revisión de la rúbrica.

Se aplicaron particiones de equivalencia, valores límite, tablas de decisión y transición de estados. Mockito permitió aislar reglas y verificar la ausencia de escrituras en escenarios negativos. H2 permitió comprobar persistencia y reversión de cambios. MockMvc comprobó el contrato HTTP dentro del contexto Spring. No se realizaron capturas nuevas: la evidencia consiste en logs completos, XML y reportes generados.

El plan adjunto sigue las 16 secciones de IEEE 829-1998 como estructura académica. El catálogo contiene $($casos.Count) casos formales trazados a métodos ejecutados. Los identificadores de Unidad 1 se conservan y los casos nuevos usan el prefijo U2-CP.

## 4. Reglas de negocio y cambios

- Libro: título, autor e ISBN obligatorios; cantidad no negativa; ISBN único. Se admite cantidad cero y no se añade una restricción de formato ISBN ajena al alcance original.
- Búsquedas: ID positivo; ausencia expresada con Optional vacío en libros y préstamos.
- Préstamo: libro y estudiante obligatorios; fechas completas; vencimiento igual o posterior al inicio; solo registros nuevos y activos. Debe haber al menos un ejemplar y se descuenta exactamente uno. Si el libro tiene ID, se lee su disponibilidad persistida para evitar confiar en una referencia desactualizada.
- Devolución: préstamo existente y activo; fecha no anterior al inicio; registro de fecha e incremento de un ejemplar. Se rechaza repetición y desbordamiento del entero antes de modificar datos.
- Multa: max(0, días calendario entre vencimiento y devolución) por 2.000 COP. Un día tarde produce 2.000 COP y tres días producen 6.000 COP. Devolver antes o el día del vencimiento produce cero. La tarifa es la propuesta académica del proyecto, no una norma legal.
- Persistencia: préstamo y cambio de disponibilidad participan en una transacción. La prueba de fallo de persistencia comprobó que el descuento no queda guardado si el estudiante no está persistido.
- API previa: se conservan estado, registro y listado de libros; se incorpora mensaje JSON 400 para cuerpos no deserializables.

## 5. Resultados verificables

Comando final: $($meta.comando). Resultado: BUILD SUCCESS; $total pruebas, $fallos fallos, $errores errores y $omitidas omitidas. El umbral de cobertura de servicios fue comprobado por jacoco:check durante verify. La ejecución anterior clean test también aprobó las mismas $total pruebas; se conserva como 10-stock-persistido.

| Clase | Ejecutadas | Fallos | Errores | Omitidas |
|---|---|---|---|---|
"@)
foreach ($suite in $suites) {
    [void]$informe.AppendLine("| $($suite.name.Split('.')[-1]) | $($suite.tests) | $($suite.failures) | $($suite.errors) | $($suite.skipped) |")
}
[void]$informe.AppendLine(@"

Las pruebas originales de LibroService, LibroRepository, Libro y carga de contexto permanecen. Las tres pruebas de préstamos recibidas se conservaron al eliminar únicamente la declaración duplicada vacía que impedía compilar. El archivo de configuración de prueba se trasladó de src/test/java/resources a src/test/resources, donde Maven sí lo copia.

## 6. Cobertura

| Ámbito | Líneas cubiertas/total | Ramas cubiertas/total |
|---|---|---|
| LibroService | $(Cobertura @($filas | Where-Object CLASS -eq 'LibroService') 'LINE') | $(Cobertura @($filas | Where-Object CLASS -eq 'LibroService') 'BRANCH') |
| PrestamoService | $(Cobertura @($filas | Where-Object CLASS -eq 'PrestamoService') 'LINE') | $(Cobertura @($filas | Where-Object CLASS -eq 'PrestamoService') 'BRANCH') |
| Paquete de servicios | $($resumen.lineasServicios) | $($resumen.ramasServicios) |
| Código de producción completo | $($resumen.lineasGlobal) | $($resumen.ramasGlobal) |

La meta era al menos 80 % de líneas de servicios. Se alcanzó sin excluir clases de producción. La cobertura global incluye modelos, controladores, manejo de excepciones y arranque; algunas líneas no ejecutadas corresponden a accesores y al método main. El 100 % de ramas no significa probar todas las combinaciones de entrada ni demostrar seguridad frente a concurrencia.

El reporte navegable se genera en target/site/jacoco/index.html. Se conserva una copia HTML, XML y CSV en evidencias/unidad2/$Evidencia/cobertura junto con jacoco.exec. Las métricas de esta tabla se calcularon desde el CSV real, no se escribieron como valores supuestos.

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

La ejecución final permitió aceptar técnicamente el alcance automatizado de esta unidad: no se encontraron fallos en las $total pruebas y se superó la meta de cobertura de servicios. Los casos negativos ayudaron a descubrir validaciones y respuestas incompletas que no se observaban en los caminos exitosos. El registro por fases de TDD y la evidencia guardada permiten revisar tanto los defectos como su corrección. El sistema requiere las verificaciones pendientes antes de afirmar condiciones de producción.

## 12. Referencias

- IEEE. IEEE Std 829-1998, sección 4, estructura del plan. Copia institucional: https://people.eecs.ku.edu/~saiedian/Teaching/Stds/829-1998.pdf
- JaCoCo. Maven Plug-in: https://www.jacoco.org/jacoco/trunk/doc/maven.html
- Spring Framework. 7.0 Release Notes, Baseline Upgrades: https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes
- Evidencia primaria del proyecto: archivos TEST-*.xml, maven.log, ejecucion.json y cobertura/jacoco.csv de evidencias/unidad2/$Evidencia.

Los anexos del documento Word contienen el plan de pruebas, el registro TDD y el catálogo completo de casos. Los documentos de Unidad 1 conservan sus resultados históricos y no deben interpretarse como el resumen actualizado de esta ejecución.
"@)
$informe.ToString() | Set-Content "$docs/informe_unidad2.md" -Encoding UTF8
Write-Output "Documentacion generada: $total pruebas, $($casos.Count) casos formales; trazabilidad XML validada."
