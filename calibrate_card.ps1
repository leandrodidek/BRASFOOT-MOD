$aoFile = "C:\Brasfoot22-23\projeto\src_full\sources\components\ao.java"
$content = [System.IO.File]::ReadAllText($aoFile)

# 1. 52x52 shirt scaling
$oldDrawShirt1 = "graphics2DCreateGraphics.drawImage(image, i9 - 4, i10 - 4, 48, 48, (ImageObserver) null);"
$newDrawShirt1 = "graphics2DCreateGraphics.drawImage(image, i9 - 6, i10 - 6, 52, 52, (ImageObserver) null);"

# 2. Card draw coordinates (perfeita sobreposicao na barra inferior)
$oldDrawCard1 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, `"`"), i9 - 33, i10 + 31, (ImageObserver) null);"
$newDrawCard1 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i8)).x(), 1, i8, `"`"), i9 - 33, i10 + 24, (ImageObserver) null);"

$oldDrawCard2 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 2, i5, `"`"), i3 - 33, i4 + 31, (ImageObserver) null);"
$newDrawCard2 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 2, i5, `"`"), i3 - 33, i4 + 24, (ImageObserver) null);"

$oldDrawCard3 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 3, i5, ((ao) arrayList.get(i5)).fg() != null ? ((ao) arrayList.get(i5)).fg().getNome() : `"`"), i6 - 33, i7 + 31, (ImageObserver) null);"
$newDrawCard3 = "graphics2DCreateGraphics.drawImage(a(((ao) arrayList.get(i5)).x(), 3, i5, ((ao) arrayList.get(i5)).fg() != null ? ((ao) arrayList.get(i5)).fg().getNome() : `"`"), i6 - 33, i7 + 24, (ImageObserver) null);"

$content = $content.Replace($oldDrawShirt1, $newDrawShirt1)
$content = $content.Replace($oldDrawCard1, $newDrawCard1)
$content = $content.Replace($oldDrawCard2, $newDrawCard2)
$content = $content.Replace($oldDrawCard3, $newDrawCard3)

[System.IO.File]::WriteAllText($aoFile, $content)
Write-Host "SUCCESS: ao.java updated with 52x52 shirts and perfect hem overlap!"
