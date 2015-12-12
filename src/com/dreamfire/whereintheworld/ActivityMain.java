package com.dreamfire.whereintheworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.billing.util.IabHelper;
import com.dreamfire.whereintheworld.billing.util.IabResult;
import com.dreamfire.whereintheworld.billing.util.Inventory;
import com.dreamfire.whereintheworld.billing.util.Purchase;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.constants.MainGameUiStateConstants;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxTutorial;
import com.dreamfire.whereintheworld.fragments.FragmentCategories;
import com.dreamfire.whereintheworld.fragments.FragmentDifficulty;
import com.dreamfire.whereintheworld.fragments.FragmentHintData;
import com.dreamfire.whereintheworld.fragments.FragmentHints;
import com.dreamfire.whereintheworld.fragments.FragmentLevels;
import com.dreamfire.whereintheworld.fragments.FragmentLoading;
import com.dreamfire.whereintheworld.fragments.FragmentLocations;
import com.dreamfire.whereintheworld.fragments.FragmentMainGame;
import com.dreamfire.whereintheworld.fragments.FragmentMainGameMap;
import com.dreamfire.whereintheworld.fragments.FragmentMainMenu;
import com.dreamfire.whereintheworld.fragments.FragmentSettings;
import com.dreamfire.whereintheworld.fragments.FragmentTokens;
import com.dreamfire.whereintheworld.fragments.IFragment;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.ActivityMainTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.LoadBitmapHintDataFlagTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.LoadCategoryImageTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.ResetDatabaseTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.ResetLocationsTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.TokensTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.UnlockCategoryTask;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.GameState;
import com.dreamfire.whereintheworld.stuff.MusicService;
import com.dreamfire.whereintheworld.stuff.MyUncaughtExceptionHandler;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


@SuppressLint({ "UseSparseArrays"})
public class ActivityMain extends NavDrawerActivity implements OnClickListener, AnimationListener
{
	private RelativeLayout layoutMain;
	private LinearLayout layoutTokens;
	private TextView textViewTokens, textViewTitle;
	private ImageButton buttonNavDrawer, buttonQuit;
	private ImageView imageViewToken;

	private int tempViewId;

	public ArrayList<String> dialogTitles;
	public ArrayList<String> dialogMessages;
	private int levelCompletedDialogCounter = 0;
	public boolean levelFailed = false;
	public boolean allLocationsCompleted = false;
	public boolean allLevelsCompleted = false;
	public boolean allCategoriesCompleted = false;

	private FrameLayout layoutLoadingScreen;
	public FragmentLoading fragmentLoadingScreen;
	private boolean loadingScreenIsRunning = false;

	public static HashMap<Integer, IFragment> fragmentList;

	private Animation animationClick;

	private Point touchUp, touchDown;

	private FragmentManager manager;
	private FragmentTransaction transaction;
	private boolean pageAlreadyLoaded = false;

	private Intent music;

	private boolean adActivityStarted = false;

	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// onCreate
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		CommonStuff.trackPosition = 0;
		CommonStuff.currentTrack = 0;

		MyUncaughtExceptionHandler handler = new MyUncaughtExceptionHandler(this);
		handler.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(handler);

		CommonStuff.gameState = new GameState();
		CommonStuff.gameState.previousMainFragment = -1;
		CommonStuff.gameState.currentMainFragment = 0;

		super.onCreate(savedInstanceState);

		fragmentLoadingScreen = new FragmentLoading();
		layoutLoadingScreen = (FrameLayout) findViewById(R.id.activity_main_framelayout_loading);
		startLoadingScreen(500);

		fragmentList = new HashMap<Integer, IFragment>();

		pageAlreadyLoaded = true;
		new ActivityMainTask(this).execute();

