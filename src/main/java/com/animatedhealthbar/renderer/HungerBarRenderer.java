package com.animatedhealthbar.renderer;

import com.animatedhealthbar.HudConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Items;

public class HungerBarRenderer {

    private static float displayHunger = -1f;
    private static long lastTime = 0;

    private static final int BAR_WIDTH  = 80;
    private static final int BAR_HEIGHT = 6;

    // Цвета overlay — больше не нужны, используем градиент
    private static final int COLOR_LOW_HUNGER    = 0xCC884400; // тусклый тёмно-оранжевый (без насыщения)
    private static final int COLOR_HIGH_SATURATION = 0xCCFFCC00; // яркий золотой (макс. насыщение)

    public static void render(GuiGraphics graphics, Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (!HudConfig.HUNGER_ENABLED.get()) return;

        FoodData foodData = player.getFoodData();
        float currentHunger = foodData.getFoodLevel();
        float maxHunger = 20f;
        float saturation = foodData.getSaturationLevel();

        if (displayHunger < 0) displayHunger = currentHunger;

        long now = System.currentTimeMillis();
        float delta = Mth.clamp((now - lastTime) / 1000f, 0.016f, 0.1f);
        lastTime = now;

        float smoothFactor = 1f - (float) Math.exp(-delta * 2.0f);
        displayHunger = Mth.lerp(smoothFactor, displayHunger, currentHunger);
        displayHunger = Mth.clamp(displayHunger, 0, maxHunger);

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int x  = sw / 2 + 91 - BAR_WIDTH + HudConfig.HUNGER_X_OFFSET.get();
        int y  = sh - 39 + HudConfig.HUNGER_Y_OFFSET.get();

        // Тень + фон
        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0x55000000);
        graphics.blit(HudHelper.BAR_BG, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

        // Основная полоска голода с градиентом насыщения
        float percent = displayHunger / maxHunger;
        int filled = Math.round(BAR_WIDTH * percent);
        if (filled > 0) {
            int hungerColor = getSaturationColor(saturation, currentHunger);
            renderGradientBar(graphics, x, y, filled, BAR_HEIGHT, hungerColor);
        }

        // Текст
        if (HudConfig.HUNGER_TEXT_ENABLED.get()) {
            renderText(graphics, mc, x, y, currentHunger, maxHunger, player);
        }
    }

    /**
     * Вычисляет цвет полоски на основе уровня насыщения.
     *
     * При saturation = 0 (нет насыщения) — тусклый оранжевый.
     * При saturation = max (полное насыщение) — яркий золотой.
     * Промежуточные значения — плавный градиент между ними.
     */
    private static int getSaturationColor(float saturation, float hunger) {
        // Насыщение не может превышать текущий голод
        float effectiveSat = Math.min(saturation, hunger);
        float maxSat = Math.max(hunger, 1f); // защита от деления на 0

        // Процент насыщения относительно текущего голода (0..1)
        float satPercent = Mth.clamp(effectiveSat / maxSat, 0f, 1f);

        // Интерполируем между тусклым и ярким цветом
        int lowR = (COLOR_LOW_HUNGER >> 24) & 0xFF;
        int lowG = (COLOR_LOW_HUNGER >> 16) & 0xFF;
        int lowB = (COLOR_LOW_HUNGER >> 8) & 0xFF;

        int highR = (COLOR_HIGH_SATURATION >> 24) & 0xFF;
        int highG = (COLOR_HIGH_SATURATION >> 16) & 0xFF;
        int highB = (COLOR_HIGH_SATURATION >> 8) & 0xFF;

        int r = (int) Mth.lerp(satPercent, lowR, highR);
        int g = (int) Mth.lerp(satPercent, lowG, highG);
        int b = (int) Mth.lerp(satPercent, lowB, highB);
        int a = 0xCC; // альфа

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Рендерит полоску с заданным цветом через fill().
     * Бегущие точки накладываются поверх через текстуру с прозрачностью.
     */
    private static void renderGradientBar(GuiGraphics graphics, int x, int y,
                                           int width, int height, int color) {
        // Основная цветная полоска
        graphics.fill(x, y, x + width, y + height, color);

        // Накладываем текстуру с точками поверх (полупрозрачная)
        graphics.blit(HudHelper.BAR_ORANGE, x, y,
                (int) HudHelper.dotOffset, 0, width, height, 160, BAR_HEIGHT);
    }

    /**
     * Больше не используется — насыщение теперь встроено в цвет полоски.
     */
    @Deprecated
    private static void renderSaturationExhaustionOverlay(GuiGraphics graphics, Player player,
                                                           int x, int y, float maxHunger) {
        // Метод оставлен для обратной совместимости, но больше не вызывается.
    }

    private static void renderText(GuiGraphics graphics, Minecraft mc,
                                    int x, int y, float currentHunger, float maxHunger,
                                    Player player) {
        String left  = String.valueOf((int) currentHunger);
        String right = String.valueOf((int) maxHunger);

        int iconSize = 8, gap = 2;
        int totalW = mc.font.width(left) + gap + iconSize + gap + mc.font.width(right);
        int startX = x + (BAR_WIDTH - totalW) / 2;
        int textY  = y - 9;
        graphics.drawString(mc.font, Component.literal(left), startX, textY, 0xFFFFAA00);
        int iconX = startX + mc.font.width(left) + gap;
        graphics.pose().pushPose();
        graphics.pose().translate(iconX, textY - 1, 0);
        graphics.pose().scale(0.5f, 0.5f, 1f);
        graphics.renderItem(Items.COOKED_BEEF.getDefaultInstance(), 0, 0);
        graphics.pose().popPose();
        graphics.drawString(mc.font, Component.literal(right), iconX + iconSize + gap, textY, 0xFFFFAA00);
    }
}
