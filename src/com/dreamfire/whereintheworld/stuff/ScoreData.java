package com.dreamfire.whereintheworld.stuff;

public class ScoreData
{
	public String locationName;
	public double distance;
	public String timeTaken;
	public String bonusTokens;
	public String distanceScore;
	public String distanceBonus;
	public String timeBonus;
	public long totalScore;
	public String wikiLink;

	public ScoreData(String locationName, double distance, String timeTaken, String bonusTokens, String distanceScore,
			String distanceBonus, String timeBonus, long totalScore, String wikiLink)
	{
		this.locationName = locationName;
		this.distance = distance;
		this.timeTaken = timeTaken;
		this.bonusTokens = bonusTokens;
		this.distanceScore = distanceScore;
		this.distanceBonus = distanceBonus;
		this.timeBonus = timeBonus;
		this.totalScore = totalScore;
		this.wikiLink = wikiLink;
	}
}
