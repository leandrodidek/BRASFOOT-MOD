$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" -c -p "a.bz"
$lines = $out -split "`r?`n"
$found = $false
foreach ($l in $lines) {
    if ($l -match "void mJ\(") {
        $found = $true
    } elseif ($found -and $l -match "kj\(") {
        Write-Host $l
    }
}
