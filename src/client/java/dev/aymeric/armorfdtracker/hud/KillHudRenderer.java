package dev.aymeric.armorfdtracker.hud;

import dev.aymeric.armorfdtracker.config.ModConfig;
import dev.aymeric.armorfdtracker.tracker.ArmorNbtReader;
import dev.aymeric.armorfdtracker.tracker.TierData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Map;

public class KillHudRenderer implements HudRenderCallback {

    // Toggle via keybind
    public static boolean visible = true;

    // Cache : rafraîchi toutes les 4 ticks (~200ms)
    private Map<String, Long> cachedKills = Map.of();
    private int lastReadTick = -1;
    private static final int REFRESH_INTERVAL = 4;

    public static final int BOX_WIDTH = 190;
    public static final int LINE_H    = 10;
    public static final int PADDING   = 4;

    private static final int COLOR_BG      = 0xAA000000;
    private static final int COLOR_BORDER  = 0xFF6A0DAD;
    private static final int COLOR_TITLE   = 0xFFCC44FF;
    private static final int COLOR_PIECE   = 0xFFAAAAAA;
    private static final int COLOR_KILLS   = 0xFFFFD700;
    private static final int COLOR_DEFENSE = 0xFF55FF55;
    private static final int COLOR_NEXT    = 0xFF888888;
    private static final int COLOR_MAX     = 0xFFFFAA00;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!visible) return;
        if (client.player == null) return;
        if (!isOnHypixel(client)) return;
        if (client.currentScreen != null && !(client.currentScreen instanceof ChatScreen)) return;

        int currentTick = client.player.age;
        if (currentTick - lastReadTick >= REFRESH_INTERVAL) {
            cachedKills = ArmorNbtReader.readEquippedFDKills();
            lastReadTick = currentTick;
        }

        render(context, client, cachedKills);
    }

    public static void render(DrawContext context, MinecraftClient client, Map<String, Long> kills) {
        if (kills.isEmpty()) return;

        ModConfig cfg = ModConfig.get();
        float scale = cfg.hudScale;

        int pieceLines = kills.size() * 2;
        int boxHeight = PADDING * 2 + LINE_H + pieceLines * LINE_H;

        // Applique position + scale via la matrice
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(cfg.hudX, cfg.hudY);
        context.getMatrices().scale(scale, scale);

        // Tout dessiné à partir de (0, 0)
        context.fill(0, 0, BOX_WIDTH, boxHeight, COLOR_BG);
        drawBorder(context, 0, 0, BOX_WIDTH, boxHeight, COLOR_BORDER);

        int textY = PADDING;

        context.drawTextWithShadow(client.textRenderer, "⚔ Final Destination",
            PADDING, textY, COLOR_TITLE);
        textY += LINE_H;

        for (Map.Entry<String, Long> entry : kills.entrySet()) {
            String piece = entry.getKey();
            long k = entry.getValue();
            int def = TierData.getCurrentDefense(k);
            long nextKills = TierData.getNextTierKills(k);
            int nextDef = TierData.getNextTierDefense(k);

            String nameLabel = padRight(piece, 12);
            String killStr   = formatKills(k);
            String defStr    = "  ❈+" + def;

            context.drawTextWithShadow(client.textRenderer, nameLabel, PADDING, textY, COLOR_PIECE);
            int killX = PADDING + client.textRenderer.getWidth(nameLabel);
            context.drawTextWithShadow(client.textRenderer, killStr, killX, textY, COLOR_KILLS);
            int defX = killX + client.textRenderer.getWidth(killStr);
            context.drawTextWithShadow(client.textRenderer, defStr, defX, textY, COLOR_DEFENSE);
            textY += LINE_H;

            if (nextKills == -1) {
                context.drawTextWithShadow(client.textRenderer, "  Palier max !", PADDING, textY, COLOR_MAX);
            } else {
                context.drawTextWithShadow(client.textRenderer,
                    "  → " + formatKills(nextKills) + " (❈+" + nextDef + ")",
                    PADDING, textY, COLOR_NEXT);
            }
            textY += LINE_H;
        }

        context.getMatrices().popMatrix();
    }

    private static void drawBorder(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x,         y,         x + w, y + 1,     color);
        ctx.fill(x,         y + h - 1, x + w, y + h,     color);
        ctx.fill(x,         y,         x + 1, y + h,     color);
        ctx.fill(x + w - 1, y,         x + w, y + h,     color);
    }

    public static String formatKills(long kills) {
        if (kills >= 1_000_000) return String.format("%.2fM", kills / 1_000_000.0);
        if (kills >= 1_000)     return String.format("%,d", kills);
        return String.valueOf(kills);
    }

    public static String padRight(String s, int len) {
        return s.length() >= len ? s : s + " ".repeat(len - s.length());
    }

    private static boolean isOnHypixel(MinecraftClient client) {
        if (client.getCurrentServerEntry() == null) return false;
        return client.getCurrentServerEntry().address.toLowerCase().contains("hypixel.net");
    }
}
