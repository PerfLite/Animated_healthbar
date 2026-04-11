package com.animatedhealthbar.compat;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

/**
 * Безопасный мост к AppleSkin.
 *
 * Все методы можно вызывать независимо от того, установлен ли AppleSkin.
 * Классы из пакета squeek.* загружаются только если isLoaded() == true,
 * поэтому ClassNotFoundException не будет.
 */
public final class AppleSkinBridge {

    private static final boolean LOADED =
            ModList.get().isLoaded("appleskin");

    private AppleSkinBridge() {}

    /** Установлен ли AppleSkin в этой сборке. */
    public static boolean isLoaded() {
        return LOADED;
    }

    /**
     * Насыщение (saturation), синхронизированное AppleSkin с сервера.
     * Без AppleSkin возвращает значение из FoodData (клиент имеет только
     * приблизительное значение, не синхронизированное с сервером).
     */
    public static float getSaturation(Player player) {
        if (!LOADED) return player.getFoodData().getSaturationLevel();
        return AppleSkinDataHelper.getSaturation(player);
    }

    /**
     * Истощение (exhaustion) 0..4, синхронизированное AppleSkin.
     * При достижении 4 снижается насыщение (или голод если sat=0).
     */
    public static float getExhaustion(Player player) {
        if (!LOADED) return 0f;
        return AppleSkinDataHelper.getExhaustion(player);
    }
}
