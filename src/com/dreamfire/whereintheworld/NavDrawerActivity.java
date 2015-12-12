package com.dreamfire.whereintheworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class NavDrawerActivity extends FragmentActivity implements OnTouchListener, AnimationListener
{
    public DrawerLayout drawerLayout;

    private ImageButton buttonNextTrack, buttonPreviousTrack, buttonBack;
    private LinearLayout layoutMusic;
    private MyButton buttonMainMenu, buttonDifficulty, buttonCategory, buttonLevel, buttonLocation, buttonBuyTokens, buttonSettings, buttonExit;
    private MyTextView textViewDifficulty, textViewCategory, textViewLevel, textViewTokens;
    private View viewSeparatorUnderTokens;

    private int layoutId;

    private Animation animationClick;
    private int tempViewId;

    private Activity callingActivity;

	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// OnCreate
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		layoutId = bundle.getInt("layout");
		setContentView(layoutId);

		setupNavDrawerUi();

		showHideStatusBar();
	}


	// Setup Ui
	@SuppressLint("ClickableViewAccessibility")
	public void setupNavDrawerUi()
	{
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		layoutMusic = (LinearLayout) findViewById(R.id.nav_drawer_layout_music);
		buttonNextTrack = (ImageButton) findViewById(R.id.nav_drawer_button_music_next);
		buttonPreviousTrack = (ImageButton) findViewById(R.id.nav_drawer_button_music_previous);
		buttonBack = (ImageButton) findViewById(R.id.nav_drawer_button_back);
		buttonMainMenu = (MyButton) findViewById(R.id.nav_drawer_button_goto_mainmenu);
		buttonDifficulty = (MyButton) findViewById(R.id.nav_drawer_button_goto_difficulties);
		buttonCategory = (MyButton) findViewById(R.id.nav_drawer_button_goto_categories);
		buttonLevel = (MyButton) findViewById(R.id.nav_drawer_button_goto_levels);
		buttonLocation = (MyButton) findViewById(R.id.nav_drawer_button_goto_locations);
		buttonBuyTokens = (MyButton) findViewById(R.id.nav_drawer_button_buy_tokens);
		buttonSettings = (MyButton) findViewById(R.id.nav_drawer_button_settings);
		buttonExit = (MyButton) findViewById(R.id.nav_drawer_button_exit);
		textViewDifficulty = (MyTextView) findViewById(R.id.nav_drawer_textview_difficulty);
		textViewCategory = (MyTextView) findViewById(R.id.nav_drawer_textview_category);
		textViewLevel = (MyTextView) findViewById(R.id.nav_drawer_textview_level);
		textViewTokens = (MyTextView) findViewById(R.id.nav_drawer_textview_tokens);
		viewSeparatorUnderTokens = findViewById(R.id.nav_drawer_separator_under_tokens);

		buttonNextTrack.setOnTouchListener(this);
		buttonPreviousTrack.setOnTouchListener(this);
        buttonBack.setOnTouchListener(this);
        buttonMainMenu.setOnTouchListener(this);
        buttonDifficulty.setOnTouchListener(this);
        buttonCategory.setOnTouchListener(this);
        buttonLevel.setOnTouchListener(this);
        buttonLocation.setOnTouchListener(this);
        buttonBuyTokens.setOnTouchListener(this);
        buttonSettings.setOnTouchListener(this);
        buttonExit.setOnTouchListener(this);

        drawerLayout.setFocusableInTouchMode(false);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        animationClick = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.button);
		animationClick.setAnimationListener(this);


		try
		{
			Glide.with(this).load(R.drawable.image_music_next).into(buttonNextTrack);
			Glide.with(this).load(R.drawable.image_music_previous).into(buttonPreviousTrack);
			Glide.with(this).load(R.drawable.image_common_back_arrow).into(buttonBack);
		}
		catch (Exception e) {}


		if (CommonStuff.gameState.infiniteTokens == true)
        {
        	buttonBuyTokens.setVisibility(View.GONE);
        	viewSeparatorUnderTokens.setVisibility(View.GONE);
        }
	}


	/* ==============================================================================================================================================================
	/																	Other methods
	/ =============================================================================================================================================================*/


	// Open the drawer
	void openDrawer(Activity callingActivity)
	{
		this.callingActivity = callingActivity;
		drawerLayout.openDrawer(Gravity.LEFT);

		if (CommonStuff.gameState.musicEnabled == true)
			layoutMusic.setVisibility(View.VISIBLE);
		else
			layoutMusic.setVisibility(View.GONE);

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Nav Drawer");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());
	}


	// Updates the stats on the right side of the navDrawer
	public void updateStats(long tokens, long points)
	{

		String[] levelNames = getResources().getStringArray(R.array.string_array_common_level_names);
		String[] difficultyNames = getResources().getStringArray(R.array.string_array_common_difficulty_names);

		try
		{
			textViewDifficulty.setText(getResources().getString(R.string.string_navdrawer_difficulty_text) +
					difficultyNames[CommonStuff.gameState.currentDifficultyIndex]);
			textViewCategory.setText(getResources().getString(R.string.string_navdrawer_category_text) +
					CommonStuff.gameState.currentCategoryName[CommonStuff.gameState.currentDifficultyIndex].replace("_", " "));
			textViewLevel.setText(getResources().getString(R.string.string_navdrawer_level_text) +
					levelNames[CommonStuff.gameState.currentLevelIndex]);
			textViewTokens.setText(getResources().getString(R.string.string_navdrawer_tokens_text) +
					(CommonStuff.gameState.infiniteTokens == true ? getResources().getString(R.string.string_tokens_infinite) : tokens));
		}
		catch (Exception e) { }
	}


	// Show/hide the status bar
	private void showHideStatusBar()
	{

		if (CommonStuff.gameState.notificationBarEnabled == true)
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		else
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// Listeners
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{

		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			CommonStuff.playClickSound(this.getApplicationContext());

			tempViewId = v.getId();

			v.startAnimation(animationClick);
		}

		return false;
	}


	// Animation End
	@Override
	public void onAnimationEnd(Animation animation)
	{

		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				afterNavDrawerOnClick();
			}
		}, GeneralConstants.ANIMATION_PAUSE_DURATION);
	}


	// After onClick
	private void afterNavDrawerOnClick()
	{

		if (CommonStuff.gameState.isFirstRun == true && tempViewId != R.id.nav_drawer_button_back &&
				tempViewId != R.id.nav_drawer_button_music_next && tempViewId != R.id.nav_drawer_button_music_previous && tempViewId != R.id.nav_drawer_button_exit)
		{
			onFirstRun(R.string.string_mainmenu_buton_try_tutorial);
			return;
		}

		if (tempViewId != R.id.nav_drawer_button_back && tempViewId != R.id.nav_drawer_button_music_next && tempViewId != R.id.nav_drawer_button_music_previous &&
				tempViewId != R.id.nav_drawer_button_exit && tempViewId != R.id.nav_drawer_button_buy_tokens && tempViewId != R.id.nav_drawer_button_settings)
			if (CommonStuff.gameState.currentMainFragment == FragmentConstants.FRAGMENT_MAIN_GAME && CommonStuff.gameState.hintUsed == true)
			{
				DialogMsgBoxGeneral.newInstance(
						getResources().getString(R.string.string_maingame_confirm),
						getResources().getString(R.string.string_maingame_lose_hints) + GeneralConstants.newLine +
						getResources().getString(R.string.string_maingame_lose_hints2),
						false, true, DialogConstants.NAV_DRAWER_HINT_USED_CHECK)
						.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

				return;
			}

		doClick();
	}


	// do the click from above method
	public void doClick()
	{

		switch (tempViewId)
		{
			case R.id.nav_drawer_button_goto_mainmenu :
				((ActivityMain)callingActivity).goToPage(FragmentConstants.FRAGMENT_MAIN_MENU);
				break;

			case R.id.nav_drawer_button_goto_difficulties :
				CommonStuff.gameState.freePlayModeOn = false;
				((ActivityMain)callingActivity).goToPage(FragmentConstants.FRAGMENT_DIFFICULTY);
				break;

			case R.id.nav_drawer_button_goto_categories :
				CommonStuff.gameState.freePlayModeOn = false;
				((ActivityMain)callingActivity).goToPage(FragmentConstants.FRAGMENT_CATEGORIES);
				break;

			case R.id.nav_drawer_button_goto_levels :
				CommonStuff.gameState.freePlayModeOn = false;
				((ActivityMain)callingActivity).goToPage(FragmentConstants.FRAGMENT_LEVELS);
				break;

			case R.id.nav_drawer_button_goto_locations :
				CommonStuff.gameState.freePlayModeOn = false;
				((ActivityMain)callingActivity).goToPage(FragmentConstants.FRAGMENT_LOCATIONS);
				break;

			case R.id.nav_drawer_button_buy_tokens :
				((ActivityMain)callingActivity).goToPage(FragmentConstants.FRAGMENT_TOKENS);
				break;

			case R.id.nav_drawer_button_settings :
				((ActivityMain)callingActivity).goToPage(FragmentConstants.FRAGMENT_SETTINGS);
				break;

			case R.id.nav_drawer_button_exit :
				DialogMsgBoxGeneral.newInstance(
						getResources().getString(R.string.string_navdrawer_exit_title),
						getResources().getString(R.string.string_navdrawer_exit_message),
						false, true, DialogConstants.EXIT_GAME)
						.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
				break;

			case R.id.nav_drawer_button_music_next :
				((ActivityMain)callingActivity).nextMusicTrack();
				return;

			case R.id.nav_drawer_button_music_previous :
				((ActivityMain)callingActivity).previousMusicTrack();
				return;

			case R.id.nav_drawer_button_back :
				drawerLayout.closeDrawer(Gravity.LEFT);
				break;
		}

		drawerLayout.closeDrawer(Gravity.LEFT);
	}


	// onBackPressed
	@Override
	public void onBackPressed()
	{

		super.onBackPressed();
		drawerLayout.closeDrawer(Gravity.LEFT);
	}


	// On first run
	private void onFirstRun(int stringResource)
	{

		CommonStuff.gameState.isFirstRun = false;

		DialogMsgBoxGeneral.newInstance("", getResources()
				.getString(stringResource), false, false, DialogConstants.NO_CALLBACK)
				.show(getSupportFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
	}



	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }
}
