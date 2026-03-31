package dev.aymeric.armorfdtracker.gui;

import dev.aymeric.armorfdtracker.config.ModConfig;
import dev.aymeric.armorfdtracker.hud.KillHudRenderer;
import dev.aymeric.armorfdtracker.tracker.ArmorNbtReader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Map;

public class HudConfigScreen extends Screen {

    // Paliers de scale nets (multiples de 0.25 → texte toujours net)
    private static final float[] SCALES = { 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f };

    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    // Données en direct pour le preview
    private Map<String, Long> previewKills = Map.of();

    public HudConfigScreen() {
        super(Text.literal("FD Tracker Config"));
    }

    @Override
    protected void init() {
        super.init();
        // Charge les kills une fois à l'ouverture
        previewKills = ArmorNbtReader.readEquippedFDKills();
    }

    @Override
    public void tick() {
        // Rafraîchit les kills à chaque tick pour le preview en direct
        previewKills = ArmorNbtReader.readEquippedFDKills();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xAA000000);

        ModConfig cfg = ModConfig.get();

        // Preview HUD en temps réel avec les vraies données
        if (previewKills.isEmpty()) {
            // Aucune pièce équipée → aperçu fictif
            drawMockPreview(context, cfg);
        } else {
            KillHudRenderer.render(context, MinecraftClient.getInstance(), previewKills);
        }

        // Instructions + indicateur de scale
        int pct = Math.round(cfg.hudScale * 100);
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§fGlisse · §aMolette§f pour zoomer · §aÉchap §fpour sauvegarder"),
            this.width / 2, this.height - 24, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("§7Taille : §e" + pct + "%"),
            this.width / 2, this.height - 14, 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    /** Aperçu fictif quand aucune pièce FD n'est équipée. */
    private void drawMockPreview(DrawContext context, ModConfig cfg) {
        float scale = cfg.hudScale;
        int x = cfg.hudX, y = cfg.hudY;
        int w = KillHudRenderer.BOX_WIDTH;
        int h = KillHudRenderer.PADDING * 2 + KillHudRenderer.LINE_H + 4 * 2 * KillHudRenderer.LINE_H;

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(x, y);
        context.getMatrices().scale(scale, scale);

        context.fill(0, 0, w, h, 0xAA000000);
        drawBorder(context, 0, 0, w, h, 0xFF6A0DAD);
        int ty = KillHudRenderer.PADDING;
        context.drawTextWithShadow(this.textRenderer, "⚔ Final Destination",         KillHudRenderer.PADDING, ty, 0xFFCC44FF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§7Helmet       §e14,018  §a❈+310", KillHudRenderer.PADDING, ty, 0xFFFFFFFF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§8  → §725,000 §8(§a❈+335§8)",    KillHudRenderer.PADDING, ty, 0xFFFFFFFF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§7Chestplate   §e14,018  §a❈+310", KillHudRenderer.PADDING, ty, 0xFFFFFFFF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§8  → §725,000 §8(§a❈+335§8)",    KillHudRenderer.PADDING, ty, 0xFFFFFFFF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§7Leggings     §e14,018  §a❈+310", KillHudRenderer.PADDING, ty, 0xFFFFFFFF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§8  → §725,000 §8(§a❈+335§8)",    KillHudRenderer.PADDING, ty, 0xFFFFFFFF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§7Boots        §e14,018  §a❈+310", KillHudRenderer.PADDING, ty, 0xFFFFFFFF); ty += KillHudRenderer.LINE_H;
        context.drawTextWithShadow(this.textRenderer, "§8  → §725,000 §8(§a❈+335§8)",    KillHudRenderer.PADDING, ty, 0xFFFFFFFF);

        context.getMatrices().popMatrix();
    }

    private void drawBorder(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
    }

    /** Trouve la zone cliquable du HUD en tenant compte du scale. */
    private boolean isOverHud(double mouseX, double mouseY) {
        ModConfig cfg = ModConfig.get();
        float scale = cfg.hudScale;
        int hudW = (int)(KillHudRenderer.BOX_WIDTH * scale);
        int hudH = (int)((KillHudRenderer.PADDING * 2 + KillHudRenderer.LINE_H + 4 * 2 * KillHudRenderer.LINE_H) * scale);
        return mouseX >= cfg.hudX && mouseX <= cfg.hudX + hudW &&
               mouseY >= cfg.hudY && mouseY <= cfg.hudY + hudH;
    }

    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        if (isOverHud(click.x(), click.y())) {
            dragging = true;
            dragOffsetX = (int) click.x() - ModConfig.get().hudX;
            dragOffsetY = (int) click.y() - ModConfig.get().hudY;
            return true;
        }
        return super.mouseClicked(click, bl);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (dragging) {
            ModConfig cfg = ModConfig.get();
            cfg.hudX = Math.max(0, Math.min((int) click.x() - dragOffsetX, this.width - KillHudRenderer.BOX_WIDTH));
            cfg.hudY = Math.max(0, Math.min((int) click.y() - dragOffsetY, this.height - 50));
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (dragging) { dragging = false; return true; }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        ModConfig cfg = ModConfig.get();
        // Snap au palier suivant/précédent pour un texte net
        float current = cfg.hudScale;
        if (vertical > 0) {
            for (float s : SCALES) {
                if (s > current + 0.01f) { cfg.hudScale = s; return true; }
            }
        } else {
            for (int i = SCALES.length - 1; i >= 0; i--) {
                if (SCALES[i] < current - 0.01f) { cfg.hudScale = SCALES[i]; return true; }
            }
        }
        return true;
    }

    @Override
    public void close() {
        ModConfig.get().save();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
