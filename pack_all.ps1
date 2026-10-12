$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;C:\Brasfoot22-23\projeto\build_mod"
& $javac --release 8 -cp $cp -d "C:\Brasfoot22-23\projeto\build_test" "C:\Users\Leandro\.gemini\antigravity-ide\scratch\EnhancedJarPacker.java"

$java = "C:\Program Files\Java\jre1.8.0_503\bin\java.exe"
$runCp = "C:\Brasfoot22-23\projeto\build_test;$cp"
& $java -cp $runCp mod.brasfoot.EnhancedJarPacker

if ($LASTEXITCODE -eq 0) {
    Write-Host "Repacking executable..."
    & powershell -ExecutionPolicy Bypass -File "C:\Users\Leandro\.gemini\antigravity-ide\scratch\repack.ps1"
}
