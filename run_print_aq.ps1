$javac = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe"
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar"
& $javac --release 8 -cp $cp -d "C:\Brasfoot22-23\projeto\build_test" "C:\Users\Leandro\.gemini\antigravity-ide\scratch\PrintAqArrays.java"

$java = "C:\Program Files\Java\jre1.8.0_503\bin\java.exe"
$runCp = "C:\Brasfoot22-23\projeto\build_test;$cp"
& $java -cp $runCp PrintAqArrays
