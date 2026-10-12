$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" -p "best.ah"
$lines = $out -split "`r?`n"
foreach ($l in $lines) {
    if ($l -match "ImageIcon") {
        Write-Host $l
    }
}
