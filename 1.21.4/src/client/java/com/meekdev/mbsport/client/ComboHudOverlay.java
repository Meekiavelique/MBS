package com.meekdev.mbsport.client;

import com.meekdev.mbsport.config.ConfigManager;
import com.meekdev.mbsport.data.PlayerData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class ComboHudOverlay {
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 4;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int BAR_BACKGROUND = 0x80000000;
    private static final int BAR_FILL_GREEN = 0xFF00FF00;
    private static final int BAR_FILL_YELLOW = 0xFFFFFF00;
    private static final int BAR_FILL_RED = 0xFFFF0000;

    public static void register() {
        HudRenderCallback.EVENT.register(ComboHudOverlay::renderComboTimer);
    }

    private static void renderComboTimer(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        PlayerData playerData = MbsportClient.getPlayerData(client.player.getUuidAsString());
        if (playerData == null || playerData.consecutiveHits == 0) return;

        long currentTime = System.currentTimeMillis();
        int timeout = ConfigManager.getConfig().comboTimeout;
        long timeElapsed = currentTime - playerData.lastHitTime;
        long timeRemaining = timeout - timeElapsed;

        if (timeRemaining <= 0) return;

        float progress = (float) timeRemaining / timeout;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int barX = (screenWidth - BAR_WIDTH) / 2;
        int barY = screenHeight - 60;
        int textX = screenWidth / 2;
        int textY = barY - 15;

        String comboText = "Combo: " + playerData.consecutiveHits + "x";
        drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal(comboText), textX, textY, TEXT_COLOR);

        drawContext.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, BAR_BACKGROUND);

        int fillWidth = (int) (BAR_WIDTH * progress);
        int fillColor = getBarColor(progress);
        drawContext.fill(barX, barY, barX + fillWidth, barY + BAR_HEIGHT, fillColor);

        int borderColor = 0xFFFFFFFF;
        drawContext.drawBorder(barX, barY, BAR_WIDTH, BAR_HEIGHT, borderColor);

        String timeText = String.format("%.1fs", timeRemaining / 1000.0f);
        int timeTextX = barX + BAR_WIDTH + 10;
        int timeTextY = barY - 2;
        drawContext.drawTextWithShadow(client.textRenderer, Text.literal(timeText), timeTextX, timeTextY, TEXT_COLOR);
    }

    private static int getBarColor(float progress) {
        if (progress > 0.6f) return BAR_FILL_GREEN;
        if (progress > 0.3f) return BAR_FILL_YELLOW;
        return BAR_FILL_RED;
    }
}