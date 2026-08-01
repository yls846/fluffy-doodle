package dev.xef2.visualkeymap.mixin;

import dev.xef2.visualkeymap.VisualKeymap;
import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ControlsOptionsScreen.class)
public class ControlsOptionsScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void addVisualKeymapButton(CallbackInfo ci) {
        ControlsOptionsScreen self = (ControlsOptionsScreen) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();

        ButtonWidget button = ButtonWidget.builder(
                Text.translatable(VisualKeymap.getTranslationKey("gui.open_keymap")),
                (btn) -> client.setScreen(new VisualKeymapScreen(
                        self,
                        client.options
                ))
        ).dimensions(self.width / 2 - 100, self.height / 6 + 144 - 26, 200, 20).build();

        // Use addDrawableChild to add the button; available via the Screen base class
        ((net.minecraft.client.gui.screen.Screen) (Object) this).addDrawableChild(button);
    }
}