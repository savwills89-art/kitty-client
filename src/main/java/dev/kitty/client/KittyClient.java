package dev.kitty.client;

import dev.kitty.core.ConfigStore;
import dev.kitty.core.Module;
import dev.kitty.core.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class KittyClient implements ClientModInitializer {
    public static final String MOD_ID = "kitty";
    private static final Logger LOG = LoggerFactory.getLogger("Kitty Client");
    private static final int WHITE = 0xFFFFFFFF;
    private static final int PADDING = 4;

    private final ModuleManager manager = new ModuleManager();
    private final Map<KeyBinding, Module> keyBindings = new LinkedHashMap<>();
    private ConfigStore config;

    @Override
    public void onInitializeClient() {
        FpsModule fps = new FpsModule();
        CoordsModule coords = new CoordsModule();
        KeystrokesModule keystrokes = new KeystrokesModule();
        ModuleListModule moduleList = new ModuleListModule();
        manager.add(fps);
        manager.add(coords);
        manager.add(keystrokes);
        manager.add(moduleList);

        fps.setEnabled(true);
        coords.setEnabled(true);

        config = new ConfigStore(FabricLoader.getInstance().getConfigDir().resolve("kitty-client.properties"));
        try {
            config.load(manager);
        } catch (IOException e) {
            LOG.warn("Could not read config, using defaults", e);
        }

        bindKey(fps, GLFW.GLFW_KEY_KP_1);
        bindKey(coords, GLFW.GLFW_KEY_KP_2);
        bindKey(keystrokes, GLFW.GLFW_KEY_KP_3);
        bindKey(moduleList, GLFW.GLFW_KEY_KP_4);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Map.Entry<KeyBinding, Module> e : keyBindings.entrySet()) {
                while (e.getKey().wasPressed()) e.getValue().toggle();
            }
            manager.tick();
        });

        HudRenderCallback.EVENT.register(this::renderHud);

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            try {
                config.save(manager);
            } catch (IOException e) {
                LOG.warn("Could not save config", e);
            }
        });
    }

    private void bindKey(Module module, int defaultKey) {
        KeyBinding kb = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key." + MOD_ID + "." + module.name(), InputUtil.Type.KEYSYM, defaultKey, "category." + MOD_ID));
        keyBindings.put(kb, module);
    }

    private void renderHud(DrawContext ctx, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;

        int lineHeight = mc.textRenderer.fontHeight + 2;
        int y = PADDING;
        for (Module m : manager.all()) {
            if (!(m instanceof HudModule hud) || !hud.enabled()) continue;
            for (String line : hud.lines(mc)) {
                ctx.drawTextWithShadow(mc.textRenderer, line, PADDING, y, WHITE);
                y += lineHeight;
            }
        }

        boolean listOn = manager.find("modulelist").map(Module::enabled).orElse(false);
        if (listOn) {
            int ry = PADDING;
            int right = ctx.getScaledWindowWidth() - PADDING;
            for (Module m : manager.all()) {
                if (!m.enabled() || m instanceof ModuleListModule) continue;
                ctx.drawTextWithShadow(mc.textRenderer, m.name(), right - mc.textRenderer.getWidth(m.name()), ry, WHITE);
                ry += lineHeight;
            }
        }
    }
}
