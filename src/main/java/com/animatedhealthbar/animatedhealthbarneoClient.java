package com.animatedhealthbar;

import com.animatedhealthbar.gui.ArmorEditorScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = animatedhealthbarneo.MODID, value = Dist.CLIENT)
public class animatedhealthbarneoClient {

    public static final KeyMapping OPEN_EDITOR = new KeyMapping(
            "key.animatedhealthbar.open_editor",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_HOME, // клавиша Home по умолчанию
            "key.categories.animatedhealthbar"
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_EDITOR);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (OPEN_EDITOR.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen == null) {
                mc.setScreen(new ArmorEditorScreen());
            }
        }
    }
}
