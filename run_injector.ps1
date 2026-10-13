$javac = (Get-ChildItem -Path "C:\Program Files\Microsoft", "C:\Program Files\Java" -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName)
if (-not $javac) { $javac = (Get-Command javac -ErrorAction SilentlyContinue).Source }
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;C:\Brasfoot22-23\projeto\build_mod;C:\Brasfoot22-23\projeto\reflectasm-1.10.1-shaded.jar"
$buildTest = "C:\Brasfoot22-23\projeto\build_test"

Write-Host "Compiling FullEaFcInjector.java..."
& $javac --release 8 -encoding UTF-8 -cp $cp -d $buildTest "$PSScriptRoot\FullEaFcInjector.java"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation of FullEaFcInjector failed!" -ForegroundColor Red
    exit 1
}

$java = "C:\Program Files\Java\jre1.8.0_503\bin\java.exe"
if (-not (Test-Path $java)) { $java = (Get-Command java -ErrorAction SilentlyContinue).Source }
$runCp = "$buildTest;$cp"
Write-Host "Running FullEaFcInjector..."
& $java -cp $runCp mod.brasfoot.FullEaFcInjector
