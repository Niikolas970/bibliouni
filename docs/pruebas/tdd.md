# Registro TDD — Unidad 2

Trabajo individual sobre `feature/plan-tests`. Se registran ciclos realizados durante esta entrega; no se atribuye TDD al código que ya existía. Los tres ensayos originales de préstamos y la lógica inicial de multa se recibieron en el área de trabajo y se conservaron en la base `0aac3cf`.

Cada carpeta de `evidencias/unidad2` contiene salida completa de Maven, XML de Surefire y un `ejecucion.json` con comando, horas reales, código de salida y commit base. El commit base es el HEAD anterior a la ejecución: los cambios probados se incorporan en el commit de la fase que aparece abajo. No representa por sí solo el árbol probado.

## Funcionalidad 1: rechazar libros con datos obligatorios ausentes

Historia: como bibliotecario, necesito impedir registros sin título, autor o ISBN antes de consultar o escribir en la base de datos.

| Fase | Cambio y resultado observado | Commit | Evidencia |
|---|---|---|---|
| Red | Se añadieron siete entradas: libro nulo, y título, autor o ISBN nulos/blancos. 13 pruebas ejecutadas; 7 fallos de aserción, 0 errores. Antes se producía NullPointerException o se aceptaba el dato inválido. | `8f4a04f` | `01-libro-red` |
| Green | Se añadió una condición explícita que produce IllegalArgumentException antes de acceder al repositorio. 13 aprobadas. | `825b425` | `02-libro-green` |
| Refactor | Se extrajo `estaVacio` para evitar repetir la comprobación null/isBlank sin cambiar la regla. 13 aprobadas. | `262142b` | `03-libro-refactor` |

Comando en cada fase: `.\mvnw.cmd clean test -Dtest=LibroServiceTest`.

## Funcionalidad 2: registrar una devolución una sola vez

Historia: como bibliotecario, necesito registrar la fecha de devolución y recuperar un ejemplar sin aumentar dos veces la disponibilidad por el mismo préstamo.

| Fase | Cambio y resultado observado | Commit | Evidencia |
|---|---|---|---|
| Red | Se añadieron dos pruebas y el contrato mínimo `registrarDevolucion`, cuyo cuerpo lanzaba UnsupportedOperationException. El contrato permitió compilar antes de implementar el comportamiento. Se ejecutaron 5 pruebas: 1 fallo de aserción y 1 error por operación sin implementar. | `75f8f47` | `04-devolucion-red` |
| Green | Se buscó el préstamo, se rechazó el estado devuelto y se guardaron la fecha y el incremento de disponibilidad. 5 aprobadas. | `8016651` | `05-devolucion-green` |
| Refactor | Se centralizó el incremento/descuento en `ajustarDisponibilidad`; se conservó la transacción. 5 aprobadas. | `27ccb71` | `06-devolucion-refactor` |

Comando en cada fase: `.\mvnw.cmd clean test -Dtest=PrestamoServiceTest`.

## Ampliación posterior

Después de estos ciclos se añadieron validaciones de identificadores y fechas, protección de desbordamiento, consulta de préstamos y pruebas H2/HTTP. Esas ampliaciones no se presentan como ciclos TDD completos. La devolución calcula su multa a través de `calcularMulta(prestamo, prestamo.getFechaDevolucion())`; no registra pagos ni utiliza una API externa.

La ejecución `08-integracion` falló porque el cuerpo `{}` no podía convertirse a Libro y la respuesta 400 no incluía el mensaje JSON esperado. Se conservó esa evidencia y se añadió manejo explícito de HttpMessageNotReadableException. `09-integracion-corregida` aprobó 67 pruebas. Después se reforzó la consulta de stock persistido, cuya verificación se conserva por separado.

## Reproducción del historial

Para revisar cada fase puede utilizarse `git show <commit>` y su evidencia incluida. Para volver a ejecutar una fase antigua sin alterar la rama actual, crear un worktree temporal en el commit elegido y utilizar el comando documentado. Los commits Red deben fallar: ese fallo es parte de la evidencia, no el estado final del entregable. Los resultados originales no deben sobrescribirse al repetir pruebas; usar un nombre de ejecución nuevo con `scripts/ejecutar-pruebas.ps1`.
