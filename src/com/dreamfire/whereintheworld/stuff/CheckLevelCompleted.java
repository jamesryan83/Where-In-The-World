package com.dreamfire.whereintheworld.stuff;

import java.util.ArrayList;

import android.content.Context;

import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;


public class CheckLevelCompleted
{
	private int numLocationsCompleted = 0;
	private long totalScore = 0;
	private long requiredScore = 0;

	public ArrayList<String> completedDialogTitles;
	public ArrayList<String> completedDialogMessages;

	private int numLevelsPassed = 0;
	private int numLevelsCompleted = 0;
	private boolean bestScoreBeaten = false;
	private boolean levelAlreadyPassed = false;
	private boolean levelAlreadyCompleted = false;

	public boolean allLocationsCompleted = false;
	public boolean allCategoriesCompleted = false;
	public boolean allLevelsCompleted = false;
	public boolean levelFailed = false;




	/* ==============================================================================================================================================================
	/																	Setup
	/ =============================================================================================================================================================*/

	// Constructor
	public CheckLevelCompleted(Context context)
	{
		CommonStuff.gameState.difficultyStateData = CommonStuff.databaseGameState.getDifficultyStateData();
		CommonStuff.gameState.levelStateData = CommonStuff.databaseGameState.getLevelDataForCurrentCategory();
		CommonStuff.gameState.locationStateData = CommonStuff.databaseGameState.getLocationsDataForCurrentLevel();

		totalScore = CommonStuff.getCurrentTotalScore();
		requiredScore = GeneralConstants.levelRequiredScores[CommonStuff.gameState.currentDifficultyIndex][CommonStuff.gameState.currentLevelIndex];

		// Find number of levels passed/completed
		for (int i = 0; i < CommonStuff.gameState.levelStateData.length; i++)
		{
			if (CommonStuff.gameState.levelStateData[i].completed == true)
				numLevelsCompleted++;

			if (CommonStuff.gameState.levelStateData[i].passed == true)
				numLevelsPassed++;
		}

		levelAlreadyPassed = CommonStuff.gameState.levelStateData[CommonStuff.gameState.currentLevelIndex].passed;
		levelAlreadyCompleted = CommonStuff.gameState.levelStateData[CommonStuff.gameState.currentLevelIndex].completed;


		// Find number of locations completed
		for (int i = 0; i < CommonStuff.gameState.locationStateData.length; i++)
			if (CommonStuff.gameState.locationStateData[i].completed == true)
				numLocationsCompleted++;


		// ArrayLists of Dialog Titles and Messages to be shown after this class finishes
		completedDialogTitles = new ArrayList<String>();
		completedDialogMessages = new ArrayList<String>();

		getResultsFromCurrentState(context);
	}


	/* ==============================================================================================================================================================
	/														Check if the level has been completed
	/ =============================================================================================================================================================*/

	// Set the levelCheckState value
	private void getResultsFromCurrentState(Context context)
	{

		boolean resetLocations = false;

		// *** All locations passed ***
		if (numLocationsCompleted >= 6)
		{

			// beat the required score
			if (totalScore >= requiredScore)
			{
				numLevelsCompleted++;

				// Check if best Score beaten
				bestScoreBeaten = CommonStuff.databaseGameState.updateLevelBestScore(totalScore);

				allLocationsCompleted = true;

				// Level Completed
				CommonStuff.databaseGameState.setLevelPassed();
				CommonStuff.databaseGameState.setLevelCompleted();


				// Best score beaten dialog
				if (bestScoreBeaten == true)
				{
					completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_new_bestscore));
					completedDialogMessages.add
					(
						context.getResources().getString(R.string.string_checklevelcompleted_beat_bestscore) +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_final_score) +
						CommonStuff.getCurrentTotalScore()
					);
				}


