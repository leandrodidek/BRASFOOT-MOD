$aoFile = "C:\Brasfoot22-23\projeto\src_full\sources\components\ao.java"
$content = [System.IO.File]::ReadAllText($aoFile)

$oldMethod = '        public static Image a(best.F f2, int i, int i2, String str) {
        if (f2 == null) {
            return null;
        }
        int w = 150;
        int h = 34;
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int forca = f2.fi();
        int energia = f2.fp();
        if (energia < 0) energia = 0;
        if (energia > 100) energia = 100;

        String nome = f2.getNome();
        if (nome == null) nome = "";
        if (nome.length() > 12) {
            nome = nome.substring(0, 11) + ".";
        }

        java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.BOLD, 11);
        g.setFont(font);
        java.awt.FontMetrics fm = g.getFontMetrics(font);

        // Linha 1: Forca + Barra de Energia sobreposta na camisa
        String forcaStr = String.valueOf(forca);
        int forcaW = fm.stringWidth(forcaStr);
        int barW = 46;
        int gap = 5;
        int row1W = forcaW + gap + barW;
        int startX = 75 - (row1W / 2);

        // Forca com sombra escura
        g.setColor(new java.awt.Color(0, 0, 0, 240));
        g.drawString(forcaStr, startX + 1, 13);
        g.setColor(java.awt.Color.WHITE);
        g.drawString(forcaStr, startX, 12);

        // Barra de energia
        int barX = startX + forcaW + gap;
        int barY = 5;
        int barH = 5;

        g.setColor(new java.awt.Color(30, 41, 59, 235));
        g.fillRoundRect(barX, barY, barW, barH, 2, 2);
        g.setColor(new java.awt.Color(0, 0, 0, 160));
        g.drawRoundRect(barX, barY, barW, barH, 2, 2);

        java.awt.Color staminaColor;
        if (energia >= 70) {
            staminaColor = new java.awt.Color(34, 197, 94); // Verde
        } else if (energia >= 45) {
            staminaColor = new java.awt.Color(234, 179, 8); // Amarelo
        } else {
            staminaColor = new java.awt.Color(239, 68, 68); // Vermelho
        }

        int fillW = (int) Math.round((energia / 100.0) * barW);
        if (fillW > barW) fillW = barW;
        if (fillW > 0) {
            g.setColor(staminaColor);
            g.fillRoundRect(barX, barY, fillW, barH, 2, 2);
        }

        // Linha 2: Nome do jogador centralizado abaixo da camisa
        int nameW = fm.stringWidth(nome);
        int nameX = 75 - (nameW / 2);
        int nameY = 25;

        g.setColor(new java.awt.Color(0, 0, 0, 240));
        g.drawString(nome, nameX + 1, nameY + 1);
        g.setColor(java.awt.Color.WHITE);
        g.drawString(nome, nameX, nameY);

        g.dispose();
        return bufferedImage;
    }'

$newMethod = '    public static Image a(best.F f2, int i, int i2, String str) {
        if (f2 == null) {
            return null;
        }
        int w = 110;
        int h = 36;
        java.awt.image.BufferedImage bufferedImage = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = bufferedImage.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int forca = f2.fi();
        int energia = f2.fp();
        if (energia < 0) energia = 0;
        if (energia > 100) energia = 100;

        String nome = f2.getNome();
        if (nome == null) nome = "";
        if (nome.length() > 12) {
            nome = nome.substring(0, 11) + ".";
        }

        java.awt.Font fontForca = new java.awt.Font("Arial", java.awt.Font.BOLD, 12);
        java.awt.Font fontNome = new java.awt.Font("Arial", java.awt.Font.BOLD, 10);

        // --- LINHA 1: OVR + BARRA DE ESTAMINA (Sobre a barra inferior da camisa) ---
        g.setFont(fontForca);
        java.awt.FontMetrics fmForca = g.getFontMetrics(fontForca);
        String forcaStr = String.valueOf(forca);
        int forcaW = fmForca.stringWidth(forcaStr);

        int barW = 38;
        int barH = 4;
        int gap = 4;
        int row1W = forcaW + gap + barW;
        int startX = (w / 2) - (row1W / 2);

        // OVR com sombra escura de alto contraste
        g.setColor(new java.awt.Color(0, 0, 0, 240));
        g.drawString(forcaStr, startX + 1, 12);
        g.setColor(java.awt.Color.WHITE);
        g.drawString(forcaStr, startX, 11);

        // Barra de Estamina fina na barra inferior da camisa
        int barX = startX + forcaW + gap;
        int barY = 5;

        g.setColor(new java.awt.Color(15, 23, 42, 230)); // Dark obsidian track
        g.fillRoundRect(barX, barY, barW, barH, 2, 2);
        g.setColor(new java.awt.Color(0, 0, 0, 160));
        g.drawRoundRect(barX, barY, barW, barH, 2, 2);

        java.awt.Color staminaColor;
        if (energia >= 70) {
            staminaColor = new java.awt.Color(34, 197, 94); // Verde Neon
        } else if (energia >= 45) {
            staminaColor = new java.awt.Color(234, 179, 8); // Amarelo
        } else {
            staminaColor = new java.awt.Color(239, 68, 68); // Vermelho
        }

        int fillW = (int) Math.round((energia / 100.0) * barW);
        if (fillW > barW) fillW = barW;
        if (fillW > 0) {
            g.setColor(staminaColor);
            g.fillRoundRect(barX, barY, fillW, barH, 2, 2);
        }

        // --- LINHA 2: NOME EM PILULA ESCURA EA FC 27 ---
        g.setFont(fontNome);
        java.awt.FontMetrics fmNome = g.getFontMetrics(fontNome);
        int nameW = fmNome.stringWidth(nome);
        int pillW = nameW + 12;
        int pillH = 15;
        int pillX = (w / 2) - (pillW / 2);
        int pillY = 16;

        // Capsula escura (Dark Glass) com borda suave
        g.setColor(new java.awt.Color(15, 23, 42, 225));
        g.fillRoundRect(pillX, pillY, pillW, pillH, 8, 8);
        g.setColor(new java.awt.Color(51, 65, 85, 180));
        g.drawRoundRect(pillX, pillY, pillW, pillH, 8, 8);

        // Texto do nome centralizado na pilula
        g.setColor(java.awt.Color.WHITE);
        g.drawString(nome, pillX + 6, pillY + 11);

        g.dispose();
        return bufferedImage;
    }'

$content = $content.Replace($oldMethod, $newMethod)
[System.IO.File]::WriteAllText($aoFile, $content)
Write-Host "SUCCESS: Method a() replaced with EA FC pill card!"
