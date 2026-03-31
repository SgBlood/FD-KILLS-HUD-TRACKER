package dev.aymeric.armorfdtracker.tracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lit les kills d'Armor Final Destination directement depuis le NBT de l'item.
 *
 * Hypixel SkyBlock stocke les données custom dans le component CustomData de l'ItemStack
 * (minecraft:custom_data en 1.21+), sous la clé "ExtraAttributes".
 *
 * Structure NBT d'un item SkyBlock :
 * {
 *   "minecraft:custom_data": {
 *     "ExtraAttributes": {
 *       "id": "FINAL_DESTINATION_HELMET",
 *       "kills": 12345,          ← C'est ce qu'on cherche
 *       "uuid": "...",
 *       ...
 *     }
 *   }
 * }
 *
 * Les IDs SkyBlock des pièces :
 *   FINAL_DESTINATION_HELMET
 *   FINAL_DESTINATION_CHESTPLATE
 *   FINAL_DESTINATION_LEGGINGS
 *   FINAL_DESTINATION_BOOTS
 */
public class ArmorNbtReader {

    // Slots d'armure dans PlayerInventory (index dans armorStacks)
    // 0 = boots, 1 = leggings, 2 = chestplate, 3 = helmet
    private static final String[] FD_IDS = {
        "FINAL_DESTINATION_BOOTS",
        "FINAL_DESTINATION_LEGGINGS",
        "FINAL_DESTINATION_CHESTPLATE",
        "FINAL_DESTINATION_HELMET"
    };

    public static final String[] PIECE_NAMES = {
        "Boots",
        "Leggings",
        "Chestplate",
        "Helmet"
    };

    /**
     * Lit les kills pour chaque pièce d'Armor Final Destination équipée.
     *
     * @return Map pieceName → killCount. Si une pièce n'est pas portée ou
     *         n'est pas une FD, elle est absente de la map.
     */
    public static Map<String, Long> readEquippedFDKills() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return Map.of();

        Map<String, Long> result = new LinkedHashMap<>();

        EquipmentSlot[] slots = { EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD };
        // itère de 3→0 pour afficher Helmet en premier
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = client.player.getEquippedStack(slots[i]);
            if (stack.isEmpty()) continue;

            NbtCompound extra = getExtraAttributes(stack);
            if (extra == null) continue;

            String skyblockId = extra.getString("id").orElse("");
            if (!skyblockId.equals(FD_IDS[i])) continue;

            // Le kill count peut être stocké sous "kills" ou "enderman_kills"
            // On essaie les deux clés connues
            long kills = 0;
            if (extra.contains("eman_kills")) {
                kills = extra.getLong("eman_kills").orElse(0L);
            } else if (extra.contains("kills")) {
                kills = extra.getLong("kills").orElse(0L);
            }

            result.put(PIECE_NAMES[i], kills);
        }

        return result;
    }

    /**
     * Lit le total de kills (somme de toutes les pièces équipées).
     * Utile pour un affichage condensé.
     */
    public static long readTotalKills() {
        return readEquippedFDKills().values().stream()
            .mapToLong(Long::longValue)
            .sum();
    }

    /**
     * Retourne vrai si au moins une pièce d'Armor Final Destination est portée.
     */
    public static boolean isWearingFD() {
        return !readEquippedFDKills().isEmpty();
    }

    /**
     * Extrait les ExtraAttributes depuis le component CustomData de l'ItemStack.
     *
     * En Minecraft 1.21+, Hypixel encode ses données custom dans
     * minecraft:custom_data (DataComponentTypes.CUSTOM_DATA).
     */
    private static NbtCompound getExtraAttributes(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return null;

        NbtCompound root = customData.copyNbt();
        // Hypixel stocke les données SkyBlock directement à la racine du custom_data
        if (!root.contains("id")) return null;

        return root;
    }

    /**
     * Pour debug : retourne tout le NBT custom d'un item sous forme de String.
     * Utilise /fdtracker debug in-game pour l'appeler.
     */
    public static String debugNbt(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return "Pas de CustomData";
        return customData.copyNbt().toString();
    }
}
