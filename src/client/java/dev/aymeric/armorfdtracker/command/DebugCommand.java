package dev.aymeric.armorfdtracker.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.aymeric.armorfdtracker.gui.HudConfigScreen;
import dev.aymeric.armorfdtracker.tracker.ArmorNbtReader;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Commande client /fdtracker
 *
 * Sous-commandes :
 *   /fdtracker debug   → Affiche le NBT complet de l'item dans la main
 *                        (pour trouver la bonne clé de kill count)
 *   /fdtracker kills   → Affiche les kills de l'armure équipée dans le chat
 */
public class DebugCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess access) {
        dispatcher.register(
            ClientCommandManager.literal("fdtracker")
                .then(ClientCommandManager.literal("debug")
                    .executes(ctx -> {
                        ItemStack held = ctx.getSource().getPlayer().getMainHandStack();
                        if (held.isEmpty()) {
                            ctx.getSource().sendFeedback(
                                Text.literal("§cTiens un item dans la main !"));
                            return 0;
                        }
                        String nbt = ArmorNbtReader.debugNbt(held);
                        // Découpe le NBT en morceaux de 200 chars pour le chat
                        ctx.getSource().sendFeedback(
                            Text.literal("§6[FDTracker] §fNBT de l'item :"));
                        int chunkSize = 200;
                        for (int i = 0; i < nbt.length(); i += chunkSize) {
                            String chunk = nbt.substring(i, Math.min(i + chunkSize, nbt.length()));
                            ctx.getSource().sendFeedback(Text.literal("§7" + chunk));
                        }
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("gui")
                    .executes(ctx -> {
                        MinecraftClient mc = MinecraftClient.getInstance();
                        mc.execute(() -> mc.setScreen(new HudConfigScreen()));
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("armor")
                    .executes(ctx -> {
                        EquipmentSlot[] slots = { EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD };
                        String[] names = { "Boots", "Leggings", "Chestplate", "Helmet" };
                        for (int i = 0; i < 4; i++) {
                            ItemStack stack = ctx.getSource().getPlayer().getEquippedStack(slots[i]);
                            String nbt = stack.isEmpty() ? "VIDE" : ArmorNbtReader.debugNbt(stack);
                            ctx.getSource().sendFeedback(Text.literal("§6" + names[i] + ": §7" + nbt.substring(0, Math.min(200, nbt.length()))));
                        }
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("kills")
                    .executes(ctx -> {
                        var kills = ArmorNbtReader.readEquippedFDKills();
                        if (kills.isEmpty()) {
                            ctx.getSource().sendFeedback(
                                Text.literal("§c[FDTracker] Aucune pièce Final Destination équipée."));
                            return 0;
                        }
                        ctx.getSource().sendFeedback(
                            Text.literal("§6[FDTracker] §fKills par pièce :"));
                        kills.forEach((piece, count) ->
                            ctx.getSource().sendFeedback(
                                Text.literal("§7  " + piece + " §f: §e" + String.format("%,d", count))));
                        if (kills.size() > 1) {
                            long total = kills.values().stream().mapToLong(Long::longValue).sum();
                            ctx.getSource().sendFeedback(
                                Text.literal("§f  Total §f: §6" + String.format("%,d", total)));
                        }
                        return 1;
                    })
                )
        );
    }
}
