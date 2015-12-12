package com.dreamfire.whereintheworld.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.maps.model.LatLng;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseLocations extends SQLiteAssetHelper
{
	private final static String DATABASE_NAME = "WhereInTheWorldLocations.db";
	private final static int DATABASE_VERSION = 33;

	private SQLiteDatabase database;
	private Cursor cursor;

	// Constructor
	public DatabaseLocations(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		//setForcedUpgrade();   // TODO : remove
	}



	/* ==============================================================================================================================================================
	/																	Get Random Locations
	/ =============================================================================================================================================================*/

	// DATABASE - Returns an array of random database rows from a single category
	public RowLocationData[] getRandomLocationsFromSingleTable(String tableName, int numberOfRows)
	{

		RowLocationData[] randomRows = new RowLocationData[numberOfRows];

		databaseOpen(false);

		try
		{
			cursor = database.rawQuery("SELECT * FROM " + tableName + " ORDER BY RANDOM() LIMIT " + numberOfRows, null);
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++)
			{
				randomRows[i] = getLocationRowFromCursor();
				cursor.moveToNext();
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return randomRows;
	}


	// DATABASE - Returns an array of random database rows from all categories
	public ArrayList<RowLocationData> getRandomLocationsFromAllTables(int numberOfRows, boolean addCurrentLocation)
	{

		String[] tableNames = CommonStuff.gameState.categoryNames;
		ArrayList<RowLocationData> randomRows = new ArrayList<RowLocationData>();

		databaseOpen(false);

		int numRows = addCurrentLocation == true ? numberOfRows - 1 : numberOfRows;

		try
		{
			for (int i = 0; i < numRows; i++)
			{
				Random random = new Random();
				String randomTable = tableNames[random.nextInt(tableNames.length)];

				cursor = database.rawQuery("SELECT * FROM " + randomTable + " ORDER BY RANDOM() LIMIT " + numRows, null);
				cursor.moveToFirst();

				RowLocationData tempRow = getLocationRowFromCursor();
				if (tempRow.equals(CommonStuff.gameState.currentLocationRow)) // Check the location from the DB isn't the current location
					i--;
				else
					randomRows.add(tempRow);
			}

			if (addCurrentLocation == true)
			{
				randomRows.add(CommonStuff.gameState.currentLocationRow);
				Collections.shuffle(randomRows);
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return randomRows;
	}


	/* ==============================================================================================================================================================
	/																	Get Specific Locations
	/ =============================================================================================================================================================*/

	// DATABASE - Returns 6 LocationRows that match the guids in the Game_State database
	public RowLocationData[] getLocationsFromTableByGuid(String[] guids)
	{

		databaseOpen(false);

		RowLocationData[] locations = new RowLocationData[guids.length];
		try
		{
			for (int i = 0; i < guids.length; i++)
			{
				cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE guid='%s'",
						CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], guids[i]), null);
				cursor.moveToFirst();
				locations[i] = getLocationRowFromCursor();
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", e.getMessage(), e); databaseClose(); } finally { databaseClose(); }

		return locations;
	}


	/* ==============================================================================================================================================================
	/																	Location Data
	/ =============================================================================================================================================================*/

	// Returns a single LocationRow from the Locations database
	private RowLocationData getLocationRowFromCursor()
	{

		RowLocationData locationRow = new RowLocationData();
		locationRow.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
		locationRow.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
		locationRow.heading = cursor.getDouble(cursor.getColumnIndex("heading"));
		locationRow.tilt = cursor.getDouble(cursor.getColumnIndex("tilt"));

		locationRow.name = cursor.getString(cursor.getColumnIndex("name"));
		locationRow.description = cursor.getString(cursor.getColumnIndex("description"));
		locationRow.suburb = cursor.getString(cursor.getColumnIndex("suburb"));
		locationRow.city = cursor.getString(cursor.getColumnIndex("city"));
		locationRow.country = cursor.getString(cursor.getColumnIndex("country"));
		locationRow.wikiurl = cursor.getString(cursor.getColumnIndex("wikiurl"));

		locationRow.hint_continent = cursor.getString(cursor.getColumnIndex("hint_continent"));
		locationRow.hint_capital_city = cursor.getString(cursor.getColumnIndex("hint_capital_city"));
		locationRow.hint_area = cursor.getString(cursor.getColumnIndex("hint_area"));
		locationRow.hint_population = cursor.getString(cursor.getColumnIndex("hint_population"));
		locationRow.hint_gdp = cursor.getString(cursor.getColumnIndex("hint_gdp"));
		locationRow.hint_currency = cursor.getString(cursor.getColumnIndex("hint_currency"));
		locationRow.hint_languages = cursor.getString(cursor.getColumnIndex("hint_languages"));

		locationRow.hint_written_1 = cursor.getString(cursor.getColumnIndex("hint_written_1"));

		locationRow.played = cursor.getString(cursor.getColumnIndex("played")) == "0" ? false : true;
		locationRow.best_score = cursor.getInt(cursor.getColumnIndex("best_score"));
		locationRow.best_time = Long.parseLong(cursor.getString(cursor.getColumnIndex("best_time")));
		locationRow.guid = cursor.getString(cursor.getColumnIndex("guid"));

		return locationRow;
	}



	/* ==============================================================================================================================================================
	/																	Get Single Location Data
	/ =============================================================================================================================================================*/

	// DATABASE - Set played and best_score for a single location
	public void setSingleLocationPlayedBestScoreAndBestTime(RowLocationData row, String tableName, long score, long time)
	{

		RowLocationData[] singleRow = getLocationsFromTableByGuid(new String[] {row.guid});

		databaseOpen(true);

		try
		{
			ContentValues values = new ContentValues();
			values.put("played", "1");
			if (score > singleRow[0].best_score)
				values.put("best_score", score);  // if current score is better than best score, add it
			if (time > singleRow[0].best_time)
				values.put("best_time", time);

			database.update(CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], values, String.format("guid='%s'", row.guid), null);

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


	// DATABASE - Get a list of every location played
	public ArrayList<LatLng> getAllLocationsPlayedSoFar()
	{

		ArrayList<LatLng> latLngList = new ArrayList<LatLng>();

		databaseOpen(true);
		try
		{
			for (int i = 0; i < CommonStuff.gameState.categoryNames.length; i++)
			{
				cursor = database.rawQuery(String.format("SELECT latitude, longitude FROM %s WHERE played='%s'", CommonStuff.gameState.categoryNames[i], "1"), null);
				cursor.moveToFirst();

				for (int j = 0; j < cursor.getCount(); j++)
				{
					latLngList.add(new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")), cursor.getDouble(cursor.getColumnIndex("longitude"))));
					cursor.moveToNext();
				}
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }

		return latLngList;
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
	/																Reset Locations Database
	/ =============================================================================================================================================================*/

	// DATABASE - Resets played and best_score in the Locations database
	public void resetLocationsDatabase()
	{

		databaseOpen(true);

		try
		{
			for (int i = 0; i < CommonStuff.gameState.categoryNames.length; i++)
			{
				ContentValues values = new ContentValues();
				values.put("played", "0");
				values.put("best_score", "0");
				database.update(CommonStuff.gameState.categoryNames[i], values, null, null);
			}

			database.setTransactionSuccessful();
		}
		catch (Exception e) { Log.e("DEBUG", "exception", e); databaseClose(); } finally { databaseClose(); }
	}


}