				// Level Completed dialog title
				completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_level_complete));


				// Unlocked next level message + bonus tokens for finishing level
				if (levelAlreadyCompleted == false && numLevelsCompleted < 5)
				{
					CommonStuff.databaseGameState.updateNumberOfTokens(GeneralConstants.tokenBonusLevelComplete[CommonStuff.gameState.currentDifficultyIndex]);

					if (levelAlreadyPassed == true)
						completedDialogMessages.add
						(
							context.getResources().getString(R.string.string_checklevelcompleted_tokens_plus) +
							GeneralConstants.tokenBonusLevelComplete[CommonStuff.gameState.currentDifficultyIndex] +
							GeneralConstants.newLine +
							context.getResources().getString(R.string.string_checklevelcompleted_final_score) +
							CommonStuff.getCurrentTotalScore()
						);
					else
						completedDialogMessages.add
						(
							context.getResources().getString(R.string.string_checklevelcompleted_unlocked_next_level) +
							GeneralConstants.newLine +
							context.getResources().getString(R.string.string_checklevelcompleted_tokens_plus) +
							GeneralConstants.tokenBonusLevelComplete[CommonStuff.gameState.currentDifficultyIndex] +
							GeneralConstants.newLine +
							context.getResources().getString(R.string.string_checklevelcompleted_final_score) +
							CommonStuff.getCurrentTotalScore()
						);
				}


				// All levels completed message + bonus tokens for finishing all levels
				else if (levelAlreadyCompleted == false && numLevelsCompleted >= 5)
				{
					long bonusTokens = GeneralConstants.tokenBonusLevelComplete[CommonStuff.gameState.currentDifficultyIndex] + GeneralConstants.tokenBonusCategoryComplete;

					completedDialogMessages.add
					(
						context.getResources().getString(R.string.string_checklevelcompleted_all_locations_finished) +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_tokens_plus) +
						GeneralConstants.tokenBonusLevelComplete[CommonStuff.gameState.currentDifficultyIndex] +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_final_score) +
						CommonStuff.getCurrentTotalScore()
					);

					completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_all_levels_complete));
					completedDialogMessages.add
					(
						context.getResources().getString(R.string.string_checklevelcompleted_finished_all_levels) +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_tokens_plus) +
						GeneralConstants.tokenBonusCategoryComplete
					);

					CommonStuff.databaseGameState.setCategoryCompleted(CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex]);


					// Check if all categories completed
					CommonStuff.gameState.categoryStateData = CommonStuff.databaseGameState.getCategoriesCompleted();
					int numCategoriesAvailable = CommonStuff.gameState.difficultyStateData[CommonStuff.gameState.currentDifficultyIndex].numCategoriesPerDifficulty;

					int completedCount = 0;
					for (int i = 0; i < numCategoriesAvailable; i++)
						if (CommonStuff.gameState.categoryStateData[i].completed == true) completedCount++;

					// Completed all categories for this difficulty
					if (completedCount == numCategoriesAvailable)
					{
						completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_all_categories_complete));
						completedDialogMessages.add
						(
							context.getResources().getString(R.string.string_checklevelcompleted_finished_all_categories) +
							GeneralConstants.newLine +
							context.getResources().getString(R.string.string_checklevelcompleted_tokens_plus) +
							GeneralConstants.tokenBonusDifficultyComplete
						);

						bonusTokens += GeneralConstants.tokenBonusDifficultyComplete;
						allCategoriesCompleted = true;


						// Everything Completed
						if (CommonStuff.gameState.currentDifficultyIndex == 3) // if expert mode
						{
							completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_game_completed_title));
							completedDialogMessages.add(context.getResources().getString(R.string.string_checklevelcompleted_game_completed) +
									GeneralConstants.newLine +
									context.getResources().getString(R.string.string_checklevelcompleted_game_completed2) +
									GeneralConstants.newLine +
									context.getResources().getString(R.string.string_checklevelcompleted_tokens_plus) +
									GeneralConstants.tokenBonusEverythingComplete
									);

							bonusTokens += GeneralConstants.tokenBonusEverythingComplete;
						}
					}

					CommonStuff.databaseGameState.updateNumberOfTokens(bonusTokens);

					allLevelsCompleted = true;
				}


				// general completed level dialog box + 1/2 tokens you got the first time finishing level
				else
				{
					CommonStuff.databaseGameState.updateNumberOfTokens(GeneralConstants.tokenBonusLevelCompleteOngoing[CommonStuff.gameState.currentDifficultyIndex]);

					completedDialogMessages.add
					(
						context.getResources().getString(R.string.string_checklevelcompleted_all_locations_finished) +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_tokens_plus) +
						GeneralConstants.tokenBonusLevelCompleteOngoing[CommonStuff.gameState.currentDifficultyIndex] +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_final_score) +
						CommonStuff.getCurrentTotalScore()
					);
				}

			}


			// failed the level
			else
			{
				completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_level_failed));
				completedDialogMessages.add(
						context.getResources().getString(R.string.string_checklevelcompleted_not_enough_points) +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_final_score) +
						CommonStuff.getCurrentTotalScore());

				levelFailed = true;
			}

			resetLocations = true;
		}


		// *** No locations passed ***
		else if (numLocationsCompleted == 0)
		{
			resetLocations = true;
		}


		// *** Some locations passed ***
		else
		{
			if (totalScore >= requiredScore)
			{
				CommonStuff.databaseGameState.updateLevelBestScore(totalScore);
				CommonStuff.databaseGameState.setLevelPassed();

				numLevelsPassed++;

				if (levelAlreadyPassed == false && numLevelsPassed < 5)
				{
					completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_level_passed));
					completedDialogMessages.add
					(
						context.getResources().getString(R.string.string_checklevelcompleted_unlocked_next_level) +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_final_score) +
						CommonStuff.getCurrentTotalScore() +
						GeneralConstants.newLine +
						GeneralConstants.newLine +
						context.getResources().getString(R.string.string_checklevelcompleted_finish_all_locations)
					);
				}

				if (levelAlreadyPassed == false && numLevelsPassed >= 5)
				{
					completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_level_passed));
					completedDialogMessages.add(context.getResources().getString(R.string.string_checklevelcompleted_finished_all_levels) +
							GeneralConstants.newLine +
							context.getResources().getString(R.string.string_checklevelcompleted_finished_all_levels2));


					// Unlock Free Play mode
					if (CommonStuff.gameState.currentDifficultyIndex == 1 && CommonStuff.gameState.freePlayModeUnlocked == false) // if normal mode
					{
						if (CommonStuff.databaseGameState.getIfFreePlayUnlocked() == true)
						{
							completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_free_play_unlocked));
							completedDialogMessages.add(context.getResources().getString(R.string.string_checklevelcompleted_unlocked_free_play_mode));
						}
					}


					// Unlock Expert mode
					if (CommonStuff.gameState.currentDifficultyIndex == 2 && CommonStuff.gameState.expertModeUnlocked == false) // if hard mode
					{
						if (CommonStuff.databaseGameState.getIfExpertUnlocked() == true)
						{
							completedDialogTitles.add(context.getResources().getString(R.string.string_checklevelcompleted_expert_unlocked));
							completedDialogMessages.add(context.getResources().getString(R.string.string_checklevelcompleted_unlocked_expert_mode));
						}

					}
				}
			}
		}


		// Reset locations
		if (resetLocations == true)
		{
			CommonStuff.databaseGameState.resetLocationsForCurrentLevel(context);
		}

		CommonStuff.setCurrentLocations();
	}

}
