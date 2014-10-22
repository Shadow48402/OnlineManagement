package nl.nielsha.plugins.onlinemanagement;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import nl.nielsha.plugins.onlinemanagement.MySql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnlineManagement extends JavaPlugin implements Listener, CommandExecutor{

	/**
	 * Copyright Niels Hamelink | Shadow48402
	 * @author Niels Hamelink
	 * @author Shadow48402
	 * @version 1.0.1
	 */

	private MySQL con;
	private Logger logger = Logger.getLogger("Minecraft");
	private boolean connected;

	private String prefix = ChatColor.GRAY + "[" + ChatColor.AQUA + "OnlineManagement" + ChatColor.GRAY + "] ";
	private String notAllowed = this.prefix + ChatColor.RED + "You are not allowed to do this!";
	private List<?> enabledInfo = this.getConfig().getList("EnabledInfo");

	public void onEnable(){
		if(!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}

		File f = new File(this.getDataFolder().getAbsolutePath(), "config.yml"); //Getting config.yml
		if(!f.exists()){ // Checking or the config exists
			this.getConfig().set("Host", "localhost"); //Settings defaults... \/
			this.getConfig().set("Port", "3306");
			this.getConfig().set("Database", "db");
			this.getConfig().set("Username", "user");
			this.getConfig().set("Password", "pass");
			this.getConfig().set("EnabledInfo", Arrays.asList("Since", "Kills", "Deads"));
			this.saveConfig();                          //Save|Create config
		}

		this.con = new MySQL( //Make connection varriable
				this,
				this.getConfig().getString("Host"),
				this.getConfig().getString("Port"),
				this.getConfig().getString("Database"),
				this.getConfig().getString("Username"),
				this.getConfig().getString("Password")
				);

		try {
			this.con.openConnection(); //Open the MySQL connection
			connected = true; // Check or the plugin is connected
		} catch (ClassNotFoundException | SQLException e) {
			logger.info("[OnlineManagement] Can't connect to MySQL database!");
			connected = false; // Check or the plugin is connected
		}

		if(connected){ //Check or is connected
			createTable(); //Create table (if not exists)
		}

		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("om").setExecutor(this);
	}

	public void onDisable(){
		try {
			con.closeConnection(); //Closing connection
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onJoinEvent(PlayerJoinEvent e){
		Player p = e.getPlayer();
		String n = p.getName();
		String d = new Date().toString();
		String[] fullDate = d.split(" ");
		String d2 = fullDate[3];

		if(connected){
			try {
				Statement stat = this.con.getConnection().createStatement();
				stat.executeUpdate("INSERT INTO `online` (Username, Since) VALUES('" + n + "', '" + d2 + "')");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		String n = p.getName();

		if(connected){
			try {
				Statement stat = this.con.getConnection().createStatement();
				stat.executeUpdate("DELETE FROM `online` WHERE Username='" + n + "'");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e){
		if(!(e.getEntity().getKiller() instanceof Player)){
			return;
		}
		String n = "";
		if(e.getEntity().getKiller() instanceof Player){
			Player k = e.getEntity().getKiller(); // Getting the killer
			n = k.getName();                      // Getting the killer his/her name
		}

		Player d = e.getEntity().getPlayer(); // Getting the dead guy/girl
		String n2 = d.getName();              // Getting the name of the dead guy/girl

		if(connected){
			try {
				Statement stat = this.con.getConnection().createStatement();

				if(e.getEntity().getKiller() instanceof Player){
					if(enabledInfo.contains("Kills")){
						ResultSet r = stat.executeQuery("SELECT * FROM online WHERE Username='" + n + "'");
						r.next();
						int kills = r.getInt("Kills");
						stat.executeUpdate("UPDATE online SET Kills='" + (kills+1) + "' WHERE Username='" + n + "'");
					}
				}

				if(enabledInfo.contains("Deads")){
					ResultSet r2 = stat.executeQuery("SELECT * FROM online WHERE Username='" + d + "'");
					r2.next();
					int deads = r2.getInt("Deads");
					stat.executeUpdate("UPDATE online SET Deads='" + (deads+1) + "' WHERE Username='" + n2 + "'");
				}

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("om")){	
			if(!(sender instanceof Player)){
				sender.sendMessage("You're not allowed to run this command :s");
				return true;
			}
			Player s = (Player) sender;

			if(args.length == 0){
				s.sendMessage(ChatColor.GOLD + "===== (" + ChatColor.AQUA + "Online Management" + ChatColor.GOLD + ") =====");
				s.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.BLUE + this.getDescription().getVersion());
				s.sendMessage(ChatColor.GRAY + "Made By: " + ChatColor.BLUE + "Niels Hamelink or Shadow48402");
				s.sendMessage(ChatColor.GRAY + "Twitter: " + ChatColor.BLUE + "@NielsHamelink");
			}
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("help")){
					s.sendMessage(ChatColor.GRAY + "-----;" + ChatColor.AQUA + " Normal Commands " + ChatColor.GRAY + ";-----");
					s.sendMessage(ChatColor.GRAY + "Information: " + ChatColor.GOLD + "/om");
					s.sendMessage(ChatColor.GRAY + "-----;" + ChatColor.AQUA + " Table Commands " + ChatColor.GRAY + ";-----");
					s.sendMessage(ChatColor.GRAY + "Delete Table Command: " + ChatColor.GOLD + "/om table delete");
					s.sendMessage(ChatColor.GRAY + "Create Table Command: " + ChatColor.GOLD + "/om table create");
					s.sendMessage(ChatColor.GRAY + "Reset Table Command: " + ChatColor.GOLD + "/om table reset");
				}
			} 
			if(args.length == 2){
				if(args[0].equalsIgnoreCase("table")){
					if(args[1].equalsIgnoreCase("create")){
						if(s.hasPermission("onlinemanagement.table.create")){
							createTable();
						} else {
							s.sendMessage(this.notAllowed);
						}
					}
					if(args[1].equalsIgnoreCase("delete")){
						if(s.hasPermission("onlinemanagement.table.delete")){
							deleteTable();
						} else {
							s.sendMessage(this.notAllowed);
						}
					}
					/*
					 * Update next time to clear online contents
					 */
					if(args[1].equalsIgnoreCase("reset")){
						if(s.hasPermission("onlinemanagement.table.reset")){
							deleteTable();
							createTable();
						} else {
							s.sendMessage(this.notAllowed);
						}
					}
				}
			}
		}
		return false;
	}

	public void createTable(){
		try {
			Statement stat = this.con.getConnection().createStatement(); 
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS online("
					+ "Username varchar(100),"
					+ "Since varchar(100),"
					+ "Kills int(100) DEFAULT 0,"
					+ "Deads int(100) DEFAULT 0)"); // Creating the table
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteTable(){
		try {
			Statement stat = this.con.getConnection().createStatement(); 
			stat.executeUpdate("DROP TABLE online"); // Deleting the table
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
