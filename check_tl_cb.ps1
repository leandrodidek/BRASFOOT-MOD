$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" c.b
$currentMethod = ""
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "^\s*(public|protected|private|static).*\(") {
        $currentMethod = $out[$i]
    }
    if ($out[$i] -match "components/s\.tL") {
        Write-Output "Method: $currentMethod"
        for ($j = [Math]::Max(0, $i - 30); $j -le [Math]::Min($out.Length - 1, $i + 30); $j++) {
            Write-Output $out[$j]
        }
    }
}
