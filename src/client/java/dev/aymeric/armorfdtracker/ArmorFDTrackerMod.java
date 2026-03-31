package dev.aymeric.armorfdtracker;

import dev.aymeric.armorfdtracker.command.DebugCommand;
import dev.aymeric.armorfdtracker.hud.KillHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmorFDTrackerMod implements ClientModInitializer {

    public static final String MOD_ID = "armorfdtracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[ArmorFDTracker] Mod chargé — lecture NBT directe de l'armure.");

        // Keybind toggle HUD (par défaut : H)
        KeyBinding.Category fdCategory = KeyBinding.Category.create(
            Identifier.of("armorfdtracker", "keys")
        );

        KeyBinding toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.armorfdtracker.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            fdCategory
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                KillHudRenderer.visible = !KillHudRenderer.visible;
            }
        });

        HudRenderCallback.EVENT.register(new KillHudRenderer());
        ClientCommandRegistrationCallback.EVENT.register(DebugCommand::register);
    }
}
