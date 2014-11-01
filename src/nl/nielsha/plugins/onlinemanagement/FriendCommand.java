package nl.nielsha.plugins.onlinemanagement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class FriendCommand implements CommandExecutor, Listener{
	public OnlineManagement plugin;
	public FriendCommand(OnlineManagement plugin){
		this.plugin = plugin;
	}
	public HashMap<String, List<String>> adds = new HashMap<String, List<String>>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(args.length == 0){

		}

		if(args.length == 1){

		}

		if(args.length == 2){
			if(args[0].equalsIgnoreCase("accept")){
				if(!(sender instanceof Player)){

					return true;
				}
				Player p = (Player) sender;
				if(adds.containsKey(args[1])){
					if(adds.get(args[1]).contains(p.getName())){
						File f = new File(plugin.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
						File f2 = new File(plugin.getDataFolder().getAbsolutePath(), args[1] + ".yml");
						if(!f.exists()){
							FileConfiguration c = YamlConfiguration.loadConfiguration(f);
							c.set("Friends", Arrays.asList(args[1]));
							try {
								c.save(f);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							FileConfiguration c = YamlConfiguration.loadConfiguration(f);
							List<String> l = c.getStringList("Friends");
							l.add(args[1]);
							try {
								c.save(f);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						if(!f2.exists()){
							FileConfiguration c = YamlConfiguration.loadConfiguration(f2);
							c.set("Friends", Arrays.asList(p.getName()));
							try {
								c.save(f2);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							FileConfiguration c = YamlConfiguration.loadConfiguration(f2);
							List<String> l = c.getStringList("Friends");
							l.add(p.getName());
							try {
								c.save(f2);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					p.sendMessage(plugin.prefix + ChatColor.RED + "You haven't got any invites!");
				}
			}
		}
		return false;
	}

	public boolean areFriends(Player p1, Player p2){
		File f = new File(plugin.getDataFolder().getAbsolutePath(), p1.getName() + ".yml");
		if(!f.exists()){
			return false;
		} else {
			FileConfiguration c = YamlConfiguration.loadConfiguration(f);
			List<String> l = c.getStringList("Friends");
			if(l.contains(p2.getName())){
				return true;
			} else {
				return false;
			}
		}
	}

	public void addFriend(Player p, Player a){
		if(!areFriends(p, a)){
			if(a.isOnline()){
				a.sendMessage(plugin.prefix + ChatColor.GRAY + "You've got a friend invite from " + p.getName() + " to accept is enter '/friend accept <Player>'");
				adds.put(p.getName(), Arrays.asList(a.getName()));
			} else {
				adds.put(p.getName(), Arrays.asList(a.getName()));
			}
		} else {
			p.sendMessage(plugin.prefix + ChatColor.RED + "You are already friends with " + a.getName() + "!");
		}
	}
}
