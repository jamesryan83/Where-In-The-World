package com.dreamfire.whereintheworld.constants;

public class GeneralConstants
{
	public static final String ANALYTICS_PROPERTY_ID = "NOT TELLING";

	public static final String DIALOG_MSGBOX_TAG = "dialog_msgbox";
	public static final int NO_INTENT_FLAG = -122;
	public static final int INFINITE_TOKENS = -123;
	public static final String GAME_PREFERENCES = "GAME_SETTINGS"; // for shared preferences
	public static final String FONT_NAME = "fonts/fabrica_modified.ttf";
	public static final int ANIMATION_PAUSE_DURATION = 100;
	public static final double KM_TO_MILES = 0.621371192;

	public static final String[] availableDifficulties = new String[]{"difficulty_1", "difficulty_2", "difficulty_3", "difficulty_4"};
	public static final String[] availableLevels = new String[]{"level_1", "level_2", "level_3", "level_4", "level_5"};
	public static final String[] availableLocations = new String[]{"location_1", "location_2", "location_3", "location_4", "location_5", "location_6"};

	public static final int[][] levelRequiredScores = new int[][]
	{
		{100, 150, 200, 250, 300}, // easy
		{200, 250, 300, 350, 400}, // normal
		{300, 350, 400, 450, 500}, // hard
		{350, 400, 450, 500, 550}  // expert
	};


	// Token stuff
	public static final int startingNumberOfTokens = 75;
	public static final int[] tokenBonusLevelComplete = new int[] { 20, 30, 40, 50 }; 	 // bonus tokens per level for each difficulty
	public static final int[] tokenBonusLevelCompleteOngoing = new int[] { 10, 15, 20, 25 }; 	 // bonus tokens per level for each difficulty after first time finished
	public static final int tokenBonusCategoryComplete = 50;  // bonus tokens for completing a category
	public static final int tokenBonusDifficultyComplete = 75;  // bonus tokens for completing a difficulty
	public static final int tokenBonusEverythingComplete = 10000;  // bonus tokens for completing all difficulties
	public static final int tokenCostPerCategory = 30;
	public static final int startingTokensFreePlay = 50;
	public static final int tutorialBonusTokens = 30;  // tokens for finishing tutorial

	public static final int tokensHintCostBest = 15;
	public static final int tokensHintCostMedium = 10;
	public static final int tokensHintCostWorst = 5;

	public static final String SKU_TOKENS_1000 = "tokens_1000";
	public static final String SKU_TOKENS_5500 = "tokens_5500";
	public static final String SKU_TOKENS_11000 = "tokens_11000";
	public static final String SKU_TOKENS_INFINITE = "tokens_infinite";
	public static final int RC_REQUEST = 10101;
	public static final String inAppBillingKey = "NOT TELLING";

	public static int[] availableTokens = new int[]{1000, 5500, 11000};


	// Text tags and other things
	public static String startFontTagOrange = "<font color='#E67E22'>";
	public static String startFontTagBlue = "<font color='#00B1DE'>";
	public static String startFontTagWhite = "<font color='#FFFFFF'>";
	public static String startFontTagRed = "<font color='#FF6B68'>";
	public static String endFontTag = "</font>";
	public static String breakTag = "<br />";

	public static String databaseEntryMissing = "_na_";

	public static String newLine = "\n";


}
