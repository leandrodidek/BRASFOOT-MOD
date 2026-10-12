$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" best.I
foreach ($line in $out) {
    if ($line -match "public int\[\]") {
        Write-Output $line
    }
}
