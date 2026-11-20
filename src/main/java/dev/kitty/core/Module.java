package dev.kitty.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Module {
    private final String name;
    private final String description;
    private final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled;
    private int keyCode = -1; // -1 = unbound; key codes are defined by the platform layer

    protected Module(String name, String description) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
    }

    protected <S extends Setting<?>> S register(S setting) { settings.add(setting); return setting; }

    public String name() { return name; }
    public String description() { return description; }
    public List<Setting<?>> settings() { return Collections.unmodifiableList(settings); }
    public boolean enabled() { return enabled; }
    public int keyCode() { return keyCode; }
    public void bind(int keyCode) { this.keyCode = keyCode; }

    public void setEnabled(boolean on) {
        if (on == enabled) return;
        enabled = on;
        if (on) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}
}
