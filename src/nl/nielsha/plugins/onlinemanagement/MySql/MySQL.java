package nl.nielsha.plugins.onlinemanagement.MySql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import nl.nielsha.plugins.onlinemanagement.MySql.Database;

public class MySQL extends Database {
	
	/**
	 * @author -_Husky_-
     * @author tips48
	 */
	
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;


	public MySQL(Plugin plugin, String hostname, String port, String database,
			String username, String password) {
		super(plugin);
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	@Override
	public Connection openConnection() throws SQLException,
			ClassNotFoundException {
		if (checkConnection()) {
			return connection;
		}
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://"
				+ this.hostname + ":" + this.port + "/" + this.database,
				this.user, this.password);
		return connection;
	}
}