$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" a.dV
foreach ($line in $out) {
    if ($line -match "^\s*public.*\(|^\s*private.*\(|^\s*protected.*\(|^\s*static.*\(|^\s*void") {
        Write-Output $line
    }
}
