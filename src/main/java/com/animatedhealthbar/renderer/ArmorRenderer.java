package com.animatedhealthbar.renderer;

import com.animatedhealthbar.HudConfig;
import com.animatedhealthbar.gui.ArmorEditorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArmorRenderer {

    // Всегда сверху вниз: шлем → нагрудник → штаны → ботинки
    // Для вертикали: шлем вверху, ботинки внизу
    // Для горизонтали: шлем слева, ботинки справа
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST,
        EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public static void render(GuiGraphics graphics, Player player) {
        if (!HudConfig.ARMOR_ENABLED.get()) return;
        if (ArmorEditorScreen.isEditing) return;
        if (player.getArmorValue() <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        int iconSize = HudConfig.ARMOR_ICON_SIZE.get();
        boolean vertical = HudConfig.ARMOR_VERTICAL.get();
        float scale = iconSize / 16f;

        int startX = getStartX(sw, sh, iconSize);
        int startY = getStartY(sw, sh, iconSize);

        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack stack = player.getItemBySlot(SLOTS[i]);
            if (stack.isEmpty()) continue;

            int ax, ay;
            if (vertical) {
                // Вертикально: i=0 (шлем) вверху, i=3 (ботинки) внизу
                ax = startX;
                ay = startY + i * (iconSize + 2);
            } else {
                // Горизонтально: i=0 (шлем) слева, i=3 (ботинки) справа
                ax = startX + i * (iconSize + 2);
                ay = startY;
            }

            graphics.pose().pushPose();
            graphics.pose().translate(ax, ay, 0);
            graphics.pose().scale(scale, scale, 1f);
            graphics.renderItem(stack, 0, 0);
            graphics.pose().popPose();
        }
    }

    private static int getStartX(int sw, int sh, int iconSize) {
        return switch (HudConfig.ARMOR_POSITION.get()) {
            case LEFT_OF_HOTBAR -> sw / 2 - 91 - iconSize - 3 + HudConfig.ARMOR_X_OFFSET.get();
            case ABOVE_HP_BAR   -> sw / 2 - 91               + HudConfig.ARMOR_X_OFFSET.get();
            case CUSTOM         -> sw / 2                     + HudConfig.ARMOR_X_OFFSET.get();
        };
    }

    private static int getStartY(int sw, int sh, int iconSize) {
        return switch (HudConfig.ARMOR_POSITION.get()) {
            // startY = верхняя иконка
            case LEFT_OF_HOTBAR -> sh - 22 - iconSize * 4 - 2 * 3 + HudConfig.ARMOR_Y_OFFSET.get();
            case ABOVE_HP_BAR   -> sh - 39 - iconSize - 2           + HudConfig.ARMOR_Y_OFFSET.get();
            case CUSTOM         -> sh / 2                            + HudConfig.ARMOR_Y_OFFSET.get();
        };
    }
}
