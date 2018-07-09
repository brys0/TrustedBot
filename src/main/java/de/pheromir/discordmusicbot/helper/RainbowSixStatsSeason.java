package de.pheromir.discordmusicbot.helper;


public class RainbowSixStatsSeason {
	
	private int id;
	private int wins;
	private int losses;
	private int abandons;
	private String region;
	private double rating;
	private double nextRating;
	private double prevRating;
	private int rankId;
	
	
	public RainbowSixStatsSeason(int id, int wins, int losses, int abandons, String region, double rating, double nextRating, double prevRating, int rankId) {
		this.id = id;
		this.wins = wins;
		this.losses = losses;
		this.abandons = abandons;
		this.region = region;
		this.rating = rating;
		this.nextRating = nextRating;
		this.prevRating = prevRating;
		this.rankId = rankId;
	}
	
	public int getId() {
		return id;
	}
	
	public int getWins() {
		return wins;
	}
	
	public int getLosses() {
		return losses;
	}
	
	public int getAbandons() {
		return abandons;
	}
	
	public String getRegion() {
		switch(region) {
			case "emea":
				return "Europa";
			case "apac":
				return "Asien";
			case "ncsa":
				return "Amerika";
			default:
				return "Unbekannt";
		}
	}
	
	public double getRating() {
		return rating;
	}
	
	public double getNextRating() {
		return nextRating;
	}
	
	public double getPreviousRating() {
		return prevRating;
	}
	
	public String getRank() {
		switch(rankId) {
			case 1:
				return "Kupfer 4";
			case 2:
				return "Kupfer 3";
			case 3:
				return "Kupfer 2";
			case 4:
				return "Kupfer 1";
			case 5:
				return "Bronze 4";
			case 6:
				return "Bronze 3";
			case 7:
				return "Bronze 2";
			case 8:
				return "Bronze 1";
			case 9:
				return "Silber 4";
			case 10:
				return "Silber 3";
			case 11:
				return "Silber 2";
			case 12:
				return "Silber 1";
			case 13:
				return "Gold 4";
			case 14:
				return "Gold 3";
			case 15:
				return "Gold 2";
			case 16:
				return "Gold 1";
			case 17:
				return "Platin 3";
			case 18:
				return "Platin 2";
			case 19:
				return "Platin 1";
			case 20:
				return "Diamant";
			default:
				return "Unranked";
		}
	}
	
	public String getSeason() {
		switch(id) {
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
			default:
				return "Unknown";
		}
	}

}
