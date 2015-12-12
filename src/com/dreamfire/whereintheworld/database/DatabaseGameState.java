package com.dreamfire.whereintheworld.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseGameState extends SQLiteAssetHelper
{
	private final static String DATABASE_NAME = "WhereInTheWorldGameState.db";
	private final static int DATABASE_VERSION = 39;

	private SQLiteDatabase database;
	private Cursor cursor;

	// Constructor
	public DatabaseGameState(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		//setForcedUpgrade();  // TODO : remove
	}

	/*@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		switch(oldVersion)
		{
			case 35 :
				database.execSQL("ALTER TABLE Game_Values ADD COLUMN free_play_unlocked TEXT DEFAULT 0");
			//case 36 :
				// upgrade 36 to 37
				break;
			default :
				throw new IllegalStateException("onUpgrade() with unknown newVersion : " + newVersion);
		}

		// leave breaks out so it falls through and updates all
	}*/



	/* ==============================================================================================================================================================
	/																Locations_State Table
	/ =============================================================================================================================================================*/

	// Sets the data of 6 locations for a particular difficulty, category and level
	public void resetLocationsForCurrentLevel(Context context)
	{

		// Add location guids to Locations_State database
		try
		{
			// Get 6 random locations
			RowLocationData[] locations = CommonStuff.databaseLocations.getRandomLocationsFromSingleTable(
					CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], 6);

			databaseOpen(true);

			// Save 6 random locations
			for (int i = 0; i < locations.length; i++)
			{
				ContentValues values = new ContentValues();
				values.put("location_guid", locations[i].guid);
				values.put("location_current_score", "0");
				values.put("location_completed", "0");

				String whereClause = String.format("category_name='%s' AND difficulty_name='%s' AND level_name='%s' AND location_name='%s'",
						CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], CommonStuff.gameState.currentDifficulty, CommonStuff.gameState.currentLevel, GeneralConstants.availableLocations[i]);

				database.update("Locations_State", values, whereClause, null);
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	// Get the guid, currentScore, completed for each location of the current level
	public RowLocationState[] getLocationsDataForCurrentLevel()
	{

		RowLocationState[] rows = new RowLocationState[6];

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery(String.format(
					"SELECT location_guid, location_current_score, location_completed FROM Locations_State WHERE category_name='%s' AND difficulty_name='%s' AND level_name='%s'",
					CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], CommonStuff.gameState.currentDifficulty, CommonStuff.gameState.currentLevel), null);

			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++)
			{
				rows[i] = new RowLocationState
				(
					cursor.getString(cursor.getColumnIndex("location_guid")),
					Long.parseLong(cursor.getString(cursor.getColumnIndex("location_current_score"))),
					cursor.getString(cursor.getColumnIndex("location_completed")).equals("0") ? false : true
				);

				cursor.moveToNext();
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return rows;
	}


	// Save single location score and completed
	public void updateLocationScoreAndCompleted(long score)
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("location_current_score", score);
			values.put("location_completed", "1");

			String whereClause = String.format("category_name='%s' AND difficulty_name='%s' AND level_name='%s' AND location_name='%s'",
					CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], CommonStuff.gameState.currentDifficulty, CommonStuff.gameState.currentLevel,
					GeneralConstants.availableLocations[CommonStuff.gameState.currentLocationIndex]);

			database.update("Locations_State", values, whereClause, null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}



	/* ==============================================================================================================================================================
	/																Levels_State Table
	/ =============================================================================================================================================================*/

	// Get the bestScore and completed for each level of the current difficulty/category
	public RowLevelState[] getLevelDataForCurrentCategory()
	{

		RowLevelState[] rows = new RowLevelState[5];

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery(String.format(
					"SELECT level_best_score, level_passed, level_completed FROM Levels_State WHERE category_name='%s' AND difficulty_name='%s'",
					CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], CommonStuff.gameState.currentDifficulty), null);

			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++)
			{
				rows[i] = new RowLevelState
				(
					Long.parseLong(cursor.getString(cursor.getColumnIndex("level_best_score"))),
					cursor.getString(cursor.getColumnIndex("level_passed")).equals("0") ? false : true,
					cursor.getString(cursor.getColumnIndex("level_completed")).equals("0") ? false : true
				);

				cursor.moveToNext();
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return rows;
	}

	// Get number of levels completed for each category
	public int[][] getLevelsCompletedForAllDifficultiesAndCategories()
	{
		int[][] levelsCompleted = new int[4][CommonStuff.gameState.categoryNames.length];

		databaseOpen(false);
		try
		{
			for (int i = 0; i < 4; i++)  // difficulties
			{
				for (int j = 0; j < CommonStuff.gameState.categoryNames.length; j++)  // categories
				{
					cursor = database.rawQuery(String.format(
							"SELECT level_completed FROM Levels_State WHERE category_name='%s' AND difficulty_name='%s'",
							CommonStuff.gameState.categoryNames[j], GeneralConstants.availableDifficulties[i]), null);

					cursor.moveToFirst();
					for (int k = 0; k < cursor.getCount(); k++)
					{
						levelsCompleted[i][j] += cursor.getInt(cursor.getColumnIndex("level_completed"));
						cursor.moveToNext();
					}
				}
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return levelsCompleted;
	}


	// Update the bestScore
	public boolean updateLevelBestScore(long score)
	{

		// check if best_score needs to be updated
		boolean newBestScore = false;
		RowLevelState[] rows = getLevelDataForCurrentCategory();

		if (score > rows[CommonStuff.gameState.currentLevelIndex].bestScore)
		{
			newBestScore = true;

			databaseOpen(true);
			try
			{
				ContentValues values = new ContentValues();
				values.put("level_best_score", score);

				String whereClause = String.format("category_name='%s' AND difficulty_name='%s' AND level_name='%s'",
						CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], CommonStuff.gameState.currentDifficulty, CommonStuff.gameState.currentLevel);

				database.update("Levels_State", values, whereClause, null);

				database.setTransactionSuccessful();
			}
			catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
		}

		return newBestScore;
	}


	// Set level passed (ie. some locations completed, but score enough to pass level)
	public void setLevelPassed()
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("level_passed", "1");

			String whereClause = String.format("category_name='%s' AND difficulty_name='%s' AND level_name='%s'",
					CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], CommonStuff.gameState.currentDifficulty, CommonStuff.gameState.currentLevel);

			database.update("Levels_State", values, whereClause, null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	// Set level completed (ie. all locations completed)
	public void setLevelCompleted()
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("level_completed", "1");

			String whereClause = String.format("category_name='%s' AND difficulty_name='%s' AND level_name='%s'",
					CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], CommonStuff.gameState.currentDifficulty, CommonStuff.gameState.currentLevel);

			database.update("Levels_State", values, whereClause, null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}

	/* ==============================================================================================================================================================
	/																Categories_State Table
	/ =============================================================================================================================================================*/

	// Get completed values for all categories for a single difficulty
	public RowCategoryState[] getCategoriesCompleted()
	{

		RowCategoryState[] rows = new RowCategoryState[CommonStuff.gameState.categoryNames.length];

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery(String.format(
					"SELECT category_locked, category_completed FROM Categories_State WHERE difficulty_name='%s'", CommonStuff.gameState.currentDifficulty), null);
			cursor.moveToFirst();

			for (int i = 0; i < CommonStuff.gameState.categoryNames.length; i++)
			{
				rows[i] = new RowCategoryState
				(
					cursor.getString(cursor.getColumnIndex("category_locked")).equals("0") ? false : true,
					cursor.getString(cursor.getColumnIndex("category_completed")).equals("0") ? false : true
				);

				cursor.moveToNext();
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return rows;
	}


	// Set a category as completed
	public void setCategoryCompleted(int index)
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("category_completed", "1");

			String whereClause = String.format("category_name='%s' AND difficulty_name='%s'",
					CommonStuff.gameState.categoryNames[index], CommonStuff.gameState.currentDifficulty);

			database.update("Categories_State", values, whereClause, null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	// Update the completed value of a category
	public void purchaseCatagory(int index)
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("category_locked", "0");

			String whereClause = String.format("category_name='%s' AND difficulty_name='%s'",
					CommonStuff.gameState.categoryNames[index], CommonStuff.gameState.currentDifficulty);

			database.update("Categories_State", values, whereClause, null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}

	/* ==============================================================================================================================================================
	/																Difficulty_State Table
	/ =============================================================================================================================================================*/

	// Get completed value for all difficulties
	public RowDifficultyState[] getDifficultyStateData()
	{

		RowDifficultyState[] rows = new RowDifficultyState[4];

		databaseOpen(false);
		try
		{
			for (int i = 0; i < GeneralConstants.availableDifficulties.length; i++)
			{
				cursor = database.rawQuery(String.format(
						"SELECT difficulty_completed, num_categories_per_difficulty FROM Difficulties_State WHERE difficulty_name='%s'",
						GeneralConstants.availableDifficulties[i]), null);
				cursor.moveToFirst();

				rows[i] = new RowDifficultyState
				(
					cursor.getString(cursor.getColumnIndex("difficulty_completed")).equals("0") ? false : true,
					cursor.getInt(cursor.getColumnIndex("num_categories_per_difficulty"))
				);
			}
			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return rows;
	}



	// Update the completed value of a difficulty
	public void updateDifficultyCompleted()
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("difficulty_completed", "1");

			String whereClause = String.format("difficulty_name='%s'", CommonStuff.gameState.currentDifficulty);

			database.update("Difficulties_State", values, whereClause, null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}





	/* ==============================================================================================================================================================
	/																Categories_Data Table
	/ =============================================================================================================================================================*/

	// Returns the category images from the Catagories_Data table
	public ArrayList<RowCategoryData> getCategoryData()
	{

		ArrayList<RowCategoryData> rows = new ArrayList<RowCategoryData>();

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery("SELECT category_name, row_count FROM Categories_Data", null);
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++)
			{
				rows.add(new RowCategoryData(cursor.getString(cursor.getColumnIndex("category_name")), cursor.getInt(cursor.getColumnIndex("row_count"))));
				cursor.moveToNext();
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return rows;
	}


	/* ==============================================================================================================================================================
	/																Categories_Images Table
	/ =============================================================================================================================================================*/

	// Returns a single category image
	public byte[] getSingleImageFromCategoryData(String categoryName)
	{

		byte[] data = null;

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery(String.format("SELECT category_image FROM Category_Images WHERE category_name='%s'", categoryName), null);
			cursor.moveToFirst();

			data = cursor.getBlob(cursor.getColumnIndex("category_image"));

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return data;
	}

	/* ==============================================================================================================================================================
	/																Free Play unlocked
	/ =============================================================================================================================================================*/

	// Returns true if free play is unlocked
	public boolean getIfFreePlayUnlocked()
	{

		boolean freePlayUnlocked = false;

		// check Game_State table
		databaseOpen(false);
		try
		{
			cursor = database.rawQuery("SELECT free_play_unlocked FROM Game_Values WHERE id=1", null);
			cursor.moveToFirst();

			String temp = cursor.getString(cursor.getColumnIndex("free_play_unlocked"));
			freePlayUnlocked = temp.equals("0") ? false : true;

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		if (freePlayUnlocked == true)
			return true;


		// Check Level_State table
		databaseOpen(false);
		try
		{
			int numCategories = getDifficultyStateData()[1].numCategoriesPerDifficulty; // number of categories in Normal mode
			ArrayList<String> categoryNames = new ArrayList<String>();

			for (int i = 0; i < numCategories; i++)
				categoryNames.add(CommonStuff.gameState.categoryNames[i]);

			cursor = database.rawQuery(String.format("SELECT category_name, level_passed FROM Levels_State WHERE difficulty_name='%s'",
					GeneralConstants.availableDifficulties[1]), null);  // Check if all difficulty_2 rows = 1
			cursor.moveToFirst();

			int levelCount = 0;
			int passedCount = 0;
			for (int i = 0; i < cursor.getCount(); i++)
			{
				String categoryName = cursor.getString(cursor.getColumnIndex("category_name"));

				if (categoryNames.contains(categoryName) == true)
				{
					levelCount++;
					if (cursor.getString(cursor.getColumnIndex("level_passed")).equals("1")) passedCount++;
					cursor.moveToNext();

				}
			}

			if (levelCount == passedCount)
				freePlayUnlocked = true;

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		if (freePlayUnlocked == true)
			setFreePlayUnlocked();

		return freePlayUnlocked;
	}


	// Set free play unlocked
	private void setFreePlayUnlocked()
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("free_play_unlocked", "1");

			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	/* ==============================================================================================================================================================
	/																Expert unlocked
	/ =============================================================================================================================================================*/

	// Returns true if expert is unlocked
	public boolean getIfExpertUnlocked()
	{

		boolean expertUnlocked = false;

		// check Game_State table
		databaseOpen(false);
		try
		{
			cursor = database.rawQuery("SELECT expert_unlocked FROM Game_Values WHERE id=1", null);
			cursor.moveToFirst();

			String temp = cursor.getString(cursor.getColumnIndex("expert_unlocked"));
			expertUnlocked = temp.equals("0") ? false : true;

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		if (expertUnlocked == true)
			return true;


		// Check Level_State table
		databaseOpen(false);
		try
		{
			int numCategories = getDifficultyStateData()[2].numCategoriesPerDifficulty; // number of categories in Hard mode
			ArrayList<String> categoryNames = new ArrayList<String>();

			for (int i = 0; i < numCategories; i++)
				categoryNames.add(CommonStuff.gameState.categoryNames[i]);

			cursor = database.rawQuery(String.format("SELECT category_name, level_passed FROM Levels_State WHERE difficulty_name='%s'",
					GeneralConstants.availableDifficulties[2]), null);  // Check if all difficulty_3 rows = 1
			cursor.moveToFirst();

			int levelCount = 0;
			int passedCount = 0;
			for (int i = 0; i < cursor.getCount(); i++)
			{
				String categoryName = cursor.getString(cursor.getColumnIndex("category_name"));

				if (categoryNames.contains(categoryName) == true)
				{
					levelCount++;
					if (cursor.getString(cursor.getColumnIndex("level_passed")).equals("1")) passedCount++;
					cursor.moveToNext();

				}
			}

			if (levelCount == passedCount)
				expertUnlocked = true;

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		if (expertUnlocked == true)
			setExpertUnlocked();

		return expertUnlocked;
	}


	// Set expert unlocked
	private void setExpertUnlocked()
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("expert_unlocked", "1");

			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	/* ==============================================================================================================================================================
	/																Flag_Images Table
	/ =============================================================================================================================================================*/

	// Returns a flag image from the Flag_Images table
	public byte[] getFlagImage(String countryName)
	{

		byte[] image = null;

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery(String.format("SELECT flag_image FROM Flag_Images WHERE country_name='%s'", countryName), null);
			cursor.moveToFirst();

			image = cursor.getBlob(cursor.getColumnIndex("flag_image"));

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return image;
	}




	/* ==============================================================================================================================================================
	/																	Tokens
	/ =============================================================================================================================================================*/

	// Get the number of tokens
	public long getNumberOfTokens()
	{

		long tokens = 0;

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery("SELECT tokens FROM Game_Values WHERE id='1'", null);
			cursor.moveToFirst();

			tokens = cursor.getLong(cursor.getColumnIndex("tokens"));

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return tokens;
	}


	// Set the number of tokens
	public void updateNumberOfTokens(long numTokens)
	{

		long existingTokens = getNumberOfTokens();

		existingTokens += numTokens;

		databaseOpen(true);

		try
		{
			ContentValues values = new ContentValues();
			values.put("tokens", existingTokens);

			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	// set the number of tokens
	public void setTokensExplicitly(long tokens)
	{

		databaseOpen(true);

		try
		{
			ContentValues values = new ContentValues();
			values.put("tokens", tokens);

			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	// Get if infinite tokens
	public boolean getInfiniteTokens()
	{

		boolean infiniteTokens = false;

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery("SELECT infinite_tokens FROM Game_Values WHERE id='1'", null);
			cursor.moveToFirst();

			infiniteTokens = cursor.getString(cursor.getColumnIndex("infinite_tokens")).equals("0") ? false : true;

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return infiniteTokens;
	}


	// set infinite tokens
	public void setInfiniteTokens(boolean infiniteTokens)
	{

		databaseOpen(true);

		try
		{
			ContentValues values = new ContentValues();
			values.put("infinite_tokens", infiniteTokens == true ? "1" : "0");

			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	/* ==============================================================================================================================================================
	/																	Other
	/ =============================================================================================================================================================*/

	// Get is first run
	public boolean getIsFirstRun()
	{

		boolean isFirstRun = false;

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery("SELECT first_run FROM Game_Values WHERE id='1'", null);
			cursor.moveToFirst();

			isFirstRun = cursor.getString(cursor.getColumnIndex("first_run")).equals("1") ? true : false;

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		if (isFirstRun == true)
		{
			setFirstRunToFalse();
		}

		return isFirstRun;
	}


	// Set the first run variable to false
	private void setFirstRunToFalse()
	{

		databaseOpen(true);
		try
		{
			ContentValues values = new ContentValues();
			values.put("first_run", "0");

			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

	}


	// Get show advertisements
	public boolean getShowAds()
	{

		boolean showAds = false;

		databaseOpen(false);
		try
		{
			cursor = database.rawQuery("SELECT show_ads FROM Game_Values WHERE id='1'", null);
			cursor.moveToFirst();

			showAds = cursor.getString(cursor.getColumnIndex("show_ads")).equals("0") ? false : true;

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return showAds;
	}


	// set show advertisements
	public void setShowAds(boolean showAds)
	{

		databaseOpen(true);

		try
		{
			ContentValues values = new ContentValues();
			values.put("show_ads", "0");

			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	/* ==============================================================================================================================================================
	/																	Open and Close database
	/ =============================================================================================================================================================*/

	// Open database
	private void databaseOpen(boolean isWritable)
	{

		if (isWritable == true) database = getWritableDatabase(); else database = getReadableDatabase();
		database.beginTransaction();
	}

	// Close database
	private void databaseClose()
	{

		if (cursor != null && cursor.isClosed() == false)
			cursor.close();

		if (database.isOpen() == true && database.inTransaction() == true)
			database.endTransaction();
	}

	// Properly close database
	public void databaseCloseProperly()
	{

		if (cursor != null && cursor.isClosed() == false)
			cursor.close();

		if (database != null && database.isOpen() == true && database.inTransaction() == false)
			database.close();
	}



	/* ==============================================================================================================================================================
	/																Reset Game State Database
	/ =============================================================================================================================================================*/

	// Resets all the values in the Locations_State database
	public void resetGameStateDatabase()
	{

		databaseOpen(true);

		try
		{
			ContentValues values = new ContentValues();
			values.put("difficulty_completed", "0");
			database.update("Difficulties_State", values, null, null);


			values = new ContentValues();
			values.put("category_locked", "1");
			values.put("category_completed", "0");
			database.update("Categories_State", values, null, null);

			values = new ContentValues();
			values.put("category_locked", "0");
			database.update("Categories_State", values, "id<=4", null);


			values = new ContentValues();
			values.put("level_best_score", "0");
			values.put("level_passed", "0");
			values.put("level_completed", "0");
			database.update("Levels_State", values, null, null);


			values = new ContentValues();
			values.put("location_guid", "0");
			values.put("location_current_score", "0");
			values.put("location_completed", "0");
			database.update("Locations_State", values, null, null);


			values = new ContentValues();
			values.put("first_run", "1");
			values.put("expert_unlocked", "0");
			values.put("free_play_unlocked", "0");
			database.update("Game_Values", values, "id=1", null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}

}
