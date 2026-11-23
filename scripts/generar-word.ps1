# Genera un DOCX OOXML con .NET, sin instalar paquetes ni requerir Microsoft Word.
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$raiz = Split-Path $PSScriptRoot -Parent
$docs = Join-Path $raiz 'docs/pruebas'
$destino = Join-Path $docs 'informe_final_unidad2.docx'
$cuerpo = [System.Text.StringBuilder]::new()

function Escapar([string]$valor) { return [System.Security.SecurityElement]::Escape($valor) }
function TextoPlano([string]$valor) {
    $valor = [regex]::Replace($valor, '\[([^\]]+)\]\(([^)]+)\)', '$1 ($2)')
    return $valor.Replace('**', '').Replace('`', '')
}
function Parrafo([string]$valor, [string]$estilo = 'Normal', [bool]$salto = $false) {
    $prop = '<w:pStyle w:val="' + $estilo + '"/>'
    if ($salto) { $prop += '<w:pageBreakBefore/>' }
    [void]$cuerpo.Append('<w:p><w:pPr>' + $prop + '</w:pPr><w:r><w:t xml:space="preserve">' + (Escapar (TextoPlano $valor)) + '</w:t></w:r></w:p>')
}
function Tabla($lineas) {
    [void]$cuerpo.Append('<w:tbl><w:tblPr><w:tblW w:w="0" w:type="auto"/><w:tblBorders><w:top w:val="single" w:sz="4" w:color="CCD5DF"/><w:left w:val="single" w:sz="4" w:color="CCD5DF"/><w:bottom w:val="single" w:sz="4" w:color="CCD5DF"/><w:right w:val="single" w:sz="4" w:color="CCD5DF"/><w:insideH w:val="single" w:sz="4" w:color="CCD5DF"/><w:insideV w:val="single" w:sz="4" w:color="CCD5DF"/></w:tblBorders><w:tblCellMar><w:top w:w="75" w:type="dxa"/><w:left w:w="90" w:type="dxa"/><w:bottom w:w="75" w:type="dxa"/><w:right w:w="90" w:type="dxa"/></w:tblCellMar></w:tblPr>')
    $columnasTabla = $lineas[0].Trim().Trim('|').Split('|').Count
    [void]$cuerpo.Append('<w:tblGrid>')
    for ($columna = 0; $columna -lt $columnasTabla; $columna++) { [void]$cuerpo.Append('<w:gridCol w:w=' + [char]34 + [int](9500 / $columnasTabla) + [char]34 + '/>') }
    [void]$cuerpo.Append('</w:tblGrid>')
    $indiceFila = 0
    foreach ($linea in $lineas) {
        if ($linea -match '^\|[\s:|\-]+\|$') { continue }
        $celdas = $linea.Trim().Trim('|').Split('|')
        [void]$cuerpo.Append('<w:tr>')
        if ($indiceFila -eq 0) { [void]$cuerpo.Append('<w:trPr><w:tblHeader/></w:trPr>') }
        foreach ($celda in $celdas) {
            $color = ''; $negrita = ''
            if ($indiceFila -eq 0) { $color = '<w:shd w:fill="17365D"/>'; $negrita = '<w:b/><w:color w:val="FFFFFF"/>' }
            elseif ($indiceFila % 2 -eq 0) { $color = '<w:shd w:fill="F0F4F8"/>' }
            [void]$cuerpo.Append('<w:tc><w:tcPr><w:tcW w:w="' + [int](9500 / $celdas.Count) + '" w:type="dxa"/>' + $color + '</w:tcPr><w:p><w:pPr><w:spacing w:after="40"/></w:pPr><w:r><w:rPr><w:sz w:val="18"/>' + $negrita + '</w:rPr><w:t xml:space="preserve">' + (Escapar (TextoPlano $celda.Trim())) + '</w:t></w:r></w:p></w:tc>')
        }
        [void]$cuerpo.Append('</w:tr>'); $indiceFila++
    }
    [void]$cuerpo.Append('</w:tbl><w:p/>')
}

