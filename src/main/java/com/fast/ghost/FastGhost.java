package com.fast.ghost;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class FastGhost extends JavaPlugin {

    private Stats stats;
    private GhostHitManager ghostHitManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        createFile("hits.log");
        createFile("ghost-hits.log");

        this.stats = new Stats();
        this.ghostHitManager = new GhostHitManager(this, stats);

        getServer().getPluginManager().registerEvents(
                new GhostHitListener(this, ghostHitManager, stats), this);

        if (getCommand("fastghost") != null) {
            FastGhostCommand cmd = new FastGhostCommand(this, stats);
            getCommand("fastghost").setExecutor(cmd);
            getCommand("fastghost").setTabCompleter(cmd);
        }

        getLogger().info("FastGhost v1.0.0 enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("FastGhost disabled. "
                + "Received=" + stats.getHitsReceived()
                + " Cancelled=" + stats.getHitsCancelled()
                + " Fixed=" + stats.getGhostHitsFixed()
                + " Logged=" + stats.getHitsLogged());
    }

    private void createFile(String name) {
        File f = new File(getDataFolder(), name);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create " + name + ": " + e.getMessage());
            }
        }
    }

    public boolean reloadPluginConfig() {
        try {
            reloadConfig();
            return true;
        } catch (Exception ex) {
            getLogger().severe("Reload failed: " + ex.getMessage());
            return false;
        }
    }

    public GhostHitManager getGhostHitManager() {
        return ghostHitManager;
    }

    public Stats getStats() {
        return stats;
    }
}
