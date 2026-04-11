package com.animatedhealthbar.renderer;

import com.animatedhealthbar.HudConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class OxygenBarRenderer {

    private static float displayAir = -1f;
    private static long lastTime = 0;

    private static final int BAR_WIDTH  = 80;
    private static final int BAR_HEIGHT = 6;

    public static void render(GuiGraphics graphics, Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (!HudConfig.OXYGEN_ENABLED.get()) return;

        int maxAir     = player.getMaxAirSupply();
        int currentAir = Math.max(0, player.getAirSupply());
        if (currentAir >= maxAir) {
            displayAir = -1f;
            return;
        }

        if (displayAir < 0) displayAir = currentAir;

        long now = System.currentTimeMillis();
        float delta = Mth.clamp((now - lastTime) / 1000f, 0.016f, 0.1f);
        lastTime = now;

        float smoothFactor = 1f - (float) Math.exp(-delta * 3.0f);
        displayAir = Mth.lerp(smoothFactor, displayAir, currentAir);
        displayAir = Mth.clamp(displayAir, 0, maxAir);

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        // Кислород — над полоской голода (голод на sh-39, кислород на sh-59 = на 20px выше)
        int x = sw / 2 + 91 - BAR_WIDTH + HudConfig.OXYGEN_X_OFFSET.get();
        int y = sh - 59 + HudConfig.OXYGEN_Y_OFFSET.get();

        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0x55000000);
        graphics.blit(HudHelper.BAR_BG, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

        float percent = displayAir / (float) maxAir;
        int filled = Math.round(BAR_WIDTH * percent);
        if (filled > 0) {
            graphics.blit(HudHelper.BAR_FREEZE, x, y,
                    (int) HudHelper.dotOffset, 0, filled, BAR_HEIGHT, 160, BAR_HEIGHT);
        }

        if (HudConfig.OXYGEN_TEXT_ENABLED.get()) {
            int bubbles = (int) Math.ceil(displayAir / (maxAir / 10f));
            bubbles = Mth.clamp(bubbles, 0, 10);
            String left  = String.valueOf(bubbles);
            String right = "10";
            int iconSize = 8, gap = 2;
            int totalW = mc.font.width(left) + gap + iconSize + gap + mc.font.width(right);
            int startX = x + (BAR_WIDTH - totalW) / 2;
            int textY  = y - 9;
            graphics.drawString(mc.font, Component.literal(left), startX, textY, 0xFF55FFFF);
            int iconX = startX + mc.font.width(left) + gap;
            graphics.pose().pushPose();
            graphics.pose().translate(iconX, textY - 1, 0);
            graphics.pose().scale(0.5f, 0.5f, 1f);
            graphics.renderItem(Items.WATER_BUCKET.getDefaultInstance(), 0, 0);
            graphics.pose().popPose();
            graphics.drawString(mc.font, Component.literal(right), iconX + iconSize + gap, textY, 0xFF55FFFF);
        }
    }
}
