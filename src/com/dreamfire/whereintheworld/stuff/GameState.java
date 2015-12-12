package com.dreamfire.whereintheworld.stuff;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.database.Hint;
import com.dreamfire.whereintheworld.database.RowCategoryState;
import com.dreamfire.whereintheworld.database.RowDifficultyState;
import com.dreamfire.whereintheworld.database.RowLevelState;
import com.dreamfire.whereintheworld.database.RowLocationData;
import com.dreamfire.whereintheworld.database.RowLocationState;
import com.google.gson.Gson;

public class GameState
{
	public long currentTokens;
	public boolean infiniteTokens;
	public long currentFreePlayTokens;
	public String[] tokenPrices;

	public boolean freePlayModeOn;
	public boolean tutorialModeOn;

	public int currentMainFragment;
	public int previousMainFragment;

	public String currentDifficulty;
	public int currentDifficultyIndex;
	public String[] currentCategoryName;
	public int[] currentCategoryIndex;
	public String currentLevel;
	public int currentLevelIndex;
	public int currentLocationIndex;

	public RowLocationData currentLocationRow;
	public RowLocationData[] currentLocations;

	public Hint currentSelectedHint;
	public ArrayList<Hint> currentHints;

	public String[] categoryNames;
	public long[] categoryRowCounts;

	public RowDifficultyState[] difficultyStateData;
	public RowCategoryState[] categoryStateData;
	public RowLevelState[] levelStateData;
	public RowLocationState[] locationStateData;

	public double[] difficultyPercentComplete;

	public boolean hintUsed;
	public boolean hotColdHintIsRunning;
	public boolean interactiveHintIsRunning;
	public boolean canAddMapMarker;
	public Point mapMarkerPoint;

	public boolean expertModeUnlocked;
	public boolean freePlayModeUnlocked;
	public boolean isFirstRun;

	public boolean notificationBarEnabled;
	public boolean musicEnabled;
	public boolean lightThemeEnabled;
	public boolean kilometresEnabled;

	public boolean tutorialBonusTokensGiven;
	public boolean[] tutorialDialogsSeen;


	public GameState()
	{
		currentTokens = 0;
		infiniteTokens = false;
		currentFreePlayTokens = 0;
		tokenPrices = null;

		freePlayModeOn = false;
		tutorialModeOn = false;

		currentMainFragment = 0;
		previousMainFragment = 0;

		currentDifficulty = "";
		currentDifficultyIndex = 0;
		currentCategoryName = null;
		currentCategoryIndex = null;
		currentLevel = "";
		currentLevelIndex = 0;
		currentLocationIndex = 0;

		currentLocationRow = null;
		currentLocations = null;

		currentSelectedHint = null;
		currentHints = null;

		categoryNames = null;
		categoryRowCounts = null;

		difficultyStateData = null;
		categoryStateData = null;
		levelStateData = null;
		locationStateData = null;

		difficultyPercentComplete = null;

		hintUsed = false;
		hotColdHintIsRunning = false;
		interactiveHintIsRunning = false;
		canAddMapMarker = false;
		mapMarkerPoint = null;

		expertModeUnlocked = false;
		freePlayModeUnlocked = false;
		isFirstRun = false;

		notificationBarEnabled = false;
		musicEnabled = true;
		lightThemeEnabled = true;
		kilometresEnabled = true;
	}


	// save GameState to sharedPreferences
	public void save(Context context)
	{

		SharedPreferences sharedPreferences = context.getSharedPreferences(GeneralConstants.GAME_PREFERENCES, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		String gameStateData = new Gson().toJson(this);

		//CommonStuff.writeToFile(context, gameStateData);

		editor.putString("gameStateData", gameStateData);

		editor.commit();
	}


	// load GameState from sharedPreferences
	public void load(Context context)
	{

		SharedPreferences sharedPreferences = context.getSharedPreferences(GeneralConstants.GAME_PREFERENCES, Activity.MODE_PRIVATE);

		String json = sharedPreferences.getString("gameStateData", null);

		CommonStuff.gameState = json == null ? new GameState() : new Gson().fromJson(json, GameState.class);
	}
}