		CommonStuff.gameState.load(this.getApplicationContext());
	}

	public void startMusic()
	{
		doBindService();
		music = new Intent();
		music.setClass(this, MusicService.class);
		startService(music);
	}

	// OnResume
	@Override
	public void onResume()
	{

		CommonStuff.gameState.load(this.getApplicationContext());

		super.onResume();

		if (CommonStuff.gameState != null && CommonStuff.gameState.musicEnabled == true)  // TODO : check this
			startMusic();

		// Check for google play services
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode == ConnectionResult.SERVICE_MISSING || resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
		           resultCode == ConnectionResult.SERVICE_DISABLED)
		{
		    GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
		}

		setupUi();

		if (CommonStuff.gameState.currentMainFragment >= 0 && pageAlreadyLoaded == false)
			goToPage(CommonStuff.gameState.currentMainFragment);
	}


	// Setup Ui
	public void setupUi()
	{
		layoutMain = (RelativeLayout) findViewById(R.id.activity_main_layout);
		layoutTokens = (LinearLayout) findViewById(R.id.activity_main_layout_token);
		textViewTokens = (TextView) findViewById(R.id.activity_main_textview_tokens);
		textViewTitle = (TextView) findViewById(R.id.activity_main_textview_title);
		buttonNavDrawer = (ImageButton) findViewById(R.id.activity_main_button_nav_drawer);
		buttonQuit = (ImageButton) findViewById(R.id.activity_main_button_back);
		imageViewToken = (ImageView) findViewById(R.id.activity_main_imageview_token);

		layoutTokens.setOnClickListener(this);
		buttonNavDrawer.setOnClickListener(this);
		buttonQuit.setOnClickListener(this);

		animationClick = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.button);
		animationClick.setAnimationListener(this);
	}


	// after async
	public void setupUiAfterAsync()
	{
		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Activity Main");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		setupInAppBilling();

		try
		{
			Glide.with(this).load(R.drawable.image_common_nav_drawer).into(buttonNavDrawer);
			Glide.with(this).load(R.drawable.image_common_back_arrow).into(buttonQuit);
		}
		catch (Exception e) {}

		goToPage(FragmentConstants.FRAGMENT_MAIN_MENU);

		if (CommonStuff.gameState.isFirstRun == true)
		{
			try { DialogMsgBoxGeneral.newInstance("", getResources()
					.getString(R.string.string_mainmenu_buton_first_run_message), false, false, DialogConstants.NO_CALLBACK)
					.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG); } catch (Exception e) {}  // crash fix
		}

		if (CommonStuff.databaseGameState.getShowAds() == true)
			BannerAdActivity.setupAdvert(this);

		adActivityStarted = false;
	}


	// onPause
	@Override
	public void onPause()
	{
		super.onPause();

		if (musicService != null)
			musicService.stopMusic();

		doUnbindService();

		CommonStuff.gameState.save(this.getApplicationContext());

		try { if (CommonStuff.gameState.currentMainFragment == FragmentConstants.FRAGMENT_MAIN_GAME)
			if (((FragmentMainGame)fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME)).dialogPostGame != null &&
				((FragmentMainGame)fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME)).dialogPostGame.isShowing())
			{
				((FragmentMainGame)fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME)).dialogPostGame.dismiss();
				dialogPostGameClicked(R.id.dialog_post_game_button_view_map);
			}} catch (Exception e) { }; // crash fix
	}


	// Close database
	@Override
	public void onDestroy()
	{
		super.onDestroy();

		CommonStuff.gameState.previousMainFragment = -1;
		CommonStuff.gameState.currentMainFragment = 0;
		CommonStuff.gameState.save(this.getApplicationContext());

		if (iabHelper != null)
		{
			iabHelper.dispose();
			iabHelper = null;
		}

		if (musicService != null)
			musicService.stopMusic();

		doUnbindService();

		try
		{
			CommonStuff.databaseGameState.databaseCloseProperly();
			CommonStuff.databaseLocations.databaseCloseProperly();
		}
		catch (Exception e) {}

	}


	/* ==============================================================================================================================================================
	/																	Load Bitmaps
	/ =============================================================================================================================================================*/

	// load a bitmap image - Category
	public void loadBitmapCategory(String categoryName, ImageView imageView)
	{

		if (imageView != null)
			new LoadCategoryImageTask(imageView, this).execute(categoryName);
	}


	// load a bitmap image - Hint Data flag
	public void loadBitmapHintDataFlag(String countryName, ImageView imageView)
	{

		if (imageView != null)
			new LoadBitmapHintDataFlagTask(imageView, this).execute(countryName);
	}


	/* ==============================================================================================================================================================
	/																	Categories
	/ =============================================================================================================================================================*/

	// When a category image is clicked
	public void categoryImageClicked(int position)
	{

		CommonStuff.playClickSound(this.getApplicationContext());

		CommonStuff.setCurrentCategoryIndex(this.getApplicationContext(), position);

		// Category Locked - unpurchased
		if (CommonStuff.gameState.categoryStateData[position].locked == true)
		{
			if (CommonStuff.gameState.tutorialModeOn == true)
			{
				CommonStuff.showToastTutorialNotAvailable(this);
				return;
			}

			if (CommonStuff.gameState.infiniteTokens == true)
			{
				callbackForDialogMsgBoxGeneral(DialogConstants.PURCHASE_CATEGORY);
				return;
			}

			// Not enough tokens
			if (CommonStuff.gameState.currentTokens < GeneralConstants.tokenCostPerCategory)
			{
				DialogMsgBoxGeneral.newInstance("", getResources()
						.getString(R.string.string_common_not_enough_tokens), true, false, DialogConstants.NO_CALLBACK)
						.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
			}

			// enough tokens
			else if (CommonStuff.gameState.currentTokens >= GeneralConstants.tokenCostPerCategory)
			{
				DialogMsgBoxGeneral.newInstance(getResources().getString(R.string.string_categories_purchase_category_title),
						getResources().getString(R.string.string_categories_purchase_category1) +
						GeneralConstants.tokenCostPerCategory +
						getResources().getString(R.string.string_categories_purchase_category2),
						false, true, DialogConstants.PURCHASE_CATEGORY)
						.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
			}
		}

		// Category unLocked - already purchased
		else
			goToPage(FragmentConstants.FRAGMENT_LEVELS);
	}


	/* ==============================================================================================================================================================
	/																	Levels
	/ =============================================================================================================================================================*/

	// Called from FragmentPagerLevel
	public void levelFragmentClicked(int position, boolean levelUnlocked)
	{

		CommonStuff.playClickSound(this.getApplicationContext());

		CommonStuff.setCurrentLevelIndex(position);

		if (levelUnlocked == false)
		{
			DialogMsgBoxGeneral.newInstance("", getResources().getString(R.string.string_levels_locked) + GeneralConstants.newLine + getResources()
					.getString(R.string.string_levels_complete_previous), false, false, DialogConstants.NO_CALLBACK)
					.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
		}
		else
		{
			goToPage(FragmentConstants.FRAGMENT_LOCATIONS);
		}
	}


	/* ==============================================================================================================================================================
	/																	Main Game
	/ =============================================================================================================================================================*/

	// Post game dialog button clicked
	public void dialogPostGameClicked(int buttonResource)
	{

		FragmentMainGame mainGameFragment = (FragmentMainGame) (fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME));

		if (buttonResource == R.id.dialog_post_game_button_view_map || buttonResource == R.id.dialog_post_game_button_wiki)
			mainGameFragment.setUiControlStates(MainGameUiStateConstants.POST_GAME);
		else if (buttonResource == R.id.dialog_post_game_button_locations)
			mainGameFragment.returnToLocationsScreen();
	}


	// Post game info dialog button clicked
	public void dialogPostGameInfoClicked(int buttonResource)
	{

		((FragmentMainGame)(fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME))).showPostGameDialog();
	}


	// after FragmentHintData is dismissed
	public void fragmentHintDataDismissed()
	{

		if (CommonStuff.gameState.currentSelectedHint == null || (CommonStuff.gameState.currentSelectedHint.repeatable == false && CommonStuff.gameState.currentSelectedHint.used == true))
			goToPage(FragmentConstants.FRAGMENT_MAIN_GAME);
		else
		{
			goToPage(FragmentConstants.FRAGMENT_MAIN_GAME);
			((FragmentMainGame)fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME)).setMapState();
		}
	}


	// Update the map class
	public void replaceFragmentMainGameMap()
	{

		manager = getSupportFragmentManager();
		transaction = manager.beginTransaction();
		transaction.remove((Fragment) fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME_MAP));
		fragmentList.put(FragmentConstants.FRAGMENT_MAIN_GAME_MAP, new FragmentMainGameMap());
		transaction.add(R.id.maingame_framelayout_map, (Fragment) fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME_MAP), "map");
		transaction.show((Fragment) fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME_MAP));


		try { transaction.commit(); } catch (Exception e) { };
	}


	/* ==============================================================================================================================================================
	/																	Dialog Stuff
	/ =============================================================================================================================================================*/


	// Shows level complete dialogs
	public void showLevelCompletedDialogs()
	{

		if (dialogTitles != null && dialogTitles.size() > 0)
		{
			if (levelCompletedDialogCounter == dialogTitles.size())  // if all dialogs have been shown
			{
				if (allCategoriesCompleted == true)
				{
					CommonStuff.gameState.currentLevelIndex = 0;
					goToPage(FragmentConstants.FRAGMENT_DIFFICULTY);
				}
				else if (allLevelsCompleted == true)
				{
					if (CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex] < GeneralConstants.availableDifficulties.length)
						CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex]++;

					goToPage(FragmentConstants.FRAGMENT_CATEGORIES);
				}
				else
				{
					if (allLocationsCompleted == true && levelFailed == false)
					{
						if (CommonStuff.gameState.currentLevelIndex < 4)
							CommonStuff.gameState.currentLevelIndex++;

						goToPage(FragmentConstants.FRAGMENT_LEVELS);
					}
				}

				levelCompletedDialogCounter = 0;

				return;
			}


			DialogMsgBoxGeneral.newInstance(dialogTitles.get(levelCompletedDialogCounter),
					dialogMessages.get(levelCompletedDialogCounter), false, false, DialogConstants.LEVEL_COMPLETE)
					.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

			levelCompletedDialogCounter++;
		}
	}

	// DialogMsgBoxGeneral callbacks
	public void callbackForDialogMsgBoxGeneral(int dialogClosingConstant)
	{
		switch(dialogClosingConstant)
		{
			case DialogConstants.PURCHASE_CATEGORY :
				new UnlockCategoryTask(this).execute(CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex], 1);
				break;

			case DialogConstants.LEVEL_COMPLETE :
				showLevelCompletedDialogs();
				break;

			case DialogConstants.GO_TO_LOCATIONS :
				goToPage(FragmentConstants.FRAGMENT_LOCATIONS);
				break;

			case DialogConstants.RESET_LOCATIONS :
				new ResetLocationsTask(this).execute();
				break;

			case DialogConstants.RESET_DATABASE :
				new ResetDatabaseTask(this).execute();
				break;

			case DialogConstants.GO_TO_TOKENS :
				openTokenScreen();
				break;

			case DialogConstants.NAV_DRAWER_HINT_USED_CHECK :
				super.doClick();
				break;

			case DialogConstants.QUIT_TUTORIAL :
				CommonStuff.gameState.tutorialModeOn = false;
				goToPage(FragmentConstants.FRAGMENT_MAIN_MENU);
				break;

			case DialogConstants.EXIT_GAME :
				startAdActivity();
				break;
		}
	}


	/* ==============================================================================================================================================================
	/																	Tutorial Dialog
	/ =============================================================================================================================================================*/

	// Show the tutorial dialog
	public void showTutorialDialog(SpannableString message, int dialogClosingConstant)
	{

		CommonStuff.gameState.tutorialModeOn = true;

		DialogMsgBoxTutorial dialog = new DialogMsgBoxTutorial(this, getResources().getString(R.string.string_tutorial_dialog_title), message, dialogClosingConstant);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}


	// DialogMsgBoxTutorial callbacks
	public void callbackForDialogMsgBoxTutorial(int dialogClosingConstant)
	{

		switch(dialogClosingConstant)
		{
			case DialogConstants.TUTORIAL_1 :
				goToPage(FragmentConstants.FRAGMENT_DIFFICULTY);
				break;

			case DialogConstants.TUTORIAL_9 :
				goToPage(FragmentConstants.FRAGMENT_HINTS);
				break;

			case DialogConstants.TUTORIAL_10 :
				DialogMsgBoxGeneral.newInstance(getResources().getString(R.string.string_tutorial_dialog_title),
						getResources().getString(R.string.string_tutorial_hints2) +
						GeneralConstants.newLine +
						GeneralConstants.newLine +
						getResources().getString(R.string.string_checklevelcompleted_tokens_plus) + GeneralConstants.tutorialBonusTokens
						, false, false, DialogConstants.NO_CALLBACK)
						.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
				CommonStuff.databaseGameState.updateNumberOfTokens(GeneralConstants.tutorialBonusTokens);
				updateCurrentTokens();
				break;
		}
	}


	/* ==============================================================================================================================================================
	/																	Go to a fragment
	/ =============================================================================================================================================================*/

	// go to a particular page
	public void goToPage(int index)
	{
		goToPage(index, false);
	}


	// go to a particular page - resetPage is for FragmentMainGame
	public void goToPage(int index, boolean resetPage)
	{

		manager = getSupportFragmentManager();

		CommonStuff.gameState.hotColdHintIsRunning = false;

		startLoadingScreen(index == FragmentConstants.FRAGMENT_MAIN_GAME || index == FragmentConstants.FRAGMENT_HINTS ? 1000 : 500);

		// Exit game
		if (index == FragmentConstants.RESTART_GAME || index < 0)
		{
			CommonStuff.gameState = new GameState();
			CommonStuff.gameState.save(this.getApplicationContext());
			startAdActivity();
			return;
		}

		// Set previous and current fragment indexes
		if (index != CommonStuff.gameState.currentMainFragment && CommonStuff.gameState.currentMainFragment != FragmentConstants.FRAGMENT_TOKENS &&
				CommonStuff.gameState.currentMainFragment != FragmentConstants.FRAGMENT_SETTINGS)
			CommonStuff.gameState.previousMainFragment = CommonStuff.gameState.currentMainFragment;

		if (CommonStuff.gameState.previousMainFragment == -1) CommonStuff.gameState.previousMainFragment = FragmentConstants.FRAGMENT_MAIN_MENU;
		CommonStuff.gameState.currentMainFragment = index;


		// Update tokens
		imageViewToken.setVisibility(View.GONE);
		textViewTokens.setVisibility(View.GONE);
		updateCurrentTokens();



		transaction = manager.beginTransaction();


		// Hide all fragments
		if (fragmentList.size() > 0)
		{
			for (Entry<Integer, IFragment> entry : fragmentList.entrySet())
				transaction.hide((Fragment) entry.getValue());
		}


		// Light/Dark theme
		setBackgroundTheme();


		switch (index)
		{
			case FragmentConstants.FRAGMENT_MAIN_MENU :
				goToFragmentPage(new FragmentMainMenu(), FragmentConstants.FRAGMENT_MAIN_MENU, "", "mainmenu");
				commitTransaction(FragmentConstants.FRAGMENT_MAIN_MENU);
				setControlsVisibility(true, true, false, false);
				break;

			case FragmentConstants.FRAGMENT_DIFFICULTY :
				goToFragmentPage(new FragmentDifficulty(), FragmentConstants.FRAGMENT_DIFFICULTY,
						getResources().getString(R.string.string_activitymain_title_difficulty), "difficulty");
				commitTransaction(FragmentConstants.FRAGMENT_DIFFICULTY);
				setControlsVisibility(true, true, true, true);
				break;

			case FragmentConstants.FRAGMENT_CATEGORIES :
				goToFragmentPage(new FragmentCategories(), FragmentConstants.FRAGMENT_CATEGORIES, "", "categories");
				commitTransaction(FragmentConstants.FRAGMENT_CATEGORIES);
				setControlsVisibility(true, true, true, true);
				break;

			case FragmentConstants.FRAGMENT_LEVELS :
				goToFragmentPage(new FragmentLevels(), FragmentConstants.FRAGMENT_LEVELS,
						getResources().getString(R.string.string_activitymain_title_levels), "mainmenu");
				commitTransaction(FragmentConstants.FRAGMENT_LEVELS);
				setControlsVisibility(true, true, true, true);
				break;

			case FragmentConstants.FRAGMENT_LOCATIONS :
				goToFragmentPage(new FragmentLocations(), FragmentConstants.FRAGMENT_LOCATIONS, "", "locations");
				commitTransaction(FragmentConstants.FRAGMENT_LOCATIONS);
				setControlsVisibility(true, true, true, true);
				break;

			case FragmentConstants.FRAGMENT_MAIN_GAME :
				if (fragmentList.containsKey(index) == false)
				{
					fragmentList.put(FragmentConstants.FRAGMENT_MAIN_GAME, new FragmentMainGame());
					transaction.add(R.id.activity_main_framelayout, (Fragment) fragmentList.get(index), "maingame");

					fragmentList.put(FragmentConstants.FRAGMENT_MAIN_GAME_MAP, new FragmentMainGameMap());
					transaction.add(R.id.maingame_framelayout_map, (Fragment) fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME_MAP), "map");
				}
				else
				{
					if (resetPage == true)
					{
						if (fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME) != null)
							transaction.remove((Fragment) fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME));

						fragmentList.put(FragmentConstants.FRAGMENT_MAIN_GAME, new FragmentMainGame());
						transaction.add(R.id.activity_main_framelayout, (Fragment) fragmentList.get(index), "maingame");
					}
				}
				commitTransaction(FragmentConstants.FRAGMENT_MAIN_GAME);
				setControlsVisibility(false, false, false, false);
				break;

			case FragmentConstants.FRAGMENT_HINTS :
				goToFragmentPage(new FragmentHints(), FragmentConstants.FRAGMENT_HINTS,
						getResources().getString(R.string.string_activitymain_title_hints), "hints");
				commitTransaction(FragmentConstants.FRAGMENT_HINTS);
				if (CommonStuff.gameState.freePlayModeOn == true)
					setControlsVisibility(true, false, true, true);
				else
					setControlsVisibility(true, true, true, true);
				break;

			case FragmentConstants.FRAGMENT_HINT_DATA :
				goToFragmentPage(new FragmentHintData(), FragmentConstants.FRAGMENT_HINT_DATA, "", "hintdata");
				commitTransaction(FragmentConstants.FRAGMENT_HINT_DATA);
				setControlsVisibility(false, false, false, false);
				break;

			case FragmentConstants.FRAGMENT_TOKENS :
				goToFragmentPage(new FragmentTokens(), FragmentConstants.FRAGMENT_TOKENS,
						getResources().getString(R.string.string_activitymain_title_tokens), "tokens");
				commitTransaction(FragmentConstants.FRAGMENT_TOKENS);
				setControlsVisibility(true, true, false, true);
				break;

			case FragmentConstants.FRAGMENT_SETTINGS :
				goToFragmentPage(new FragmentSettings(), FragmentConstants.FRAGMENT_SETTINGS,
						getResources().getString(R.string.string_activitymain_title_settings), "settings");
				commitTransaction(FragmentConstants.FRAGMENT_SETTINGS);
				setControlsVisibility(true, true, false, true);
				break;
		}

		super.updateStats(CommonStuff.gameState.currentTokens, -1);
	}


	// Go to a framgent
	private void goToFragmentPage(IFragment fragment, int fragmentConstant, String title, String tag)
	{

		textViewTitle.setText(title);

		if (fragmentList.containsKey(fragmentConstant) == false)
		{
			fragmentList.put(fragmentConstant, fragment);
			transaction.add(R.id.activity_main_framelayout, (Fragment) fragmentList.get(fragmentConstant), tag);
		}
		else
		{
			fragmentList.get(fragmentConstant).updateUi();
		}
	}


	// Commit fragment transaction
	private void commitTransaction(int fragmentConstant)
	{

		transaction.show(((Fragment) fragmentList.get(fragmentConstant)));

		if (fragmentConstant == FragmentConstants.FRAGMENT_MAIN_GAME)
			transaction.show(((Fragment)fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME_MAP)));

		try { transaction.commit(); } catch (Exception e) {}; // crash fix
	}


	// Sets the visibility of the controls around the outside of the screen
	private void setControlsVisibility(boolean backButton, boolean navDrawerButton, boolean tokens, boolean title)
	{

		buttonQuit.setVisibility(backButton == true ? View.VISIBLE : View.GONE);
		buttonNavDrawer.setVisibility(navDrawerButton == true ? View.VISIBLE : View.GONE);
		layoutTokens.setVisibility(tokens == true ? View.VISIBLE : View.GONE);
		textViewTitle.setVisibility(title == true ? View.VISIBLE : View.GONE);
	}


	/* ==============================================================================================================================================================
	/																	Loading Screen
	/ =============================================================================================================================================================*/

	private void startLoadingScreen(int timerLength)
	{
		if (loadingScreenIsRunning == true)
			return;

		FragmentTransaction tempTransaction;

		loadingScreenIsRunning = true;

		if (manager == null) manager = getSupportFragmentManager();

		if (manager.findFragmentByTag("loading") == null)
		{
			tempTransaction = manager.beginTransaction();
			layoutLoadingScreen.setClickable(true);
			tempTransaction.add(R.id.activity_main_framelayout_loading, fragmentLoadingScreen, "loading");
			try { tempTransaction.commit(); } catch (Exception e) { };
		}
		else
		{
			tempTransaction = manager.beginTransaction();
			tempTransaction.show(fragmentLoadingScreen);
			try { tempTransaction.commit(); } catch (Exception e) { };  // crash fix
		}



		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					FragmentTransaction tempTransaction = manager.beginTransaction();
					layoutLoadingScreen.setClickable(false);
					tempTransaction.hide(fragmentLoadingScreen);
					tempTransaction.commit();
					loadingScreenIsRunning = false;
				}
				catch (Exception e) {}
			}
		}, timerLength);
	}



	/* ==============================================================================================================================================================
	/																	Tokens
	/ =============================================================================================================================================================*/

	// Update the tokens textView
	private void updateCurrentTokens()
	{

		if (CommonStuff.gameState.freePlayModeOn == false || CommonStuff.gameState.infiniteTokens == false)
			new TokensTask(this).execute();
		else
			afterUpdateTokens();
	}
	// callback for updateCurrentTokens
	public void afterUpdateTokens()
	{

		if (CommonStuff.gameState.currentMainFragment == FragmentConstants.FRAGMENT_MAIN_GAME)
		{
			imageViewToken.setVisibility(View.VISIBLE);
			return;
		}

		if (CommonStuff.gameState.infiniteTokens == true && CommonStuff.gameState.freePlayModeOn == false)
		{
			imageViewToken.getLayoutParams().width = (int) getResources().getDimension(R.dimen.dimen_common_button_size_token_infinity);
			Glide.with(this).load(R.drawable.image_common_token_infinity).dontTransform().into(imageViewToken);
			textViewTokens.setVisibility(View.GONE);
			textViewTokens.setText("");
		}
		else
		{
			imageViewToken.getLayoutParams().width = (int) getResources().getDimension(R.dimen.dimen_common_button_size_token);
			imageViewToken.requestLayout();
			textViewTokens.setVisibility(View.VISIBLE);

			if (CommonStuff.gameState.freePlayModeOn == false)
			{
				Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken);
				textViewTokens.setText(CommonStuff.gameState.currentTokens + "");
			}
			else
			{
				Glide.with(this).load(R.drawable.image_common_token_freeplay).into(imageViewToken);
				textViewTokens.setText(CommonStuff.gameState.currentFreePlayTokens + "");
			}
		}

		if (fragmentList.get(FragmentConstants.FRAGMENT_TOKENS) != null)
			((FragmentTokens) fragmentList.get(FragmentConstants.FRAGMENT_TOKENS)).setTokensTextView();

		imageViewToken.setVisibility(View.VISIBLE);
		imageViewToken.requestLayout();
	}


	// open the tokens screen
	private void openTokenScreen()
	{

		goToPage(FragmentConstants.FRAGMENT_TOKENS);
	}

	/* ==============================================================================================================================================================
	/																	Other stuff
	/ =============================================================================================================================================================*/


	// Opens the navigation drawer
	public void openNavigationDrawer()
	{

		super.openDrawer(this);
	}


	// Banner Ad Shown on exit
	private void startAdActivity()
	{
		CommonStuff.gameState.save(getApplicationContext());

		if (CommonStuff.databaseGameState.getShowAds() == true)
		{
			adActivityStarted = true;
			BannerAdActivity.showAd();
		}
		else
			finish();
	}


	// Set the color of the background
	public void setBackgroundTheme()
	{

		if (CommonStuff.gameState.lightThemeEnabled == true)
		{
			layoutMain.setBackgroundColor(getResources().getColor(R.color.color_white));
			fragmentLoadingScreen.setBackgroundColor(true);
		}
		else
		{
			layoutMain.setBackgroundColor(getResources().getColor(R.color.color_gray_dark));
			fragmentLoadingScreen.setBackgroundColor(false);
		}
	}


	// Set the text of the title
	public void setTextViewTitle(String text)
	{

		textViewTitle.setText(Html.fromHtml(text));
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// Listeners
	@Override
	public void onClick(View v)
	{

		CommonStuff.playClickSound(this.getApplicationContext());

		tempViewId = v.getId();

		v.startAnimation(animationClick);
	}

	// Called after animation is finished
	private void afterOnClick()
	{

		switch (tempViewId)
		{
			case R.id.activity_main_layout_token :
				if (CommonStuff.gameState.tutorialModeOn == true)
				{
					CommonStuff.showToastTutorialNotAvailable(this);
					return;
				}
				if (CommonStuff.gameState.infiniteTokens == false && CommonStuff.gameState.freePlayModeOn == false)
					openTokenScreen();
				break;

			case R.id.activity_main_button_nav_drawer :
				if (CommonStuff.gameState.tutorialModeOn == true)
				{
					CommonStuff.showToastTutorialNotAvailable(this);
					return;
				}
				openNavigationDrawer();
				break;

			case R.id.activity_main_button_back :
				backPressed();
				break;
		}
	}


	// Animation End - pause for 200ms to let animation finish
	@Override
	public void onAnimationEnd(Animation animation)
	{

		if (animation == animationClick)
		{
			new Handler().postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					afterOnClick();
				}
			}, GeneralConstants.ANIMATION_PAUSE_DURATION);
		}
		else
			super.onAnimationEnd(null);
	}


	// onBackPressed
	@Override
	public void onBackPressed()
	{
		CommonStuff.playClickSound(this.getApplicationContext());

		if (adActivityStarted == true)
		{
			finish();
			return;
		}

		if (super.drawerLayout.isDrawerOpen(Gravity.START))
		{
			super.drawerLayout.closeDrawers();
			return;
		}

		backPressed();
	}


	// called when hardware or software back is pressed
	public void backPressed()
	{

		if (CommonStuff.gameState.tutorialModeOn == true)
		{
			DialogMsgBoxGeneral.newInstance(getResources().getString(R.string.string_tutorial_quit_title),
					getResources().getString(R.string.string_tutorial_quit)
					, false, true, DialogConstants.QUIT_TUTORIAL)
					.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
			return;
		}

		switch(CommonStuff.gameState.currentMainFragment)
		{
			case FragmentConstants.FRAGMENT_MAIN_MENU :
				startAdActivity();
				break;

			case FragmentConstants.FRAGMENT_DIFFICULTY :
				goToPage(FragmentConstants.FRAGMENT_MAIN_MENU);
				break;

			case FragmentConstants.FRAGMENT_CATEGORIES :
				goToPage(FragmentConstants.FRAGMENT_DIFFICULTY);
				break;

			case FragmentConstants.FRAGMENT_LEVELS :
				goToPage(FragmentConstants.FRAGMENT_CATEGORIES);
				break;

			case FragmentConstants.FRAGMENT_LOCATIONS :
				goToPage(FragmentConstants.FRAGMENT_LEVELS);
				break;

			case FragmentConstants.FRAGMENT_MAIN_GAME :
				if (CommonStuff.gameState.freePlayModeOn == false)
					((FragmentMainGame)fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME)).returnToLocationsScreen();
				else
					goToPage(FragmentConstants.FRAGMENT_MAIN_MENU);
				break;

			case FragmentConstants.FRAGMENT_HINTS :
				goToPage(FragmentConstants.FRAGMENT_MAIN_GAME);
				break;

			case FragmentConstants.FRAGMENT_HINT_DATA :
				fragmentHintDataDismissed();
				break;

			case FragmentConstants.FRAGMENT_SETTINGS :
				goToPage(CommonStuff.gameState.previousMainFragment);
				break;

			case FragmentConstants.FRAGMENT_TOKENS :
				goToPage(CommonStuff.gameState.previousMainFragment);
				break;
		}
	}



	// dispatchTouchEvent - for adding map markers
	@Override
	public boolean dispatchTouchEvent(MotionEvent m)
	{

		if (CommonStuff.gameState != null && fragmentList != null && fragmentList.containsKey(FragmentConstants.FRAGMENT_MAIN_GAME_MAP) == true)
		{
			CommonStuff.gameState.canAddMapMarker = false;

			// Get initial position touched
			if (m.getAction() == MotionEvent.ACTION_DOWN)
				touchDown = new Point((int) m.getX(), (int) m.getY());

			// Get final position touched
			if (m.getAction() == MotionEvent.ACTION_UP)
			{
				touchUp = new Point((int) m.getX(), (int) m.getY());

				// Initial position touched must equal final position touched +- 30 pixels
				if (touchDown != null && touchUp != null)
					if (touchUp.x > touchDown.x - 30 && touchUp.x < touchDown.x + 30)
					{
						if (touchUp.y > touchDown.y - 30 && touchUp.y < touchDown.y + 30)
						{
							CommonStuff.gameState.canAddMapMarker = true;
							CommonStuff.gameState.mapMarkerPoint = touchUp;

							if (CommonStuff.gameState.hotColdHintIsRunning == true)
								((FragmentMainGameMap)fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME_MAP)).addHotColdTimerMarker(touchUp.x, touchUp.y);
						}
					}
			}
		}

		return super.dispatchTouchEvent(m);
	}



	/* ==============================================================================================================================================================
	/																	Music
	/ =============================================================================================================================================================*/

	private boolean musicServiceBound = false;
	public MusicService musicService = null;

	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			musicService = ((MusicService.ServiceBinder) binder).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			musicService = null;
		}
	};

	void doBindService()
	{
 		bindService(new Intent(this,MusicService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		musicServiceBound = true;
	}



	public void doUnbindService()
	{

		if (musicServiceBound == true && music != null)
		{
			stopService(music);
			unbindService(serviceConnection);
      		musicServiceBound = false;
		}
	}

	public void nextMusicTrack()
	{
		musicService.nextTrack(false);
	}

	public void previousMusicTrack()
	{
		musicService.previousTrack();
	}


	/* ==============================================================================================================================================================
	/																	In App Billing
	/ =============================================================================================================================================================*/


	public IabHelper iabHelper;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    CommonStuff.myLog("onActivityResult(" + requestCode + ", " + resultCode + ", " + data);

	    if (iabHelper == null)
	    	return;

	    if (iabHelper.handleActivityResult(requestCode, resultCode, data) == false)
	    {
	    	super.onActivityResult(requestCode, resultCode, data);
	    }
	    else
	    	CommonStuff.myLog("onActivityResult handled by iabHelper");
	}


	// setupInAppBilling
	public void setupInAppBilling()
	{

		iabHelper = new IabHelper(this.getApplicationContext(), GeneralConstants.inAppBillingKey);

		iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
		{
			@Override
			public void onIabSetupFinished(IabResult result)
			{
				iabHelper.flagEndAsync();


				if (result.isSuccess() == false)
				{
					CommonStuff.myLog("Problem with in app billing : " + result);
					return;
				}

				if (iabHelper == null)
					return;

				ArrayList<String> skuList = new ArrayList<String>();
				skuList.add(GeneralConstants.SKU_TOKENS_1000);
				skuList.add(GeneralConstants.SKU_TOKENS_5500);
				skuList.add(GeneralConstants.SKU_TOKENS_11000);
				skuList.add(GeneralConstants.SKU_TOKENS_INFINITE);

				iabHelper.queryInventoryAsync(true, skuList, queryInventoryFinishedListener);
			}
		});
	}


	// queryInventoryFinishedListener
	private IabHelper.QueryInventoryFinishedListener queryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener()
	{
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory)
		{
			iabHelper.flagEndAsync();

			CommonStuff.myLog("query inventory finished");

			if (iabHelper == null)
				return;

			if (result.isFailure())
			{
				CommonStuff.myLog("Failed to query Inventory : " + result);
				return;
			}

			// Check for purchased items
			Purchase infiniteTokens = inventory.getPurchase(GeneralConstants.SKU_TOKENS_INFINITE);
			CommonStuff.gameState.infiniteTokens = infiniteTokens == null ? false : true;

			Purchase tokens1000 = inventory.getPurchase(GeneralConstants.SKU_TOKENS_1000);
			if (tokens1000 != null)
				iabHelper.consumeAsync(inventory.getPurchase(GeneralConstants.SKU_TOKENS_1000), consumeFinishedListener);

			Purchase tokens5500 = inventory.getPurchase(GeneralConstants.SKU_TOKENS_5500);
			if (tokens5500 != null)
				iabHelper.consumeAsync(inventory.getPurchase(GeneralConstants.SKU_TOKENS_5500), consumeFinishedListener);

			Purchase tokens11000 = inventory.getPurchase(GeneralConstants.SKU_TOKENS_11000);
			if (tokens11000 != null)
				iabHelper.consumeAsync(inventory.getPurchase(GeneralConstants.SKU_TOKENS_11000), consumeFinishedListener);

			// Get prices of items
			if (inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_1000) != null && inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_5500) != null &&
					inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_11000) != null && inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_INFINITE) != null)
			{
				CommonStuff.gameState.tokenPrices = new String[4];
				CommonStuff.gameState.tokenPrices[0] = inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_1000).getPrice();
				CommonStuff.gameState.tokenPrices[1] = inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_5500).getPrice();
				CommonStuff.gameState.tokenPrices[2] = inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_11000).getPrice();
				CommonStuff.gameState.tokenPrices[3] = inventory.getSkuDetails(GeneralConstants.SKU_TOKENS_INFINITE).getPrice();
			}
			else
			{
				CommonStuff.gameState.tokenPrices = new String[4];
				CommonStuff.gameState.tokenPrices[0] = "$1.00";
				CommonStuff.gameState.tokenPrices[1] = "$5.00";
				CommonStuff.gameState.tokenPrices[2] = "$10.00";
				CommonStuff.gameState.tokenPrices[3] = "$20.00";
			}

			CommonStuff.myLog("Inventory query complete");
		}
	};


	// consumeFinishedListener
	private IabHelper.OnConsumeFinishedListener consumeFinishedListener = new IabHelper.OnConsumeFinishedListener()
	{
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result)
		{
			iabHelper.flagEndAsync();

			CommonStuff.myLog("Consume finished, Purchase : " + purchase + ", result : " + result);

			if (iabHelper == null)
				return;

			if (result.isSuccess())
			{
				if (purchase.getSku().equals(GeneralConstants.SKU_TOKENS_1000))
				{
					CommonStuff.databaseGameState.updateNumberOfTokens(1000);
					updateCurrentTokens();
				}
				else if (purchase.getSku().equals(GeneralConstants.SKU_TOKENS_5500))
				{
					CommonStuff.databaseGameState.updateNumberOfTokens(5500);
					updateCurrentTokens();
				}
				else if (purchase.getSku().equals(GeneralConstants.SKU_TOKENS_11000))
				{
					CommonStuff.databaseGameState.updateNumberOfTokens(11000);
					updateCurrentTokens();
				}
				else
					CommonStuff.myLog("Couldn't match SKU after consumption - nothing updated");
			}
		}
	};


	// purchaseFinishedListener
	public IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener()
	{
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase)
		{
			iabHelper.flagEndAsync();

			CommonStuff.myLog("Purchase finished, Purchase : " + purchase + ", result : " + result);

			if (iabHelper == null)
				return;

			if (result.isFailure())
			{
				CommonStuff.myLog("Error making purchase : " + result);
				return;
			}

			CommonStuff.myLog("Purchase successful !");

			if (purchase.getSku().equals(GeneralConstants.SKU_TOKENS_1000))
			{
				iabHelper.consumeAsync(purchase, consumeFinishedListener);
				CommonStuff.databaseGameState.setShowAds(false);
			}
			else if (purchase.getSku().equals(GeneralConstants.SKU_TOKENS_5500))
			{
				iabHelper.consumeAsync(purchase, consumeFinishedListener);
				CommonStuff.databaseGameState.setShowAds(false);
			}
			else if (purchase.getSku().equals(GeneralConstants.SKU_TOKENS_11000))
			{
				iabHelper.consumeAsync(purchase, consumeFinishedListener);
				CommonStuff.databaseGameState.setShowAds(false);
			}
			else if (purchase.getSku().equals(GeneralConstants.SKU_TOKENS_INFINITE))
			{
				CommonStuff.gameState.infiniteTokens = true;
				CommonStuff.databaseGameState.setShowAds(false);
				updateCurrentTokens();
			}
			else
				CommonStuff.myLog("Couldn't match SKU after purchase - nothing updated");
		}
	};

	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }
}
