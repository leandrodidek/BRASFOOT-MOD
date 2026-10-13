$javac = (Get-ChildItem -Path "C:\Program Files\Microsoft", "C:\Program Files\Java" -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName)
if (-not $javac) { $javac = (Get-Command javac -ErrorAction SilentlyContinue).Source }
$cp = "C:\Brasfoot22-23\bf22-23_extracted.jar;C:\Brasfoot22-23\projeto\build_mod"
$out = "C:\Brasfoot22-23\projeto\build_mod"

if (-not (Test-Path $out)) { New-Item -ItemType Directory -Path $out -Force | Out-Null }

Write-Host "Compiling mod helper classes using $javac..."
& $javac --release 8 -encoding UTF-8 -cp $cp -d $out `
    "$PSScriptRoot\SubstitutionManager.java" `
    "$PSScriptRoot\VarManager.java" `
    "$PSScriptRoot\VarDialog.java" `
    "$PSScriptRoot\ModBridge.java" `
    "$PSScriptRoot\EaFcUiHelper.java" `
    "$PSScriptRoot\TacticalRadarPanel.java" `
    "$PSScriptRoot\VarSoundHelper.java" `
    "$PSScriptRoot\VarLogoBadge.java" `
    "$PSScriptRoot\VarOptionsHelper.java" `
    "$PSScriptRoot\UndoHelper.java" `
    "$PSScriptRoot\PositionPenaltyHelper.java"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Classes successfully compiled into $out!" -ForegroundColor Green
} else {
    Write-Host "Compilation failed!" -ForegroundColor Red
}
