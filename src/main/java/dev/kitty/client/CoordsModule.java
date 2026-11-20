package dev.kitty.client;

import dev.kitty.core.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;
import java.util.Locale;

public final class CoordsModule extends HudModule {
    private final Setting.Bool showFacing = register(new Setting.Bool("showFacing", true));

    public CoordsModule() { super("coords", "Shows your position and facing direction."); }

    @Override public List<String> lines(MinecraftClient client) {
        ClientPlayerEntity p = client.player;
        if (p == null) return List.of();
        String xyz = String.format(Locale.ROOT, "XYZ %.1f / %.1f / %.1f", p.getX(), p.getY(), p.getZ());
        if (!showFacing.get()) return List.of(xyz);
        return List.of(xyz, "Facing " + p.getHorizontalFacing().asString());
    }
}
