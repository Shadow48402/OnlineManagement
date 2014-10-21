package nl.nielsha.plugins.onlinemanagement;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import nl.nielsha.plugins.onlinemanagement.MySql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OnlineManagement extends JavaPlugin implements Listener{
	private MySQL con;
	private Logger logger = Logger.getLogger("Minecraft");
	private boolean connected;

	public void onEnable(){
		if(!this.getDataFolder().exists()){
			this.getDataFolder().mkdir();
		}

		File f = new File(this.getDataFolder().getAbsolutePath(), "config.yml");
		if(!f.exists()){
			this.getConfig().set("Host", "localhost");
			this.getConfig().set("Port", "3306");
			this.getConfig().set("Database", "db");
			this.getConfig().set("Username", "user");
			this.getConfig().set("Password", "pass");
			this.saveConfig();
		}

		this.con = new MySQL(
				this,
				this.getConfig().getString("Host"),
				this.getConfig().getString("Port"),
				this.getConfig().getString("Database"),
				this.getConfig().getString("Username"),
				this.getConfig().getString("Password")
				);

		try {
			this.con.openConnection();
			connected = true;
		} catch (ClassNotFoundException | SQLException e) {
			logger.info("[OnlineManagement] Can't connect to MySQL database!");
			connected = false;
		}

		if(connected){
			try {
				Statement stat = this.con.getConnection().createStatement();
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS online("
						+ "Username varchar(100))");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	public void onDisable(){
		try {
			con.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onJoinEvent(PlayerJoinEvent e){
		Player p = e.getPlayer();
		String n = p.getName();

		try {
			Statement stat = this.con.getConnection().createStatement();
			stat.executeUpdate("INSERT INTO `online` (Username) VALUES('" + n + "')");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@EventHandler
	public void onQuitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		String n = p.getName();

		try {
			Statement stat = this.con.getConnection().createStatement();
			stat.executeUpdate("DELETE FROM `online` WHERE Username='" + n + "'");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}
