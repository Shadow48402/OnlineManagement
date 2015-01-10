package nl.nielsha.plugins.onlinemanagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnlineCommand implements CommandExecutor{
	
	/**
	 * @author Niels Hamelink
	 * This class doesn't contain any APIs or resources
	 */
	
	public OnlineManagement plugin;
	public OnlineCommand(OnlineManagement plugin){
		this.plugin = plugin;
	}
	
	/**
	 * @author Niels Hamelink
	 * This class doesn't contain any APIs or resources
	 */

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		int i = 0;
		if(cmd.getName().equalsIgnoreCase("online")){
			if(args.length == 0){

				StringBuilder sb = new StringBuilder();
				for(Player o : Bukkit.getServer().getOnlinePlayers()) {
					if(sender instanceof Player){
						if(o.isOp()){
							sb.append(ChatColor.RED + o.getName() 
									+ ChatColor.WHITE + ", ");
						} else {
							sb.append(ChatColor.GRAY + o.getName() 
									+ ChatColor.WHITE + ", ");
						}
					} else {
						sb.append(o.getName() + ", ");
					}
					i++;
				}
				String list = sb.toString();
				Pattern p = Pattern.compile(", $");
				Matcher m = p.matcher(list);
				list = m.replaceAll("");

				if(sender instanceof Player){
					Player s = (Player) sender;
					s.sendMessage(this.plugin.prefix + ChatColor.GREEN + "Online Players (" 
							+ ChatColor.GRAY + i 
							+ ChatColor.GREEN + ")");
					s.sendMessage(list);
				} else {
					sender.sendMessage("Online Players (" + i + ")");
					sender.sendMessage(list);
				}

			} 
			if(args.length == 1){
				String s = args[0];
				if(s.equalsIgnoreCase("")){
					
				}
			}
			
			if(args.length >= 2){
				if(sender instanceof Player){
					Player p = (Player) sender;
					p.sendMessage(this.plugin.prefix + ChatColor.RED + "Invalid arguments!");
				} else {
					System.out.println("[OnlineManagement] Invalid arguments!");
				}
			}

		}

		return false;
	}

}
