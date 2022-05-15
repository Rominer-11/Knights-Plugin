package me.rominer_11.knights;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class Knights extends JavaPlugin implements Listener {

    public static Knights getPlugin() {
        return plugin;
    }

    private static Knights plugin;

    Date now = new Date();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        System.out.println("Knights has started!");

        getServer().getPluginManager().registerEvents(this, this);

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        //Thanks to Kody Simpson https://github.com/KodySimpson, and these two threads https://www.spigotmc.org/threads/how-to-modify-packet-data.438625/, https://devdreamz.com/question/703027-bukkit-change-players-name-above-head for this function.
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Knights is shutting down!");
    }

    private void setskin(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        GameProfile gp = ep.getBukkitEntity().getProfile();
        PropertyMap pm = gp.getProperties();
        //removes current skin
        Property property = pm.get("textures").iterator().next();
        pm.put("textures", property);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setDisplayName("Knight");
        setskin(event.getPlayer());

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



}