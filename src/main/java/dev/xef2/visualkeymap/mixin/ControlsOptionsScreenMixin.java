package dev.xef2.visualkeymap.mixin;

import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
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
    protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T element);

    @Inject(method = "init", at = @At("TAIL"))
    private void addVisualKeymapButton(CallbackInfo ci) {
        // 仅在 ControlsOptionsScreen 中生效
        if ((Object) this instanceof ControlsOptionsScreen) {
            ButtonWidget button = ButtonWidget.builder(
                    Text.literal("Visual Keymap"),
                    btn -> {
                        if (this instanceof Screen screen) {
                            screen.client.setScreen(new VisualKeymapScreen(screen));
                        }
                    }
            ).dimensions(((Screen) (Object) this).width / 2 - 100,
                    ((Screen) (Object) this).height - 28, 200, 20).build();

            this.addDrawableChild(button);
        }
    }
}