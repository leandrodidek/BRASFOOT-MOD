$origCampo = "C:\Brasfoot22-23\projeto\src_full\resources\aicons\campo.png"
$targetCampo = "C:\Brasfoot22-23\projeto\build_mod\aicons\campo.png"
[System.IO.File]::Copy($origCampo, $targetCampo, $true)

$origBanco = "C:\Brasfoot22-23\projeto\src_full\resources\aicons\campobanco1.png"
$targetBanco = "C:\Brasfoot22-23\projeto\build_mod\aicons\campobanco1.png"
[System.IO.File]::Copy($origBanco, $targetBanco, $true)

Write-Host "SUCCESS: Restored native campo.png and campobanco1.png in build_mod"
