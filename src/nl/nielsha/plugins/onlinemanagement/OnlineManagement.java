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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
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
	private boolean connected;
	private Logger logger = Logger.getLogger("Minecraft");
	public OnlineCommand oc = new OnlineCommand(this);

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
			this.getConfig().set("DefaultBanMessage", "&4You are banned from this server!");
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
			this.createOnlineTable();  //Create online table (if not exists)
			//this.createBanTable();     //Create ban table (if not exists)
			//this.createTimeBanTable(); //Create time ban table (if not exists)
		}

		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("om").setExecutor(this);
		getCommand("online").setExecutor(this.oc);
	}

	public void onDisable(){
		try {
			this.deleteOnlineTable();
			this.createOnlineTable();
			con.closeConnection(); //Closing connection
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLoginEvent(PlayerLoginEvent e){
		/**
		 *   Bans Part
		 *

		Player p = e.getPlayer();
		if(p.isBanned()){
			e.setKickMessage(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("DefaultBanMessage")));
		}
		*/
	}

	@EventHandler
	public void onJoinEvent(PlayerJoinEvent e){
		/**
		 *   Online Part
		 */
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
				logger.info("[OnlineManagement] MySQL Error?!");
			}
		}
		/**
		 *   End Online Part
		 */
	}

	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){

		/**
		 *   Online Part
		 */
		Player p = e.getPlayer();
		String n = p.getName();

		if(connected){
			try {
				Statement stat = this.con.getConnection().createStatement();
				stat.executeUpdate("DELETE FROM `online` WHERE Username='" + n + "'");
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.info("[OnlineManagement] MySQL Error?!");
			}
		}

		/**
		 *   End Online Part
		 */
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e){

		/**
		 *  Online Part
		 */
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
				logger.info("[OnlineManagement] MySQL Error?!");
			}
		}
		/**
		 *    End Online Part
		 */
	}

	//@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

		/**
		 *   Online Management Command
		 */
		if(cmd.getName().equalsIgnoreCase("om")){	
			if(!(sender instanceof Player)){
				sender.sendMessage("[OnlineManagement] You're not allowed to run this command :s");
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
							createOnlineTable();
						} else {
							s.sendMessage(this.notAllowed);
						}
					}
					if(args[1].equalsIgnoreCase("delete")){
						if(s.hasPermission("onlinemanagement.table.delete")){
							deleteOnlineTable();
						} else {
							s.sendMessage(this.notAllowed);
						}
					}
					/*
					 * Update next time to clear online contents
					 */
					if(args[1].equalsIgnoreCase("reset")){
						if(s.hasPermission("onlinemanagement.table.reset")){
							deleteOnlineTable();
							createOnlineTable();
						} else {
							s.sendMessage(this.notAllowed);
						}
					}
				}
			}
		}

		/**
		 *   Ban Commands
		 *

		if(cmd.getName().equalsIgnoreCase("ban")){
			if(args.length >= 1){
				Player t = Bukkit.getPlayer(args[0]);

				if(t == null){
					if(sender instanceof Player){
						((Player) sender).sendMessage(this.prefix + ChatColor.RED + "That player is not valid!");
					} else {
						logger.info("[OnlineManagement] That player is not valid!");
					}
					return true;
				}

				String id = t.getUniqueId().toString();
				String d = new Date().toString();

				try {
					Statement stat = this.con.getConnection().createStatement();
					ResultSet r = stat.executeQuery("SELECT * FROM bans WHERE UUID='" + id + "'");
					if(!r.next()){
						if(args.length == 1){
							t.kickPlayer(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("DefaultBanMessage")));
							stat.executeUpdate("INSERT INTO `bans` (UUID, Since) VALUES('" + id + "', '" + d + "')");
						} else {
							String re = this.getArguments(args, 1);
							t.kickPlayer(ChatColor.translateAlternateColorCodes('&', re));
							stat.executeUpdate("INSERT INTO `bans` (UUID, Since, Reason) VALUES('" + id + "', '" + d + "', '" + re + "')");
						}
						if(sender instanceof Player){
							((Player) sender).sendMessage(this.prefix + ChatColor.GREEN + "You've banned " + t.getName() + "!");
						} else {
							logger.info("[OnlineManagement] You've banned " + t.getName() + "!");
						}
						t.setBanned(true);
					} else {
						//Already banned
					}
				} catch (SQLException e) {
					if(sender instanceof Player){
						((Player) sender).sendMessage(this.prefix + ChatColor.RED + "MySQL Error?!");
					} else {
						logger.info("[OnlineManagement] MySQL Error?!");
					}
					e.printStackTrace();
				}
			}
		}
		*/


		return false;
	}


	/***********************************************************************
	 *                                                                     *
	 *                            TABLES                                   *
	 *                                                                     *
	 ***********************************************************************/
	public void createOnlineTable(){
		try {
			Statement stat = this.con.getConnection().createStatement(); 
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS online("
					+ "Username varchar(100),"
					+ "Since varchar(100),"
					+ "Kills int(100) DEFAULT 0,"
					+ "Deads int(100) DEFAULT 0)"); // Creating the online table
		} catch (SQLException e) {
			e.printStackTrace();
			logger.info("[OnlineManagement] MySQL Error?!");
		}
	}

	/**
	public void createTimeBanTable(){
		try {
			Statement stat = this.con.getConnection().createStatement(); 
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS timebans("
					+ "UUID varchar(100),"
					+ "Since varchar(100),"
					+ "Time int(100),"
					+ "Reason varchar(200) DEFAULT null)"); // Creating the time ban table
		} catch (SQLException e) {
			e.printStackTrace();
			logger.info("[OnlineManagement] MySQL Error?!");
		}
	}

	public void createBanTable(){
		try {
			Statement stat = this.con.getConnection().createStatement(); 
			stat.executeUpdate("CREATE TABLE IF NOT EXISTS bans("
					+ "UUID varchar(100),"
					+ "Since varchar(100),"
					+ "Reason varchar(200) DEFAULT null)"); // Creating the ban table
		} catch (SQLException e) {
			e.printStackTrace();
			logger.info("[OnlineManagement] MySQL Error?!");
		}
	}
	*/

	public void deleteOnlineTable(){
		try {
			Statement stat = this.con.getConnection().createStatement(); 
			stat.executeUpdate("DROP TABLE online"); // Deleting the table
		} catch (SQLException e) {
			e.printStackTrace();
			logger.info("[OnlineManagement] MySQL Error?!");
		}
	}

	/**
	 *   Argument Splitting
	 *

	public String getArguments(String[] args, int start) {
		StringBuilder br = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) {
				br.append(" ");
			}
			br.append(args[i]);
		}
		return br.toString();
	}
	*/

}
