$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" a.dV
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "invokevirtual.*(wa|wb|wc|wd|we|wf|wg|wh|wi|vZ|vY|vX)") {
        for ($j = [Math]::Max(0, $i - 4); $j -le [Math]::Min($out.Length - 1, $i + 6); $j++) {
            Write-Output $out[$j]
        }
        Write-Output "---"
    }
}
