package com.animatedhealthbar.gui;

import com.animatedhealthbar.HudConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArmorEditorScreen extends Screen {

    public static boolean isEditing = false;

    private int armorX, armorY, iconSize;
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    private boolean hpEnabled, hpTextEnabled;
    private boolean hungerEnabled, hungerTextEnabled;
    private boolean armorEnabled, armorVertical;

    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST,
        EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final ItemStack[] PREVIEW = {
        Items.DIAMOND_HELMET.getDefaultInstance(),
        Items.DIAMOND_CHESTPLATE.getDefaultInstance(),
        Items.DIAMOND_LEGGINGS.getDefaultInstance(),
        Items.DIAMOND_BOOTS.getDefaultInstance()
    };

    public ArmorEditorScreen() {
        super(Component.translatable("gui.animatedhealthbar.hud_editor"));
    }

    @Override
    protected void init() {
        isEditing = true;
        iconSize          = HudConfig.ARMOR_ICON_SIZE.get();
        hpEnabled         = HudConfig.HP_ENABLED.get();
        hpTextEnabled     = HudConfig.HP_TEXT_ENABLED.get();
        hungerEnabled     = HudConfig.HUNGER_ENABLED.get();
        hungerTextEnabled = HudConfig.HUNGER_TEXT_ENABLED.get();
        armorEnabled      = HudConfig.ARMOR_ENABLED.get();
        armorVertical     = HudConfig.ARMOR_VERTICAL.get();

        // Позиция брони
        if (HudConfig.ARMOR_POSITION.get() == HudConfig.ArmorPosition.CUSTOM) {
            armorX = width / 2 + HudConfig.ARMOR_X_OFFSET.get();
            armorY = height / 2 + HudConfig.ARMOR_Y_OFFSET.get();
        } else if (HudConfig.ARMOR_POSITION.get() == HudConfig.ArmorPosition.LEFT_OF_HOTBAR) {
            armorX = width / 2 - 91 - iconSize - 3;
            armorY = height - 22 - iconSize * 4 - 2 * 3;
        } else {
            armorX = width / 2 - 91;
            armorY = height - 39 - iconSize - 2;
        }

        int panelX = 8;
        int btnW = 150, btnH = 18, gap = 22, sy = 30;

        // Переключатели
        addRenderableWidget(makeToggle("gui.animatedhealthbar.toggle_hp",             panelX, sy,           btnW, btnH, () -> hpEnabled,         v -> hpEnabled = v));
        addRenderableWidget(makeToggle("gui.animatedhealthbar.toggle_hp_text",        panelX, sy + gap,     btnW, btnH, () -> hpTextEnabled,     v -> hpTextEnabled = v));
        addRenderableWidget(makeToggle("gui.animatedhealthbar.toggle_hunger",         panelX, sy + gap * 2, btnW, btnH, () -> hungerEnabled,     v -> hungerEnabled = v));
        addRenderableWidget(makeToggle("gui.animatedhealthbar.toggle_hunger_text",    panelX, sy + gap * 3, btnW, btnH, () -> hungerTextEnabled, v -> hungerTextEnabled = v));
        addRenderableWidget(makeToggle("gui.animatedhealthbar.toggle_armor",          panelX, sy + gap * 4, btnW, btnH, () -> armorEnabled,      v -> armorEnabled = v));
        addRenderableWidget(makeToggle("gui.animatedhealthbar.toggle_armor_vertical", panelX, sy + gap * 5, btnW, btnH, () -> armorVertical,     v -> armorVertical = v));

        // Кнопки сразу под переключателями
        int btnY = sy + gap * 6 + 6;
        addRenderableWidget(Button.builder(Component.translatable("gui.animatedhealthbar.save"),   btn -> saveAndClose()).bounds(panelX, btnY,          btnW, btnH).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.animatedhealthbar.cancel"), btn -> cancelAndClose()).bounds(panelX, btnY + gap,  btnW, btnH).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.animatedhealthbar.reset"),  btn -> resetToDefault()).bounds(panelX, btnY + gap*2, btnW, btnH).build());
    }

    private Button makeToggle(String key, int x, int y, int w, int h,
                               java.util.function.Supplier<Boolean> getter,
                               java.util.function.Consumer<Boolean> setter) {
        return Button.builder(toggleLabel(key, getter.get()), btn -> {
            boolean v = !getter.get();
            setter.accept(v);
            btn.setMessage(toggleLabel(key, v));
        }).bounds(x, y, w, h).build();
    }

    private Component toggleLabel(String key, boolean on) {
        return Component.literal(on ? "§a[ON]§r " : "§c[OFF]§r ")
                .append(Component.translatable(key));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0x88000000);

        graphics.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF);
        graphics.drawCenteredString(font,
                Component.translatable("gui.animatedhealthbar.drag_hint"),
                width / 2, 20, 0xAAAAAA);

        graphics.drawString(font,
                Component.translatable("gui.animatedhealthbar.toggles_header"),
                10, 20, 0xFFFFAA);

        if (armorEnabled) {
            renderArmorPreview(graphics);
            // Рамка
            int total = 4 * (iconSize + 2);
            if (armorVertical) {
                graphics.fill(armorX - 2, armorY - 2, armorX + iconSize + 2, armorY + total + 2, 0x44FFFFFF);
            } else {
                graphics.fill(armorX - 2, armorY - 2, armorX + total + 2, armorY + iconSize + 2, 0x44FFFFFF);
            }
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void renderArmorPreview(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        float scale = iconSize / 16f;
        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack stack = mc.player != null ? mc.player.getItemBySlot(SLOTS[i]) : ItemStack.EMPTY;
            if (stack.isEmpty()) stack = PREVIEW[i];
            int ix = armorVertical ? armorX : armorX + i * (iconSize + 2);
            int iy = armorVertical ? armorY + i * (iconSize + 2) : armorY;
            graphics.pose().pushPose();
            graphics.pose().translate(ix, iy, 0);
            graphics.pose().scale(scale, scale, 1f);
            graphics.renderItem(stack, 0, 0);
            graphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0 && armorEnabled && isOverArmor(mx, my)) {
            dragging = true;
            dragOffsetX = (int)mx - armorX;
            dragOffsetY = (int)my - armorY;
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        dragging = false;
        return super.mouseReleased(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (dragging) { armorX = (int)mx - dragOffsetX; armorY = (int)my - dragOffsetY; return true; }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    // Скролл меняет размер
    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (sy > 0 && iconSize < 32) iconSize += 2;
        if (sy < 0 && iconSize > 4)  iconSize -= 2;
        return true;
    }

    private boolean isOverArmor(double mx, double my) {
        int total = 4 * (iconSize + 2);
        if (armorVertical)
            return mx >= armorX - 4 && mx <= armorX + iconSize + 4 && my >= armorY - 4 && my <= armorY + total + 4;
        else
            return mx >= armorX - 4 && mx <= armorX + total + 4 && my >= armorY - 4 && my <= armorY + iconSize + 4;
    }

    private void saveAndClose() {
        HudConfig.HP_ENABLED.set(hpEnabled);
        HudConfig.HP_TEXT_ENABLED.set(hpTextEnabled);
        HudConfig.HUNGER_ENABLED.set(hungerEnabled);
        HudConfig.HUNGER_TEXT_ENABLED.set(hungerTextEnabled);
        HudConfig.ARMOR_ENABLED.set(armorEnabled);
        HudConfig.ARMOR_VERTICAL.set(armorVertical);
        HudConfig.ARMOR_X_OFFSET.set(armorX - width / 2);
        HudConfig.ARMOR_Y_OFFSET.set(armorY - height / 2);
        HudConfig.ARMOR_ICON_SIZE.set(iconSize);
        HudConfig.ARMOR_POSITION.set(HudConfig.ArmorPosition.CUSTOM);
        HudConfig.SPEC.save();
        isEditing = false;
        onClose();
    }

    private void cancelAndClose() { isEditing = false; onClose(); }

    private void resetToDefault() {
        hpEnabled = hpTextEnabled = hungerEnabled = hungerTextEnabled = armorEnabled = armorVertical = true;
        iconSize = 8;
        armorX = width / 2 - 91 - iconSize - 3;
        armorY = height - 22 - iconSize * 4 - 2 * 3;
        clearWidgets(); init();
    }

    @Override
    public void onClose() { isEditing = false; super.onClose(); }

    @Override
    public boolean isPauseScreen() { return false; }
}
