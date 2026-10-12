$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" -c -p "a.dV"
$lines = $out -split "`r?`n"
$current = ""
foreach ($l in $lines) {
    if ($l -match "^\s*(public|private|protected|static).*\(") {
        $current = $l
    }
    if ($l -match "a\(Lcomponents/v;Lbest/I;Lbest/A;I\)V") {
        Write-Host "In dV: $current -> $l"
    }
}
