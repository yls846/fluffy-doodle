package dev.xef2.visualkeymap;

import dev.xef2.visualkeymap.api.KeyBinding;
import dev.xef2.visualkeymap.api.MinecraftImpl;
import dev.xef2.visualkeymap.api.VisualKeymapApi;
import dev.xef2.visualkeymap.gui.screen.VisualKeymapScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class VisualKeymap implements ClientModInitializer {
    private static final String MOD_ID = "visualkeymap";

    private static final List<VisualKeymapApi<?>> apiImpl = new ArrayList<>(List.of(
            new MinecraftImpl()
    ));

    @Override
    public void onInitializeClient() {
        // 原有的 API 加载逻辑
        FabricLoader.getInstance().getEntrypointContainers(MOD_ID, VisualKeymapApi.class).forEach(entrypoint -> {
            try {
                VisualKeymapApi<?> api = entrypoint.getEntrypoint();
                apiImpl.add(api);
            } catch (Throwable ignored) {
            }
        });

        // 在“按键控制”界面添加自定义按钮
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof ControlsOptionsScreen) {
                screen.addDrawableChild(
                        ButtonWidget.builder(
                                Text.literal("Visual Keymap"),
                                btn -> client.setScreen(new VisualKeymapScreen(screen))
                        ).dimensions(screen.width / 2 - 100, screen.height - 28, 200, 20).build()
                );
            }
        });
    }

    public static List<? extends KeyBinding> getKeyBindings() {
        return apiImpl.stream().flatMap(api -> api.getKeyBindings().stream()).toList();
    }

    public static void saveKeyBindings() {
        apiImpl.forEach(VisualKeymapApi::save);
    }

    public static String getTranslationKey(String key) {
        return MOD_ID + "." + key;
    }
}