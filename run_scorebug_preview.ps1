$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$java = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\java.exe"

& $javac -d . RenderScorebugPreview.java
& $java -cp . mod.brasfoot.RenderScorebugPreview
