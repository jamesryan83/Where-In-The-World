package com.dreamfire.whereintheworld.stuff;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.constants.HintNameConstants;
import com.dreamfire.whereintheworld.database.Hint;
import com.dreamfire.whereintheworld.database.RowLocationData;

public class HintClass
{
	private Context context;
	private ArrayList<Hint> hints1token, hints2token, hints3token;

	public HintClass(Context context)
	{
		this.context = context;
	}

	/* ==============================================================================================================================================================
	/																	Get hints
	/ =============================================================================================================================================================*/


	// Returns 9 randomised hints
	public ArrayList<Hint> get9RandomHints(RowLocationData row)
	{
		setHints(row);

		ArrayList<Hint> hints9 = new ArrayList<Hint>();

		// Shuffle hints and add to final list
		Collections.shuffle(hints1token);
		Collections.shuffle(hints2token);
		Collections.shuffle(hints3token);

		hints9.add(hints1token.get(0));
		hints9.add(hints1token.get(1));
		hints9.add(hints1token.get(2));
		hints9.add(hints2token.get(0));
		hints9.add(hints2token.get(1));
		hints9.add(hints2token.get(2));
		hints9.add(hints3token.get(0));
		hints9.add(hints3token.get(1));
		hints9.add(hints3token.get(2));

		return hints9;
	}


