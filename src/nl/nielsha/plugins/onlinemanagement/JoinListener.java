package nl.nielsha.plugins.onlinemanagement;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener{
	public OnlineManagement plugin;
	public JoinListener(OnlineManagement plugin){
		this.plugin = plugin;
	}
	private String m;
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		File f = new File(plugin.getDataFolder().getAbsolutePath(), "messages.yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
		m = ChatColor.translateAlternateColorCodes('&', c.getString("JoinMessage"))
				.replaceAll("%name%", e.getPlayer().getName())
				.replaceAll("%displayname%", e.getPlayer().getDisplayName())
				.replaceAll("%level%", String.valueOf(e.getPlayer().getLevel()))
				.replaceAll("%world%", e.getPlayer().getWorld().getName());
		
		e.setJoinMessage(m);
	}

}
