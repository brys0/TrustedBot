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
package de.pheromir.trustedbot.r6stats;

import javax.naming.NameNotFoundException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;

public class RainbowSixStats {

	public static int currentSeason = 13;

	private String apiUrl;
	private String updatedAgo;
	private String uuid;
	private String username;
	private String p_user;

	private int level;
	private int playtime;
	private int currentRank;
	private int currentMMR;
	private String mainRegion;

	private String favAttacker;
	private String favDefender;

	private int r_wins;
	private int r_losses;
	private double r_wlr;
	private int r_kills;
	private int r_deaths;
	private double r_kd;

	private int c_wins;
	private int c_losses;
	private double c_wlr;
	private int c_kills;
	private int c_deaths;
	private double c_kd;

	// int[rank][mmr]
	private int[][] seasons;

	public RainbowSixStats(String user) throws Exception {
		JSONObject usersearch = Unirest.get(String.format("https://r6tab.com/api/search.php?platform=uplay&search=%s", user)).asJson().getBody().getObject();
		if (usersearch.getInt("totalresults") < 1) {
			throw new NameNotFoundException("Sorry, i can't find the specified user.");
		} else {
			uuid = ((JSONObject) usersearch.getJSONArray("results").get(0)).getString("p_id");
		}
		apiUrl = String.format("https://r6tab.com/api/player.php?p_id=%s", uuid);
		seasons = new int[3][2];

		JSONObject jo;
		jo = Unirest.get(apiUrl).asJson().getBody().getObject();

		username = jo.getString("p_name");
		p_user = jo.getString("p_user");
		level = jo.getInt("p_level");
		currentRank = jo.getInt("p_currentrank");
		currentMMR = jo.getInt("p_currentmmr");

		JSONArray p_data = jo.getJSONArray("data");

		r_wins = p_data.getInt(26);
		r_losses = p_data.getInt(27);
		r_wlr = Math.floor(((double) r_wins / (double) (r_wins + r_losses)) * 10000.0) / 100.0;
		r_wlr = Double.isNaN(r_wlr) ? 0d : r_wlr;

		r_kills = p_data.getInt(1);
		r_deaths = p_data.getInt(2);
		r_kd = (jo.getInt("kd") / 100.0);

		c_wins = p_data.getInt(8);
		c_losses = p_data.getInt(9);
		c_wlr = Math.floor(((double) c_wins / (double) (c_wins + c_losses)) * 10000.0) / 100.0;
		c_wlr = Double.isNaN(c_wlr) ? 0d : c_wlr;

		c_kills = p_data.getInt(6);
		c_deaths = p_data.getInt(7);
		c_kd = Math.round(((double)c_kills / (c_deaths == 0.0 ? 1 : (double)c_deaths)) * 100.0)/100.0;

		int ranked_playtime = p_data.getInt(0);
		int casual_playtime = p_data.getInt(5);
		playtime = Math.round((ranked_playtime + casual_playtime) / (60 * 60));

		favAttacker = jo.getString("favattacker");
		favDefender = jo.getString("favdefender");

		// latest season
		seasons[0][0] = jo.getInt("p_maxrank");
		seasons[0][1] = jo.getInt("p_maxmmr");

		// 1 season before
		if (jo.getString("season" + (currentSeason - 1)).split(":").length == 2) {
			String[] tempSeason = jo.getString("season" + (currentSeason - 1)).split(":");
			seasons[1][0] = Integer.parseInt(tempSeason[0]);
			seasons[1][1] = Integer.parseInt(tempSeason[1]);
		}

		// 2 seasons before
		if (jo.getString("season" + (currentSeason - 2)).split(":").length == 2) {
			String[] tempSeason = jo.getString("season" + (currentSeason - 2)).split(":");
			seasons[2][0] = Integer.parseInt(tempSeason[0]);
			seasons[2][1] = Integer.parseInt(tempSeason[1]);
		}

		updatedAgo = jo.getString("updatedon").replaceAll("(<u>|</u>)", "");

		if (currentMMR == jo.getInt("p_EU_currentmmr")) {
			mainRegion = "Europe";
		} else if (currentMMR == jo.getInt("p_NA_currentmmr")) {
			mainRegion = "America";
		} else if (currentMMR == jo.getInt("p_AS_currentmmr")) {
			mainRegion = "Asia";
		} else {
			mainRegion = "Unknown";
		}
	}

	public String getUUID() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public int getRankedWins() {
		return r_wins;
	}

	public int getRankedLosses() {
		return r_losses;
	}

	public double getRankedWinLoseRate() {
		return r_wlr;
	}

	public int getRankedKills() {
		return r_kills;
	}

	public int getRankedDeaths() {
		return r_deaths;
	}

	public double getRankedKDR() {
		return r_kd;
	}
	
	public int getCasualWins() {
		return c_wins;
	}

