# Script para abrir o preview interativo em janela Swing na tela do usuario
$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$java = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\java.exe"

& $javac -encoding UTF-8 -cp "C:\Brasfoot22-23\projeto\build_mod;C:\Brasfoot22-23\bf22-23_extracted.jar" -d C:\Brasfoot22-23\projeto\build_mod LiveMatchPreviewRenderer.java
& $java -cp "C:\Brasfoot22-23\projeto\build_mod;C:\Brasfoot22-23\bf22-23_extracted.jar" mod.brasfoot.LiveMatchPreviewRenderer
