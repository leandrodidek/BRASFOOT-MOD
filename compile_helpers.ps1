$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;C:\Brasfoot22-23\projeto\build_mod"
$out = "C:\Brasfoot22-23\projeto\build_mod"

Write-Host "Compiling mod helper classes..."
& $javac --release 8 -cp $cp -d $out `
    "C:\Users\Leandro\.gemini\antigravity-ide\scratch\SubstitutionManager.java" `
    "C:\Users\Leandro\.gemini\antigravity-ide\scratch\VarManager.java" `
    "C:\Users\Leandro\.gemini\antigravity-ide\scratch\VarDialog.java" `
    "C:\Users\Leandro\.gemini\antigravity-ide\scratch\ModBridge.java" `
    "C:\Users\Leandro\.gemini\antigravity-ide\scratch\EaFcUiHelper.java" `
    "C:\Users\Leandro\.gemini\antigravity-ide\scratch\VarOptionsHelper.java"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Classes successfully compiled into $out!" -ForegroundColor Green
} else {
    Write-Host "Compilation failed!" -ForegroundColor Red
}
