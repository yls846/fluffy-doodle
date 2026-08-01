package dev.xef2.visualkeymap.gui.widget;

import dev.xef2.visualkeymap.api.KeyBinding;
import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class KeybindsListWidget extends ElementListWidget<KeybindsListWidget.Entry> {

    private List<KeyBinding> keyBindings = new ArrayList<>();
    private final VisualKeymapScreen.SharedData sharedData;
    private final java.util.function.Consumer<KeyBinding> onReset;

    public KeybindsListWidget(
            MinecraftClient client, int width, int height, int top,
            int itemHeight,
            VisualKeymapScreen.SharedData sharedData,
            java.util.function.Consumer<KeyBinding> onReset
    ) {
        super(client, width, height, top, itemHeight);
        this.sharedData = sharedData;
        this.onReset = onReset;
    }

    public void setKeyBindings(List<KeyBinding> bindings) {
        this.keyBindings = bindings;
        rebuildEntries();
    }

    public void updateAllEntries() {
        rebuildEntries();
    }

    private void rebuildEntries() {
        this.clearEntries();
        for (KeyBinding kb : this.keyBindings) {
            this.addEntry(new Entry(kb));
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

        // 1.21 中 render 的鼠标坐标为 double
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           double mouseX, double mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int contentY = y + 2;
            int contentX = x + 5;

            Text displayName = keyBinding.getDisplayName();
            int nameColor = (sharedData != null && sharedData.selectedKeyBinding == keyBinding) ? 0xFFFF55 : 0xFFFFFF;
            context.drawText(
                    textRenderer,
                    displayName,
                    contentX,
                    contentY,
                    nameColor,
                    false
            );

            Text boundKeysText = keyBinding.getBoundKeysLocalizedText();
            int keyNameWidth = textRenderer.getWidth(boundKeysText);
            context.drawText(
                    textRenderer,
                    boundKeysText,
                    (int)(x + entryWidth - keyNameWidth - 10),
                    contentY,
                    0xAAAAAA,
                    false
            );
        }

        // 必须实现的两个抽象方法，去掉 @Override 以避免签名冲突
        public List<Selectable> selectableChildren() {
            return List.of();
        }

        public List<Element> children() {
            return List.of();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                if (sharedData != null && button == 1) {
                    onReset.accept(keyBinding);
                    return true;
                }
                if (sharedData != null) {
                    sharedData.selectedKeyBinding = keyBinding;
                    sharedData.selectedKeyCode = null;
                }
                return true;
            }
            return false;
        }
    }
}
