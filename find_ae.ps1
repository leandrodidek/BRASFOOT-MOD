$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" -c -p "a.cD"
$lines = $out -split "`r?`n"
$currentMethod = ""
foreach ($l in $lines) {
    if ($l -match "^\s*(public|private|protected|static).*\(") {
        $currentMethod = $l
    }
    if ($l -match "Field AE:Ljava/util/ArrayList;") {
        Write-Host "In $currentMethod : $l"
    }
}
