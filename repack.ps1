$originalExe = "C:\Brasfoot22-23\bf22-23_original.exe"
$exeSource = "C:\Brasfoot22-23\bf22-23.exe"
$jarSource = "C:\Brasfoot22-23\bf22-23_modded.jar"
$exeModded = "C:\Brasfoot22-23\bf22-23_modded.exe"

if (-not (Test-Path $originalExe)) {
    Write-Host "Creating backup: $originalExe..."
    Copy-Item $exeSource $originalExe -Force
}

$allExe = [System.IO.File]::ReadAllBytes($originalExe)
$header = New-Object byte[] 125440
[System.Array]::Copy($allExe, 0, $header, 0, 125440)

$jarBytes = [System.IO.File]::ReadAllBytes($jarSource)

# 1. Escreve bf22-23_modded.exe
$fs = [System.IO.File]::Create($exeModded)
$fs.Write($header, 0, $header.Length)
$fs.Write($jarBytes, 0, $jarBytes.Length)
$fs.Close()
$len = (Get-Item $exeModded).Length
Write-Host "SUCCESS: Written $len bytes to $exeModded"

# 2. Também sincroniza bf22-23.exe para garantir que qualquer atalho abra a versao correta
try {
    $fs2 = [System.IO.File]::Create($exeSource)
    $fs2.Write($header, 0, $header.Length)
    $fs2.Write($jarBytes, 0, $jarBytes.Length)
    $fs2.Close()
    Write-Host "SUCCESS: Updated $exeSource with modded JAR!"
} catch {
    Write-Host "Notice: $exeSource is currently running or locked by another process."
}
