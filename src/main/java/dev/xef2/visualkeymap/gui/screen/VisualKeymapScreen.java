package dev.xef2.visualkeymap.gui.screen;

import dev.xef2.visualkeymap.VisualKeymap;
import dev.xef2.visualkeymap.api.KeyBinding;
import dev.xef2.visualkeymap.gui.widget.KeybindsListWidget;
import dev.xef2.visualkeymap.gui.widget.KeyboardWidget;
import dev.xef2.visualkeymap.gui.widget.KeyWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class VisualKeymapScreen extends Screen {

    private List<? extends KeyBinding> keyBindings = List.of();
    private final SharedData sharedData = new SharedData();
    private final List<InputUtil.Key> pressedKeys = new ArrayList<>();
    private final Screen parent;

    private KeyboardWidget keyboardWidget;
    private KeybindsListWidget keybindsListWidget;
    private ButtonWidget doneButton;

    private static final int KEYBOARD_PADDING = 5;
    private static final int DONE_BUTTON_HEIGHT = 20;

    public VisualKeymapScreen(Screen parent) {
        super(Text.translatable(VisualKeymap.getTranslationKey("gui.title")));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.keyBindings = VisualKeymap.getKeyBindings();

        int headerHeight = 30;
        int doneArea = DONE_BUTTON_HEIGHT + 10;
        int contentHeight = this.height - headerHeight - doneArea;
        int keyboardHeight = contentHeight / 2;
        int listHeight = contentHeight - keyboardHeight;

        this.doneButton = ButtonWidget.builder(
                Text.translatable("gui.done"),
                (button) -> this.close()
        ).dimensions(this.width / 2 - 100, this.height - doneArea + 4, 200, DONE_BUTTON_HEIGHT).build();
        this.addDrawableChild(this.doneButton);

        this.keyboardWidget = new KeyboardWidget(
                KEYBOARD_PADDING, headerHeight,
                this.width - KEYBOARD_PADDING * 2, keyboardHeight - KEYBOARD_PADDING,
                true, sharedData,
                this::getBindingsForKey, this::setSelectedKey
        );
        this.keyboardWidget.refreshPositions();

        for (KeyWidget kw : this.keyboardWidget.getKeyWidgetMap().values()) {
            this.addDrawableChild(kw);
        }

        this.keybindsListWidget = new KeybindsListWidget(
                this.client, this.width,
                listHeight - KEYBOARD_PADDING,
                headerHeight + keyboardHeight + KEYBOARD_PADDING,
                20,
                sharedData,
                k -> {
                    k.resetToDefault();
                    this.keyboardWidget.updateKeyBindings();
                }
        );
        this.keybindsListWidget.setKeyBindings(new ArrayList<>(this.getUnboundBindings()));
        this.addDrawableChild(this.keybindsListWidget);
    }

    private List<? extends KeyBinding> getUnboundBindings() {
        return this.keyBindings.stream()
                .filter(binding -> binding.getKeyCodes().isEmpty())
                .toList();
    }

    private List<? extends KeyBinding> getBindingsForKey(InputUtil.Key key) {
        int keyCode = key.getCode();
        return this.keyBindings.stream()
                .filter(binding -> binding.getKeyCodes().contains(keyCode))
                .toList();
    }

    private void setSelectedKey(InputUtil.Key key) {
        int keyCode = key.getCode();
        if (this.sharedData.selectedKeyCode != null && this.sharedData.selectedKeyCode == keyCode) {
            this.sharedData.selectedKeyCode = null;
            this.keybindsListWidget.setKeyBindings(new ArrayList<>(this.getUnboundBindings()));
        } else {
            this.sharedData.selectedKeyCode = keyCode;
            this.keybindsListWidget.setKeyBindings(new ArrayList<>(this.getBindingsForKey(key)));
        }
        this.sharedData.selectedKeyBinding = null;
    }

    private void setKeyBinding(boolean escape) {
        this.sharedData.selectedKeyBinding.setBoundKeys(new ArrayList<>(this.pressedKeys));
        if (escape || this.pressedKeys.size() >= this.sharedData.selectedKeyBinding.getMaxBoundKeys()) {
            this.sharedData.selectedKeyBinding = null;
            this.pressedKeys.clear();
        }
        this.keybindsListWidget.updateAllEntries();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.sharedData.selectedKeyBinding != null) {
            this.pressedKeys.add(InputUtil.Type.MOUSE.createFromCode(button));
            this.setKeyBinding(true);
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputUtil.Key key = InputUtil.fromKeyCode(keyCode, scanCode);
        if (this.sharedData.selectedKeyBinding != null && !this.pressedKeys.contains(key)) {
            if (keyCode != 256) {
                this.pressedKeys.add(key);
            }
            this.setKeyBinding(keyCode == 256);
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.sharedData.selectedKeyBinding != null) {
            this.setKeyBinding(true);
            return true;
        } else {
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.keyboardWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    public void removed() {
        VisualKeymap.saveKeyBindings();
        super.removed();
    }

    public static class SharedData {
        public Integer selectedKeyCode = null;
        public KeyBinding selectedKeyBinding = null;
    }
}