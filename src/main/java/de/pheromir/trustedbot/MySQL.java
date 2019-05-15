/*******************************************************************************
 * Copyright (C) 2019 Pheromir
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.pheromir.trustedbot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.pheromir.trustedbot.config.Configuration;
import de.pheromir.trustedbot.config.YamlConfiguration;

public class MySQL {

	private String host;
	private int port;
	private String user;
	private String password;
	private String database;

	private Connection conn;

	public MySQL() {
		YamlConfiguration yaml = new YamlConfiguration();
		Configuration cfg;
		try {
			cfg = yaml.load(Main.configFile);
		} catch (IOException e) {
			Main.LOG.error("", e);
			return;
		}

		this.host = cfg.getString("MySQL.Host");
		this.port = cfg.getInt("MySQL.Port");
		this.user = cfg.getString("MySQL.Username");
		this.password = cfg.getString("MySQL.Password");
		this.database = cfg.getString("MySQL.Database");
	}

	public Connection openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/"
					+ this.database, this.user, this.password);
			this.conn = conn;
			return conn;
		} catch (SQLException e) {
			Main.LOG.error("", e);
		} catch (ClassNotFoundException e) {
			Main.LOG.error("", e);
		}
		return null;
	}

	public Connection getConnection() {
		return this.conn;
	}

	public boolean hasConnection() {
		try {
			return this.conn != null || this.conn.isValid(1);
		} catch (SQLException e) {
			return false;
		}
	}

	public void queryUpdate(String query) {
		Connection conn = this.conn;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(query);
			st.executeUpdate();
		} catch (SQLException e) {
			Main.LOG.error("Failed to send update " + query + ".", e);
		} finally {
			this.closeRessources(null, st);
		}
	}

	public void closeRessources(ResultSet rs, PreparedStatement st) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				Main.LOG.error("", e);
			}
		}
	}

	public void closeConnection() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			Main.LOG.error("", e);
		} finally {
			this.conn = null;
		}
	}
}
