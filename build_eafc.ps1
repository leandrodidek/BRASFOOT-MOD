$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;C:\Brasfoot22-23\projeto\build_mod"
& $javac --release 8 -cp $cp -d "C:\Brasfoot22-23\projeto\build_mod" "C:\Users\Leandro\.gemini\antigravity-ide\scratch\EaFcUiHelper.java"

if ($LASTEXITCODE -eq 0) {
    Write-Host "SUCCESS: compiled EaFcUiHelper.class!"
} else {
    Write-Host "FAILED compiling EaFcUiHelper.class"
}