Parrafo 'BIBLIOUNI' 'Title'
Parrafo 'Pruebas de Software - Unidad 2' 'Subtitle'
Parrafo 'Informe final y anexos verificables' 'Subtitle'
Parrafo 'Trabajo individual'
Parrafo 'Responsable: Nicolas Pirajan (identidad de Git)'
Parrafo '14 de noviembre de 2026'
Parrafo 'Rama: feature/plan-tests'
Parrafo 'Contenido: informe de resultados, plan IEEE 829-1998, registro TDD y 40 casos formales.'
Parrafo 'Estado: preparado para revision academica. Los commits y las evidencias son locales.'
foreach ($nombre in @('informe_unidad2.md', 'plan_de_pruebas.md', 'tdd.md', 'casos_de_prueba.md')) {
    $lineas = @(Get-Content (Join-Path $docs $nombre) -Encoding UTF8)
    $enCodigo = $false
    for ($i = 0; $i -lt $lineas.Count; $i++) {
        $linea = $lineas[$i].Trim()
        if (-not $linea) { continue }
        if ($linea.StartsWith('```')) { $enCodigo = -not $enCodigo; continue }
        if ($enCodigo) { Parrafo $linea 'Code'; continue }
        if ($linea.StartsWith('|')) {
            $filas = [System.Collections.Generic.List[string]]::new()
            while ($i -lt $lineas.Count -and $lineas[$i].Trim().StartsWith('|')) {
                $filas.Add($lineas[$i].Trim()); $i++
            }
            $i--; Tabla $filas; continue
        }
        if ($linea -match '^(#{1,3})\s+(.+)$') {
            Parrafo $Matches[2] ('Heading' + $Matches[1].Length) ($Matches[1].Length -eq 1)
        } elseif ($linea.StartsWith('- ')) {
            Parrafo ([char]0x2022 + ' ' + $linea.Substring(2))
        } else { Parrafo $linea }
    }
}
$sect = '<w:sectPr><w:footerReference w:type="default" r:id="rId2"/><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1134" w:footer="567"/></w:sectPr>'
$documento = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"><w:body>' + $cuerpo.ToString() + $sect + '</w:body></w:document>'
$estilos = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
<w:docDefaults><w:rPrDefault><w:rPr><w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/><w:sz w:val="22"/><w:lang w:val="es-CO"/></w:rPr></w:rPrDefault><w:pPrDefault><w:pPr><w:spacing w:after="120" w:line="276" w:lineRule="auto"/><w:widowControl/></w:pPr></w:pPrDefault></w:docDefaults>
<w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:name w:val="Normal"/></w:style>
<w:style w:type="paragraph" w:styleId="Title"><w:name w:val="Title"/><w:basedOn w:val="Normal"/><w:pPr><w:spacing w:before="1800" w:after="300"/></w:pPr><w:rPr><w:b/><w:color w:val="17365D"/><w:sz w:val="60"/></w:rPr></w:style>
<w:style w:type="paragraph" w:styleId="Subtitle"><w:name w:val="Subtitle"/><w:basedOn w:val="Normal"/><w:rPr><w:color w:val="425B76"/><w:sz w:val="32"/></w:rPr></w:style>
<w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="heading 1"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:pPr><w:keepNext/><w:spacing w:before="240" w:after="180"/><w:outlineLvl w:val="0"/></w:pPr><w:rPr><w:b/><w:color w:val="17365D"/><w:sz w:val="34"/></w:rPr></w:style>
<w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="heading 2"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:pPr><w:keepNext/><w:spacing w:before="200" w:after="100"/><w:outlineLvl w:val="1"/></w:pPr><w:rPr><w:b/><w:color w:val="17365D"/><w:sz w:val="26"/></w:rPr></w:style>
<w:style w:type="paragraph" w:styleId="Heading3"><w:name w:val="heading 3"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:pPr><w:keepNext/><w:outlineLvl w:val="2"/></w:pPr><w:rPr><w:b/><w:sz w:val="23"/></w:rPr></w:style>
<w:style w:type="paragraph" w:styleId="Code"><w:name w:val="Code"/><w:basedOn w:val="Normal"/><w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/><w:sz w:val="18"/></w:rPr></w:style>
</w:styles>
'@
$partes = @{
    '[Content_Types].xml' = '<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/><Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/><Override PartName="/word/footer1.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml"/></Types>'
    '_rels/.rels' = '<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>'
    'word/document.xml' = $documento
    'word/styles.xml' = $estilos
    'word/_rels/document.xml.rels' = '<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/><Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/footer" Target="footer1.xml"/></Relationships>'
    'word/footer1.xml' = '<?xml version="1.0" encoding="UTF-8"?><w:ftr xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:p><w:pPr><w:jc w:val="right"/></w:pPr><w:r><w:rPr><w:sz w:val="18"/><w:color w:val="667788"/></w:rPr><w:t>BiblioUni - Unidad 2 | </w:t></w:r><w:r><w:fldChar w:fldCharType="begin"/></w:r><w:r><w:instrText> PAGE </w:instrText></w:r><w:r><w:fldChar w:fldCharType="end"/></w:r></w:p></w:ftr>'
}
$flujo = [System.IO.File]::Open($destino, [System.IO.FileMode]::Create)
$zip = [System.IO.Compression.ZipArchive]::new($flujo, [System.IO.Compression.ZipArchiveMode]::Create)
try {
    foreach ($nombre in $partes.Keys) {
        [xml]$validacion = $partes[$nombre]
        $entrada = $zip.CreateEntry($nombre)
        $escritor = [System.IO.StreamWriter]::new($entrada.Open(), [System.Text.UTF8Encoding]::new($false))
        try { $escritor.Write($partes[$nombre]) } finally { $escritor.Dispose() }
    }
} finally { $zip.Dispose(); $flujo.Dispose() }
Write-Output "Word generado: $destino"
