Add-Type -AssemblyName System.Drawing
$f = 'C:\Brasfoot22-23\projeto\src_full\resources\aicons\campo.png'
if (Test-Path $f) {
    $img = [System.Drawing.Image]::FromFile($f)
    Write-Host "campo.png size: $($img.Width) x $($img.Height)"
    $img.Dispose()
} else {
    Write-Host "campo.png not found at $f"
    Get-ChildItem -Path 'C:\Brasfoot22-23\projeto' -Filter 'campo.png' -Recurse | Select-Object FullName
}
