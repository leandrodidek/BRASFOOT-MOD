$files = Get-ChildItem -Path "C:\Brasfoot22-23\bf22-23_extracted" -Recurse -Filter "*.class"
$enc = [System.Text.Encoding]::GetEncoding("ISO-8859-1")
foreach ($f in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($f.FullName)
    $str = $enc.GetString($bytes)
    if ($str -match "desarmes|finaliza|erros passes") {
        Write-Output "Matched: $($f.FullName)"
    }
}
