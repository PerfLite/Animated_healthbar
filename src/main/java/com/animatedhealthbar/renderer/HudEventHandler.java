package com.animatedhealthbar.renderer;

import com.animatedhealthbar.HudConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = "animatedhealthbar", value = Dist.CLIENT)
public class HudEventHandler {

    // Ванильные слои
    private static final ResourceLocation HEALTH = ResourceLocation.parse("minecraft:player_health");
    private static final ResourceLocation FOOD   = ResourceLocation.parse("minecraft:food_level");
    private static final ResourceLocation ARMOR  = ResourceLocation.parse("minecraft:armor_level");
    private static final ResourceLocation AIR    = ResourceLocation.parse("minecraft:air_level");

    // AppleSkin слои (отменяем их все — наш рендер показывает всё сам)
    private static final ResourceLocation APPLESKIN_HEALTH_OFFSET = ResourceLocation.parse("appleskin:health_offset");
    private static final ResourceLocation APPLESKIN_FOOD_OFFSET   = ResourceLocation.parse("appleskin:food_offset");
    private static final ResourceLocation APPLESKIN_HEALTH_RESTORED = ResourceLocation.parse("appleskin:health_restored");
    private static final ResourceLocation APPLESKIN_HUNGER_RESTORED = ResourceLocation.parse("appleskin:hunger_restored");
    private static final ResourceLocation APPLESKIN_SATURATION    = ResourceLocation.parse("appleskin:saturation_level");
    private static final ResourceLocation APPLESKIN_EXHAUSTION    = ResourceLocation.parse("appleskin:exhaustion_level");

    private static long lastAnimTime = 0;

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (mc.gameMode == null) return;
        GameType mode = mc.gameMode.getPlayerMode();
        if (mode == GameType.CREATIVE || mode == GameType.SPECTATOR) return;

        Player player = mc.player;
        var name = event.getName();

        // Всегда отменяем ванильные иконки, даже если наши бары отключены —
        // иначе при низком здоровье будут проскальзывать ванильные сердца.
        if (name.equals(HEALTH)) {
            event.setCanceled(true);
            if (HudConfig.HP_ENABLED.get()) {
                long now = System.currentTimeMillis();
                float delta = Mth.clamp((now - lastAnimTime) / 1000f, 0.016f, 0.1f);
                HudHelper.updateAnimation(delta);
                lastAnimTime = now;
                HealthBarRenderer.render(event.getGuiGraphics(), player);
            }
            return;
        }

        if (name.equals(FOOD)) {
            event.setCanceled(true);
            if (HudConfig.HUNGER_ENABLED.get()) {
                HungerBarRenderer.render(event.getGuiGraphics(), player);
            }
            return;
        }

        if (name.equals(ARMOR)) {
            event.setCanceled(true);
            if (HudConfig.ARMOR_ENABLED.get()) {
                ArmorRenderer.render(event.getGuiGraphics(), player);
            }
            return;
        }

        if (name.equals(AIR)) {
            event.setCanceled(true);
            if (HudConfig.OXYGEN_ENABLED.get()) {
                OxygenBarRenderer.render(event.getGuiGraphics(), player);
            }
            return;
        }

        // Отменяем все слои AppleSkin — наш рендер показывает насыщение/истощение сам
        if (isAppleSkinLayer(name)) {
            event.setCanceled(true);
        }
    }

    /**
     * Проверяет является ли слой одним из слоёв AppleSkin.
     */
    private static boolean isAppleSkinLayer(ResourceLocation name) {
        return name.equals(APPLESKIN_HEALTH_OFFSET) ||
               name.equals(APPLESKIN_FOOD_OFFSET) ||
               name.equals(APPLESKIN_HEALTH_RESTORED) ||
               name.equals(APPLESKIN_HUNGER_RESTORED) ||
               name.equals(APPLESKIN_SATURATION) ||
               name.equals(APPLESKIN_EXHAUSTION);
    }
}
