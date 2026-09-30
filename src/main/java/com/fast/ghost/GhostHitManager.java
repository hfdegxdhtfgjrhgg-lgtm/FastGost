package com.fast.ghost;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class GhostHitManager {

    private final FastGhost plugin;
    private final Stats stats;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GhostHitManager(FastGhost plugin, Stats stats) {
        this.plugin = plugin;
        this.stats = stats;
    }

    public boolean processGhostHit(EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("ghost-hit-fixer.enabled", true)) {
            return false;
        }

        boolean onlyPlayers = plugin.getConfig().getBoolean("ghost-hit-fixer.only-player-hits", true);
        if (onlyPlayers) {
            if (!(event.getDamager() instanceof Player)) return false;
            if (!(event.getEntity() instanceof Player)) return false;
        }

        double damage = event.getDamage();
        double minDamage = plugin.getConfig().getDouble("ghost-hit-fixer.min-damage", 0.01);
        double maxDamage = plugin.getConfig().getDouble("ghost-hit-fixer.max-damage", 1000.0);
        if (damage < minDamage || damage > maxDamage) {
            return false;
        }

        if (!event.isCancelled()) {
            return false;
        }

        event.setCancelled(false);
        stats.incrementFixed();

        logGhostHit(event, true, true);
        return true;
    }

    public void logGhostHit(EntityDamageByEntityEvent event, boolean cancelledBefore, boolean fixed) {
        boolean cancelledAfter = event.isCancelled();

        String attacker = event.getDamager() instanceof Player p
                ? p.getName() : event.getDamager().getType().name();
        String target = event.getEntity() instanceof Player p
                ? p.getName() : event.getEntity().getType().name();

        double damage = event.getFinalDamage();
        String time = dateFormat.format(new Date());

        if (plugin.getConfig().getBoolean("logging.console", false)) {
            plugin.getLogger().info("[FastGhost] "
                    + attacker + " -> " + target
                    + " | Cancelled: " + cancelledBefore
                    + " | Fixed: " + fixed
                    + " | Damage: " + String.format("%.2f", damage));
        }

        if (plugin.getConfig().getBoolean("logging.ghost-file", true)) {
            writeGhostLog(time, attacker, target, damage, cancelledBefore, cancelledAfter, fixed);
        }
    }

    private synchronized void writeGhostLog(String time, String attacker, String target,
                                             double damage, boolean cancelledBefore,
                                             boolean cancelledAfter, boolean fixed) {
        File ghostFile = new File(plugin.getDataFolder(), "ghost-hits.log");
        try (PrintWriter out = new PrintWriter(new FileWriter(ghostFile, true))) {
            out.println("[" + time + "]"
                    + " attacker=" + attacker
                    + " target=" + target
                    + " damage=" + String.format("%.2f", damage)
                    + " cancelledBefore=" + cancelledBefore
                    + " cancelledAfter=" + cancelledAfter
                    + " fixed=" + fixed);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write ghost-hits.log: " + e.getMessage());
        }
    }

    public synchronized void logHit(EntityDamageByEntityEvent event) {
        if (!plugin.getConfig().getBoolean("logging.file", true)) return;

        String attacker = event.getDamager() instanceof Player p
                ? p.getName() : event.getDamager().getType().name();
        String target = event.getEntity() instanceof Player p
                ? p.getName() : event.getEntity().getType().name();

        double damage = event.getFinalDamage();
        String world = event.getEntity().getWorld().getName();
        String time = dateFormat.format(new Date());

        File hitsFile = new File(plugin.getDataFolder(), "hits.log");
        try (PrintWriter out = new PrintWriter(new FileWriter(hitsFile, true))) {
            out.println("[" + time + "]"
                    + " attacker=" + attacker
                    + " target=" + target
                    + " damage=" + String.format("%.2f", damage)
                    + " world=" + world
                    + " cancelled=" + event.isCancelled());
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to write hits.log: " + e.getMessage());
        }
    }
}
