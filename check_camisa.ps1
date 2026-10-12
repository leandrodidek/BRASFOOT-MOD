Add-Type -AssemblyName System.Drawing
$f = "C:\Brasfoot22-23\projeto\src_full\resources\aicons\camisat.png"
if (Test-Path $f) {
    $bmp = [System.Drawing.Bitmap]::FromFile($f)
    Write-Host "camisat.png size: $($bmp.Width) x $($bmp.Height)"
    
    # Find bounding box of non-transparent pixels
    $minY = $bmp.Height
    $maxY = 0
    for ($y = 0; $y -lt $bmp.Height; $y++) {
        for ($x = 0; $x -lt $bmp.Width; $x++) {
            $pixel = $bmp.GetPixel($x, $y)
            if ($pixel.A -gt 10) {
                if ($y -lt $minY) { $minY = $y }
                if ($y -gt $maxY) { $maxY = $y }
            }
        }
    }
    Write-Host "Content bounds Y: min=$minY, max=$maxY"
    $bmp.Dispose()
} else {
    Write-Host "File not found"
}
