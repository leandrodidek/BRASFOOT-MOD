$javac = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\javac.exe"
$java = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe"

& $javac -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" -d . "InspectBzB.java"
& $java -cp ".;C:\Brasfoot22-23\bf22-23_extracted.jar" mod.brasfoot.InspectBzB
