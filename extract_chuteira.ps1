$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" b.aD
$printing = $false
$count = 0
foreach ($line in $out) {
    if ($line -match "ic_chuteira") {
        $printing = $true
    }
}
# print instructions around ic_chuteira
$idx = 0
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "ic_chuteira") {
        for ($j = [Math]::Max(0, $i - 20); $j -le [Math]::Min($out.Length - 1, $i + 20); $j++) {
            Write-Output $out[$j]
        }
        break
    }
}
