package me.rominer_11.knights;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.BanList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import static org.bukkit.Bukkit.getPlayer;

public final class Knights extends JavaPlugin implements Listener, CommandExecutor {

    private static Knights plugin;
    private boolean enabled = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        System.out.println("[Knights] Starting!");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("track").setExecutor(new Knights());

        if (getConfig().getBoolean("finalPhase")) {
            System.out.println("Enabled track command.");
        }


        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        //Thanks to Kody Simpson https://github.com/KodySimpson, and these two threads https://www.spigotmc.org/threads/how-to-modify-packet-data.438625/, https://devdreamz.com/question/703027-bukkit-change-players-name-above-head for this function.
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.COMMANDS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!getConfig().getBoolean("finalPhase")) {
                    event.setCancelled(true);
                }
            }
        });
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
        System.out.println("[Knights] Initialized onPlayerJoin");
        if (!getConfig().getBoolean("finalPhase")) {
            player.setDisplayName("Knight");
            player.setPlayerListName("Knight");

            if (!event.getPlayer().hasPlayedBefore()) {
                event.setJoinMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " joined the server for the first time!");
                player.sendMessage("Welcome to the knights server.");
            } else {
                event.setJoinMessage(ChatColor.YELLOW + "A player has entered the server...");
            }

            System.out.println("[Knights] On player join stuff set!");

        }

    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (!getConfig().getBoolean("finalPhase")) {
            event.setQuitMessage(ChatColor.YELLOW + "A player has left the server...");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!getConfig().getBoolean("finalPhase")) {
            System.out.println(event.getDeathMessage());
            event.setDeathMessage(ChatColor.RED + "A player died.");
        } else {
            getServer().getBanList(BanList.Type.NAME).addBan(player.getName(), "You died lmao", null, null);
            player.kickPlayer("You died lmao");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!getConfig().getBoolean("finalPhase")) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                if (getServer().getPlayer(args[0]) != null) {
                    Player target = getPlayer(args[0]);

                    if (enabled) {
                        enabled = false;
                        player.sendMessage(ChatColor.AQUA + "Tracking stopped.");
                        BukkitScheduler scheduler = getServer().getScheduler();
                        scheduler.scheduleSyncRepeatingTask(this, () -> trackMsg(player, target), 100, 100);
                        scheduler.cancelTasks(this);
                    } else {
                        enabled = true;
                        player.sendMessage(ChatColor.AQUA + "Now tracking " + target.getName() + ".");
                        BukkitScheduler scheduler = getServer().getScheduler();
                        scheduler.scheduleSyncRepeatingTask(this, () -> trackMsg(player, target), 100, 100);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + args[0] + " is either not a player or not online!");
                }
            }
        }
        return false;
    }

    static boolean trackMsg(Player player, Player target) {
        player.sendMessage(ChatColor.GREEN + target.getName() + " is at " + Math.round(target.getLocation().getX()) + ", " + Math.round(target.getLocation().getY()) + ", " + Math.round(target.getLocation().getZ()) + ", In " + target.getLocation().getWorld().getName());
        System.out.println(player.getName() + " is tracking " + target.getName());
        return true;
    }


}