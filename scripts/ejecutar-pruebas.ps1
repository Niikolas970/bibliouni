param(
    [Parameter(Mandatory = $true)][string]$Nombre,
    [string[]]$Objetivos = @('clean', 'test')
)
$ErrorActionPreference = 'Stop'
$raiz = Split-Path $PSScriptRoot -Parent
Push-Location $raiz
try {
    $destino = Join-Path $raiz "docs/pruebas/evidencias/unidad2/$Nombre"
    New-Item -ItemType Directory -Force $destino | Out-Null
    $inicio = Get-Date -Format o
    & .\mvnw.cmd @Objetivos 2>&1 | ForEach-Object { "$_" } | Tee-Object -FilePath "$destino/maven.log"
    $codigo = $LASTEXITCODE
    Get-ChildItem target/surefire-reports/TEST-*.xml -ErrorAction SilentlyContinue | Copy-Item -Destination $destino
    [ordered]@{ inicio=$inicio; fin=(Get-Date -Format o); comando=(".\mvnw.cmd " + ($Objetivos -join ' ')); codigoSalida=$codigo; commitBase=(git rev-parse HEAD); rama=(git branch --show-current) } | ConvertTo-Json | Set-Content -Encoding UTF8 "$destino/ejecucion.json"
    exit $codigo
} finally { Pop-Location }
