package de.pheromir.discordmusicbot.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import de.pheromir.discordmusicbot.Methods;
import de.pheromir.discordmusicbot.commands.R6Command;

public class RainbowSixStats {

	private String profileStatsUrl;
	private String seasonStatsUrl;
	private ArrayList<RainbowSixStatsSeason> seasons;
	private String uuid;
	private String username;

	private int rankWins;
	private int rankLosses;
	private double rankWLR;
	private int rankKills;
	private int rankDeaths;
	private double rankKD;

	private int casualWins;
	private int casualLosses;
	private double casualWLR;
	private int casualKills;
	private int casualDeaths;
	private double casualKD;

	private boolean isValid;
	private long validUntil;

	public RainbowSixStats(String username) throws NullPointerException, IOException {
		isValid = true;
		profileStatsUrl = String.format("https://api.r6stats.com/api/v1/players/%s?platform=uplay", username);
		seasonStatsUrl = String.format("https://api.r6stats.com/api/v1/players/%s/seasons?platform=uplay", username);
		seasons = new ArrayList<>();
		validUntil = System.currentTimeMillis() + (3 * 60 * 60 * 1000L);
		requestStats();
		requestSeasons();
		sortSeasons();
		R6Command.statsCache.put(username.toLowerCase(), this);
	}

	private void requestStats() throws IOException {
		JSONObject jo;
		try {
			jo = Methods.httpRequest(profileStatsUrl);
		} catch (IOException e) {
			throw e;
		}
		JSONObject stats = jo.getJSONObject("player").getJSONObject("stats");

		uuid = jo.getJSONObject("player").getString("ubisoft_id");
		username = jo.getJSONObject("player").getString("username");

		JSONObject ranked = stats.getJSONObject("ranked");
		rankWins = ranked.getInt("wins");
		rankLosses = ranked.getInt("losses");
		rankWLR = ranked.getDouble("wlr");
		rankKills = ranked.getInt("kills");
		rankDeaths = ranked.getInt("deaths");
		rankKD = ranked.getDouble("kd");

		JSONObject casual = stats.getJSONObject("casual");
		casualWins = casual.getInt("wins");
		casualLosses = casual.getInt("losses");
		casualWLR = casual.getDouble("wlr");
		casualKills = casual.getInt("kills");
		casualDeaths = casual.getInt("deaths");
		casualKD = casual.getDouble("kd");
	}

	private void requestSeasons() {
		JSONObject jo;
		try {
			jo = Methods.httpRequest(seasonStatsUrl).getJSONObject("seasons");
		} catch (JSONException | IOException e) {
			return;
		}
		for (String seasonStr : jo.keySet()) {
			for (String region : jo.getJSONObject(seasonStr).keySet()) {
				JSONObject ssn = jo.getJSONObject(seasonStr).getJSONObject(region);

				int wins = ssn.getInt("wins");
				int losses = ssn.getInt("losses");
				int abandons = ssn.getInt("abandons");
				int seasonId = ssn.getInt("season");
				String reg = ssn.getString("region");

				JSONObject ranking = ssn.getJSONObject("ranking");
				double rating = ranking.getDouble("rating");
				double nextRat = ranking.getDouble("next_rating");
				double prevRat = ranking.getDouble("prev_rating");
				int rank = ranking.getInt("rank");

				RainbowSixStatsSeason s = new RainbowSixStatsSeason(seasonId, wins, losses, abandons, reg, rating,
						nextRat, prevRat, rank);
				this.seasons.add(s);
			}

		}
	}

	private void sortSeasons() {
		seasons.sort(Comparator.comparing(RainbowSixStatsSeason::getId).reversed());
	}

	public String getUUID() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public int getRankedWins() {
		return rankWins;
	}

	public int getRankedLosses() {
		return rankLosses;
	}

	public double getRankedWinLoseRate() {
		return rankWLR;
	}

	public int getRankedKills() {
		return rankKills;
	}

	public int getRankedDeaths() {
		return rankDeaths;
	}

	public double getRankedKillDeathRate() {
		return rankKD;
	}

	public int getCasualWins() {
		return casualWins;
	}

	public int getCasualLosses() {
		return casualLosses;
	}

	public double getCasualWinLoseRate() {
		return casualWLR;
	}

	public int getCasualKills() {
		return casualKills;
	}

	public int getCasualDeaths() {
		return casualDeaths;
	}

	public double getCasualKillDeathRate() {
		return casualKD;
	}

	public boolean isValid() {
		return isValid;
	}

	public ArrayList<RainbowSixStatsSeason> getSeasons() {
		return seasons;
	}

	public long getValidUntil() {
		return validUntil;
	}
}