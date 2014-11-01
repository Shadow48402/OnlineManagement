package nl.nielsha.plugins.onlinemanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class StaffCommand{
	
	/**
	 * @author Niels Hamelink
	 * This class doesn't contain any APIs or resources
	 */
	
	public OnlineManagement plugin;
	public StaffCommand(OnlineManagement plugin){
		this.plugin = plugin;
	}

	@SuppressWarnings({ "deprecation" })
	public boolean commandHandler(CommandSender sender, String[] args){
		File f = new File(plugin.getDataFolder().getAbsolutePath(), "staff.yml");
		FileConfiguration c = YamlConfiguration.loadConfiguration(f);
		List<String> l = c.getStringList("Staff");

		if(args.length == 0){                                       // Check or arguments length = 0 (none)
			if(sender instanceof Player){                           // Is the command sender a player?
				((Player) sender).sendMessage(                      // Send message
						ChatColor.GREEN + "Type '/staff help' for all commands"
						);
			} else {                                                // What if sender isn't a player?
				sender.sendMessage(                                 // Send message
						"Type '/staff help' for all commands"       // Removed chatcolors on this string
						);
			}
		}

		if(args.length == 1){
			if(args[0].equalsIgnoreCase("help")){
				if(sender instanceof Player){
					((Player) sender).sendMessage(ChatColor.GRAY + "(============= " + this.plugin.prefix + " =============)");
					((Player) sender).sendMessage(ChatColor.BLUE + "/staff list - Watch the staff list");
					((Player) sender).sendMessage(ChatColor.BLUE + "/staff online - Watch the online staff");
				} else {
					sender.sendMessage("(============= [OnlineManagement] =============");
					sender.sendMessage("/staff list - Watch the staff list");
					sender.sendMessage("/staff online - Watch the online staff");
				}
			}
			
			if(args[0].equalsIgnoreCase("list")){                    // Check or first argument is 'list'
				StringBuilder b = new StringBuilder();               // Stringbuilder
				for(String s : l){                                // Run all names
					String r = c.getString(s);                       // Getting the rank
					String nc = c.getString("Color." + r + ".Name"); // Color of the name
					String rc = c.getString("Color." + r + ".Rank"); // Color of the rank

					if(sender instanceof Player){
						b.append(ChatColor.WHITE + "[" 
								+ ChatColor.translateAlternateColorCodes('&', rc) + r
								+ ChatColor.WHITE + "] "
								+ ChatColor.translateAlternateColorCodes('&', nc) + s);
					} else {
						b.append("[" + r + "] " + s);
					}
				}

				String s = b.toString();
				if(sender instanceof Player){
					((Player) sender).sendMessage(this.plugin.prefix + ChatColor.GOLD + "Staff List:");
					((Player) sender).sendMessage(s);
				} else {
					sender.sendMessage("[OnlineManagement] Staff List:");
					sender.sendMessage(s);
				}
			}

			if(args[0].equalsIgnoreCase("online")){
				ArrayList<String> online = new ArrayList<String>();
				StringBuilder b = new StringBuilder();
				for(String s : l){
					for(Player o : Bukkit.getOnlinePlayers()){
						if(s.equalsIgnoreCase(o.getName())){
							online.add(o.getName());
						}
					}
				}

				for(String op : online){
					String r = c.getString(op);                      // Getting the rank
					String nc = c.getString("Color." + r + ".Name"); // Color of the name
					String rc = c.getString("Color." + r + ".Rank"); // Color of the rank
					if(sender instanceof Player){
						b.append(ChatColor.WHITE + "[" 
								+ ChatColor.translateAlternateColorCodes('&', rc) + r
								+ ChatColor.WHITE + "] " 
								+ ChatColor.translateAlternateColorCodes('&', nc) + op 
								+ ChatColor.WHITE + ", ");
					} else {
						b.append("[" + r + "] " + op + ", ");
					}
				}
				String list = b.toString();
				Pattern p = Pattern.compile(", $");
				Matcher m = p.matcher(list);
				list = m.replaceAll("");

				if(sender instanceof Player){
					((Player) sender).sendMessage(this.plugin.prefix + ChatColor.GOLD + "Online Staff:");
					((Player) sender).sendMessage(list);
				} else {
					sender.sendMessage("[OnlineManagement] Online Staff:");
					sender.sendMessage(list);
				}
			}
		}
		
		if(args.length == 2){
			
		}

		return false;
	}

}
