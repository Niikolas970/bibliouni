# BiblioUni — entrega de Unidad 2

Abrir informe_final_unidad2.docx para el informe con anexos. Los documentos editables son informe_unidad2.md, plan_de_pruebas.md, tdd.md y casos_unidad2.csv. casos_de_prueba.md se genera desde el CSV y los XML de ejecución.

Resultados: 70 pruebas aprobadas; servicios 100 % de líneas y ramas; global 87,77 % de líneas. Ver resultados_unidad2.json y evidencias/unidad2/11-final/cobertura/index.html. Los resultados Red y el fallo inicial de integración se conservan y no representan el estado final.

Reproducción desde la raíz:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean verify
```

El primero genera cobertura en target/site/jacoco. El segundo también exige 80 % de líneas de servicios. Para guardar una ejecución nueva sin sobrescribir la entregada:

```powershell
.\scripts\ejecutar-pruebas.ps1 -Nombre ensayo-nuevo -Objetivos clean,verify
```

Para regenerar documentos desde la evidencia final conservada y producir Word sin dependencias externas:

```powershell
.\scripts\generar-documentacion.ps1
.\scripts\generar-word.ps1
```

validar-word.ps1 es opcional y requiere Microsoft Word instalado: abre el DOCX en modo lectura y comprueba la paginación con un tiempo máximo de 45 segundos. La generación del DOCX utiliza .NET y no requiere Office. Ejecutar validar-entrega.ps1 para revisar la correspondencia de documentos, evidencia y código.

La versión efectiva es Jupiter 6.0.3, administrada por Boot 4.1.2; no se afirma una ejecución con JUnit 5. Consultar las limitaciones en el informe y el plan.

Los documentos y capturas de Unidad 1 se conservan como antecedentes. El borrador del PR está en pull_request.md. No se ha publicado ningún cambio.

La apertura en Microsoft Word 16.0 fue comprobada: 27 páginas y 12 tablas. Registro: evidencias/unidad2/validacion_word.json. No se realizó inspección visual. Un intento opcional de exportación PDF excedió el tiempo disponible y se conserva en word_intento_exportacion; no se entrega un PDF como resultado verificado.
