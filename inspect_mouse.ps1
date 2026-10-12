$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" -c -p "a.cD"
$lines = $out -split "`r?`n"
$found = $false
foreach ($l in $lines) {
    if ($l -match "void d\(java.awt.event.MouseEvent\)" -or $l -match "void b\(java.awt.event.MouseEvent\)") {
        $found = $true
        Write-Host "============================"
    } elseif ($found -and $l -match "^\s*(public|private|protected|static).*\(") {
        if (!($l -match "void d\(" -or $l -match "void b\(")) { $found = $false }
    }
    if ($found) {
        Write-Host $l
    }
}
