package dev.xef2.visualkeymap.gui.widget;

import dev.xef2.visualkeymap.api.KeyBinding;
import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class KeyWidget extends ClickableWidget {

    private final InputUtil.Key key;
    private final VisualKeymapScreen.SharedData sharedData;
    private final Supplier<List<? extends KeyBinding>> bindingGetter;
    private final Runnable onClick;

    public KeyWidget(
            InputUtil.Key key, Text message,
            VisualKeymapScreen.SharedData sharedData,
            Supplier<List<? extends KeyBinding>> bindingGetter,
            Runnable onClick
    ) {
        super(0, 0, 0, 0, message);
        this.key = key;
        this.sharedData = sharedData;
        this.bindingGetter = bindingGetter;
        this.onClick = onClick;
    }

    public void setupTooltip() {
        List<? extends KeyBinding> bindings = this.bindingGetter.get();
        if (bindings.isEmpty()) {
            this.setTooltip(null);
        } else {
            this.setTooltip(Tooltip.of(
                    Text.literal(String.join(", ",
                            bindings.stream().map(kb -> kb.getDisplayName().getString()).toList()))
            ));
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onClick.run();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int color;
        if (this.sharedData.selectedKeyCode != null && this.sharedData.selectedKeyCode == this.key.getCode()) {
            color = 0xFFFFFF00;
        } else if (!this.bindingGetter.get().isEmpty()) {
            color = 0xFF00AA00;
        } else if (this.isHovered()) {
            color = 0xFF888888;
        } else {
            color = 0xFF444444;
        }

        context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
        context.drawBorder(this.getX(), this.getY(), this.width, this.height, 0xFF000000);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text message = this.getMessage();
        int textWidth = textRenderer.getWidth(message);
        context.drawText(
                textRenderer,
                message,
                this.getX() + (this.width - textWidth) / 2,
                this.getY() + (this.height - 8) / 2,
                0xFFFFFF,
                false
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        appendDefaultNarrations(builder);
    }
}