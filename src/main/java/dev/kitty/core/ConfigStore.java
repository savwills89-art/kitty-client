package dev.kitty.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/** Flat key=value config: "module.enabled", "module.key", "module.setting.<name>". */
public final class ConfigStore {
    private final Path file;

    public ConfigStore(Path file) { this.file = file; }

    public void save(ModuleManager manager) throws IOException {
        Properties p = new Properties();
        for (Module m : manager.all()) {
            p.setProperty(m.name() + ".enabled", Boolean.toString(m.enabled()));
            p.setProperty(m.name() + ".key", Integer.toString(m.keyCode()));
            for (Setting<?> s : m.settings()) p.setProperty(m.name() + ".setting." + s.name(), s.serialize());
        }
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        try (OutputStream out = Files.newOutputStream(tmp)) { p.store(out, "Kitty Client"); }
        Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
    }

    public void load(ModuleManager manager) throws IOException {
        if (!Files.isRegularFile(file)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(file)) { p.load(in); }
        for (Module m : manager.all()) {
            for (Setting<?> s : m.settings()) {
                String v = p.getProperty(m.name() + ".setting." + s.name());
                if (v != null) s.deserialize(v);
            }
            String key = p.getProperty(m.name() + ".key");
            if (key != null) { try { m.bind(Integer.parseInt(key.trim())); } catch (NumberFormatException ignored) {} }
            String en = p.getProperty(m.name() + ".enabled");
            if (en != null) m.setEnabled(Boolean.parseBoolean(en.trim()));
        }
    }
}
