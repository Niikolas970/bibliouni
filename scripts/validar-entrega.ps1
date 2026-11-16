$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$raiz = Split-Path $PSScriptRoot -Parent
Push-Location $raiz
try {
    $docs = Join-Path $raiz 'docs/pruebas'
    $evidencia = Join-Path $docs 'evidencias/unidad2/11-final'
    $meta = Get-Content "$evidencia/ejecucion.json" -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($meta.codigoSalida -ne 0) { throw 'La ejecucion final no fue exitosa.' }
    & git diff --quiet $meta.commitBase -- pom.xml src
    if ($LASTEXITCODE -ne 0) { throw 'El codigo difiere del commit verificado.' }
    & git diff --quiet cd0b899 -- src/test/java/com/bibliouni/bibliouni/BibliouniApplicationTests.java src/test/java/com/bibliouni/bibliouni/model/LibroTest.java src/test/java/com/bibliouni/bibliouni/repository/LibroRepositoryTest.java docs/pruebas/estrategia.md docs/pruebas/escenarios.md docs/pruebas/calidad_iso25010.md
    if ($LASTEXITCODE -ne 0) { throw 'Se alteraron pruebas o documentos originales de U1.' }
    & git merge-base --is-ancestor develop HEAD
    if ($LASTEXITCODE -ne 0) { throw 'develop local ya no es antecesora; revisar integracion.' }
    & git diff --check
    if ($LASTEXITCODE -ne 0) { throw 'Hay errores de espacios en el diff.' }
    $total = 0
    foreach ($archivo in Get-ChildItem "$evidencia/TEST-*.xml") {
        [xml]$xml = Get-Content $archivo.FullName -Raw -Encoding UTF8
        if ([int]$xml.testsuite.failures + [int]$xml.testsuite.errors + [int]$xml.testsuite.skipped -ne 0) {
            throw "Suite final no aprobada: $($archivo.Name)"
        }
        $total += [int]$xml.testsuite.tests
    }
    $resumen = Get-Content "$docs/resultados_unidad2.json" -Raw -Encoding UTF8 | ConvertFrom-Json
    if ($total -ne $resumen.pruebas) { throw 'Conteo de pruebas inconsistente.' }
    $csv = @(Import-Csv "$evidencia/cobertura/jacoco.csv")
    $servicios = @($csv | Where-Object PACKAGE -eq 'com.bibliouni.bibliouni.service')
    if ($servicios.Count -ne 2) { throw 'No se encontraron ambos servicios en cobertura.' }
    $cubiertas = ($servicios | Measure-Object LINE_COVERED -Sum).Sum
    $noCubiertas = ($servicios | Measure-Object LINE_MISSED -Sum).Sum
    if ($cubiertas / ($cubiertas + $noCubiertas) -lt 0.80) { throw 'Cobertura insuficiente.' }
    $casos = @(Import-Csv "$docs/casos_unidad2.csv" -Delimiter '|' -Encoding UTF8)
    if ($casos.Count -ne $resumen.casosFormales -or $casos.Count -lt 15) { throw 'Casos formales inconsistentes.' }
    $markdown = Get-Content "$docs/casos_de_prueba.md" -Raw -Encoding UTF8
    if ([regex]::Matches($markdown, '(?m)^## U2-CP-\d{3}').Count -ne $casos.Count) { throw 'Catalogo Markdown incompleto.' }
    $plan = Get-Content "$docs/plan_de_pruebas.md" -Raw -Encoding UTF8
    if ([regex]::Matches($plan, '(?m)^## \d+\.').Count -ne 16) { throw 'Faltan secciones del plan IEEE 829.' }

    $zip = [System.IO.Compression.ZipFile]::OpenRead("$docs/informe_final_unidad2.docx")
    try {
        foreach ($nombre in @('[Content_Types].xml', '_rels/.rels', 'word/document.xml', 'word/styles.xml', 'word/_rels/document.xml.rels', 'word/footer1.xml')) {
            $entrada = $zip.GetEntry($nombre)
            if ($null -eq $entrada) { throw "Falta parte OOXML: $nombre" }
            $lector = [System.IO.StreamReader]::new($entrada.Open())
            try { [xml]$contenido = $lector.ReadToEnd() } finally { $lector.Dispose() }
            if ($nombre -eq 'word/document.xml') { $documento = $contenido }
        }
        foreach ($caso in $casos) {
            if (-not $documento.DocumentElement.InnerText.Contains($caso.id)) { throw "El Word no contiene $($caso.id)" }
        }
        if (-not $documento.DocumentElement.InnerText.Contains("$total pruebas")) { throw 'El Word no contiene el resultado real.' }
    } finally { $zip.Dispose() }

    # Verificar que los ciclos Red conservaron fallos reales y Green/Refactor terminaron correctamente.
    foreach ($nombre in @('01-libro-red', '04-devolucion-red')) {
        $fase = Get-Content "$docs/evidencias/unidad2/$nombre/ejecucion.json" -Raw -Encoding UTF8 | ConvertFrom-Json
        if ($fase.codigoSalida -eq 0) { throw "Fase Red sin fallo: $nombre" }
    }
    foreach ($nombre in @('00-base', '02-libro-green', '03-libro-refactor', '05-devolucion-green', '06-devolucion-refactor')) {
        $fase = Get-Content "$docs/evidencias/unidad2/$nombre/ejecucion.json" -Raw -Encoding UTF8 | ConvertFrom-Json
        if ($fase.codigoSalida -ne 0) { throw "Fase verde con fallo: $nombre" }
    }
    $registro = [ordered]@{
        fecha=(Get-Date -Format o); codigoVerificado=$meta.commitBase; pruebas=$total
        casosFormales=$casos.Count; seccionesPlan=16; estructuraOOXML='Valida'
        codigoSinCambiosDesdeEjecucion=$true; regresionU1Conservada=$true
        developLocalEsAntecesora=$true; estadoRemoto='No consultado'
        ciclosTDDEvidencia='Red fallido y Green/Refactor aprobados'
    }
    $registro | ConvertTo-Json | Set-Content "$docs/evidencias/unidad2/validacion_entrega.json" -Encoding UTF8
    Write-Output "Entrega validada: $total pruebas; $($casos.Count) casos; 16 secciones IEEE; Word OOXML valido; U1 conservada; codigo coincide con evidencia."
} finally { Pop-Location }
