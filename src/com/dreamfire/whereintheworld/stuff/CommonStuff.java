package com.dreamfire.whereintheworld.stuff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.database.DatabaseGameState;
import com.dreamfire.whereintheworld.database.DatabaseLocations;
import com.dreamfire.whereintheworld.database.RowCategoryData;
import com.dreamfire.whereintheworld.database.RowLocationState;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class CommonStuff
{
	public static GameState gameState;

	// Databases
	public static DatabaseLocations databaseLocations = null;
	public static DatabaseGameState databaseGameState = null;

	public static GoogleAnalytics analytics;
	public static Tracker tracker;

	public static MediaPlayer mediaPlayer;
	public static int trackPosition = 0;
	public static int currentTrack = 0;

	/* ==============================================================================================================================================================
	/																	Pre Game Setup
	/ =============================================================================================================================================================*/

	// This is run from Categories Activity when the game is started
	public static void preGameSetup(Context context)
	{
		// Analytics
		analytics = GoogleAnalytics.getInstance(context);
		analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
		tracker = analytics.newTracker(GeneralConstants.ANALYTICS_PROPERTY_ID);
		tracker.enableAdvertisingIdCollection(true);

		// Databases
		databaseLocations = new DatabaseLocations(context);
		databaseGameState = new DatabaseGameState(context);

		// First run
		if (databaseGameState.getIsFirstRun() == true)
		{
			CommonStuff.gameState.isFirstRun = true;
			databaseGameState.setTokensExplicitly(GeneralConstants.startingNumberOfTokens);
		}
		else
			CommonStuff.gameState.isFirstRun = false;


		// Tokens
		CommonStuff.gameState.infiniteTokens = databaseGameState.getInfiniteTokens();


		// Category data
		ArrayList<RowCategoryData> rows = databaseGameState.getCategoryData();

		CommonStuff.gameState.categoryNames = new String[rows.size()];
		CommonStuff.gameState.categoryRowCounts = new long[rows.size()];

		for (int i = 0; i < rows.size(); i++)
		{
			CommonStuff.gameState.categoryNames[i] = rows.get(i).name;
			CommonStuff.gameState.categoryRowCounts[i] = rows.get(i).rowCount;
		}

		CommonStuff.gameState.currentCategoryName = new String[GeneralConstants.availableDifficulties.length];
		CommonStuff.gameState.currentCategoryIndex = new int[GeneralConstants.availableDifficulties.length];

		// Some prelim values
		CommonStuff.gameState.currentDifficulty = GeneralConstants.availableDifficulties[0];
		CommonStuff.gameState.currentDifficultyIndex = 0;
		for (int i = 0; i < CommonStuff.gameState.currentCategoryName.length; i++) CommonStuff.gameState.currentCategoryName[i] = CommonStuff.gameState.categoryNames[0];
		for (int i = 0; i < CommonStuff.gameState.currentCategoryIndex.length; i++) CommonStuff.gameState.currentCategoryIndex[i] = 0;
		CommonStuff.gameState.currentLevel = GeneralConstants.availableLevels[0];
		CommonStuff.gameState.currentLevelIndex = 0;
	}




	// Check if any of the static stuff is null
	public static void isPreGameSetupNull(Context context)
	{
		if (databaseLocations == null || databaseGameState == null || CommonStuff.gameState.categoryNames == null || CommonStuff.gameState.categoryRowCounts == null)
			preGameSetup(context);
	}


	/* ==============================================================================================================================================================
	/																	Set Index and database stuff
	/ =============================================================================================================================================================*/

	// Set the currentDifficultyIndex
	public static void setCurrentDifficultyIndex(int index)
	{

		CommonStuff.gameState.currentDifficultyIndex = index;
		CommonStuff.gameState.currentDifficulty = GeneralConstants.availableDifficulties[index];
	}


	// Set the currentCategoryIndex
	public static void setCurrentCategoryIndex(Context context, int index)
	{
		isPreGameSetupNull(context);

		CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex] = index;
		CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex] = CommonStuff.gameState.categoryNames[index];
	}


	// Set the currentLevelIndex
	public static void setCurrentLevelIndex(int index)
	{

		CommonStuff.gameState.currentLevelIndex = index;
		CommonStuff.gameState.currentLevel = GeneralConstants.availableLevels[index];
	}


	// Set the currentLocationIndex and Row variables
	public static void setCurrentLocationIndex(int index)
	{

		CommonStuff.gameState.currentLocationRow = CommonStuff.gameState.currentLocations[index];
		CommonStuff.gameState.currentLocationIndex = index;
	}


	// Sets the currentLocations variable
	public static void setCurrentLocations()
	{

		RowLocationState[] rows = databaseGameState.getLocationsDataForCurrentLevel();

		String[] guids = new String[rows.length];
		for (int i = 0; i < rows.length; i++)
			guids[i] = rows[i].guid;

		CommonStuff.gameState.currentLocations = databaseLocations.getLocationsFromTableByGuid(guids);
	}


	// Set the current hint
	public static void setCurrentHint(int index)
	{

		if (CommonStuff.gameState.currentHints.size() > 0)
			CommonStuff.gameState.currentSelectedHint = CommonStuff.gameState.currentHints.get(index);
	}


	/* ==============================================================================================================================================================
	/																	Internet
	/ =============================================================================================================================================================*/

	// Check if connected to internet
	public static boolean isInternetConnected(Context context)
	{

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}


	/* ===============================================================================================================================================================
	/																	Hints
	/ ==============================================================================================================================================================*/

	// Purchase a hint
	public static boolean purchaseHint(int index)
	{

		setCurrentHint(index);

		if (CommonStuff.gameState.currentHints.get(index).purchased == true) return true;

		if (gameState.infiniteTokens == false)
		{
			long currentTokens = CommonStuff.gameState.freePlayModeOn == false ? databaseGameState.getNumberOfTokens() : CommonStuff.gameState.currentFreePlayTokens;
			if (currentTokens < CommonStuff.gameState.currentHints.get(index).tokenCost) return false;
		}

		CommonStuff.gameState.currentHints.get(index).purchased = true;

		if (CommonStuff.gameState.freePlayModeOn == false)
		{
			if (gameState.infiniteTokens == false)
				databaseGameState.updateNumberOfTokens(-CommonStuff.gameState.currentHints.get(index).tokenCost);
		}
		else
			CommonStuff.gameState.currentFreePlayTokens -= CommonStuff.gameState.currentHints.get(index).tokenCost;

		return true;
	}

	// Set Hint used
	public static void setHintUsed(int name)
	{

		for (int i = 0; i < CommonStuff.gameState.currentHints.size(); i++)
			if (CommonStuff.gameState.currentHints.get(i).id == name)
			{
				CommonStuff.gameState.currentHints.get(i).used = true;
				break;
			}
	}


	/* ===============================================================================================================================================================
	/																	Sound
	/ ==============================================================================================================================================================*/

	// Plays the sound for clicking on something
	public static void playClickSound(Context context)
	{
		if (mediaPlayer != null)
			mediaPlayer.release();

		mediaPlayer = MediaPlayer.create(context, R.raw.button_49);
		mediaPlayer.setVolume(10, 10);
		mediaPlayer.start();
	}



	/* ===============================================================================================================================================================
	/																	Other Methods
	/ ==============================================================================================================================================================*/


	// Returns the sum of all the current scores for this level
	public static long getCurrentTotalScore()
	{

		long totalScore = 0;

		if (CommonStuff.gameState.locationStateData[0] != null)
		{
			for (int i = 0; i < CommonStuff.gameState.locationStateData.length; i++)
				totalScore += CommonStuff.gameState.locationStateData[i].currentScore;
		}

		return totalScore;
	}

	// Start an activity with a particular layout
	public static void runActivityWithLayout(Context context, Class<?> aClass, int layoutId, int intentFlag)
	{

		Intent intent = new Intent(context, aClass);
		Bundle bundle = new Bundle();
		bundle.putInt("layout", layoutId);
		intent.putExtras(bundle);
		if (intentFlag != GeneralConstants.NO_INTENT_FLAG) intent.setFlags(intentFlag);
		context.startActivity(intent);
		((Activity)context).overridePendingTransition(0, 0);
	}


	// Start an activity
	public static void runActivity(Context context, Class<?> aClass, int intentFlag)
	{

		Intent intent = new Intent(context, aClass);
		if (intentFlag != GeneralConstants.NO_INTENT_FLAG) intent.setFlags(intentFlag);
		context.startActivity(intent);
		((Activity)context).overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
	}


	// Displays a toast message box thing
	public static void showToast(Activity activity, String message, boolean toastLengthShort)
	{

		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_custom, (ViewGroup) activity.findViewById(R.id.toast_custom_layout));
		MyTextView textView = (MyTextView) layout.findViewById(R.id.toast_custom_textview);
		textView.setText(message);

		Toast toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(toastLengthShort == true ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
		toast.setView(layout);

		toast.show();
	}


	// Displays Toast for tutorial
	public static void showToastTutorialNotAvailable(Activity activity)
	{

		showToast(activity, activity.getResources().getString(R.string.string_tutorial_not_available), true);
	}


	// Log to logcat
	public static void myLog(String message)
	{
		Log.d("**** MYDEBUG ****", message);
	}

	// Log to logcat
	public static void myLog(String message, Throwable t)
	{
		Log.d("**** MYDEBUG ****", message, t);
	}


	// Get screen width/height
	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public static int[] getScreenWidthHeight(Context context)
	{

		int[] widthAndHeight = new int[2];

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		if (android.os.Build.VERSION.SDK_INT >= 13)
		{
			Point size = new Point();
			display.getSize(size);
			widthAndHeight[0] = size.x;
			widthAndHeight[1] = size.y;
		}
		else
		{
			widthAndHeight[0] = display.getWidth();
			widthAndHeight[1] = display.getHeight();
		}

		return widthAndHeight;
	}


	// Round a number
	public static double round(double value, int places)
	{

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	// Add commas to a string every 3rd number (123456789.1234 -> 123,456,789.1234)
	public static String addCommasToStringNumber(String number)
	{

		double num = 0;
		DecimalFormat formatter;

		try
		{
			num = Double.parseDouble(number);
			formatter = new DecimalFormat("#,###");
		}
		catch (Exception e)
		{
			return number;
		}

		return formatter.format(num);
	}

}