	// Add all hints to their respective hint list
	private void setHints(RowLocationData row)
	{

		hints1token = new ArrayList<Hint>();
		hints2token = new ArrayList<Hint>();
		hints3token = new ArrayList<Hint>();

		if (row.hint_continent.equals(GeneralConstants.databaseEntryMissing) == false) hints1token.add(new Hint(HintNameConstants.CONTINENT, HintNameConstants.CONTINENT_NAME, GeneralConstants.tokensHintCostWorst, true));  // (HintName, token cost, repeatable)
		if (row.hint_area.equals(GeneralConstants.databaseEntryMissing) == false) hints1token.add(new Hint(HintNameConstants.AREA, HintNameConstants.AREA_NAME, GeneralConstants.tokensHintCostWorst, true));
		if (row.hint_population.equals(GeneralConstants.databaseEntryMissing) == false) hints1token.add(new Hint(HintNameConstants.POPULATION, HintNameConstants.POPULATION_NAME, GeneralConstants.tokensHintCostWorst, true));
		if (row.hint_gdp.equals(GeneralConstants.databaseEntryMissing) == false) hints1token.add(new Hint(HintNameConstants.GDP, HintNameConstants.GDP_NAME, GeneralConstants.tokensHintCostWorst, true));
		if (row.hint_currency.equals(GeneralConstants.databaseEntryMissing) == false) hints1token.add(new Hint(HintNameConstants.CURRENCY, HintNameConstants.CURRENCY_NAME, GeneralConstants.tokensHintCostWorst, true));
		if (row.hint_languages.equals(GeneralConstants.databaseEntryMissing) == false) hints1token.add(new Hint(HintNameConstants.LANGUAGES, HintNameConstants.LANGUAGES_NAME, GeneralConstants.tokensHintCostWorst, true));
		hints1token.add(new Hint(HintNameConstants.FLAG, HintNameConstants.FLAG_NAME, GeneralConstants.tokensHintCostWorst, true));
		hints1token.add(new Hint(HintNameConstants.MARKERS_6, HintNameConstants.MARKERS_6_NAME, GeneralConstants.tokensHintCostWorst, false));
		hints1token.add(new Hint(HintNameConstants.CIRCLE_BIG, HintNameConstants.CIRCLE_BIG_NAME, GeneralConstants.tokensHintCostWorst, false));
		hints1token.add(new Hint(HintNameConstants.HOT_COLD_TIMER_10, HintNameConstants.HOT_COLD_TIMER_10_NAME, GeneralConstants.tokensHintCostWorst, false));
		hints1token.add(new Hint(HintNameConstants.SECOND_GUESS_1, HintNameConstants.SECOND_GUESS_1_NAME, GeneralConstants.tokensHintCostWorst, false));
		hints1token.add(new Hint(HintNameConstants.MISSING_LETTERS_75_PCT, HintNameConstants.MISSING_LETTERS_75_PCT_NAME, GeneralConstants.tokensHintCostWorst, true));
		hints1token.add(new Hint(HintNameConstants.ARROW_2, HintNameConstants.ARROW_2_NAME, GeneralConstants.tokensHintCostWorst, false));

		if (row.hint_capital_city.equals(GeneralConstants.databaseEntryMissing) == false) hints2token.add(new Hint(HintNameConstants.CAPITAL_CITY, HintNameConstants.CAPITAL_CITY_NAME, GeneralConstants.tokensHintCostMedium, true));
		if (row.country.equals(GeneralConstants.databaseEntryMissing) == false) hints2token.add(new Hint(HintNameConstants.COUNTRY, HintNameConstants.COUNTRY_NAME, GeneralConstants.tokensHintCostMedium, true));
		hints2token.add(new Hint(HintNameConstants.HOT_COLD_TIMER_20, HintNameConstants.HOT_COLD_TIMER_20_NAME, GeneralConstants.tokensHintCostMedium, false));
		hints2token.add(new Hint(HintNameConstants.OPPOSITE_SIDE_EARTH, HintNameConstants.OPPOSITE_SIDE_EARTH_NAME, GeneralConstants.tokensHintCostMedium, false));
		hints2token.add(new Hint(HintNameConstants.MARKERS_4, HintNameConstants.MARKERS_4_NAME, GeneralConstants.tokensHintCostMedium, false));
		hints2token.add(new Hint(HintNameConstants.CIRCLE_MEDIUM, HintNameConstants.CIRCLE_MEDIUM_NAME, GeneralConstants.tokensHintCostMedium, false));
		hints2token.add(new Hint(HintNameConstants.SECOND_GUESS_2, HintNameConstants.SECOND_GUESS_2_NAME, GeneralConstants.tokensHintCostMedium, false));
		hints2token.add(new Hint(HintNameConstants.MISSING_LETTERS_50_PCT, HintNameConstants.MISSING_LETTERS_50_PCT_NAME, GeneralConstants.tokensHintCostMedium, true));
		hints2token.add(new Hint(HintNameConstants.ARROW_4, HintNameConstants.ARROW_4_NAME, 2, false));

		hints3token.add(new Hint(HintNameConstants.ANAGRAM, HintNameConstants.ANAGRAM_NAME, GeneralConstants.tokensHintCostBest, true));
		hints3token.add(new Hint(HintNameConstants.CAMERA_ZOOM, HintNameConstants.CAMERA_ZOOM_NAME, GeneralConstants.tokensHintCostBest, true));
		if (row.city.equals(GeneralConstants.databaseEntryMissing) == false) hints3token.add(new Hint(HintNameConstants.CITY, HintNameConstants.CITY_NAME, GeneralConstants.tokensHintCostBest, true));
		hints3token.add(new Hint(HintNameConstants.HOT_COLD_TIMER_30, HintNameConstants.HOT_COLD_TIMER_30_NAME, GeneralConstants.tokensHintCostBest, false));
		hints3token.add(new Hint(HintNameConstants.CIRCLE_AROUND_EARTH, HintNameConstants.CIRCLE_AROUND_EARTH_NAME, GeneralConstants.tokensHintCostBest, false));
		if (row.hint_written_1.equals(GeneralConstants.databaseEntryMissing) == false) hints3token.add(new Hint(HintNameConstants.WRITTEN_HINT_1, HintNameConstants.WRITTEN_HINT_1_NAME, GeneralConstants.tokensHintCostBest, true));
		hints3token.add(new Hint(HintNameConstants.MARKERS_2, HintNameConstants.MARKERS_2_NAME, GeneralConstants.tokensHintCostBest, false));
		hints3token.add(new Hint(HintNameConstants.CIRCLE_SMALL, HintNameConstants.CIRCLE_SMALL_NAME, GeneralConstants.tokensHintCostBest, false));
		hints3token.add(new Hint(HintNameConstants.SECOND_GUESS_3, HintNameConstants.SECOND_GUESS_3_NAME, GeneralConstants.tokensHintCostBest, false));
		hints3token.add(new Hint(HintNameConstants.MISSING_LETTERS_25_PCT, HintNameConstants.MISSING_LETTERS_25_PCT_NAME, GeneralConstants.tokensHintCostBest, true));
		hints3token.add(new Hint(HintNameConstants.ARROW_6, HintNameConstants.ARROW_6_NAME, GeneralConstants.tokensHintCostBest, false));
	}


