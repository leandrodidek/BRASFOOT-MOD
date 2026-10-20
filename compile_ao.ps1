$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;C:\Brasfoot22-23\projeto\build_mod;C:\Brasfoot22-23\projeto\reflectasm-1.10.1-shaded.jar;C:\Brasfoot22-23\projeto\kryo-4.0.2.jar"
$srcAo = "C:\Brasfoot22-23\projeto\src_full\sources\components\ao.java"
$srcK  = "C:\Brasfoot22-23\projeto\src_full\sources\components\K.java"
$out = "C:\Brasfoot22-23\projeto\build_mod"

Write-Host "Compiling ao.java and K.java with all dependencies..."
& $javac --release 8 -cp $cp -d $out $srcAo $srcK
if ($LASTEXITCODE -eq 0) {
    Write-Host "SUCCESS: compiled ao.java and K.java!"
} else {
    Write-Host "ERROR: compilation failed with code $LASTEXITCODE"
}
