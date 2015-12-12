package com.dreamfire.whereintheworld.stuff;

import android.os.AsyncTask;


public class ScoreClass
{
	// Calculates the score for a single accepted guess for a single location
	public ScoreData calculateScore(double distance, long time, String timeString)
	{

		int bonusTokens = 0;
		long distanceScore = 0;
		long distanceBonus = 0;
		long timeBonus = 0;
		long totalScore = 0;


		// Calculate Regular points
		if (distance >= 18000)
			distanceScore = 0;
		else if (distance < 18000 && distance > 100)
			distanceScore = (long) (50 - ((distance / 18000) * 50));
		else
			distanceScore = 50;



		// Calculate Distance bonus
		if (distance >= 18000)
			distanceBonus = 5;
		else if (distance >= 9000 && distance < 18000)
			distanceBonus = 0;
		else if (distance >= 1000 && distance < 9000)
			distanceBonus = 1;
		else if (distance >= 500 && distance < 1000)
		{
			bonusTokens = 1;
			distanceBonus = 2;
		}
		else if (distance >= 200 && distance < 500)
		{
			bonusTokens = 3;
			distanceBonus = 3;
		}
		else if (distance >= 50 && distance < 200)
		{
			bonusTokens = 6;
			distanceBonus = 10;
		}
		else if (distance >= 10 && distance < 50)
		{
			bonusTokens = 8;
			distanceBonus = 20;
		}
		else if (distance >= 1 && distance < 10)
		{
			bonusTokens = 10;
			distanceBonus = 30;
		}
		else if (distance >= 0.5 && distance < 1)
		{
			bonusTokens = 15;
			distanceBonus = 35;
		}
		else if (distance > 0.1 && distance < 0.5)
		{
			bonusTokens = 20;
			distanceBonus = 40;
		}
		else if (distance <= 0.1)
		{
			bonusTokens = 25;
			distanceBonus = 45;
		}


		// Calculate Time bonus
		if (time >= 240)
			timeBonus = 0;
		else if (time >= 180 && time < 240)
			timeBonus = 1;
		else if (time >= 90 && time < 180)
			timeBonus = 2;
		else if (time >= 30 && time < 90)
			timeBonus = 3;
		else if (time >= 15 && time < 30)
		{
			bonusTokens += 1;
			timeBonus = 4;
		}
		else if (time < 15)
		{
			bonusTokens += 2;
			timeBonus = 5;
		}


		// Tokens for finishing location
		if (CommonStuff.gameState.currentDifficultyIndex == 0)
			bonusTokens += 2;
		else
			bonusTokens += 5;


		// Total points
		totalScore = distanceScore + distanceBonus + timeBonus;


		DatabaseTask task = new DatabaseTask();
		task.execute(bonusTokens);

		ScoreData scoreData = new ScoreData
		(
			CommonStuff.gameState.currentLocationRow.name,
			distance,
			timeString,
			bonusTokens + "",
			distanceScore + "",
			distanceBonus + "",
			timeBonus + "",
			totalScore,
			CommonStuff.gameState.currentLocationRow.wikiurl
		);

		return scoreData;
	}



	// Async task used to add bonus tokens
	private class DatabaseTask extends AsyncTask<Integer, Void, Void>
	{
		@Override
		protected Void doInBackground(Integer... params)
		{

			CommonStuff.databaseGameState.updateNumberOfTokens(params[0]);

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
		}
	}
}