	/* ==============================================================================================================================================================
	/																	Set Hints
	/ =============================================================================================================================================================*/


	// Sets data for hint dialog subscreen
	public void setSelectedHintDisplayData(Hint hint)
	{
		switch (hint.id)
		{
			// 1, 2 or 3 tokens from LocationRow data
			case HintNameConstants.CONTINENT :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_continent_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_continent) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.gameState.currentLocationRow.hint_continent +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_continents;
				break;

			case HintNameConstants.AREA :
				String unit = CommonStuff.gameState.kilometresEnabled == true ? " km" : " mi";
				hint.hintTitle = context.getResources().getString(R.string.string_hint_area_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_area) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.addCommasToStringNumber(CommonStuff.gameState.currentLocationRow.hint_area) +
						unit + "²" +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_area;
				break;

			case HintNameConstants.POPULATION :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_population_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_population) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.addCommasToStringNumber(CommonStuff.gameState.currentLocationRow.hint_population) +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_population;
				break;

			case HintNameConstants.GDP :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_gdp_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_gdp) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						"$" + CommonStuff.addCommasToStringNumber(CommonStuff.gameState.currentLocationRow.hint_gdp) + ",000,000" +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_gdp;
				break;

			case HintNameConstants.CURRENCY :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_currency_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_currency) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.gameState.currentLocationRow.hint_currency +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_currency;
				break;

			case HintNameConstants.LANGUAGES :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_languages_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_languages) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.gameState.currentLocationRow.hint_languages +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_language;
				break;

			case HintNameConstants.CAPITAL_CITY :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_capital_city_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_capital_city) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.gameState.currentLocationRow.hint_capital_city +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_worldmap;
				break;

			case HintNameConstants.COUNTRY :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_country_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_country) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.gameState.currentLocationRow.country +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_country;
				break;

			case HintNameConstants.CITY :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_city_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_city) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						CommonStuff.gameState.currentLocationRow.city +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_worldmap;
				break;

			case HintNameConstants.WRITTEN_HINT_1 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_general_hint_title);
				hint.hintMessage =
						GeneralConstants.startFontTagOrange +
						CommonStuff.gameState.currentLocationRow.hint_written_1 +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_general;
				break;


			// 1 token
			case HintNameConstants.FLAG :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_flag_title);
				hint.hintMessage = context.getResources().getString(R.string.string_hint_flag);
				hint.flagCountryName = CommonStuff.gameState.currentLocationRow.country;
				break;

			case HintNameConstants.MISSING_LETTERS_75_PCT :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_missing_letters_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_missing_letters1) +
						GeneralConstants.startFontTagOrange +
						"75%" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_missing_letters2) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						getMissingLetters(hint, 0.75) +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_missingletters;
				break;

			case HintNameConstants.MARKERS_6 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_random_markers_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_random_markers1) +
						GeneralConstants.startFontTagOrange +
						"6" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_random_markers2) +
						GeneralConstants.startFontTagOrange +
						context.getResources().getString(R.string.string_hint_random_markers3) +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_random_markers4);
				hint.imageResourceId = R.drawable.image_hint_marker;
				break;

			case HintNameConstants.HOT_COLD_TIMER_10 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_hot_cold_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_hot_cold1) +
						GeneralConstants.startFontTagOrange + "10" + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold2) + GeneralConstants.startFontTagRed +
						context.getResources().getString(R.string.string_hint_hot_cold3) + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold4) + GeneralConstants.startFontTagBlue +
						context.getResources().getString(R.string.string_hint_hot_cold5) + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold6);
				hint.imageResourceId = R.drawable.image_hint_hotcold;
				break;

			case HintNameConstants.CIRCLE_BIG :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_circle_big_title);
				hint.hintMessage = context.getResources().getString(R.string.string_hint_circle);
				hint.imageResourceId = R.drawable.image_hint_circle;
				break;

			case HintNameConstants.SECOND_GUESS_1 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_second_guess_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_second_guess1) +
						GeneralConstants.startFontTagOrange +
						"1" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_second_guess2);
				hint.imageResourceId = R.drawable.image_hint_secondguess;
				break;

			case HintNameConstants.ARROW_2 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_arrows_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_arrows1) +
						GeneralConstants.startFontTagOrange +
						"2" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_arrows2);
				hint.imageResourceId = R.drawable.image_hint_arrows;
				break;

			// 2 tokens
			case HintNameConstants.MISSING_LETTERS_50_PCT :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_missing_letters_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_missing_letters1) +
						GeneralConstants.startFontTagOrange +
						"50%" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_missing_letters2) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						getMissingLetters(hint, 0.5) +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_missingletters;
				break;

			case HintNameConstants.HOT_COLD_TIMER_20 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_hot_cold_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_hot_cold1) +
						GeneralConstants.startFontTagOrange + "20" + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold2) + GeneralConstants.startFontTagRed +
						context.getResources().getString(R.string.string_hint_hot_cold3) + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold4) + GeneralConstants.startFontTagBlue +
						context.getResources().getString(R.string.string_hint_hot_cold5) + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold6);
				hint.imageResourceId = R.drawable.image_hint_hotcold;
				break;

			case HintNameConstants.OPPOSITE_SIDE_EARTH :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_opposite_side_title);
				hint.hintMessage = context.getResources().getString(R.string.string_hint_opposite_side);
				hint.imageResourceId = R.drawable.image_hint_opposite_side;
				break;

			case HintNameConstants.MARKERS_4 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_random_markers_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_random_markers1) +
						GeneralConstants.startFontTagOrange +
						"4" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_random_markers2) +
						GeneralConstants.startFontTagOrange +
						context.getResources().getString(R.string.string_hint_random_markers3) +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_random_markers4);
				hint.imageResourceId = R.drawable.image_hint_marker;
				break;

			case HintNameConstants.CIRCLE_MEDIUM :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_circle_medium_title);
				hint.hintMessage = context.getResources().getString(R.string.string_hint_circle);
				hint.imageResourceId = R.drawable.image_hint_circle;
				break;

			case HintNameConstants.SECOND_GUESS_2 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_second_guess_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_second_guess1) +
						GeneralConstants.startFontTagOrange +
						"2" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_second_guess3);
				hint.imageResourceId = R.drawable.image_hint_secondguess;
				break;

			case HintNameConstants.ARROW_4 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_arrows_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_arrows1) +
						GeneralConstants.startFontTagOrange +
						"4" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_arrows2);
				hint.imageResourceId = R.drawable.image_hint_arrows;
				break;


			// 3 tokens
			case HintNameConstants.ANAGRAM :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_anagram_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_anagram) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						getAnagram() +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_anagram;
				break;

			case HintNameConstants.CAMERA_ZOOM :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_camera_zoom_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_camera_zoom1) +
						GeneralConstants.startFontTagOrange +
						context.getResources().getString(R.string.string_hint_camera_zoom2) +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_camera_zoom3);
				hint.imageResourceId = R.drawable.image_hint_camerazoom;
				break;

			case HintNameConstants.MISSING_LETTERS_25_PCT :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_missing_letters_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_missing_letters1) +
						GeneralConstants.startFontTagOrange +
						"25%" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_missing_letters2) +
						GeneralConstants.breakTag +
						GeneralConstants.startFontTagOrange +
						getMissingLetters(hint, 0.25) +
						GeneralConstants.endFontTag;
				hint.imageResourceId = R.drawable.image_hint_missingletters;
				break;
			case HintNameConstants.HOT_COLD_TIMER_30 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_hot_cold_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_hot_cold1) +
						GeneralConstants.startFontTagOrange + "30" + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold2) + GeneralConstants.startFontTagRed +
						context.getResources().getString(R.string.string_hint_hot_cold3) + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold4) + GeneralConstants.startFontTagBlue +
						context.getResources().getString(R.string.string_hint_hot_cold5) + GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_hot_cold6);
				hint.imageResourceId = R.drawable.image_hint_hotcold;
				break;
			case HintNameConstants.CIRCLE_AROUND_EARTH :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_circle_around_earth_title);
				hint.hintMessage = context.getResources().getString(R.string.string_hint_circle_around_earth);
				hint.imageResourceId = R.drawable.image_hint_circlearoundearth;
				break;
			case HintNameConstants.MARKERS_2 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_random_markers_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_random_markers1) +
						GeneralConstants.startFontTagOrange +
						"2" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_random_markers2) +
						GeneralConstants.startFontTagOrange +
						context.getResources().getString(R.string.string_hint_random_markers3) +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_random_markers4);
				hint.imageResourceId = R.drawable.image_hint_marker;
				break;
			case HintNameConstants.CIRCLE_SMALL :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_circle_small_title);
				hint.hintMessage = context.getResources().getString(R.string.string_hint_circle);
				hint.imageResourceId = R.drawable.image_hint_circle;
				break;

			case HintNameConstants.SECOND_GUESS_3 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_second_guess_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_second_guess1) +
						GeneralConstants.startFontTagOrange +
						"3" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_second_guess3);
				hint.imageResourceId = R.drawable.image_hint_secondguess;
				break;

			case HintNameConstants.ARROW_6 :
				hint.hintTitle = context.getResources().getString(R.string.string_hint_arrows_title);
				hint.hintMessage =
						context.getResources().getString(R.string.string_hint_arrows1) +
						GeneralConstants.startFontTagOrange +
						"6" +
						GeneralConstants.endFontTag +
						context.getResources().getString(R.string.string_hint_arrows2);
				hint.imageResourceId = R.drawable.image_hint_arrows;
				break;

			default :
				break;
		}
	}



	/* ==============================================================================================================================================================
	/																	Hint - Missing Letters
	/ =============================================================================================================================================================*/

	// Returns the percent letters missing as a String - percent as decimal
	private String getMissingLetters(Hint hint, double percent)
	{

		String suburb = CommonStuff.gameState.currentLocationRow.suburb;
		String city = CommonStuff.gameState.currentLocationRow.city;
		String country = CommonStuff.gameState.currentLocationRow.country;
		String answer = "";

		int suburbNumLettersToReplace = (int) Math.floor(suburb.length() * percent);
		int cityNumLettersToReplace = (int) Math.floor(city.length() * percent);
		int countryNumLettersToReplace = (int) Math.floor(country.length() * percent);

		// Concatenate suburb, city and country.  Suburb/city may or may not be added
		if (suburb.length() > 0)
			answer = replaceRandomLettersWithUnderscores(suburb, suburbNumLettersToReplace);
		if (city.length() > 0)
		{
			if (suburb.length() > 0)
				answer = answer + ", " + replaceRandomLettersWithUnderscores(city, cityNumLettersToReplace);
			else
				answer = replaceRandomLettersWithUnderscores(city, cityNumLettersToReplace);
		}

		answer = answer + ", " + replaceRandomLettersWithUnderscores(country, countryNumLettersToReplace);

		return answer;
	}


	// Replaces a set number of random chosen letters in a word with underscores.  numLettersToReplace must be > 0 and <= word.length
	private String replaceRandomLettersWithUnderscores(String word, int numLettersToReplace)
	{

		// Insert a '_' character at random position
		char[] chars = word.toCharArray();
		int rand = (int) (Math.random() * word.length());
		chars[rand] = '_';
		word = String.valueOf(chars);

		// Count occurances of '_'
		int count = 0;
		for (int i = 0; i < word.length(); i++)
			if (word.charAt(i) == '_')
				count++;

		// If not enough '_' characters, run again
		if (count < numLettersToReplace)
			word = replaceRandomLettersWithUnderscores(word, numLettersToReplace);

		return word;
	}


	/* ==============================================================================================================================================================
	/																	Hint - Anagram
	/ =============================================================================================================================================================*/

	// Returns an anagram of the answer location
	public String getAnagram()
	{

		String suburb = CommonStuff.gameState.currentLocationRow.suburb;
		String city = CommonStuff.gameState.currentLocationRow.city;
		String country = CommonStuff.gameState.currentLocationRow.country;
		String answer = "";

		// Concatenate suburb, city and country.  Suburb/city may or may not be added
		if (suburb.length() > 0)
			answer = shuffleWord(suburb);
		if (city.length() > 0)
		{
			if (suburb.length() > 0)
				answer = answer + ", " + shuffleWord(city);
			else
				answer = shuffleWord(city);
		}

		answer = answer + ", " + shuffleWord(country);

		return answer;
	}

	// Shuffles a given word
	private String shuffleWord(String word)
	{

		ArrayList<Character> tempList = new ArrayList<Character>();
		StringBuilder sb = new StringBuilder();

		for (char c : word.toCharArray()) tempList.add(c);

		Collections.shuffle(tempList);

		for (Character c : tempList) sb.append(c);

		return sb.toString();
	}

}
