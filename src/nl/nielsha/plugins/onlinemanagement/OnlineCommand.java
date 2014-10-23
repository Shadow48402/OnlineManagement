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
	public OnlineManagement plugin;
	public OnlineCommand(OnlineManagement plugin){
		this.plugin = plugin;
	}

	private String prefix = ChatColor.GRAY + "[" + ChatColor.AQUA + "OnlineManagement" + ChatColor.GRAY + "] ";

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		int i = 0;
		if(cmd.getName().equalsIgnoreCase("online")){
			if(args.length == 0){
				if(sender instanceof Player){
					StringBuilder sb = new StringBuilder();
					for(Player o : Bukkit.getServer().getOnlinePlayers()) {
						if(o.isOp()){
							sb.append(ChatColor.RED + o.getName() 
									+ ChatColor.WHITE + ", ");
						} else {
							sb.append(ChatColor.GRAY + o.getName() 
									+ ChatColor.WHITE + ", ");
						}
						i++;
					}
					String list = sb.toString();
					Pattern p = Pattern.compile(", $");
					Matcher m = p.matcher(list);
					list = m.replaceAll("");
					Player s = (Player) sender;
					s.sendMessage(this.prefix + ChatColor.GREEN + "Online Players (" 
							+ ChatColor.GRAY + i 
							+ ChatColor.GREEN + ")");
					s.sendMessage(list);
				} else {

				}
			} else {
				if(sender instanceof Player){
					Player p = (Player) sender;
					p.sendMessage(this.prefix + ChatColor.RED + "Invalid arguments!");
				} else {
					System.out.println("[OnlineManagement] Invalid arguments!");
				}
			}

		}

		return false;
	}

}
