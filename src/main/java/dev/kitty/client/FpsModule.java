package dev.kitty.client;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class FpsModule extends HudModule {
    public FpsModule() { super("fps", "Shows the current frames per second."); }

    @Override public List<String> lines(MinecraftClient client) {
        return List.of(client.getCurrentFps() + " FPS");
    }
}
