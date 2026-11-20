package dev.kitty.client;

import dev.kitty.core.Module;

/** Marker module: KittyClient renders the list of enabled modules on the right side while this is enabled. */
public final class ModuleListModule extends Module {
    public ModuleListModule() { super("modulelist", "Shows enabled modules on the right side of the screen."); }
}
