package me.rominer_11.knights;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
        System.out.println("Knights has started!");

        getServer().getPluginManager().registerEvents(this, this);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Knights is shutting down!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setDisplayName("Knight");
        player.setPlayerListName("Knight");

        if (!event.getPlayer().hasPlayedBefore()) {
            event.setJoinMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " joined the server for the first time!");
            player.sendMessage("Welcome to the knights server.");
        } else {
            event.setJoinMessage(ChatColor.YELLOW + "A player has entered the server...");
        }

    }


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.YELLOW + "A player has left the server...");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(ChatColor.RED + "A player died.");
    }



}