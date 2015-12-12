package com.dreamfire.whereintheworld.stuff;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.database.Hint;
import com.dreamfire.whereintheworld.database.RowDifficultyState;
import com.dreamfire.whereintheworld.fragments.FragmentCategories;
import com.dreamfire.whereintheworld.fragments.FragmentDifficulty;
import com.dreamfire.whereintheworld.fragments.FragmentHints;
import com.dreamfire.whereintheworld.fragments.FragmentLevels;
import com.dreamfire.whereintheworld.fragments.FragmentLocations;
import com.dreamfire.whereintheworld.fragments.FragmentMainGame;

public class AsyncTasks
{

	// setup the databases and other stuff
	public static class ActivityMainTask extends AsyncTask<Void, Void, Void>
	{
		private ActivityMain callingActivity;

		public ActivityMainTask(ActivityMain callingActivity)
		{
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			CommonStuff.preGameSetup(callingActivity.getApplicationContext());

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			callingActivity.setupUiAfterAsync();
		}
	}


	// load tokens
	public static class TokensTask extends AsyncTask<Void, Void, Void>
	{
		private ActivityMain callingActivity;

		public TokensTask(ActivityMain callingActivity)
		{
			CommonStuff.isPreGameSetupNull(callingActivity.getApplicationContext());
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			CommonStuff.gameState.currentTokens = CommonStuff.databaseGameState.getNumberOfTokens();
			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			callingActivity.afterUpdateTokens();
		}
	}


	// get difficulty percent complete
	public static class DifficultyTask extends AsyncTask<Void, Void, Void>
	{
		private double[] tempPercentages;
		private boolean expertUnlocked = false;

