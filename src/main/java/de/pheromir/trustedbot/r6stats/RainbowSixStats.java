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

import java.net.SocketTimeoutException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NameNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import kong.unirest.*;

import de.pheromir.trustedbot.Main;

public class RainbowSixStats {

	public static int currentSeason = 18;

	private final Long updatedMillis;
	private final String uuid;
	private final String username;
	private final String p_user;

	private final int level;
	private final int playtime;
	private final int currentRank;
	private final int currentMMR;
	private final int currentMMRchange;
	private String mainRegion;

	// Ranked Stats
	// Total
	private final int r_wins;
	private final int r_losses;
	private double r_wlr;
	private final int r_kills;
	private final int r_deaths;
	private final double r_kd;
	// Seasonal
	private final int rs_wins;
	private final int rs_losses;
	private double rs_wlr;
	private final int rs_kills;
	private final int rs_deaths;
	private final double rs_kd;

	// Casual Stats
	// Total
	private final int c_wins;
	private final int c_losses;
	private double c_wlr;
	private final int c_kills;
	private final int c_deaths;
	private final double c_kd;

	private final List<String> aliases;

	// int[rank][mmr]
	private final int[][] seasons;

	public RainbowSixStats(String user) throws Exception {
		kong.unirest.json.JSONObject usersearch;
		usersearch = Unirest.get(String.format("https://r6.apitab.com/search/uplay/%s?u=%d&cid=%s", user, System.currentTimeMillis() / 1000, Main.r6TabKey)).asJson().getBody().getObject();
		if (!usersearch.getBoolean("foundmatch")) {
			throw new NameNotFoundException("Sorry, i can't find the specified user.");
		} else {
			uuid = usersearch.getJSONObject("players").getJSONObject(usersearch.getJSONObject("players").keys().next()).getJSONObject("profile").getString("p_user");
		}
		String apiUrl = String.format("https://r6.apitab.com/player/%s?u=%d&cid=%s", uuid, System.currentTimeMillis() / 1000, Main.r6TabKey);
		seasons = new int[3][2];
		aliases = new ArrayList<>();
		kong.unirest.json.JSONObject jo;
		try {
			Unirest.get(String.format("https://r6.apitab.com/update/%s&cid=%s", uuid, Main.r6TabKey)).asString();
		} catch (UnirestException ex) {
			if (ex.getCause() instanceof SocketTimeoutException) {
				Main.LOG.warn("[R6STATS] Timeout while requesting stats update");
			} else {
				Main.LOG.error("", ex);
			}
		}
		jo = Unirest.get(apiUrl).asJson().getBody().getObject();
		JSONObject ranked = (JSONObject) jo.get("ranked");
		// Seasonal
		//	private int cs_wins;
		//	private int cs_losses;
		//	private double cs_wlr;
		//	private int cs_kills;
		//	private int cs_deaths;
		//	private double cs_kd;
		String latestSeasonPlayed;
		try {
			latestSeasonPlayed = "EU";
			mainRegion = "Europe";
			switch (ranked.getString("topregion")) {
				case "Europe":
					latestSeasonPlayed = "EU";
					mainRegion = "Europe";
					break;
				case "America":
					latestSeasonPlayed = "NA";
					mainRegion = "America";
					break;
				case "Asia":
					latestSeasonPlayed = "AS";
					mainRegion = "Asia";
					break;
			}
		} catch (DateTimeParseException ex) {
			Main.LOG.warn("[R6STATS] Error parsing last update time for regions, falling back to EU");
			latestSeasonPlayed = "EU";
		}

		username = jo.getJSONObject("player").getString("p_name");
		p_user = jo.getJSONObject("player").getString("p_user");

		kong.unirest.json.JSONObject stats = jo.getJSONObject("stats");

		level = stats.getInt("level");
		currentRank = ranked.getInt(latestSeasonPlayed + "_rank");
		currentMMR = ranked.getInt(latestSeasonPlayed + "_mmr");
		currentMMRchange = ranked.getInt(latestSeasonPlayed + "_mmrchange");

		if (jo.has("aliases") && jo.get("aliases") instanceof JSONObject) {
			kong.unirest.json.JSONObject aliasesObj = jo.getJSONObject("aliases");
			aliasesObj.keys().forEachRemaining(ind -> aliases.add(aliasesObj.getJSONObject(ind).getString("name")));
		}

		// Ranked overall
		r_wins = stats.getInt("rankedpvp_matchwon");
		r_losses = stats.getInt("rankedpvp_matchlost");
		r_wlr = Math.floor(((double) r_wins / (double) (r_wins + r_losses)) * 10000.0) / 100.0;
		r_wlr = Double.isNaN(r_wlr) ? 0d : r_wlr;

		r_kills = stats.getInt("rankedpvp_kills");
		r_deaths = stats.getInt("rankedpvp_death");
		r_kd = Double.parseDouble(stats.getString("rankedpvp_kd"));

		// Ranked seasonal
		rs_wins = ranked.getInt(latestSeasonPlayed + "_wins");
		rs_losses = ranked.getInt(latestSeasonPlayed + "_losses");
		rs_wlr = Math.floor(((double) rs_wins / (double) (rs_wins + rs_losses)) * 10000.0) / 100.0;
		rs_wlr = Double.isNaN(rs_wlr) ? 0d : rs_wlr;

		rs_kills = ranked.getInt(latestSeasonPlayed + "_kills");
		rs_deaths = ranked.getInt(latestSeasonPlayed + "_deaths");
		rs_kd = Math.floor(((double) rs_kills / (double) (rs_deaths)) * 100.0) / 100.0;

		// Casual overall
		c_wins = stats.getInt("casualpvp_matchwon");
		c_losses = stats.getInt("casualpvp_matchlost");
		c_wlr = Math.floor(((double) c_wins / (double) (c_wins + c_losses)) * 10000.0) / 100.0;
		c_wlr = Double.isNaN(c_wlr) ? 0d : c_wlr;

		c_kills = stats.getInt("casualpvp_kills");
		c_deaths = stats.getInt("casualpvp_death");
		c_kd = Double.parseDouble(stats.getString("casualpvp_kd"));

//		// Casual seasonal
//		if(jo.has("db")) {
//			JSONObject db = jo.getJSONObject("db");
//			cs_wins = db.getInt("casual_wins");
//			cs_losses = db.getInt("casual_losses");
//			cs_wlr = Math.floor(((double) cs_wins / (double) (cs_wins + cs_losses)) * 10000.0) / 100.0;
//			cs_wlr = Double.isNaN(cs_wlr) ? 0d : cs_wlr;
//
//			cs_kills = db.getInt("casual_kills");
//			cs_deaths = db.getInt("casual_deaths");
//			cs_kd = Math.floor(((double) cs_kills / (double) (cs_deaths)) * 100.0) / 100.0;
//		} else {
//			cs_wins = 0;
//			cs_losses = 0;
//			cs_wlr = 0;
//			cs_wlr = 0;
//
//			cs_kills = 0;
//			cs_deaths = 0;
//			cs_kd = 0;
//		}


		int ranked_playtime = stats.getInt("rankedpvp_timeplayed");
		int casual_playtime = stats.getInt("casualpvp_timeplayed");
		playtime = Math.round((ranked_playtime + casual_playtime) / 3600F);

		kong.unirest.json.JSONObject pastSeasons = jo.getJSONObject("seasons");
		// latest season
		seasons[0][0] = ranked.getInt(latestSeasonPlayed + "_maxrank");
		seasons[0][1] = ranked.getInt(latestSeasonPlayed + "_maxmmr");

		// 1 season before
		try {
			seasons[1][0] = pastSeasons.getJSONObject(currentSeason - 1 + "").getInt("maxrank");
			seasons[1][1] = pastSeasons.getJSONObject(currentSeason - 1 + "").get("maxmmr") instanceof String ? 0 : pastSeasons.getJSONObject(currentSeason - 1 + "").getInt("maxmmr");
		} catch (JSONException ex) {
			Main.LOG.error("", ex);
			seasons[1][0] = seasons[1][1] = -1;
		}

		// 2 seasons before
		try {
			seasons[2][0] = pastSeasons.getJSONObject(currentSeason - 2 + "").getInt("maxrank");
			seasons[2][1] = pastSeasons.getJSONObject(currentSeason - 2 + "").get("maxmmr") instanceof String ? 0 : pastSeasons.getJSONObject(currentSeason - 2 + "").getInt("maxmmr");
		} catch (JSONException ex) {
			Main.LOG.error("", ex);
			seasons[2][0] = seasons[2][1] = -1;
		}

		updatedMillis = jo.getJSONObject("refresh").getLong("utime") * 1000;
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

	public int getRankedWinsSeasonal() {
		return rs_wins;
	}

	public int getRankedLossesSeasonal() {
		return rs_losses;
	}

	public double getRankedWinLoseRateSeasonal() {
		return rs_wlr;
	}

	public int getRankedKillsSeasonal() {
		return rs_kills;
	}

	public int getRankedDeathsSeasonal() {
		return rs_deaths;
	}

	public double getRankedKDRSeasonal() {
		return rs_kd;
	}

//	public int getCasualWinsSeasonal() {
//		return cs_wins;
//	}
//
//	public int getCasualLossesSeasonal() {
//		return cs_losses;
//	}
//
//	public double getCasualWinLoseRateSeasonal() {
//		return cs_wlr;
//	}
//
//	public int getCasualKillsSeasonal() {
//		return cs_kills;
//	}
//
//	public int getCasualDeathsSeasonal() {
//		return cs_deaths;
//	}
//
//	public double getCasualKDRSeasonal() {
//		return cs_kd;
//	}

	public int getCurrentRank() {
		return currentRank;
	}

	public int getCurrentMMR() {
		return currentMMR;
	}

	public int getCurrentMMRChange() {
		return currentMMRchange;
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

	public Long getUpdatedMillis() {
		return updatedMillis;
	}

	public List<String> getAliases() {
		return aliases;
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
			case 14:
				return "Phantom Sight";
			case 15:
				return "Ember Rise";
			case 16:
				return "Shifting Tides";
			case 17:
				return "Void Edge";
			case 18:
				return "Steel Wave";
			default:
				return "Unknown";
		}
	}
//
//	public static String translateOperators(String op) {
//		switch (op) {
//			case "1:1":
////				return "Recruit (SAS)";
//			case "1:2":
////				return "Recruit (FBI)";
//			case "1:3":
////				return "Recruit (GIGN)";
//			case "1:4":
////				return "Recruit (Spetsnaz)";
//			case "1:5":
////				return "Recruit (GSG9)";
//				return "Recruit";
//			case "2:1":
//				return "Smoke";
//			case "2:2":
//				return "Castle";
//			case "2:3":
//				return "Doc";
//			case "2:4":
//				return "Glaz";
//			case "2:5":
//				return "Blitz";
//			case "2:6":
//				return "Buck";
//			case "2:7":
//				return "Blackbeard";
//			case "2:8":
//				return "Capitao";
//			case "2:9":
//				return "Hibana";
//			case "2:10":
//				return "Maverick";
//			case "2:11":
//				return "Nomad";
//			case "2:12":
//				return "Gridlock";
//			case "2:A":
//				return "Jackal";
//			case "2:B":
//				return "Ying";
//			case "2:C":
//				return "Ela";
//			case "2:D":
//				return "Dokkaebi";
//			case "2:F":
//				return "Maestro";
//			case "3:1":
//				return "Mute";
//			case "3:2":
//				return "Ash";
//			case "3:3":
//				return "Rook";
//			case "3:4":
//				return "Fuze";
//			case "3:5":
//				return "IQ";
//			case "3:6":
//				return "Frost";
//			case "3:7":
//				return "Valkyrie";
//			case "3:8":
//				return "Caveira";
//			case "3:9":
//				return "Echo";
//			case "3:10":
//				return "Clash";
//			case "3:11":
//				return "Kaid";
//			case "3:12":
//				return "Mozzie";
//			case "3:A":
//				return "Mira";
//			case "3:B":
//				return "Lesion";
//			case "3:C":
//				return "Zofia";
//			case "3:D":
//				return "Vigil";
//			case "3:E":
//				return "Lion";
//			case "3:F":
//				return "Alibi";
//			case "3:17":
//				return "Wamai";
//			case "4:1":
//				return "Sledge";
//			case "4:2":
//				return "Pulse";
//			case "4:3":
//				return "Twitch";
//			case "4:4":
//				return "Kapkan";
//			case "4:5":
//				return "Jäger";
//			case "4:E":
//				return "Finka";
//			case "5:1":
//				return "Thatcher";
//			case "5:2":
//				return "Thermite";
//			case "5:3":
//				return "Montagne";
//			case "5:4":
//				return "Tachanka";
//			case "5:5":
//				return "Bandit";
//
//			default:
//				return "Unknown";
//		}
//	}
}
