$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;C:\Brasfoot22-23\projeto\build_mod"
$src = "C:\Brasfoot22-23\projeto\src_full\sources\a\C0114eg.java"

Write-Host "Testing compilation of C0114eg.java..."
& $javac --release 8 -cp $cp -d "C:\Brasfoot22-23\projeto\build_test" $src 2>&1
