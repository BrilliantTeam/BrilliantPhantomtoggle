package Rice.Chen.BrilliantPhantomtoggle;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BrilliantPhantomtoggle extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private boolean isFolia;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        
        isFolia = isFolia();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        PhantomCommand phantomCommand = new PhantomCommand(this);
        getCommand("phantom").setExecutor(phantomCommand);
        getCommand("phantom").setTabCompleter(phantomCommand);
        
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PhantomPlaceholder(this).register();
        }

        if (isFolia) {
            getServer().getGlobalRegionScheduler().runAtFixedRate(this, task -> {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (!getPhantomSpawn(player)) {
                        player.getScheduler().run(this, t -> 
                            resetPlayerRestTime(player), null);
                    }
                }
            }, 20L, 20L * 30);
        } else {
            getServer().getScheduler().runTaskTimer(this, () -> {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (!getPhantomSpawn(player)) {
                        resetPlayerRestTime(player);
                    }
                }
            }, 20L, 20L * 30);
        }
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public boolean getPhantomSpawn(Player player) {
        return config.getBoolean("players." + player.getUniqueId(), true);
    }

    public void setPhantomSpawn(Player player, boolean newState, boolean oldState) {
        config.set("players." + player.getUniqueId(), newState);
        saveConfig();
        
        if (!newState && oldState) {
            resetPlayerRestTime(player);
        }
    }

    public void forcePhantomSpawn(Player player) {
        if (isFolia) {
            player.getScheduler().run(this, task -> 
                player.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 72000), null);
        } else {
            player.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 72000);
        }
    }

    private void resetPlayerRestTime(Player player) {
        if (isFolia) {
            player.getScheduler().run(this, task -> {
                player.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 0);
            }, null);
        } else {
            player.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 0);
        }
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        if (!getPhantomSpawn(player)) {
            resetPlayerRestTime(player);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.PHANTOM) {
            event.getLocation().getNearbyPlayers(64).forEach(player -> {
                if (!getPhantomSpawn(player)) {
                    event.setCancelled(true);
                }
            });
        }
    }

    @EventHandler
    public void onStatisticIncrement(org.bukkit.event.player.PlayerStatisticIncrementEvent event) {
        if (event.getStatistic() == org.bukkit.Statistic.TIME_SINCE_REST) {
            Player player = event.getPlayer();
            if (!getPhantomSpawn(player)) {
                event.setCancelled(true);
                resetPlayerRestTime(player);
            }
        }
    }
}