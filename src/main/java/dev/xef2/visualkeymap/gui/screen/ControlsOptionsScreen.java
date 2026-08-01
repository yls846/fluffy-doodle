package dev.xef2.visualkeymap.gui.screen;

import dev.xef2.visualkeymap.gui.widget.KeybindsListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ControlsOptionsScreen extends Screen {
    private final Screen parent;

    public ControlsOptionsScreen(Screen parent) {
        super(Text.translatable("visualkeymap.keybinds.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);

        // 注意：此处 SharedData 和 Consumer 传 null / 空实现，因为这个界面只展示不交互
        KeybindsListWidget keybindsList = new KeybindsListWidget(
            this.client, this.width, this.height - 64, 32, 20,
            null,
            kb -> {}
        );
        this.addSelectableChild(keybindsList);

        adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).width(200).build(),
                2, adder.copyPositioner().marginTop(6));

        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height - 48, this.width, 64);
        gridWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}