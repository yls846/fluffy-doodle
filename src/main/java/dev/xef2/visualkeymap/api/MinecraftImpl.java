package dev.xef2.visualkeymap.api;

import dev.xef2.visualkeymap.mixin.KeyBindingAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class MinecraftImpl implements VisualKeymapApi<MinecraftImpl.MinecraftKeyBinding> {

    @Override
    public List<MinecraftKeyBinding> getKeyBindings() {
        return Arrays.stream(MinecraftClient.getInstance().options.allKeys).map(MinecraftKeyBinding::new).toList();
    }

    @Override
    public void save() {
        // Minecraft handles keybinding saving automatically.
    }

    public static class MinecraftKeyBinding extends dev.xef2.visualkeymap.api.KeyBinding {

        private final KeyBinding binding;

        public MinecraftKeyBinding(KeyBinding binding) {
            super(Text.translatable(binding.getCategory()), Text.translatable(binding.getTranslationKey()), 1);
            this.binding = binding;
        }

        @Override
        public List<Integer> getKeyCodes() {
            InputUtil.Key boundKey = ((KeyBindingAccessor) this.binding).getBoundKey();
            return boundKey == null || boundKey.equals(InputUtil.UNKNOWN_KEY) ? List.of() : List.of(boundKey.getCode());
        }

        @Override
        public void setBoundKeys(List<InputUtil.Key> keys) {
            this.binding.setBoundKey(keys.isEmpty() ? InputUtil.UNKNOWN_KEY : keys.get(0));
        }

        @Override
        public boolean isDefault() {
            return this.binding.isDefault();
        }

        @Override
        public void resetToDefault() {
            this.binding.setBoundKey(this.binding.getDefaultKey());
        }
    }
}