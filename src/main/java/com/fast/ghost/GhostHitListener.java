package com.fast.ghost;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class GhostHitListener implements Listener {

    private final FastGhost plugin;
    private final GhostHitManager manager;
    private final Stats stats;

    public GhostHitListener(FastGhost plugin, GhostHitManager manager, Stats stats) {
        this.plugin = plugin;
        this.manager = manager;
        this.stats = stats;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onLowest(EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("enabled", true)) return;

        stats.incrementReceived();

        if (event.isCancelled()) {
            stats.incrementCancelled();
            manager.processGhostHit(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onHighest(EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("enabled", true)) return;

        if (event.isCancelled()) {
            manager.processGhostHit(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onMonitor(EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("enabled", true)) return;

        boolean onlyPlayers = plugin.getConfig().getBoolean("ghost-hit-fixer.only-player-hits", true);
        if (onlyPlayers) {
            if (!(event.getDamager() instanceof Player)) return;
            if (!(event.getEntity() instanceof Player)) return;
        }

        stats.incrementLogged();
        manager.logHit(event);
    }
}
