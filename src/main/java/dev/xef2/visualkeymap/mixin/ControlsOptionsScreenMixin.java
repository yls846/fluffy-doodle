package dev.xef2.visualkeymap.mixin;

import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ControlsOptionsScreenMixin {

    @Shadow
    protected MinecraftClient client;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T element);

    @Inject(method = "init", at = @At("TAIL"))
    private void addVisualKeymapButton(CallbackInfo ci) {
        // 只在 ControlsOptionsScreen 中添加按钮
        if ((Object) this instanceof ControlsOptionsScreen) {
            ButtonWidget button = ButtonWidget.builder(
                    Text.literal("Visual Keymap"),
                    btn -> {
                        if (this.client != null) {
                            this.client.setScreen(new VisualKeymapScreen((Screen) (Object) this));
                        }
                    }
            ).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build();

            this.addDrawableChild(button);
        }
    }
}