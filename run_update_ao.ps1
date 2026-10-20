$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$java = "C:\Program Files\Java\jre1.8.0_503\bin\java.exe"
if (-not (Test-Path $java)) { $java = (Get-Command java -ErrorAction SilentlyContinue).Source }

Write-Host "Compiling helpers first..."
& powershell -ExecutionPolicy Bypass -File "$PSScriptRoot\compile_helpers.ps1"
if ($LASTEXITCODE -ne 0) { exit 1 }

& $javac --release 8 -encoding UTF-8 -d "C:\Brasfoot22-23\projeto\build_test" "$PSScriptRoot\UpdateAoPreMatch.java"
if ($LASTEXITCODE -ne 0) { exit 1 }

& $java -cp "C:\Brasfoot22-23\projeto\build_test" UpdateAoPreMatch
if ($LASTEXITCODE -ne 0) { exit 1 }

& powershell -ExecutionPolicy Bypass -File "$PSScriptRoot\compile_ao.ps1"
if ($LASTEXITCODE -ne 0) { exit 1 }

Copy-Item -Path "C:\Brasfoot22-23\projeto\build_mod\components\ao*.class" -Destination "$PSScriptRoot\base_patches\components\" -Force
Write-Host "SUCCESS: ao.class compiled and synced to base_patches!"
