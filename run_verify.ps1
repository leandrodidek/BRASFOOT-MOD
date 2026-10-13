$javac = (Get-ChildItem -Path "C:\Program Files\Microsoft", "C:\Program Files\Java" -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName)
if (-not $javac) { $javac = (Get-Command javac -ErrorAction SilentlyContinue).Source }
$cp = "C:\Brasfoot22-23\projeto\build_mod;C:\Brasfoot22-23\bf22-23_extracted.jar"
& $javac --release 8 -cp $cp -d "C:\Brasfoot22-23\projeto\build_test" "$PSScriptRoot\TestVerifyAll.java"

$java = "C:\Program Files\Java\jre1.8.0_503\bin\java.exe"
if (-not (Test-Path $java)) { $java = (Get-Command java -ErrorAction SilentlyContinue).Source }
$runCp = "C:\Brasfoot22-23\projeto\build_test;C:\Brasfoot22-23\projeto\build_mod;C:\Brasfoot22-23\bf22-23_extracted.jar"
& $java -cp $runCp mod.brasfoot.TestVerifyAll
