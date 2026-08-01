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

    // 去掉 @Override 以避免签名不匹配（1.21 可能改变了方法签名）
    public int getRowWidth() {
        return this.width - 20;
    }

    // 同上
    protected int getScrollbarPositionX() {
        return this.width - 6;
    }

    public class Entry extends ElementListWidget.Entry<Entry> {

        private final KeyBinding keyBinding;

        public Entry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
        }

        // 使用 int 类型的 mouseX, mouseY，符合父类抽象方法
        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
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
                    x + entryWidth - keyNameWidth - 10,
                    contentY,
                    0xAAAAAA,
                    false
            );
        }

        // 实现抽象方法，去掉 @Override 避免签名问题
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
