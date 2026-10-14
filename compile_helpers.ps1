$javac = @(
    "C:\Users\Leandro\.antigravity-ide\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\javac.exe",
    (Get-ChildItem -Path "C:\Program Files\Microsoft", "C:\Program Files\Java", "C:\Users\Leandro\.antigravity-ide" -Filter "javac.exe" -Recurse -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty FullName),
    (Get-Command javac -ErrorAction SilentlyContinue).Source
) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
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
    "$PSScriptRoot\PositionPenaltyHelper.java" `
    "$PSScriptRoot\InMatchStatsPanel.java" `
    "$PSScriptRoot\InMatchDualRadarPanel.java" `
    "$PSScriptRoot\InMatchHudManager.java"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Classes successfully compiled into $out!" -ForegroundColor Green
} else {
    Write-Host "Compilation failed!" -ForegroundColor Red
}
