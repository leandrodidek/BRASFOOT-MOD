Add-Type -AssemblyName System.Drawing

$src = "C:\Brasfoot22-23\projeto\src_full\resources\aicons\campo.png"
$origImg = [System.Drawing.Image]::FromFile($src)

# Rescale smoothly to 600 x 700
$targetW = 600
$targetH = 700

$bmp = New-Object System.Drawing.Bitmap $targetW, $targetH
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
$g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

$g.DrawImage($origImg, 0, 0, $targetW, $targetH)

$g.Dispose()
$origImg.Dispose()

# Save scaled image
$outPath = "C:\Brasfoot22-23\projeto\build_mod\aicons\campo.png"
[System.IO.Directory]::CreateDirectory([System.IO.Path]::GetDirectoryName($outPath)) | Out-Null
$bmp.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()

Write-Host "SUCCESS: Generated high quality 600x700 campo.png in build_mod\aicons\campo.png"
