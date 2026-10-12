$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" best.F
$currentMethod = ""
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "^\s*(public|protected|private|static).*\(") {
        $currentMethod = $out[$i]
    }
    if ($currentMethod -match "a\(int, best\.ah, best\.at\)") {
        Write-Output $out[$i]
    }
}
