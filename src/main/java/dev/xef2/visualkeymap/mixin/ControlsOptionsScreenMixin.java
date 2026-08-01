package dev.xef2.visualkeymap.mixin;

import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)                     // 改为所有屏幕的基类
public abstract class ControlsOptionsScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void addVisualKeymapButton(CallbackInfo ci) {
        // 只在 ControlsOptionsScreen 里添加按钮
        if ((Object) this instanceof ControlsOptionsScreen screen) {
            ButtonWidget button = ButtonWidget.builder(
                    Text.literal("Visual Keymap"),
                    btn -> {
                        if (screen.client != null) {
                            screen.client.setScreen(new VisualKeymapScreen(screen));
                        }
                    }
            ).dimensions(screen.width / 2 - 100, screen.height - 28, 200, 20).build();

            screen.addDrawableChild(button);
        }
    }
}