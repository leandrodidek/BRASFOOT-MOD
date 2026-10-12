$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" c.b
$currentMethod = ""
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "^\s*(public|protected|private|static).*\(") {
        $currentMethod = $out[$i]
    }
    if ($currentMethod -match "a\(best\.A, best\.F\)") {
        if ($out[$i] -match "Code:") {
            for ($j = $i; $j -le $i + 200; $j++) {
                Write-Output $out[$j]
            }
            break
        }
    }
}
