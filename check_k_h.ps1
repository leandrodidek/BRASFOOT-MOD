$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" best.k
$currentMethod = ""
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "^\s*(public|protected|private|static).*\(") {
        $currentMethod = $out[$i]
    }
    if ($out[$i] -match "putfield.*(Pe|Pf|cc)") {
        Write-Output "In best.k: $currentMethod -> $($out[$i])"
    }
}
$outH = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" best.H
for ($i=0; $i -lt $outH.Length; $i++) {
    if ($outH[$i] -match "^\s*(public|protected|private|static).*\(") {
        $currentMethod = $outH[$i]
    }
    if ($outH[$i] -match "putfield") {
        Write-Output "In best.H: $currentMethod -> $($outH[$i])"
    }
}
