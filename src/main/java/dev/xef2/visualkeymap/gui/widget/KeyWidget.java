package dev.xef2.visualkeymap.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class KeyWidget extends ButtonWidget {

    private static final int WHITE_COLOR = 0xFFFFFF;

    public KeyWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000);
        context.drawBorder(this.getX(), this.getY(), this.width, this.height, 0xFFFFFFFF);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text message = this.getMessage();
        int textWidth = textRenderer.getWidth(message);
        int textX = this.getX() + (this.width - textWidth) / 2;
        int textY = this.getY() + (this.height - 8) / 2;

        context.drawText(
            textRenderer,
            message,
            textX,
            textY,
            WHITE_COLOR,
            false
        );
    }
}