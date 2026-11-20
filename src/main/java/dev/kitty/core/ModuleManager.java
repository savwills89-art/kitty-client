package dev.kitty.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class ModuleManager {
    private final Map<String, Module> byName = new LinkedHashMap<>();

    public void add(Module module) {
        String key = module.name().toLowerCase(Locale.ROOT);
        if (byName.putIfAbsent(key, module) != null)
            throw new IllegalStateException("duplicate module: " + module.name());
    }

    public Optional<Module> find(String name) {
        return Optional.ofNullable(byName.get(name.toLowerCase(Locale.ROOT)));
    }

    public List<Module> all() { return Collections.unmodifiableList(new ArrayList<>(byName.values())); }

    public void onKeyPressed(int keyCode) {
        if (keyCode < 0) return;
        for (Module m : byName.values()) if (m.keyCode() == keyCode) m.toggle();
    }

    public void tick() {
        for (Module m : byName.values()) {
            if (!m.enabled()) continue;
            try { m.onTick(); }
            catch (RuntimeException e) { m.setEnabled(false); } // a faulty module must not crash the game loop
        }
    }

    public void disableAll() { for (Module m : byName.values()) m.setEnabled(false); }
}
