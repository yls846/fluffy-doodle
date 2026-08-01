package dev.xef2.visualkeymap.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import java.util.List;

public class KeybindsListWidget extends ElementListWidget<KeybindsListWidget.Entry> {

    private final List<KeyBinding> keyBindings;

    public KeybindsListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.keyBindings = client.options.allKeys.stream().toList();

        for (KeyBinding keyBinding : this.keyBindings) {
            this.addEntry(new Entry(keyBinding));
        }
    }

    @Override
    public int getRowWidth() {
        return this.width - 20;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.width - 6;
    }

    public class Entry extends ElementListWidget.Entry<Entry> {

        private final KeyBinding keyBinding;

        public Entry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int contentY = y + 2;
            int contentX = x + 5;

            context.drawText(
                textRenderer,
                Text.literal(this.keyBinding.getTranslationKey()),
                contentX,
                contentY,
                0xFFFFFF,
                false
            );

            String keyName = this.keyBinding.getBoundKeyLocalizedText().getString();
            int keyNameWidth = textRenderer.getWidth(keyName);
            context.drawText(
                textRenderer,
                Text.literal(keyName),
                x + entryWidth - keyNameWidth - 10,
                contentY,
                0xAAAAAA,
                false
            );
        }
    }
}