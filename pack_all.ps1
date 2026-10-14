$javac = @(
    "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe",
    (Get-ChildItem -Path "C:\Program Files\Microsoft", "C:\Program Files\Java", "C:\Users\Leandro\.antigravity-ide" -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName),
    (Get-Command javac -ErrorAction SilentlyContinue).Source
) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1

# 1. Ensure extracted base jar exists
$extractedJar = "C:\Brasfoot22-23\bf22-23_extracted.jar"
if (-not (Test-Path $extractedJar)) {
    Write-Host "Extracting base JAR from bf22-23.exe..."
    $fsIn = [System.IO.File]::OpenRead("C:\Brasfoot22-23\bf22-23.exe")
    $fsIn.Seek(125440, [System.IO.SeekOrigin]::Begin)
    $fsOut = [System.IO.File]::Create($extractedJar)
    $fsIn.CopyTo($fsOut)
    $fsOut.Close()
    $fsIn.Close()
    Write-Host "Extracted base JAR ($((Get-Item $extractedJar).Length) bytes)."
}

# 2. Ensure directories exist and copy base patches
$buildMod = "C:\Brasfoot22-23\projeto\build_mod"
$buildTest = "C:\Brasfoot22-23\projeto\build_test"
if (-not (Test-Path $buildMod)) { New-Item -ItemType Directory -Path $buildMod -Force | Out-Null }
if (-not (Test-Path $buildTest)) { New-Item -ItemType Directory -Path $buildTest -Force | Out-Null }

Write-Host "Syncing base_patches to build_mod..."
Copy-Item -Path "$PSScriptRoot\base_patches\*" -Destination "$buildMod\" -Recurse -Force

# 3. Compile helper classes
Write-Host "Compiling helper classes..."
& powershell -ExecutionPolicy Bypass -File "$PSScriptRoot\compile_helpers.ps1"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Helper compilation failed!" -ForegroundColor Red
    exit 1
}

# 3.5 Run FullEaFcInjector to inject bytecode patches into build_mod
Write-Host "Running FullEaFcInjector bytecode patches..."
& powershell -ExecutionPolicy Bypass -File "$PSScriptRoot\run_injector.ps1"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Injector execution failed!" -ForegroundColor Red
    exit 1
}

# 4. Compile EnhancedJarPacker
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;$buildMod"
Write-Host "Compiling EnhancedJarPacker..."
& $javac --release 8 -encoding UTF-8 -cp $cp -d $buildTest "$PSScriptRoot\EnhancedJarPacker.java"

# 5. Run EnhancedJarPacker
$java = "C:\Program Files\Java\jre1.8.0_503\bin\java.exe"
if (-not (Test-Path $java)) { $java = (Get-Command java -ErrorAction SilentlyContinue).Source }
$runCp = "$buildTest;$cp"
Write-Host "Building modded JAR..."
& $java -cp $runCp mod.brasfoot.EnhancedJarPacker

# 6. Repack into executable
if ($LASTEXITCODE -eq 0) {
    Write-Host "Repacking executable..."
    & powershell -ExecutionPolicy Bypass -File "$PSScriptRoot\repack.ps1"
}
