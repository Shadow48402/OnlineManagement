package nl.nielsha.plugins.onlinemanagement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class FriendCommand implements CommandExecutor, Listener{

	/**
	 * @author Niels Hamelink
	 * This class doesn't contain any APIs or resources
	 */

	public OnlineManagement plugin;
	public FriendCommand(OnlineManagement plugin){
		this.plugin = plugin;
	}
	public HashMap<String, List<String>> adds = new HashMap<String, List<String>>();

	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		int i = 0;
		if(args.length == 0){
			if(!(sender instanceof Player)){
				System.out.println("You can't run this command!");
				return true;
			}
			Player p = (Player) sender;
			p.sendMessage(ChatColor.AQUA + "'/friend' information:");
			p.sendMessage(ChatColor.GOLD + "Subcommands: list, add, remove");
		}

		if(args.length == 1){
			if(args[0].equalsIgnoreCase("list")){
				if(!(sender instanceof Player)){
					System.out.println("You can't run this command!");
					return true;
				}
				Player p = (Player) sender;
				if(!p.hasPermission("friend.list")){
					p.sendMessage(ChatColor.RED + "You are not allowed to do this!");
					return true;
				}
				StringBuilder sb = new StringBuilder();
				File f = new File(plugin.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
				if(!f.exists()){
					p.sendMessage(ChatColor.RED + "You have no friends!");
					return true;
				}
				FileConfiguration c = YamlConfiguration.loadConfiguration(f);
				List<String> l = c.getStringList("Friends");
				if(l.size() == 0){
					p.sendMessage(ChatColor.RED + "You have no friends!");
					return true;
				}
				for(String s : l){
					Player t = Bukkit.getPlayer(s);
					if(t.isOnline()){
						sb.append(ChatColor.GREEN + s + ChatColor.WHITE + ", ");
					} else {
						sb.append(ChatColor.GRAY + s + ChatColor.WHITE + ", ");
					}
					i++;
				}
				String list = sb.toString();
				Pattern pa = Pattern.compile(", $");
				Matcher m = pa.matcher(list);
				p.sendMessage(ChatColor.GOLD + "Your friends are (" + i + "):");
				p.sendMessage(list);
				i = 0;
			}
		}

		if(args.length == 2){
			if(args[0].equalsIgnoreCase("add")){
				if(!(sender instanceof Player)){
					System.out.println("You can't run this command!");
					return true;
				}
				Player p = (Player) sender;
				if(!p.hasPermission("friend.add")){
					p.sendMessage(ChatColor.RED + "You are not allowed to do this!");
					return true;
				}
				Player t = Bukkit.getPlayer(args[1]);
				this.addFriend(p, t);
			}
			if(args[0].equalsIgnoreCase("remove")){
				if(!(sender instanceof Player)){
					System.out.println("You can'trun this command!");
					return true;
				}
				Player p = (Player) sender;
				if(Bukkit.getPlayer(args[1]) == null){
					p.sendMessage(ChatColor.RED + "That player is not valid!");
					return true;
				}
				Player t = Bukkit.getPlayer(args[1]);
				if(!areFriends(p, t)){
					p.sendMessage(ChatColor.RED + "That player is not a friend!");
					return true;
				}
				File f = new File(plugin.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
				File f2 = new File(plugin.getDataFolder().getAbsolutePath(), t.getName() + ".yml");
				FileConfiguration c = YamlConfiguration.loadConfiguration(f);
				FileConfiguration c2 = YamlConfiguration.loadConfiguration(f2);
				List<String> l = c.getStringList("Friends");
				l.remove(t.getName());
				List<String> l2 = c.getStringList("Friends");
				l2.remove(p.getName());
				try {
					c.save(f);
					c2.save(f2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(args[0].equalsIgnoreCase("accept")){
				if(!(sender instanceof Player)){
					System.out.println("You can't run this command!");
					return true;
				}
				Player p = (Player) sender;
				if(!p.hasPermission("friend.accept")){
					p.sendMessage(ChatColor.RED + "You are not allowed to do this!");
					return true;
				}
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
			if(a != null){
				a.sendMessage(plugin.prefix + ChatColor.GRAY + "You've got a friend invite from " + p.getName() + " to accept is enter '/friend accept <Player>'");
				if(adds.containsKey(p.getName())){
					List<String> l = adds.get(p.getName());
					l.add(a.getName());
				} else {
					adds.put(p.getName(), Arrays.asList(a.getName()));
				}
			} else {
				p.sendMessage(ChatColor.RED + "That player is not valid!");
				return;
			}
		} else {
			p.sendMessage(plugin.prefix + ChatColor.RED + "You are already friends with " + a.getName() + "!");
			return;
		}
		File f = new File(plugin.getDataFolder().getAbsolutePath(), p.getName() + ".yml");
		File f2 = new File(plugin.getDataFolder().getAbsolutePath(), a.getName() + ".yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
		FileConfiguration c2 = YamlConfiguration.loadConfiguration(f2);
		try {
			c.save(f);
			c2.save(f2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
