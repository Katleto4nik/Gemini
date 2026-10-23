package com.gabdab.gaboptimization;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GabOptimizationScreen extends Screen {
    private static final int W = 160;
    private static final int H = 76;

    public GabOptimizationScreen() {
        super(Text.literal("GabOptimization"));
    }

    private static Text label() {
        return Text.literal(Config.enabled ? "ON" : "OFF")
                .formatted(Config.enabled ? Formatting.GREEN : Formatting.RED);
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(label(), b -> {
            Config.enabled = !Config.enabled;
            Config.save();
            b.setMessage(label());
        }).dimensions(width / 2 - 40, height / 2 + 2, 80, 20).build());
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x60000000);
        int x = width / 2 - W / 2;
        int y = height / 2 - 34;
        context.fill(x, y, x + W, y + H, 0xE0101010);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String fps = "FPS: " + client.getCurrentFps();
        context.drawText(textRenderer, fps, width / 2 - textRenderer.getWidth(fps) / 2,
                height / 2 - 24, 0xFFFFFFFF, true);

        String lvl = "Рівень: " + Optimizer.level + "/" + Optimizer.MAX_LEVEL;
        context.drawText(textRenderer, lvl, width / 2 - textRenderer.getWidth(lvl) / 2,
                height / 2 - 10, 0xFFAAAAAA, true);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
