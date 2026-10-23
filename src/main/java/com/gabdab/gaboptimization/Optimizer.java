package com.gabdab.gaboptimization;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.ParticlesMode;

/**
 * Авто-оптимізація: якщо FPS низький - поступово знижує налаштування графіки,
 * якщо FPS високий - повертає назад. При вимкненні (OFF) відновлює оригінальні налаштування.
 */
public final class Optimizer {
    public static final int MAX_LEVEL = 4;
    public static int level = 0;

    private static boolean saved = false;
    private static int origView, origSim;
    private static double origEntityScale;
    private static boolean origShadows;
    private static ParticlesMode origParticles;
    private static GraphicsMode origGraphics;
    private static CloudRenderMode origClouds;

    private static int tickCounter = 0;
    private static int lowStreak = 0;
    private static int highStreak = 0;

    public static void tick(MinecraftClient c) {
        if (!Config.enabled) {
            if (saved) restore(c);
            return;
        }
        if (c.player == null || c.world == null) return;

        // перевірка раз на 2 секунди
        if (++tickCounter < 40) return;
        tickCounter = 0;

        GameOptions o = c.options;
        if (!saved) save(o);

        int fps = c.getCurrentFps();
        if (fps < 45) {
            lowStreak++;
            highStreak = 0;
        } else if (fps > 110) {
            highStreak++;
            lowStreak = 0;
        } else {
            lowStreak = 0;
            highStreak = 0;
        }

        if (lowStreak >= 2 && level < MAX_LEVEL) {
            lowStreak = 0;
            level++;
            apply(o, level);
        } else if (highStreak >= 6 && level > 0) {
            highStreak = 0;
            level--;
            apply(o, level);
        }
    }

    private static void save(GameOptions o) {
        origView = o.getViewDistance().getValue();
        origSim = o.getSimulationDistance().getValue();
        origEntityScale = o.getEntityDistanceScaling().getValue();
        origShadows = o.getEntityShadows().getValue();
        origParticles = o.getParticles().getValue();
        origGraphics = o.getGraphicsMode().getValue();
        origClouds = o.getCloudRenderMode().getValue();
        saved = true;
        level = 0;
    }

    private static void restore(MinecraftClient c) {
        apply(c.options, 0);
        c.options.write();
        saved = false;
        level = 0;
        lowStreak = 0;
        highStreak = 0;
        tickCounter = 0;
    }

    private static void apply(GameOptions o, int lvl) {
        // рівень 0 = оригінальні налаштування гравця
        boolean shadows = origShadows;
        CloudRenderMode clouds = origClouds;
        GraphicsMode graphics = origGraphics;
        ParticlesMode particles = origParticles;
        double entityScale = origEntityScale;
        int view = origView;
        int sim = origSim;

        if (lvl >= 1) {
            shadows = false;
            clouds = CloudRenderMode.OFF;
            graphics = GraphicsMode.FAST;
        }
        if (lvl >= 2) {
            if (particles == ParticlesMode.ALL) particles = ParticlesMode.DECREASED;
            entityScale = Math.min(origEntityScale, 0.75);
        }
        if (lvl >= 3) {
            view = Math.max(8, origView - 4);
            sim = Math.min(origSim, 8);
        }
        if (lvl >= 4) {
            particles = ParticlesMode.MINIMAL;
            entityScale = Math.min(origEntityScale, 0.5);
            view = Math.max(6, origView - 6);
            sim = Math.min(origSim, 6);
        }
        // не піднімаємо вище того, що було у гравця
        view = Math.min(view, origView);

        o.getEntityShadows().setValue(shadows);
        o.getCloudRenderMode().setValue(clouds);
        o.getGraphicsMode().setValue(graphics);
        o.getParticles().setValue(particles);
        o.getEntityDistanceScaling().setValue(entityScale);
        o.getViewDistance().setValue(view);
        o.getSimulationDistance().setValue(sim);
    }

    private Optimizer() {}
}
