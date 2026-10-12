package com.example.basefinder;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class BaseFinderScreen extends Screen {
    public BaseFinderScreen() {
        super(Text.literal("BaseFinder Menu"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int row = this.height / 2 - 55;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Scanner: " + (BaseFinderClient.enabled ? "ON" : "OFF")),
                button -> {
                    BaseFinderClient.enabled = !BaseFinderClient.enabled;
                    this.client.setScreen(new BaseFinderScreen());
                }
        ).dimensions(cx - 100, row, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("-"), button -> {
            BaseFinderClient.radius = Math.max(8, BaseFinderClient.radius - 4);
            this.client.setScreen(new BaseFinderScreen());
        }).dimensions(cx - 100, row + 30, 45, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Radius: " + BaseFinderClient.radius + " blocks"), button -> {
            BaseFinderClient.radius = BaseFinderClient.radius >= 32 ? 8 : BaseFinderClient.radius + 4;
            this.client.setScreen(new BaseFinderScreen());
        }).dimensions(cx - 50, row + 30, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> {
            BaseFinderClient.radius = Math.min(32, BaseFinderClient.radius + 4);
            this.client.setScreen(new BaseFinderScreen());
        }).dimensions(cx + 55, row + 30, 45, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("-"), button -> {
            BaseFinderClient.threshold = Math.max(3, BaseFinderClient.threshold - 1);
            this.client.setScreen(new BaseFinderScreen());
        }).dimensions(cx - 100, row + 60, 45, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Threshold: " + BaseFinderClient.threshold), button -> {
            BaseFinderClient.threshold = BaseFinderClient.threshold >= 20 ? 3 : BaseFinderClient.threshold + 1;
            this.client.setScreen(new BaseFinderScreen());
        }).dimensions(cx - 50, row + 60, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> {
            BaseFinderClient.threshold = Math.min(20, BaseFinderClient.threshold + 1);
            this.client.setScreen(new BaseFinderScreen());
        }).dimensions(cx + 55, row + 60, 45, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> this.close())
                .dimensions(cx - 100, row + 95, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 90, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Checks nearby loaded blocks for base-like activity"),
                this.width / 2, this.height / 2 + 67, 0xB8C7D9);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("N = menu  |  alerts have a 30-second cooldown"),
                this.width / 2, this.height / 2 + 80, 0xB8C7D9);
    }
}
