$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" -c -p "a.dV"
$lines = $out -split "`r?`n"
$found = $false
foreach ($l in $lines) {
    if ($l -match "public a\.dV\(javax\.swing\.JFrame, int\);") {
        $found = $true
    } elseif ($found -and $l -match "^\s*(public|private|protected|static).*\(") {
        $found = $false
    }
    if ($found) {
        Write-Host $l
    }
}
