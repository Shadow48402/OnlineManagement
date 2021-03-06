package nl.nielsha.plugins.onlinemanagement.MySql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.Plugin;

public abstract class Database {
	
	/**
	 * @author -_Husky_-
     * @author tips48
	 */

	protected Connection connection;
	protected Plugin plugin;

	protected Database(Plugin plugin) {
		this.plugin = plugin;
		this.connection = null;
	}

	public abstract Connection openConnection() throws SQLException,
			ClassNotFoundException;

	public boolean checkConnection() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	public Connection getConnection() {
		return connection;
	}
	
	public boolean closeConnection() throws SQLException {
		if (connection == null) {
			return false;
		}
		connection.close();
		return true;
	}


	public ResultSet querySQL(String query) throws SQLException,
			ClassNotFoundException {
		if (!checkConnection()) {
			openConnection();
		}

		Statement statement = connection.createStatement();

		ResultSet result = statement.executeQuery(query);

		return result;
	}

	public int updateSQL(String query) throws SQLException,
			ClassNotFoundException {
		if (!checkConnection()) {
			openConnection();
		}

		Statement statement = connection.createStatement();

		int result = statement.executeUpdate(query);

		return result;
	}
}