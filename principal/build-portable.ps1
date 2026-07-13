param(
    [string]$AppName = "Game2D",
    [string]$AppVersion = "1.0.0",
    [string]$JarName = "principal-1.0-SNAPSHOT.jar",
    [switch]$UseMaven,
    [switch]$SkipMaven
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$javaHome = "C:\Program Files\Java\jdk-21"
$javac = Join-Path $javaHome "bin\javac.exe"
$jar = Join-Path $javaHome "bin\jar.exe"
$jpackage = Join-Path $javaHome "bin\jpackage.exe"
$maven = "C:\Program Files (x86)\apache-maven-3.9.16\bin\mvn.cmd"
$sourceJavaDir = Join-Path $projectRoot "src\main\java"
$sourceResourcesDir = Join-Path $projectRoot "src\main\resources"
$targetDir = Join-Path $projectRoot "target"
$distDir = Join-Path $projectRoot "dist"
$portableDir = Join-Path $distDir $AppName
$portableZip = Join-Path $distDir ($AppName + "-portable.zip")
$offlineBuildDir = Join-Path $projectRoot "build\portable"
$offlineClassesDir = Join-Path $offlineBuildDir "classes"

if (-not (Test-Path $jpackage)) {
    throw "No se encontro jpackage en $jpackage"
}

if (-not (Test-Path $javac)) {
    throw "No se encontro javac en $javac"
}

if (-not (Test-Path $jar)) {
    throw "No se encontro jar en $jar"
}

if ($UseMaven -and -not (Test-Path $maven)) {
    throw "No se encontro Maven en $maven"
}

function Remove-ProjectPath {
    param(
        [string]$PathToRemove
    )

    if (-not (Test-Path $PathToRemove)) {
        return
    }

    $resolvedPath = (Resolve-Path $PathToRemove).Path
    if (-not $resolvedPath.StartsWith($projectRoot, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "La ruta resuelta queda fuera del proyecto: $resolvedPath"
    }

    Remove-Item -LiteralPath $resolvedPath -Recurse -Force
}

function Invoke-MavenBuild {
    & $maven -q clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo el empaquetado Maven."
    }
}

function Invoke-OfflineBuild {
    Remove-ProjectPath $offlineBuildDir
    New-Item -ItemType Directory -Force -Path $offlineClassesDir | Out-Null
    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null

    $javaFiles = Get-ChildItem -Path $sourceJavaDir -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
    if ($javaFiles.Count -eq 0) {
        throw "No se encontraron archivos Java en $sourceJavaDir"
    }

    & $javac --release 21 -d $offlineClassesDir $javaFiles
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo la compilacion con javac."
    }

    if (Test-Path $sourceResourcesDir) {
        Copy-Item -Path (Join-Path $sourceResourcesDir "*") -Destination $offlineClassesDir -Recurse -Force
    }

    $jarPath = Join-Path $targetDir $JarName
    if (Test-Path $jarPath) {
        Remove-Item -LiteralPath $jarPath -Force
    }

    & $jar --create --file $jarPath --main-class main.game.Main -C $offlineClassesDir .
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo la creacion del jar."
    }
}

if (-not $SkipMaven) {
    if ($UseMaven) {
        Invoke-MavenBuild
    } else {
        Invoke-OfflineBuild
    }
}

$jarPath = Join-Path $targetDir $JarName
if (-not (Test-Path $jarPath)) {
    throw "No se encontro el jar esperado: $jarPath"
}

New-Item -ItemType Directory -Force -Path $distDir | Out-Null

Remove-ProjectPath $portableDir

if (Test-Path $portableZip) {
    Remove-Item -LiteralPath $portableZip -Force
}

& $jpackage `
    --type app-image `
    --name $AppName `
    --app-version $AppVersion `
    --input $targetDir `
    --main-jar $JarName `
    --main-class main.game.Main `
    --dest $distDir `
    --vendor "Game2D" `
    --description "Juego portable de Game2D"

if ($LASTEXITCODE -ne 0) {
    throw "Fallo jpackage."
}

Compress-Archive -Path $portableDir -DestinationPath $portableZip -Force

Write-Host ""
Write-Host "Portable generado en:"
Write-Host "  $portableDir"
Write-Host ""
Write-Host "ZIP generado en:"
Write-Host "  $portableZip"
