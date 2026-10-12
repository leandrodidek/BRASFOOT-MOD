$javap = "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javap.exe"
$files = Get-ChildItem -Path "C:\Brasfoot22-23\bf22-23_extracted" -Recurse -Filter "*.class"
foreach ($f in $files) {
    $out = & $javap -c $f.FullName 2>$null
    if ($out -match "Method best/A\.a:\(I\)V") {
        Write-Output "Class: $($f.FullName)"
    }
}
