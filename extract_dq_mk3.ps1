$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" a.dq
$printing = $false
$count = 0
foreach ($line in $out) {
    if ($line -match "228: ") {
        $printing = $true
    }
    if ($printing) {
        Write-Output $line
        $count++
        if ($count -gt 80) {
            break
        }
    }
}
