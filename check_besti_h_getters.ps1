$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" best.I
for ($i=0; $i -lt $out.Length; $i++) {
    if ($out[$i] -match "public int\[\] h(A|B|C|D|Q|T|Y|Z)") {
        for ($j = $i; $j -le $i + 5; $j++) {
            Write-Output $out[$j]
        }
        Write-Output "---"
    }
}
