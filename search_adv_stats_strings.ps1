$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" a.dV
$current = ""
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "^\s*(public|private|protected).*\(") {
        $current = $out[$i]
    }
    if ($out[$i] -match "Finaliza|Desarmes|Erros|Faltas") {
        Write-Output "$current -> $($out[$i])"
    }
}