		public DifficultyTask(Context context)
		{
			CommonStuff.isPreGameSetupNull(context);
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			// get number of categories per difficulty
			int[] numCategoriesPerDifficulty = new int[4];
			RowDifficultyState[] difficultyRows = CommonStuff.databaseGameState.getDifficultyStateData();
			for(int i = 0; i < difficultyRows.length; i++)
				numCategoriesPerDifficulty[i] = difficultyRows[i].numCategoriesPerDifficulty;

			// get number of levels completed per difficulty
			int[][] levelsCompleted = CommonStuff.databaseGameState.getLevelsCompletedForAllDifficultiesAndCategories();
			tempPercentages = new double[4];

			for (int i = 0; i < levelsCompleted.length; i++)
			{
				int levelsCompletedCount = 0;
				for (int j = 0; j < levelsCompleted[i].length; j++)
					levelsCompletedCount += levelsCompleted[i][j];

				tempPercentages[i] = (levelsCompletedCount / (numCategoriesPerDifficulty[i] * 5.0)) * 100.0;   // 5 is number of levels, 100 is convert to percent
			}

			expertUnlocked = CommonStuff.databaseGameState.getIfExpertUnlocked();

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			CommonStuff.gameState.difficultyPercentComplete = tempPercentages;
			CommonStuff.gameState.expertModeUnlocked = expertUnlocked;
			((FragmentDifficulty)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_DIFFICULTY)).setupUiAfterAsync();
		}
	}


	// load categories data
	public static class CategoriesDataTask extends AsyncTask<Void, Void, Void>
	{
		public CategoriesDataTask(Context context)
		{
			CommonStuff.isPreGameSetupNull(context);
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			CommonStuff.gameState.difficultyStateData = CommonStuff.databaseGameState.getDifficultyStateData();
			CommonStuff.gameState.categoryStateData = CommonStuff.databaseGameState.getCategoriesCompleted();

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			((FragmentCategories)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_CATEGORIES)).setupUiAfterAsync();
		}
	}


	// load category bitmap
	public static class LoadCategoryImageTask extends AsyncTask<String, Void, Void>
	{
		private final WeakReference<ImageView> imageViewReference;
		private Activity callingActivity;
		private byte[] image;

		public LoadCategoryImageTask(ImageView imageView, Activity callingActivity)
		{

			imageViewReference = new WeakReference<ImageView>(imageView);
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(String... params)
		{

			image = CommonStuff.databaseGameState.getSingleImageFromCategoryData(params[0]);

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			if (imageViewReference != null)
			{
				final ImageView imageView = imageViewReference.get();
				if (imageView != null)
					Glide.with(callingActivity).load(image).into(imageView);
			}
		}
	}


	// load flag bitmap for hint data
	public static class LoadBitmapHintDataFlagTask extends AsyncTask<String, Void, Void>
	{
		private final WeakReference<ImageView> imageViewReference;
		private Activity callingActivity;
		private byte[] image;

		public LoadBitmapHintDataFlagTask(ImageView imageView, Activity callingActivity)
		{

			imageViewReference = new WeakReference<ImageView>(imageView);
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(String... params)
		{

			image = CommonStuff.databaseGameState.getFlagImage(params[0]);

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			if (imageViewReference != null)
			{
				final ImageView imageView = imageViewReference.get();
				if (imageView != null)
					Glide.with(callingActivity).load(image).into(imageViewReference.get());
			}
		}
	}


	// unlock a category
	public static class UnlockCategoryTask extends AsyncTask<Integer, Void, Void>
	{
		private ActivityMain callingActivity;

		public UnlockCategoryTask(ActivityMain callingActivity)
		{
			CommonStuff.isPreGameSetupNull(callingActivity.getApplicationContext());
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Integer... params)
		{
			int position = params[0];
			int useTokens = params[1];

			CommonStuff.databaseGameState.purchaseCatagory(position);

			if (CommonStuff.gameState.infiniteTokens == false && useTokens == 1)
				CommonStuff.databaseGameState.updateNumberOfTokens(-GeneralConstants.tokenCostPerCategory);

			CommonStuff.setCurrentCategoryIndex(callingActivity.getApplicationContext(), position);

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			callingActivity.goToPage(FragmentConstants.FRAGMENT_CATEGORIES);
			((FragmentCategories)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_CATEGORIES)).viewPager
				.setCurrentItem(CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex]);
		}
	}


	// load levels data
	public static class LevelDataTask extends AsyncTask<Void, Void, Void>
	{
		public LevelDataTask(Context context)
		{
			CommonStuff.isPreGameSetupNull(context);
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			CommonStuff.gameState.levelStateData = CommonStuff.databaseGameState.getLevelDataForCurrentCategory();

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			((FragmentLevels)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_LEVELS)).setupUiAfterAsync();
		}
	}


	// check level completed
	public static class CheckLevelCompletedTask extends AsyncTask<Void, Void, Void>
	{
		private CheckLevelCompleted checkLevelCompleted;
		private ActivityMain callingActivity;

		public CheckLevelCompletedTask(ActivityMain callingActivity)
		{
			CommonStuff.isPreGameSetupNull(callingActivity.getApplicationContext());
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			checkLevelCompleted = new CheckLevelCompleted(callingActivity.getApplicationContext());

			callingActivity.dialogTitles = checkLevelCompleted.completedDialogTitles;
			callingActivity.dialogMessages = checkLevelCompleted.completedDialogMessages;

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			callingActivity.allLocationsCompleted = checkLevelCompleted.allLocationsCompleted;
			callingActivity.allLevelsCompleted = checkLevelCompleted.allLevelsCompleted;
			callingActivity.allCategoriesCompleted = checkLevelCompleted.allCategoriesCompleted;
			callingActivity.levelFailed = checkLevelCompleted.levelFailed;

			((FragmentLocations)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_LOCATIONS)).setupUiAfterAsync();
			callingActivity.showLevelCompletedDialogs();
		}

	}

	// reset the locations
	public static class ResetLocationsTask extends AsyncTask<Void, Void, Void>
	{
		private ActivityMain callingActivity;

		public ResetLocationsTask(ActivityMain callingActivity)
		{
			CommonStuff.isPreGameSetupNull(callingActivity);
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			CommonStuff.databaseGameState.resetLocationsForCurrentLevel(callingActivity.getApplicationContext());

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			callingActivity.goToPage(FragmentConstants.FRAGMENT_LOCATIONS);
		}
	}


	// regular game mode
	public static class RegularGameModeTask extends AsyncTask<Void, Void, Void>
	{
		private ActivityMain callingActivity;

		public RegularGameModeTask(ActivityMain callingActivity)
		{
			CommonStuff.isPreGameSetupNull(callingActivity.getApplicationContext());
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			HintClass hintClass = new HintClass(callingActivity.getApplicationContext());
			ArrayList<Hint> hints = hintClass.get9RandomHints(CommonStuff.gameState.currentLocationRow);

			for (Hint h : hints)
				hintClass.setSelectedHintDisplayData(h);

			CommonStuff.gameState.currentHints = hints;

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			((FragmentMainGame)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME)).setupUiAfterAsync();
		}
	}


	// free play
	public static class FreePlayGameModeTask extends AsyncTask<Void, Void, Void>
	{
		private ActivityMain callingActivity;

		public FreePlayGameModeTask(ActivityMain callingActivity)
		{
			CommonStuff.isPreGameSetupNull(callingActivity.getApplicationContext());
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			CommonStuff.gameState.currentLocationRow = CommonStuff.databaseLocations.getRandomLocationsFromAllTables(1, false).get(0);
			CommonStuff.gameState.currentHints = new HintClass(callingActivity.getApplicationContext())
				.get9RandomHints(CommonStuff.gameState.currentLocationRow);

			HintClass hintClass = new HintClass(callingActivity.getApplicationContext());
			for (Hint h : CommonStuff.gameState.currentHints)
				hintClass.setSelectedHintDisplayData(h);

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			((FragmentMainGame)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME)).setupUiAfterAsync();
		}
	}


	// location finished
	public static class MainGameLocationCompleteTask extends AsyncTask<Long, Void, Void>
	{
		private ActivityMain callingActivity;

		public MainGameLocationCompleteTask(ActivityMain callingActivity)
		{
			CommonStuff.isPreGameSetupNull(callingActivity.getApplicationContext());
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Long... params)
		{
			long totalScore = params[0];
			long elapsedTimeSeconds = params[1];

			CommonStuff.databaseGameState.updateLocationScoreAndCompleted(totalScore);
			CommonStuff.databaseLocations.setSingleLocationPlayedBestScoreAndBestTime(
					CommonStuff.gameState.currentLocationRow, CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex], totalScore, elapsedTimeSeconds);

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			callingActivity.goToPage(FragmentConstants.FRAGMENT_LOCATIONS);
		}
	}


	// purchase a hint
	public static class PurchaseHintTask extends AsyncTask<Integer, Void, Void>
	{
		private int index = 0;
		private boolean purchased = false;

		public PurchaseHintTask(Context context)
		{
			CommonStuff.isPreGameSetupNull(context);
		}

		@Override
		protected Void doInBackground(Integer... params)
		{
			purchased = CommonStuff.purchaseHint(params[0]);
			index = params[0];

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			((FragmentHints)ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_HINTS)).afterPurchaseHintTask(purchased, index);
		}
	}


	// reset the databases
	public static class ResetDatabaseTask extends AsyncTask<Void, Void, Void>
	{
		private ActivityMain callingActivity;

		public ResetDatabaseTask(ActivityMain callingActivity)
		{
			this.callingActivity = callingActivity;
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			CommonStuff.databaseLocations.resetLocationsDatabase();
			CommonStuff.databaseGameState.resetGameStateDatabase();

			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{

			callingActivity.goToPage(FragmentConstants.RESTART_GAME);
		}
	}


}