	public int getCasualLosses() {
		return c_losses;
	}

	public double getCasualWinLoseRate() {
		return c_wlr;
	}

	public int getCasualKills() {
		return c_kills;
	}

	public int getCasualDeaths() {
		return c_deaths;
	}

	public double getCasualKDR() {
		return c_kd;
	}

	public int getCurrentRank() {
		return currentRank;
	}

	public int getCurrentMMR() {
		return currentMMR;
	}

	public int getMaxRank() {
		return seasons[0][0];
	}

	public int getMaxMMR() {
		return seasons[0][1];
	}

	public int getLevel() {
		return level;
	}

	public int getPlaytime() {
		return playtime;
	}

	public String getFavAttacker() {
		return translateOperators(favAttacker);
	}

	public String getFavDefender() {
		return translateOperators(favDefender);
	}

	public String getMainRegion() {
		return mainRegion;
	}

	public String getProfileURL() {
		return String.format("https://r6tab.com/%s", uuid);
	}

	public String getAvatarURL() {
		return String.format("https://ubisoft-avatars.akamaized.net/%s/default_256_256.png", p_user);
	}

	public int[][] getLastThreeSeasonRanks() {
		return seasons;
	}

	public String getUpdatedAgo() {
		return updatedAgo;
	}

	public static String translateRank(int rankId) {
		switch (rankId) {
			case 1:
				return "Copper 4";
			case 2:
				return "Copper 3";
			case 3:
				return "Copper 2";
			case 4:
				return "Copper 1";
			case 5:
				return "Bronze 4";
			case 6:
				return "Bronze 3";
			case 7:
				return "Bronze 2";
			case 8:
				return "Bronze 1";
			case 9:
				return "Silver 4";
			case 10:
				return "Silver 3";
			case 11:
				return "Silver 2";
			case 12:
				return "Silver 1";
			case 13:
				return "Gold 4";
			case 14:
				return "Gold 3";
			case 15:
				return "Gold 2";
			case 16:
				return "Gold 1";
			case 17:
				return "Platinum 3";
			case 18:
				return "Platinum 2";
			case 19:
				return "Platinum 1";
			case 20:
				return "Diamond";
			default:
				return "Unranked";
		}
	}

	public static String translateSeason(int id) {
		switch (id) {
			case 1:
				return "Black Ice";
			case 2:
				return "Dust Line";
			case 3:
				return "Skull Rain";
			case 4:
				return "Red Crow";
			case 5:
				return "Velvet Shell";
			case 6:
				return "Health";
			case 7:
				return "Blood Orchid";
			case 8:
				return "White Noise";
			case 9:
				return "Chimera";
			case 10:
				return "Para Bellum";
			case 11:
				return "Grim Sky";
			case 12:
				return "Wind Bastion";
			case 13:
				return "Burnt Horizon";
			default:
				return "Unknown";
		}
	}

	public static String translateOperators(String op) {
		switch (op) {
			case "2:1":
				return "Smoke";
			case "2:2":
				return "Castle";
			case "2:3":
				return "Doc";
			case "2:4":
				return "Glaz";
			case "2:5":
				return "Blitz";
			case "2:6":
				return "Buck";
			case "2:7":
				return "Blackbeard";
			case "2:8":
				return "Capitao";
			case "2:9":
				return "Hibana";
			case "2:10":
				return "Maverick";
			case "2:11":
				return "Nomad";
			case "2:A":
				return "Jackal";
			case "2:B":
				return "Ying";
			case "2:C":
				return "Ela";
			case "2:D":
				return "Dokkaebi";
			case "2:F":
				return "Maestro";
			case "3:1":
				return "Mute";
			case "3:2":
				return "Ash";
			case "3:3":
				return "Rook";
			case "3:4":
				return "Fuze";
			case "3:5":
				return "IQ";
			case "3:6":
				return "Frost";
			case "3:7":
				return "Valkyrie";
			case "3:8":
				return "Caveira";
			case "3:9":
				return "Echo";
			case "3:10":
				return "Clash";
			case "3:11":
				return "Kaid";
			case "3:A":
				return "Mira";
			case "3:B":
				return "Lesion";
			case "3:C":
				return "Zofia";
			case "3:D":
				return "Vigil";
			case "3:E":
				return "Lion";
			case "3:F":
				return "Alibi";
			case "4:1":
				return "Sledge";
			case "4:2":
				return "Pulse";
			case "4:3":
				return "Twitch";
			case "4:4":
				return "Kapkan";
			case "4:5":
				return "Jäger";
			case "4:E":
				return "Finka";
			case "5:1":
				return "Thatcher";
			case "5:2":
				return "Thermite";
			case "5:3":
				return "Montagne";
			case "5:4":
				return "Tachanka";
			case "5:5":
				return "Bandit";
			default:
				return "Unknown";
		}
	}
}
