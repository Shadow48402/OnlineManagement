package nl.nielsha.plugins.onlinemanagement;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommand implements CommandExecutor{
	public OnlineManagement plugin;
	public FriendCommand(OnlineManagement plugin){
		this.plugin = plugin;
	}
	public HashMap<Player, ArrayList<String>> adds = new HashMap<Player, ArrayList<String>>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		
		return false;
	}
	
	public boolean areFriends(Player p1, Player p2){
		return false;
	}
	
	public void addFriend(Player p, Player a){
		if(a.isOnline()){
			
		}
	}
}
