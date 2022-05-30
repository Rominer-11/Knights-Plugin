package me.rominer_11.knights;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public final class Knights extends JavaPlugin implements Listener {

    public static Knights getPlugin() {
        return plugin;
    }

    private static Knights plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        System.out.println("[Knights] Starting!");

        getConfig().options().copyDefaults();
        saveDefaultConfig();


        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        //Thanks to Kody Simpson https://github.com/KodySimpson, and these two threads https://www.spigotmc.org/threads/how-to-modify-packet-data.438625/, https://devdreamz.com/question/703027-bukkit-change-players-name-above-head for this function.
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.COMMANDS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (getConfig().getBoolean("finalPhase") == false) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[Knights] Shutting down!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (getConfig().getBoolean("finalPhase") == false) {
            player.setDisplayName("Knight");
            player.setPlayerListName("Knight");

            if (!event.getPlayer().hasPlayedBefore()) {
                event.setJoinMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " joined the server for the first time!");
                player.sendMessage("Welcome to the knights server.");
            } else {
                event.setJoinMessage(ChatColor.YELLOW + "A player has entered the server...");
            }

        }

    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (getConfig().getBoolean("finalPhase") == false) {
            event.setQuitMessage(ChatColor.YELLOW + "A player has left the server...");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (getConfig().getBoolean("finalPhase") == false) {
            System.out.println(event.getDeathMessage());
            event.setDeathMessage(ChatColor.RED + "A player died.");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (getConfig().getBoolean("finalPhase") == false) {
            event.setCancelled(true);
        }
    }



}