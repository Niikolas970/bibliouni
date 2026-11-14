param([switch]$Interno)
$ErrorActionPreference = 'Stop'
$raiz = Split-Path $PSScriptRoot -Parent
$docs = Join-Path $raiz 'docs/pruebas'
$registro = Join-Path $docs 'evidencias/unidad2/validacion_word.json'
if (-not $Interno) {
    $inicio = Get-Date
    $idsPrevios = @(Get-Process WINWORD -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Id)
    $salida = Join-Path $docs 'evidencias/unidad2/word_automatizacion.log'
    $errores = Join-Path $docs 'evidencias/unidad2/word_automatizacion_error.log'
    $proceso = Start-Process -FilePath (Join-Path $PSHOME 'powershell.exe') -WindowStyle Hidden -PassThru -ArgumentList @('-NoProfile', '-ExecutionPolicy', 'Bypass', '-File', ('"' + $PSCommandPath + '"'), '-Interno') -RedirectStandardOutput $salida -RedirectStandardError $errores
    if (-not $proceso.WaitForExit(45000)) {
        Stop-Process -Id $proceso.Id -Force -ErrorAction SilentlyContinue
        foreach ($tareaWord in @(Get-CimInstance Win32_Process -Filter "Name = 'WINWORD.EXE'")) {
            if ($tareaWord.ProcessId -notin $idsPrevios -and $tareaWord.CreationDate -ge $inicio -and $tareaWord.CommandLine -match '/Automation -Embedding') {
                Stop-Process -Id $tareaWord.ProcessId -Force -ErrorAction SilentlyContinue
            }
        }
        [ordered]@{ fecha=(Get-Date -Format o); aplicacion='Microsoft Word'; apertura='No verificada: automatizacion excedio 45 segundos'; estructuraOOXML='Verificada por validar-entrega.ps1'; inspeccionVisual='No realizada'; exportacionPDF='No confirmada'; log='word_automatizacion.log' } | ConvertTo-Json | Set-Content -Encoding UTF8 $registro
        Write-Output 'Word no completo la automatizacion en 45 segundos; DOCX validado estructuralmente. Ver log.'
        exit 1
    }
    Get-Content $salida
    if ($proceso.ExitCode -ne 0) { Get-Content $errores; exit $proceso.ExitCode }
    exit 0
}
$word = $null; $documento = $null
try {
    Write-Output 'Inicio: creando instancia Word.Application'
    $word = New-Object -ComObject Word.Application
    Write-Output 'Instancia COM creada'
    $word.Visible = $false
    $word.DisplayAlerts = 0
    $word.AutomationSecurity = 3
    Write-Output 'Abriendo DOCX en lectura'
    $documento = $word.Documents.Open((Join-Path $docs 'informe_final_unidad2.docx'), $false, $true, $false)
    Write-Output 'DOCX abierto; repaginando'
    $documento.Repaginate()
    $paginas = $documento.ComputeStatistics(2)
    $tablas = $documento.Tables.Count
    Write-Output "Documento repaginado: $paginas paginas, $tablas tablas"
    [ordered]@{ fecha=(Get-Date -Format o); aplicacion='Microsoft Word'; version=$word.Version; apertura='Correcta, lectura'; paginas=$paginas; tablas=$tablas; inspeccionVisual='No realizada; apertura y paginacion automatizadas' } | ConvertTo-Json | Set-Content -Encoding UTF8 $registro
    Write-Output 'Apertura y paginacion verificadas correctamente'
} finally {
    if ($null -ne $documento) { $documento.Close(0); [void][System.Runtime.InteropServices.Marshal]::ReleaseComObject($documento) }
    if ($null -ne $word) { $word.Quit(); [void][System.Runtime.InteropServices.Marshal]::ReleaseComObject($word) }
}
