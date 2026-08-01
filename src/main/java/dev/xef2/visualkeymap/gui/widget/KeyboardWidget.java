package dev.xef2.visualkeymap.gui.widget;

import dev.xef2.visualkeymap.VisualKeymap;
import dev.xef2.visualkeymap.api.KeyBinding;
import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class KeyboardWidget implements Element {

    private static final int KEY_SPACING = 1;

    private int x, y;
    private int width;
    private int maxHeight;
    private int height;
    private int keySize;
    private final KeyLayoutHelper.KeyboardLayout keyboardLayout;
    private final Map<KeyLayoutHelper.KeyLayout, KeyWidget> keyWidgetMap = new HashMap<>();

    public KeyboardWidget(
            int x, int y, int width, int maxHeight,
            boolean isFull, VisualKeymapScreen.SharedData sharedData,
            Function<InputUtil.Key, List<? extends KeyBinding>> bindingGetter,
            Consumer<InputUtil.Key> keySelector
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.maxHeight = maxHeight;

        this.keyboardLayout = KeyLayoutHelper.getLayout(isFull);

        for (KeyLayoutHelper.KeyLayout keyLayout : this.keyboardLayout.keys()) {
            String translationKey = VisualKeymap.getTranslationKey("key." + keyLayout.translationKey());
            InputUtil.Key key = keyLayout.getKey();
            Text text = I18n.hasTranslation(translationKey) ?
                    Text.translatable(translationKey) :
                    key.getLocalizedText();
            this.keyWidgetMap.put(keyLayout, new KeyWidget(
                    key, text, sharedData, () -> bindingGetter.apply(key), () -> keySelector.accept(key)
            ));
        }

        this.updateSizeAndHeight();
    }

    public void updateKeyBindings() {
        this.keyWidgetMap.values().forEach(KeyWidget::setupTooltip);
    }

    public void setSize(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.updateSizeAndHeight();
    }

    public int getHeight() {
        return this.height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Map<KeyLayoutHelper.KeyLayout, KeyWidget> getKeyWidgetMap() {
        return this.keyWidgetMap;
    }

    private int getSizeWithSpacing(double mult) {
        return (int) (mult * this.keySize + (mult - 1) * KEY_SPACING);
    }

    private void updateSizeAndHeight() {
        double totalCols = this.keyboardLayout.columns();
        double totalRows = this.keyboardLayout.rows();

        this.keySize = MathHelper.floor(Math.min(
                (this.width - (totalCols - 1) * KEY_SPACING) / totalCols,
                (this.maxHeight - (totalRows - 1) * KEY_SPACING) / totalRows
        ));
        this.height = getSizeWithSpacing(totalRows);
    }

    public void refreshPositions() {
        int totalWidth = getSizeWithSpacing(this.keyboardLayout.columns());

        int startX = this.x + (this.width - totalWidth) / 2;
        int startY = this.y;

        for (KeyLayoutHelper.KeyLayout keyLayout : this.keyboardLayout.keys()) {
            int width = getSizeWithSpacing(keyLayout.widthMult());
            int height = getSizeWithSpacing(keyLayout.heightMult());

            int keyX = startX + (int) (keyLayout.col() * (this.keySize + KEY_SPACING));
            int keyY = startY + (int) (keyLayout.row() * (this.keySize + KEY_SPACING));

            KeyWidget widget = this.keyWidgetMap.get(keyLayout);
            widget.setX(keyX);
            widget.setY(keyY);
            widget.setWidth(width);
            widget.setHeight(height);
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (KeyWidget widget : this.keyWidgetMap.values()) {
            widget.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (KeyWidget widget : this.keyWidgetMap.values()) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }
}