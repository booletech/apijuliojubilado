param(
  [string]$OutputPath = "rubrica-avaliacao-devops.xlsx"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem

function Escape-Xml {
  param([string]$Value)

  if ($null -eq $Value) {
    return ""
  }

  return [System.Security.SecurityElement]::Escape($Value)
}

function Get-ExcelColumnName {
  param([int]$ColumnNumber)

  $name = ""
  $number = $ColumnNumber
  while ($number -gt 0) {
    $remainder = ($number - 1) % 26
    $name = [char](65 + $remainder) + $name
    $number = [math]::Floor(($number - 1) / 26)
  }

  return $name
}

function New-InlineCell {
  param(
    [int]$ColumnNumber,
    [int]$RowNumber,
    [string]$Value
  )

  $cellRef = "$(Get-ExcelColumnName -ColumnNumber $ColumnNumber)$RowNumber"
  $escaped = Escape-Xml -Value $Value
  return "<c r=`"$cellRef`" t=`"inlineStr`"><is><t xml:space=`"preserve`">$escaped</t></is></c>"
}

function Add-TextEntry {
  param(
    [System.IO.Compression.ZipArchive]$Archive,
    [string]$EntryPath,
    [string]$Content
  )

  $entry = $Archive.CreateEntry($EntryPath)
  $stream = $entry.Open()
  try {
    $writer = New-Object System.IO.StreamWriter($stream, (New-Object System.Text.UTF8Encoding($false)))
    try {
      $writer.Write($Content)
    }
    finally {
      $writer.Dispose()
    }
  }
  finally {
    $stream.Dispose()
  }
}

$rows = @(
  [pscustomobject]@{
    Item = "O aluno utilizou Docker para criar containers com imagens da sua aplicação?"
    Avaliacao = "Demonstrou"
    Justificativa = "Há Dockerfiles em frontend, JulioJubiladoapi e juliopedidoapi, e o build das imagens está configurado no docker-compose.yml."
  },
  [pscustomobject]@{
    Item = "O aluno utilizou os recursos básicos do Docker (binds, volumes)?"
    Avaliacao = "Demonstrou"
    Justificativa = "Há volume nomeado sqlserver_data e bind mounts para bootstrap do banco e scripts do Flyway no docker-compose.yml."
  },
  [pscustomobject]@{
    Item = "O aluno utilizou o K8s para rodar seu projeto de forma a conseguir alta disponibilidade?"
    Avaliacao = "Demonstrou"
    Justificativa = "As APIs foram configuradas com múltiplas réplicas no Kubernetes. Ressalva: SQL Server e Elasticsearch seguem com réplica única, então a alta disponibilidade é parcial do stack."
  },
  [pscustomobject]@{
    Item = "O aluno utilizou os recursos primitivos do K8s (Pods, Services, Volumes)?"
    Avaliacao = "Demonstrou"
    Justificativa = "Há Deployments, Services e uso de volume persistente/PVC nos manifestos do diretório k8s."
  },
  [pscustomobject]@{
    Item = "O aluno criou networks no Docker?"
    Avaliacao = "Não demonstrou"
    Justificativa = "O projeto usa a rede padrão automática do Docker Compose, mas não declara um bloco networks explícito no docker-compose.yml."
  },
  [pscustomobject]@{
    Item = "O aluno enviou sua imagem para o DockerHub?"
    Avaliacao = "Demonstrou"
    Justificativa = "O workflow de CD faz docker login e docker push para Docker Hub. O repositório prova a automação, embora não prove sozinho a execução bem-sucedida."
  },
  [pscustomobject]@{
    Item = "O aluno criou PV no K8s?"
    Avaliacao = "Não demonstrou"
    Justificativa = "Não há manifesto kind PersistentVolume no repositório; o que existe são apenas PersistentVolumeClaims."
  },
  [pscustomobject]@{
    Item = "O aluno criou PVC no K8s?"
    Avaliacao = "Demonstrou"
    Justificativa = "Há PVCs para SQL Server e Prometheus nos manifestos Kubernetes."
  },
  [pscustomobject]@{
    Item = "O aluno utilizou Readiness Probe?"
    Avaliacao = "Demonstrou"
    Justificativa = "As APIs e componentes de infraestrutura/monitoramento possuem readinessProbe nos manifestos Kubernetes."
  },
  [pscustomobject]@{
    Item = "O aluno utilizou Liveness Probe?"
    Avaliacao = "Demonstrou"
    Justificativa = "As APIs e componentes de infraestrutura/monitoramento possuem livenessProbe nos manifestos Kubernetes."
  },
  [pscustomobject]@{
    Item = "O aluno desenvolveu stress test via interface gráfica?"
    Avaliacao = "Não demonstrou"
    Justificativa = "Não há artefato de teste de carga via interface gráfica no repositório. O que existe são scripts automatizados."
  },
  [pscustomobject]@{
    Item = "O aluno desenvolveu stress test via script?"
    Avaliacao = "Demonstrou"
    Justificativa = "Existe teste de carga por script usando k6 no arquivo scripts/api-stress-test.js."
  },
  [pscustomobject]@{
    Item = "O aluno exportou as métricas do seu projeto?"
    Avaliacao = "Demonstrou"
    Justificativa = "As duas APIs usam Spring Boot Actuator e Micrometer com registry Prometheus, expondo métricas por actuator/prometheus."
  },
  [pscustomobject]@{
    Item = "O aluno utilizou o Prometheus para fazer o scrape das métricas do seu projeto?"
    Avaliacao = "Demonstrou"
    Justificativa = "Há deployment de Prometheus, configuração de scrape e anotações prometheus.io/scrape nas APIs."
  },
  [pscustomobject]@{
    Item = "O aluno instanciou o Grafana no seu Cluster?"
    Avaliacao = "Demonstrou"
    Justificativa = "Há Service e Deployment do Grafana no Kubernetes."
  },
  [pscustomobject]@{
    Item = "O aluno criou dashboards no Grafana?"
    Avaliacao = "Demonstrou"
    Justificativa = "Existe dashboard versionado no repositório e provisionado via Kustomize/ConfigMap."
  },
  [pscustomobject]@{
    Item = "O aluno criou testes com o JMeter?"
    Avaliacao = "Não demonstrou"
    Justificativa = "Não há arquivos .jmx nem referências a JMeter no repositório."
  },
  [pscustomobject]@{
    Item = "O aluno criou script de teste com o JMeter?"
    Avaliacao = "Não demonstrou"
    Justificativa = "O projeto possui stress test em k6, não em JMeter."
  },
  [pscustomobject]@{
    Item = "O aluno instanciou o Jenkins?"
    Avaliacao = "Não demonstrou"
    Justificativa = "Não há Jenkinsfile nem manifestos de Jenkins. A automação existente usa GitHub Actions."
  },
  [pscustomobject]@{
    Item = "O aluno criou um pipeline de entrega com o Jenkins?"
    Avaliacao = "Não demonstrou"
    Justificativa = "Existe pipeline de entrega, mas ele foi implementado com GitHub Actions e kubectl apply -k, não com Jenkins."
  }
)

$header = [pscustomobject]@{
  Item = "Item"
  Avaliacao = "Avaliação sugerida"
  Justificativa = "Justificativa"
}

$sheetRows = @($header) + $rows
$lastRow = $sheetRows.Count

$sheetDataBuilder = New-Object System.Text.StringBuilder

for ($rowIndex = 0; $rowIndex -lt $sheetRows.Count; $rowIndex++) {
  $excelRow = $rowIndex + 1
  $null = $sheetDataBuilder.Append("<row r=`"$excelRow`">")
  $null = $sheetDataBuilder.Append((New-InlineCell -ColumnNumber 1 -RowNumber $excelRow -Value $sheetRows[$rowIndex].Item))
  $null = $sheetDataBuilder.Append((New-InlineCell -ColumnNumber 2 -RowNumber $excelRow -Value $sheetRows[$rowIndex].Avaliacao))
  $null = $sheetDataBuilder.Append((New-InlineCell -ColumnNumber 3 -RowNumber $excelRow -Value $sheetRows[$rowIndex].Justificativa))
  $null = $sheetDataBuilder.Append("</row>")
}

$worksheetXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <dimension ref="A1:C$lastRow"/>
  <sheetViews>
    <sheetView workbookViewId="0"/>
  </sheetViews>
  <sheetFormatPr defaultRowHeight="18"/>
  <cols>
    <col min="1" max="1" width="55" customWidth="1"/>
    <col min="2" max="2" width="22" customWidth="1"/>
    <col min="3" max="3" width="110" customWidth="1"/>
  </cols>
  <sheetData>
    $sheetDataBuilder
  </sheetData>
</worksheet>
"@

$workbookXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
          xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Rubrica" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>
"@

$workbookRelsXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1"
                Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet"
                Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2"
                Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles"
                Target="styles.xml"/>
</Relationships>
"@

$rootRelsXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1"
                Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument"
                Target="xl/workbook.xml"/>
  <Relationship Id="rId2"
                Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties"
                Target="docProps/core.xml"/>
  <Relationship Id="rId3"
                Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties"
                Target="docProps/app.xml"/>
</Relationships>
"@

$contentTypesXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml"
            ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml"
            ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml"
            ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
  <Override PartName="/docProps/core.xml"
            ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml"
            ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>
"@

$stylesXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="1">
    <font>
      <sz val="11"/>
      <name val="Calibri"/>
      <family val="2"/>
    </font>
  </fonts>
  <fills count="2">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
  </fills>
  <borders count="1">
    <border><left/><right/><top/><bottom/><diagonal/></border>
  </borders>
  <cellStyleXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
  </cellStyleXfs>
  <cellXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
  </cellXfs>
  <cellStyles count="1">
    <cellStyle name="Normal" xfId="0" builtinId="0"/>
  </cellStyles>
</styleSheet>
"@

$createdUtc = (Get-Date).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ")

$coreXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                   xmlns:dc="http://purl.org/dc/elements/1.1/"
                   xmlns:dcterms="http://purl.org/dc/terms/"
                   xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>Rubrica de Avaliação DevOps</dc:title>
  <dc:creator>Codex</dc:creator>
  <cp:lastModifiedBy>Codex</cp:lastModifiedBy>
  <dcterms:created xsi:type="dcterms:W3CDTF">$createdUtc</dcterms:created>
  <dcterms:modified xsi:type="dcterms:W3CDTF">$createdUtc</dcterms:modified>
</cp:coreProperties>
"@

$appXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
            xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>Microsoft Excel</Application>
  <DocSecurity>0</DocSecurity>
  <ScaleCrop>false</ScaleCrop>
  <HeadingPairs>
    <vt:vector size="2" baseType="variant">
      <vt:variant><vt:lpstr>Worksheets</vt:lpstr></vt:variant>
      <vt:variant><vt:i4>1</vt:i4></vt:variant>
    </vt:vector>
  </HeadingPairs>
  <TitlesOfParts>
    <vt:vector size="1" baseType="lpstr">
      <vt:lpstr>Rubrica</vt:lpstr>
    </vt:vector>
  </TitlesOfParts>
</Properties>
"@

$resolvedOutputPath = if ([System.IO.Path]::IsPathRooted($OutputPath)) {
  $OutputPath
}
else {
  Join-Path (Get-Location) $OutputPath
}

if (Test-Path $resolvedOutputPath) {
  Remove-Item -LiteralPath $resolvedOutputPath -Force
}

$fileStream = [System.IO.File]::Create($resolvedOutputPath)
try {
  $archive = New-Object System.IO.Compression.ZipArchive($fileStream, [System.IO.Compression.ZipArchiveMode]::Create, $false)
  try {
    Add-TextEntry -Archive $archive -EntryPath "[Content_Types].xml" -Content $contentTypesXml
    Add-TextEntry -Archive $archive -EntryPath "_rels/.rels" -Content $rootRelsXml
    Add-TextEntry -Archive $archive -EntryPath "docProps/core.xml" -Content $coreXml
    Add-TextEntry -Archive $archive -EntryPath "docProps/app.xml" -Content $appXml
    Add-TextEntry -Archive $archive -EntryPath "xl/workbook.xml" -Content $workbookXml
    Add-TextEntry -Archive $archive -EntryPath "xl/_rels/workbook.xml.rels" -Content $workbookRelsXml
    Add-TextEntry -Archive $archive -EntryPath "xl/styles.xml" -Content $stylesXml
    Add-TextEntry -Archive $archive -EntryPath "xl/worksheets/sheet1.xml" -Content $worksheetXml
  }
  finally {
    $archive.Dispose()
  }
}
finally {
  $fileStream.Dispose()
}

Write-Host "Arquivo gerado em: $resolvedOutputPath"
