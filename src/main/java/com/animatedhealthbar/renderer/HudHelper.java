package com.animatedhealthbar.renderer;

import net.minecraft.resources.ResourceLocation;

/**
 * Общие утилиты для всех рендереров HUD.
 * Здесь хранится анимация точек и хелперы текстур.
 */
public class HudHelper {

    // Анимация бегущих точек (общая для HP и Hunger)
    public static float dotOffset = 0f;
    public static long lastUpdateTime = 0;

    public static void updateAnimation(float delta) {
        dotOffset -= delta * 8f;
        if (dotOffset < 0) dotOffset += 80;
    }

    public static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(
                "animatedhealthbar", "textures/gui/" + name + ".png");
    }

    public static final ResourceLocation BAR_BG         = texture("bar_bg");
    public static final ResourceLocation BAR_RED        = texture("bar_fill_red");
    public static final ResourceLocation BAR_ORANGE     = texture("bar_fill_orange");
    public static final ResourceLocation BAR_POISON     = texture("bar_fill_poison");
    public static final ResourceLocation BAR_WITHER     = texture("bar_fill_wither");
    public static final ResourceLocation BAR_FREEZE     = texture("bar_fill_freeze");
    public static final ResourceLocation BAR_FIRE       = texture("bar_fill_fire");
    public static final ResourceLocation BAR_REGEN      = texture("bar_fill_regen");
    public static final ResourceLocation BAR_ABSORPTION = texture("bar_fill_absorption");
}
