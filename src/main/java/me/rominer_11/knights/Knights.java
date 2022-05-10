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
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Knights extends JavaPlugin implements Listener {

    public static Knights getPlugin() { return plugin; }

    private static Knights plugin;
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

                List<PlayerInfoData> data = event.getPacket().getPlayerInfoDataLists().read(0);
                PlayerInfoData playerInfoData = data.get(0);
                playerInfoData = new PlayerInfoData(data.get(0).getProfile().withName("Knight"), playerInfoData.getLatency(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());

                data.set(0, playerInfoData);
                event.getPacket().getPlayerInfoDataLists().write(0, data);

                //System.out.println(event.getPacket().getPlayerInfoDataLists().read(0).get(0).getProfile().getName() + " logged in.");

            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Knights is shutting down!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        setSkin(event.getPlayer());
        player.setDisplayName("Knight");
        //player.setPlayerListName("Knight");
        event.getPlayer().setCustomName("Knight");

        if (!event.getPlayer().hasPlayedBefore()) {
            event.setJoinMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " joined the server for the first time!");
            player.sendMessage("Welcome to knights. \nYou have been assigned a team at random and given starting equipment. \nYour goal is to be the last team alive. \nIn 4 weeks, hardcore mode will be turned on and everybody's coordinates will show. \nDon't die. \nGood luck.");
        } else {
            event.setJoinMessage(ChatColor.YELLOW + "A player has entered the server...");
        }

    }

    //Thanks to Seb for this code
    private void setSkin(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        GameProfile gp = ep.getBukkitEntity().getProfile();
        PropertyMap pm = gp.getProperties();

        //Removes current skin
        Property property = pm.get("textures").iterator().next();
        pm.remove("textures", property);

        //Find at https://minesk.in/0742eedf2b0f49539aca33044244e279
        pm.put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1MTk2OTc4MTg3OSwKICAicHJvZmlsZUlkIiA6ICI2NmI0ZDRlMTFlNmE0YjhjYTFkN2Q5YzliZTBhNjQ5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmFzdG9vWXNmIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQxOWZmMjJiZTM1MWQ0YWFlODJiYmNkZjY3ZjY5MzM2NzJjYWU4MDdlNjNiY2QyZTdhZTFlMWRkMGQwZGE2M2IiCiAgICB9CiAgfQp9ewogICJ0aW1lc3RhbXAiIDogMTY1MTk2OTc4MTg3OSwKICAicHJvZmlsZUlkIiA6ICI2NmI0ZDRlMTFlNmE0YjhjYTFkN2Q5YzliZTBhNjQ5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmFzdG9vWXNmIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQxOWZmMjJiZTM1MWQ0YWFlODJiYmNkZjY3ZjY5MzM2NzJjYWU4MDdlNjNiY2QyZTdhZTFlMWRkMGQwZGE2M2IiCiAgICB9CiAgfQp9", "UNjrzsD00ANyWSV1gXRlnQ9NTwA5AFFWxHMrHLH5g81uuPxzd8pqJ0qj5LWDFiw5W5cTa/wQUFSXPgxf2vfrEYdSaohqZtZd8jHVNZE5ZI21V/2TdqXkAYaLIcW2hlo5nnAqmkIph9LYhYHzmyL1lFMih9Vx5Wgpjv86b9CfUOSth/GiLXvpcr6kT7Sxw3TH9PXceilGgOVhfFVg/PR29kgOcVQgle9k4iDwRaMeIWqFrB15FR4vYhZwgPv39LqFPN4Axek+EZw8iHeI0cPSoQSGQhRGkid40/NvF2iaVBojEtsOFsu9Xhh17WL1Wb1B16zGUw1CQ+x4q9ZhzZ4ggQdNyZ9/ElqHN8JxRzIw1Rq5UB6AK1a2uoeSI7iIhSiVVZ3k18Ji38MKeiotoc0J2+hMJ6nZmRg+rv9/j6+3GtjKrkLFS5NGLr2e/uXRjBb4Vm0SwoWsai9d66gcQ4bnqe6IpE+eX88HeCBYGxnq3otSWy6F8c6fnfSpypn+rN8WJ7KEjxFBKFzFHvHqf4NU+61jFBgCyyB82AD59rYV0A/BHMXuqiyvfjbKylt3M3QzmTwUUJY+Hx8pgw4m8NDmq1tYdyHHU8q4D23jnKTCB9Xtt+SIejdW/gBnKUWD+6BXax+X1OvLH8Fy5k3cI6hmAx8usXXDHYtZWlD3mRut7rY="));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.YELLOW + "A player has left the server...");
    }


}