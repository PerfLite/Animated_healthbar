package com.animatedhealthbar.renderer;

import com.animatedhealthbar.HudConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * Рендерер полоски HP.
 * Только логика здоровья — ничего лишнего.
 */
public class HealthBarRenderer {

    private static float displayHealth = -1f;

    private static final int BAR_WIDTH  = 80;
    private static final int BAR_HEIGHT = 6;

    public static void render(GuiGraphics graphics, Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (!HudConfig.HP_ENABLED.get()) return;

        float currentHealth = player.getHealth();
        float maxHealth     = player.getMaxHealth();

        if (displayHealth < 0) displayHealth = currentHealth;

        long now  = System.currentTimeMillis();
        float delta = Mth.clamp((now - HudHelper.lastUpdateTime) / 1000f, 0.016f, 0.1f);

        float smoothFactor = 1f - (float)Math.exp(-delta * 2.0f);
        displayHealth = Mth.lerp(smoothFactor, displayHealth, currentHealth);
        displayHealth = Mth.clamp(displayHealth, 0, maxHealth);

        HudHelper.updateAnimation(delta);
        HudHelper.lastUpdateTime = now;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int x  = sw / 2 - 91 + HudConfig.HP_X_OFFSET.get();
        int y  = sh - 39 + HudConfig.HP_Y_OFFSET.get();

        // Тень
        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0x55000000);
        // Фон
        graphics.blit(HudHelper.BAR_BG, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        // Заполнение
        float percent = displayHealth / maxHealth;
        int filled = Math.round(BAR_WIDTH * percent);
        if (filled > 0) {
            graphics.blit(getBarTexture(player), x, y,
                    (int)HudHelper.dotOffset, 0, filled, BAR_HEIGHT, 160, BAR_HEIGHT);
        }

        // Числа над полоской
        if (HudConfig.HP_TEXT_ENABLED.get()) {
            renderText(graphics, mc, x, y, currentHealth, maxHealth);
        }
    }

    private static void renderText(GuiGraphics graphics, Minecraft mc,
                                    int x, int y, float current, float max) {
        String left  = String.valueOf((int)Math.ceil(current));
        String right = String.valueOf((int)max);
        int iconSize = 8, gap = 2;
        int totalW = mc.font.width(left) + gap + iconSize + gap + mc.font.width(right);
        int startX = x + (BAR_WIDTH - totalW) / 2;
        int textY  = y - 9;

        graphics.drawString(mc.font, Component.literal(left), startX, textY, 0xFFFF6666);
        int iconX = startX + mc.font.width(left) + gap;
        graphics.blitSprite(ResourceLocation.withDefaultNamespace("hud/heart/full"),
                iconX, textY - 1, iconSize, iconSize);
        graphics.drawString(mc.font, Component.literal(right), iconX + iconSize + gap, textY, 0xFFFF6666);
    }

    private static ResourceLocation getBarTexture(Player player) {
        if (player.hasEffect(MobEffects.WITHER) || player.getActiveEffectsMap().keySet().stream()
                .anyMatch(e -> e.value().getDescriptionId().contains("wither")))
            return HudHelper.BAR_WITHER;
        if (player.hasEffect(MobEffects.POISON))       return HudHelper.BAR_POISON;
        if (player.isFullyFrozen())                     return HudHelper.BAR_FREEZE;
        if (player.isOnFire())                          return HudHelper.BAR_FIRE;
        if (player.hasEffect(MobEffects.REGENERATION))  return HudHelper.BAR_REGEN;
        if (player.hasEffect(MobEffects.ABSORPTION))    return HudHelper.BAR_ABSORPTION;
        return HudHelper.BAR_RED;
    }
}
