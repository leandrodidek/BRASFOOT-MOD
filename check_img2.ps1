Add-Type -AssemblyName System.Drawing
$f = 'C:\Brasfoot22-23\projeto\src_full\resources\aicons\campobanco1.png'
if (Test-Path $f) {
    $img = [System.Drawing.Image]::FromFile($f)
    Write-Host "campobanco1.png size: $($img.Width) x $($img.Height)"
    $img.Dispose()
}
