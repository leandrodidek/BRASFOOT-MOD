$files = Get-ChildItem -Path "C:\Brasfoot22-23\bf22-23_extracted" -Recurse -Filter "*.class"
foreach ($f in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($f.FullName)
    $str = [System.Text.Encoding]::UTF8.GetString($bytes)
    if ($str -match "desarmes|faltas|posse de bola") {
        Write-Output "Found in: $($f.FullName)"
    }
}
