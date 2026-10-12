$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -cp "C:\Brasfoot22-23\projeto\build_mod" -c -p "a.dV"
$lines = $out -split "`r?`n"
$found = $false
foreach ($l in $lines) {
    if ($l -match "void a\(components\.v") {
        $found = $true
        Write-Host "============================"
    } elseif ($found -and $l -match "^  [a-z]") {
        if (!($l -match "void a\(")) { $found = $false }
    }
    if ($found) {
        Write-Host $l
    }
}
