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

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlsOptionsScreenMixin extends Screen {

    protected ControlsOptionsScreenMixin(Text title) {
        super(title);
    }

    // 硬编码方法描述符，不依赖 refmap
    @Inject(method = "init()V", at = @At("TAIL"))
    private void addVisualKeymapButton(CallbackInfo ci) {
        ButtonWidget button = ButtonWidget.builder(
                Text.literal("Visual Keymap"),
                btn -> {
                    if (this.client != null) {
                        this.client.setScreen(new VisualKeymapScreen(this));
                    }
                }
        ).dimensions(this.width / 2 - 100, this.height - 28, 200, 20).build();

        this.addDrawableChild(button);
    }
}