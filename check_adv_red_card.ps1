$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" a.dV
$inMethod = $false
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "a\(components\.v, best\.I, best\.A, int\)") {
        $inMethod = $true
    }
    if ($inMethod) {
        if ($out[$i] -match "iconst_4|bipush.*4") {
            for ($j = [Math]::Max(0, $i - 5); $j -le [Math]::Min($out.Length - 1, $i + 30); $j++) {
                Write-Output $out[$j]
            }
            Write-Output "---"
        }
        if ($out[$i] -match "^\s*public" -and $out[$i] -notmatch "a\(components\.v") {
            break
        }
    }
}
