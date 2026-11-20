package dev.kitty.client;

import dev.kitty.core.Module;
import net.minecraft.client.MinecraftClient;

import java.util.List;

/** A module that contributes text lines to the top-left HUD stack while enabled. */
public abstract class HudModule extends Module {
    protected HudModule(String name, String description) { super(name, description); }

    /** Called on the render thread with a non-null player and world. */
    public abstract List<String> lines(MinecraftClient client);
}
