$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$out = & $javap -c -p -cp "C:\Brasfoot22-23\bf22-23_extracted.jar" c.b
$printing = $false
$count = 0
foreach ($line in $out) {
    if ($line -match "areturn") {
        # find the areturn of vT
    }
}
# Let's inspect where best/A is constructed in c.b
foreach ($line in $out) {
    if ($line -match "new.*class best/A") {
        Write-Output $line
    }
}
