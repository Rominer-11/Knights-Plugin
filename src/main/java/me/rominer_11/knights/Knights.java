package me.rominer_11.knights;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;

import static org.bukkit.Bukkit.getPlayer;

public final class Knights extends JavaPlugin implements Listener, CommandExecutor {

    private static Knights plugin;
    public static HashMap<Player, Player> tracking = new HashMap<Player, Player>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        boolean o;

        System.out.println("[Knights] Starting!");

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("phase1").setExecutor(new Knights());
        getCommand("phase2").setExecutor(new Knights());

        if (getConfig().getBoolean("finalPhase")) {
            getCommand("track").setExecutor(new Knights());
            System.out.println("Enabled track command.");
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            System.out.println("wassup");
        }, 100, 100);

        System.out.print("[Knights] Enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[Knights] Shutting down!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!getConfig().getBoolean("finalPhase")) {
            if (!event.getPlayer().hasPlayedBefore()) {
                event.setJoinMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " joined the server for the first time!");
                player.sendMessage("Welcome to the knights server.");
            }
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (getConfig().getBoolean("finalPhase")) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("track")) {
                if (args.length == 1) {
                    System.out.println("Args 1");
                    if (getServer().getPlayer(args[0]) != null) {
                        if (tracking.containsKey(player)) {
                            System.out.println("Remove");
                            tracking.remove(player);
                        } else {
                            System.out.println("Put");
                            tracking.put(player, getPlayer(args[0]));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + args[0] + " is either not a player or not online!");
                    }
                }
            }

            if (command.getName().equalsIgnoreCase("phase1")) {
                getConfig().set("finalPhase", false);
                player.sendMessage("Phase 1 initiated");
                saveConfig();
            }

            if (command.getName().equalsIgnoreCase("phase2")) {
                getConfig().set("finalPhase", true);
                player.sendMessage("Phase 2 initiated");
                saveConfig();
            }

        }
        return false;
    }

    static boolean track() {
        System.out.println("exxec");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            System.out.println("for");
            if (tracking.containsKey(player)) {
                System.out.println("contains");
                Player target = tracking.get(player);
                if (target.isOnline()) {
                    player.sendMessage(ChatColor.GREEN + target.getName() + " is at " + Math.round(target.getLocation().getX()) + ", " + Math.round(target.getLocation().getY()) + ", " + Math.round(target.getLocation().getZ()) + ", In " + target.getLocation().getWorld().getName());
                    System.out.println(player.getName() + " is tracking " + target.getName());
                } else {
                    player.sendMessage(ChatColor.RED + "Target is not online!");
                    tracking.remove(player);
                }
            }
        }

        return true;
    }


}