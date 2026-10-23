package com.gabdab.gaboptimization;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Config {
    public static boolean enabled = true;

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("gaboptimization.properties");
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (Reader r = Files.newBufferedReader(f)) {
            Properties p = new Properties();
            p.load(r);
            enabled = Boolean.parseBoolean(p.getProperty("enabled", "true"));
        } catch (IOException ignored) {
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(file())) {
            Properties p = new Properties();
            p.setProperty("enabled", Boolean.toString(enabled));
            p.store(w, "GabOptimization");
        } catch (IOException ignored) {
        }
    }

    private Config() {}
}
