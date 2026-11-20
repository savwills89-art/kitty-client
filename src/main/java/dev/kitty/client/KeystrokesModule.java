package dev.kitty.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

import java.util.List;

public final class KeystrokesModule extends HudModule {
    public KeystrokesModule() { super("keystrokes", "Shows which movement keys are held."); }

    private static String key(boolean down, String label) { return down ? "[" + label + "]" : " " + label.toLowerCase() + " "; }

    @Override public List<String> lines(MinecraftClient client) {
        GameOptions o = client.options;
        return List.of(
            "     " + key(o.forwardKey.isPressed(), "W"),
            key(o.leftKey.isPressed(), "A") + key(o.backKey.isPressed(), "S") + key(o.rightKey.isPressed(), "D"),
            key(o.jumpKey.isPressed(), "SPACE"));
    }
}
