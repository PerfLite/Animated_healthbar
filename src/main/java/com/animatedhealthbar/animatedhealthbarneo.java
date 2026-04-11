package com.animatedhealthbar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod("animatedhealthbar")
public class animatedhealthbarneo {

    public static final String MODID = "animatedhealthbar";

    public animatedhealthbarneo(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, HudConfig.SPEC);

        // AppleSkin слои теперь отменяются напрямую в HudEventHandler
        // через RenderGuiLayerEvent.Pre — отдельная регистрация не нужна.
    }
}
