package com.animatedhealthbar;

import net.neoforged.neoforge.common.ModConfigSpec;

public class HudConfig {

    public static final ModConfigSpec SPEC;

    // ===== ВКЛЮЧЕНИЕ/ВЫКЛЮЧЕНИЕ =====
    public static final ModConfigSpec.BooleanValue HP_ENABLED;
    public static final ModConfigSpec.BooleanValue HUNGER_ENABLED;
    public static final ModConfigSpec.BooleanValue ARMOR_ENABLED;
    public static final ModConfigSpec.BooleanValue OXYGEN_ENABLED;
    public static final ModConfigSpec.BooleanValue HP_TEXT_ENABLED;
    public static final ModConfigSpec.BooleanValue HUNGER_TEXT_ENABLED;
    public static final ModConfigSpec.BooleanValue OXYGEN_TEXT_ENABLED;

    // ===== HP ПОЛОСКА =====
    public static final ModConfigSpec.IntValue HP_X_OFFSET;
    public static final ModConfigSpec.IntValue HP_Y_OFFSET;

    // ===== ГОЛОД ПОЛОСКА =====
    public static final ModConfigSpec.IntValue HUNGER_X_OFFSET;
    public static final ModConfigSpec.IntValue HUNGER_Y_OFFSET;

    // ===== КИСЛОРОД ПОЛОСКА =====
    public static final ModConfigSpec.IntValue OXYGEN_X_OFFSET;
    public static final ModConfigSpec.IntValue OXYGEN_Y_OFFSET;

    // ===== БРОНЯ =====
    public static final ModConfigSpec.EnumValue<ArmorPosition> ARMOR_POSITION;
    public static final ModConfigSpec.IntValue ARMOR_X_OFFSET;
    public static final ModConfigSpec.IntValue ARMOR_Y_OFFSET;
    public static final ModConfigSpec.BooleanValue ARMOR_VERTICAL;
    public static final ModConfigSpec.IntValue ARMOR_ICON_SIZE;

    public enum ArmorPosition {
        ABOVE_HP_BAR,
        LEFT_OF_HOTBAR,
        CUSTOM
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Animated Health Bar - HUD Configuration").push("hud");

        builder.comment("=== Enable / Disable ===").push("toggles");
        HP_ENABLED          = builder.comment("Show animated HP bar").define("hp_bar", true);
        HP_TEXT_ENABLED     = builder.comment("Show HP numbers above the bar").define("hp_text", true);
        HUNGER_ENABLED      = builder.comment("Show animated Hunger bar").define("hunger_bar", true);
        HUNGER_TEXT_ENABLED = builder.comment("Show Hunger numbers above the bar").define("hunger_text", true);
        OXYGEN_ENABLED      = builder.comment("Show animated Oxygen bar (when underwater)").define("oxygen_bar", true);
        OXYGEN_TEXT_ENABLED = builder.comment("Show Oxygen numbers above the bar").define("oxygen_text", true);
        ARMOR_ENABLED       = builder.comment("Show armor icons").define("armor_icons", true);
        builder.pop();

        builder.comment("=== HP Bar ===").push("hp_bar");
        HP_X_OFFSET = builder.comment("X offset from default position").defineInRange("x_offset", 0, -500, 500);
        HP_Y_OFFSET = builder.comment("Y offset from default position").defineInRange("y_offset", 0, -500, 500);
        builder.pop();

        builder.comment("=== Hunger Bar ===").push("hunger_bar");
        HUNGER_X_OFFSET = builder.comment("X offset from default position").defineInRange("x_offset", 0, -500, 500);
        HUNGER_Y_OFFSET = builder.comment("Y offset from default position").defineInRange("y_offset", 0, -500, 500);
        builder.pop();

        builder.comment("=== Oxygen Bar ===").push("oxygen_bar");
        OXYGEN_X_OFFSET = builder.comment("X offset from default position").defineInRange("x_offset", 0, -500, 500);
        OXYGEN_Y_OFFSET = builder.comment("Y offset from default position").defineInRange("y_offset", 0, -500, 500);
        builder.pop();

        builder.comment("=== Armor Icons ===").push("armor");
        ARMOR_POSITION = builder.comment(
                "ABOVE_HP_BAR - above the HP bar",
                "LEFT_OF_HOTBAR - vertical column left of hotbar",
                "CUSTOM - use x_offset and y_offset"
        ).defineEnum("position", ArmorPosition.LEFT_OF_HOTBAR);
        ARMOR_VERTICAL  = builder.comment("Vertical (true) or horizontal (false)")
                .define("vertical", true);
        ARMOR_ICON_SIZE = builder.comment("Size of each armor icon in pixels")
                .defineInRange("icon_size", 8, 4, 32);
        ARMOR_X_OFFSET  = builder.comment("X offset (used when position=CUSTOM)")
                .defineInRange("x_offset", 0, -500, 500);
        ARMOR_Y_OFFSET  = builder.comment("Y offset (used when position=CUSTOM)")
                .defineInRange("y_offset", 0, -500, 500);
        builder.pop();

        builder.pop();
        SPEC = builder.build();
    }
}
