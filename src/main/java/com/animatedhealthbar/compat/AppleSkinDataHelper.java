package com.animatedhealthbar.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

/**
 * Хелпер для получения данных о еде.
 *
 * В AppleSkin 3.x для NeoForge публичный API (FoodDataAccessor) был удалён.
 * Серверные данные насыщения/истощения НЕ синхронизируются с клиентом в ванили,
 * поэтому AppleSkin использует собственные пакеты для синхронизации.
 *
 * Поскольку публичного API больше нет, мы используем ванильные методы FoodData.
 * Клиентские значения насыщения приближённые (не точные серверные),
 * но достаточны для визуального отображения.
 */
final class AppleSkinDataHelper {

    private AppleSkinDataHelper() {}

    /**
     * Возвращает уровень насыщения из FoodData игрока.
     * На клиенте это значение приблизительное (не синхронизировано с сервером).
     */
    static float getSaturation(Player player) {
        return player.getFoodData().getSaturationLevel();
    }

    /**
     * Возвращает уровень истощения из FoodData игрока.
     * Значение от 0 до 4. При достижении 4 снижается насыщение или голод.
     *
     * В ванильном клиенте это значение доступно через FoodData.getExhaustionLevel().
     */
    static float getExhaustion(Player player) {
        FoodData foodData = player.getFoodData();
        return foodData.getExhaustionLevel();
    }
}
