$exeSource = "C:\Brasfoot22-23\bf22-23.exe"
$jarSource = "C:\Brasfoot22-23\bf22-23_modded.jar"
$exeTarget = "C:\Brasfoot22-23\bf22-23_modded.exe"

$allExe = [System.IO.File]::ReadAllBytes($exeSource)
$header = New-Object byte[] 125440
[System.Array]::Copy($allExe, 0, $header, 0, 125440)

$jarBytes = [System.IO.File]::ReadAllBytes($jarSource)

$fs = [System.IO.File]::Create($exeTarget)
$fs.Write($header, 0, $header.Length)
$fs.Write($jarBytes, 0, $jarBytes.Length)
$fs.Close()

$len = (Get-Item $exeTarget).Length
Write-Host "SUCCESS: Written $len bytes to $exeTarget"
