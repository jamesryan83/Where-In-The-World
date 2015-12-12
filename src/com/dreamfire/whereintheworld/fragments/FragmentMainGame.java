package com.dreamfire.whereintheworld.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.constants.HintNameConstants;
import com.dreamfire.whereintheworld.constants.MainGameUiStateConstants;
import com.dreamfire.whereintheworld.constants.MapStateConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.dialogs.DialogPostGame;
import com.dreamfire.whereintheworld.dialogs.DialogPostGameInfo;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.FreePlayGameModeTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.MainGameLocationCompleteTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.RegularGameModeTask;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.ScoreClass;
import com.dreamfire.whereintheworld.stuff.ScoreData;
import com.dreamfire.whereintheworld.stuff.StopWatch;
import com.dreamfire.whereintheworld.stuff.TutorialStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


@SuppressLint("SetJavaScriptEnabled")
public class FragmentMainGame extends AbstractFragment implements OnLongClickListener
{
	private boolean postGameDialogHasBeenDisplayed;
	public boolean guessHasBeenAccepted;
	public boolean isOnMapView;  // true for google map, false for street view
	private FragmentMainGameMap mapFragment;

	private WebView webView;

	private ViewSwitcher viewSwitcher;

	private ImageButton buttonNavDrawer, buttonZoomIn, buttonZoomOut;
	private ImageButton buttonGotoMarker, buttonMapStreet, buttonBack, buttonHints;
	public ImageButton buttonHideHints;
	private MyButton buttonLeft, buttonMiddle, buttonRight;
	private MyTextView textViewDistanceAnswer, textViewHint, textViewHintStart;
	private LinearLayout linearLayoutLeft, linearLayoutRight;

	public DialogPostGame dialogPostGame;

	private Animation animationScoreScaleZoom, animationScorePause, animationHintTextPause, animationHintTextTranslate;
	public boolean animationIsRunning = false;

	private StopWatch stopWatch;
	private ScoreData scoreData;

	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		isOnMapView = false;
		guessHasBeenAccepted = false;
		postGameDialogHasBeenDisplayed = false;
		CommonStuff.gameState.interactiveHintIsRunning = false;

		View view = inflater.inflate(R.layout.fragment_main_game, container, false);

		// UI Controls
		viewSwitcher = (ViewSwitcher) view.findViewById(R.id.maingame_viewswitcher);
		linearLayoutLeft = (LinearLayout) view.findViewById(R.id.maingame_linearlayout_left);
		linearLayoutRight = (LinearLayout) view.findViewById(R.id.maingame_linearlayout_right);
		textViewDistanceAnswer = (MyTextView) view.findViewById(R.id.maingame_textview_distance);
		textViewHint = (MyTextView) view.findViewById(R.id.maingame_textview_hint);
		textViewHintStart = (MyTextView) view.findViewById(R.id.maingame_textview_hint_start);
		buttonMapStreet = (ImageButton) view.findViewById(R.id.maingame_button_map_street);
		buttonHints = (ImageButton) view.findViewById(R.id.maingame_button_hint);
		buttonHideHints = (ImageButton) view.findViewById(R.id.maingame_button_hide_hints);
		buttonBack = (ImageButton) view.findViewById(R.id.maingame_button_back);
		buttonNavDrawer = (ImageButton) view.findViewById(R.id.maingame_button_nav_drawer);
		buttonZoomIn = (ImageButton) view.findViewById(R.id.maingame_button_zoom_in);
		buttonZoomOut = (ImageButton) view.findViewById(R.id.maingame_button_zoom_out);
		buttonGotoMarker = (ImageButton) view.findViewById(R.id.maingame_button_go_to_marker);
		buttonLeft = (MyButton) view.findViewById(R.id.maingame_button_left);
		buttonMiddle = (MyButton) view.findViewById(R.id.maingame_button_middle);
		buttonRight = (MyButton) view.findViewById(R.id.maingame_button_right);
		webView = (WebView) view.findViewById(R.id.maingame_webview);


		// Listeners
		buttonMapStreet.setOnClickListener(this);
		buttonHints.setOnClickListener(this);
		buttonHideHints.setOnClickListener(this);
		buttonBack.setOnClickListener(this);
		buttonNavDrawer.setOnClickListener(this);
		buttonZoomIn.setOnClickListener(this);
		buttonZoomOut.setOnClickListener(this);
		buttonGotoMarker.setOnClickListener(this);
		buttonLeft.setOnClickListener(this);
		buttonMiddle.setOnClickListener(this);
		buttonRight.setOnClickListener(this);


		// Long click Listeners
		buttonMapStreet.setOnLongClickListener(this);
		buttonHints.setOnLongClickListener(this);
		buttonHideHints.setOnLongClickListener(this);
		buttonBack.setOnLongClickListener(this);
		buttonNavDrawer.setOnLongClickListener(this);
		buttonZoomIn.setOnLongClickListener(this);
		buttonZoomOut.setOnLongClickListener(this);
		buttonGotoMarker.setOnLongClickListener(this);
		buttonLeft.setOnLongClickListener(this);
		buttonMiddle.setOnClickListener(this);
		buttonRight.setOnLongClickListener(this);

		updateUi();

		CommonStuff.gameState.hintUsed = false;

		if (CommonStuff.gameState.freePlayModeOn == false)
			new RegularGameModeTask(mainActivity).execute();
		else
			new FreePlayGameModeTask(mainActivity).execute();

		try
		{
			Glide.with(this).load(R.drawable.image_common_nav_drawer).into(buttonNavDrawer);
			Glide.with(this).load(R.drawable.image_maingame_zoom_in).into(buttonZoomIn);
			Glide.with(this).load(R.drawable.image_maingame_zoom_out).into(buttonZoomOut);
			Glide.with(this).load(R.drawable.image_common_back_arrow).into(buttonBack);
			Glide.with(this).load(R.drawable.image_maingame_map).into(buttonMapStreet);
			Glide.with(this).load(R.drawable.image_maingame_hints).into(buttonHints);
			Glide.with(this).load(R.drawable.image_maingame_zoom_to_marker).into(buttonGotoMarker);
			Glide.with(this).load(R.drawable.image_maingame_question_mark_crossed_out).into(buttonHideHints);
		}
		catch (Exception e) {}

		return view;
	}


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			Glide.with(this).load(R.drawable.image_common_nav_drawer).into(buttonNavDrawer);
			Glide.with(this).load(R.drawable.image_maingame_zoom_in).into(buttonZoomIn);
			Glide.with(this).load(R.drawable.image_maingame_zoom_out).into(buttonZoomOut);
			Glide.with(this).load(R.drawable.image_common_back_arrow).into(buttonBack);
			Glide.with(this).load(R.drawable.image_maingame_map).into(buttonMapStreet);
			Glide.with(this).load(R.drawable.image_maingame_hints).into(buttonHints);
			Glide.with(this).load(R.drawable.image_maingame_zoom_to_marker).into(buttonGotoMarker);
			Glide.with(this).load(R.drawable.image_maingame_question_mark_crossed_out).into(buttonHideHints);
		}
		catch (Exception e) {}
	}


	// Update Ui
	@Override
	public void updateUi()
	{
		super.updateUi();
	}


	// called after DatabaseStartupTask is run
	@Override
	@SuppressLint({ "InlinedApi", "NewApi" })
	public void setupUiAfterAsync()
	{
		super.setupUiAfterAsync();

		if (CommonStuff.gameState.freePlayModeOn == false)
		{
			Tracker tracker = CommonStuff.tracker;
			tracker.setScreenName("Fragment Main Game");
			tracker.send(new HitBuilders.ScreenViewBuilder().build());
		}
		else
		{
			Tracker tracker = CommonStuff.tracker;
			tracker.setScreenName("Fragment Main Game Free Play");
			tracker.send(new HitBuilders.ScreenViewBuilder().build());
		}

		animationScoreScaleZoom = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.maingame_score_animation);
		animationScorePause = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.pause_500);
		animationHintTextPause = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.pause_1000);
		animationHintTextTranslate = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.maingame_hint_text_animation);

		animationScoreScaleZoom.setAnimationListener(this);
		animationScorePause.setAnimationListener(this);
		animationHintTextPause.setAnimationListener(this);
		animationHintTextTranslate.setAnimationListener(this);

		mainActivity.replaceFragmentMainGameMap();
		mapFragment = (FragmentMainGameMap) ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME_MAP);

		CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_loading_streetview), true);

		// webView
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		if (Build.VERSION.SDK_INT >= 11) webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.loadUrl("file:///android_asset/webpages/streetview.html");

		setUiControlStates(MainGameUiStateConstants.STARTUP);

		//mainActivity.stopLoadingScreen();

		stopWatch = new StopWatch();
		viewSwitcher.showNext();

		stopWatch.start();

		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true)
		{
			mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog7(mainActivity.getApplicationContext()), DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[6] = true;
		}
	}


	/* ==============================================================================================================================================================
	/																After Answer/Cancel Clicked Stuff
	/ =============================================================================================================================================================*/


	// Run when left green button is clicked
	private void leftButtonClicked()
	{

		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true && CommonStuff.gameState.tutorialDialogsSeen[8] == false)
		{
			mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog9(mainActivity.getApplicationContext()), DialogConstants.TUTORIAL_9);
			CommonStuff.gameState.tutorialDialogsSeen[8] = true;
			return;
		}

		if (postGameDialogHasBeenDisplayed == true)
		{
			showPostGameDialog();
			return;
		}

		if (mapFragment.guessMarkerExists == true)
		{
			stopWatch.stop();

			if (CommonStuff.isInternetConnected(mainActivity.getApplicationContext()) == true)
			{
				setUiControlStates(MainGameUiStateConstants.ACCEPT_CLICKED);

				textViewDistanceAnswer.startAnimation(AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), android.R.anim.slide_in_left));
				mapFragment.drawPolyline();
			}
			else
			{
				DialogMsgBoxGeneral.newInstance("", getResources()
						.getString(R.string.string_maingame_not_connected_to_internet), false, false, DialogConstants.NO_CALLBACK)
						.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
			}
		}
	}


	// Run when middle green button is clicked
	private void middleButtonClicked()
	{

		if (mapFragment.mapState == MapStateConstants.HINT_CAMERA_ZOOM_ENABLED)
			mapFragment.removeCameraZoomHint();
	}


	// Run when right green button is clicked
	private void rightButtonClicked()
	{

		if (guessHasBeenAccepted == true)
			returnToLocationsScreen();
		else
			setUiControlStates(MainGameUiStateConstants.GUESS_MARKER_REMOVED);
	}


	/* ==============================================================================================================================================================
	/																	Animation Methods
	/ =============================================================================================================================================================*/

	// Start the animation of the answer textView
	public void startTextViewAnswerAnimation()
	{

		textViewDistanceAnswer.startAnimation(animationScoreScaleZoom);
	}


	// Update the text of the distance textView
	public void updateTextViewAnswerText(double distance)
	{

		String unit = CommonStuff.gameState.kilometresEnabled == true ? " km" : " miles";
		if (CommonStuff.gameState.kilometresEnabled == false) distance *= GeneralConstants.KM_TO_MILES;
		textViewDistanceAnswer.setText((distance > 1) ? String.format("%.0f" + unit, distance) : String.format("%.2f" + unit, distance));
	}


	// Pause animation
	private void startAnimationHintPause()
	{

		animationIsRunning = true;
		textViewHintStart.setText(getResources().getString(R.string.string_maingame_ready));
		textViewHintStart.setVisibility(View.VISIBLE);
		textViewHintStart.startAnimation(animationHintTextPause);
	}


	// Translate animation
	private void startAnimationTranslate()
	{

		textViewHintStart.setText(getResources().getString(R.string.string_maingame_go));
		textViewHintStart.startAnimation(animationHintTextTranslate);
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// Button Clicks
	@Override
	public void onClick(View v)
	{

		super.onClick(v);

		v.startAnimation(animationClick);
	}


	// Animation end listener
	@Override
	public void onAnimationEnd(Animation animation)
	{

		if (animation == animationScorePause) // Polyline timer part 4
			showDescriptionInfoDialog();

		else if (animation == animationScoreScaleZoom)
			textViewDistanceAnswer.startAnimation(animationScorePause);

		else if (animation == animationHintTextPause)
			startAnimationTranslate();

		else if (animation == animationHintTextTranslate)
		{
			afterAnimation();
			mapFragment.afterReadyGo();
		}

		else if (animation == animationClick)
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
	}


	// After onClick
	@Override
	public void afterOnClick()
	{

		switch (tempViewId)
		{
			case R.id.maingame_button_map_street :
				if (isOnMapView == true)
					setUiControlStates(MainGameUiStateConstants.SWITCH_TO_STREET);
				else
					setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);
				break;

			case R.id.maingame_button_hint :
				if (CommonStuff.gameState.tutorialModeOn == true && CommonStuff.gameState.tutorialDialogsSeen[9] == false)
				{
					CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_tutorial_not_available_yet), true);
					return;
				}
				mainActivity.goToPage(FragmentConstants.FRAGMENT_HINTS);
				break;

			case R.id.maingame_button_back :
				if (CommonStuff.gameState.tutorialModeOn == true) { mainActivity.backPressed(); return; }
				returnToLocationsScreen();
				break;

			case R.id.maingame_button_nav_drawer :
				if (CommonStuff.gameState.tutorialModeOn == true) { CommonStuff.showToastTutorialNotAvailable(mainActivity); return; }
				mainActivity.openNavigationDrawer();
				break;

			case R.id.maingame_button_zoom_in :
				if (isOnMapView == true)
					mapFragment.zoomIn();
				else
					webView.loadUrl("javascript:zoomInPanorama()");
				break;

			case R.id.maingame_button_zoom_out :
				if (isOnMapView == true)
					mapFragment.zoomOut();
				else
					webView.loadUrl("javascript:zoomOutPanorama()");
				break;

			case R.id.maingame_button_go_to_marker :
				mapFragment.zoomToGuessMarker();
				break;

			case R.id.maingame_button_left :
				leftButtonClicked();
				break;

			case R.id.maingame_button_right :
				rightButtonClicked();
				break;

			case R.id.maingame_button_middle :
				middleButtonClicked();
				break;

			case R.id.maingame_button_hide_hints :
				mapFragment.hideHints();
				break;
		}
	}






	// Long click on buttons shows a tooltip
	@Override
	public boolean onLongClick(View v)
	{

		CommonStuff.playClickSound(mainActivity.getApplicationContext());

		switch (v.getId())
		{
			case R.id.maingame_button_map_street :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_mapstreet), true);
				break;

			case R.id.maingame_button_hint :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_hint), true);
				break;

			case R.id.maingame_button_back :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_back), true);
				break;

			case R.id.maingame_button_nav_drawer :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_navdrawer), true);
				break;

			case R.id.maingame_button_zoom_in :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_zoomin), true);
				break;

			case R.id.maingame_button_zoom_out :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_zoomout), true);
				break;

			case R.id.maingame_button_go_to_marker :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_gotomarker), true);
				break;

			case R.id.maingame_button_left :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_accept), true);
				break;

			case R.id.maingame_button_right :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_cancelguess), true);
				break;

			case R.id.maingame_button_middle :
				if (mapFragment.mapState == MapStateConstants.HINT_CAMERA_ZOOM_ENABLED)
					CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_score1), true);
				break;

			case R.id.maingame_button_hide_hints :
				CommonStuff.showToast(mainActivity, getResources().getString(R.string.string_maingame_longclick_hidehints), true);
				break;
		}

		return true;
	}



	/* ==============================================================================================================================================================
	/																	Map functions
	/ =============================================================================================================================================================*/

	// loads a location through the webview using javascript
	private void loadLocation()
	{
		CommonStuff.myLog("Current location");
		CommonStuff.myLog("Name : " + CommonStuff.gameState.currentLocationRow.name);
		CommonStuff.myLog("Lat : " + CommonStuff.gameState.currentLocationRow.latitude + ", Lng : " + CommonStuff.gameState.currentLocationRow.longitude);

		webView.loadUrl("javascript:loadPanorama(" +
				CommonStuff.gameState.currentLocationRow.latitude + ", " +
				CommonStuff.gameState.currentLocationRow.longitude + ", " +
				CommonStuff.gameState.currentLocationRow.heading + ", " +
				(90 - CommonStuff.gameState.currentLocationRow.tilt) + ")");
	}


	// Class to call javascript and stop other webpages loading
	private class MyWebViewClient extends WebViewClient
	{
		@Override
		public void onPageFinished(WebView view, String url)
		{

			loadLocation();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{

			if (url.equals(webView.getUrl()))
				webView.loadUrl(url);

			return true;
		}
	}



	/* ==============================================================================================================================================================
	/																	Other functions
	/ =============================================================================================================================================================*/

	// UI Control states
	public void setUiControlStates(int state)
	{

		switch (state)
		{
			case MainGameUiStateConstants.STARTUP :
				buttonNavDrawer.setVisibility(CommonStuff.gameState.freePlayModeOn == true ? View.GONE : View.VISIBLE);
				textViewDistanceAnswer.setVisibility(View.GONE);
				textViewHint.setVisibility(View.GONE);
				textViewHintStart.setVisibility(View.GONE);
				buttonLeft.setVisibility(View.GONE);
				buttonRight.setVisibility(View.GONE);
				buttonMiddle.setVisibility(View.GONE);
				buttonGotoMarker.setVisibility(View.GONE);
				buttonHideHints.setVisibility(View.GONE);
				buttonLeft.setText(getResources().getString(R.string.string_maingame_button_accept));
				buttonRight.setText(getResources().getString(R.string.string_common_cancel));
				break;

			case MainGameUiStateConstants.SWITCH_TO_MAP :
				if (isOnMapView == true) break;
				viewSwitcher.showNext();
				Glide.with(this).load(R.drawable.image_maingame_street).into(buttonMapStreet);
				isOnMapView = true;
				if (mapFragment.mapState != MapStateConstants.HINT_CAMERA_ZOOM_ENABLED && mapFragment.needToShowHideHintsButton == true)
					buttonHideHints.setVisibility(View.VISIBLE);
				if (mapFragment.guessMarkerExists == true)
				{
					buttonGotoMarker.setEnabled(true);
					buttonLeft.setVisibility(View.VISIBLE);
					buttonRight.setVisibility(View.VISIBLE);
				}

				// Tutorial
				if (CommonStuff.gameState.tutorialModeOn == true && CommonStuff.gameState.tutorialDialogsSeen[7] == false)
				{
					mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog8(mainActivity.getApplicationContext()), DialogConstants.NO_CALLBACK);
					CommonStuff.gameState.tutorialDialogsSeen[7] = true;
				}

				break;

			case MainGameUiStateConstants.SWITCH_TO_STREET :
				if (isOnMapView == false) break;
				viewSwitcher.showNext();
				Glide.with(this).load(R.drawable.image_maingame_map).into(buttonMapStreet);
				buttonHideHints.setVisibility(View.GONE);
				isOnMapView = false;
				if (mapFragment.guessMarkerExists)
				{
					buttonGotoMarker.setVisibility(View.GONE);
					buttonLeft.setVisibility(View.GONE);
					buttonRight.setVisibility(View.GONE);
				}
				break;

			case MainGameUiStateConstants.GUESS_MARKER_ADDED :
				buttonGotoMarker.setVisibility(View.VISIBLE);
				buttonLeft.setVisibility(View.VISIBLE);
				buttonRight.setVisibility(View.VISIBLE);
				mapFragment.guessMarkerExists = true;
				break;

			case MainGameUiStateConstants.GUESS_MARKER_REMOVED :
				buttonGotoMarker.setVisibility(View.GONE);
				buttonLeft.setVisibility(View.GONE);
				buttonRight.setVisibility(View.GONE);
				mapFragment.removeGuessMarker();
				break;

			case MainGameUiStateConstants.MAP_HINT_STARTED :
				setUiControlStates(MainGameUiStateConstants.GUESS_MARKER_REMOVED);
				startHint();
				break;

			case MainGameUiStateConstants.MAP_HINT_FINISHED :
				finishHint();
				break;

			case MainGameUiStateConstants.ACCEPT_CLICKED :
				mapFragment.setMapViewSettings(false, false);
				linearLayoutLeft.setVisibility(View.GONE);
				linearLayoutRight.setVisibility(View.GONE);
				textViewHint.setVisibility(View.GONE);
				buttonGotoMarker.setVisibility(View.GONE);
				buttonLeft.setVisibility(View.GONE);
				buttonRight.setVisibility(View.GONE);
				buttonHideHints.setVisibility(View.GONE);
				textViewDistanceAnswer.setVisibility(View.VISIBLE);
				textViewDistanceAnswer.setText(getResources().getString(R.string.string_maingame_calculating));
				guessHasBeenAccepted = true;
				break;

			case MainGameUiStateConstants.POST_GAME :
				linearLayoutLeft.setVisibility(View.VISIBLE);
				linearLayoutRight.setVisibility(View.VISIBLE);
				buttonHints.setVisibility(View.GONE);
				textViewDistanceAnswer.setVisibility(View.GONE);
				buttonNavDrawer.setVisibility(View.GONE);
				buttonGotoMarker.setVisibility(View.VISIBLE);
				buttonLeft.setVisibility(View.VISIBLE);
				buttonRight.setVisibility(View.VISIBLE);
				buttonLeft.setText(getResources().getString(R.string.string_maingame_score));
				buttonRight.setText(CommonStuff.gameState.freePlayModeOn == true ?
						getResources().getString(R.string.string_common_return) :
						getResources().getString(R.string.string_dialogpostgame_next));
				buttonMiddle.setVisibility(View.GONE);
				if (mapFragment.needToShowHideHintsButton == true) buttonHideHints.setVisibility(View.VISIBLE);
				mapFragment.setMapViewSettings(true, true);
				break;
		}
	}


	// Return to the locations screen either from this activity or from the PostGameDialog
	public void returnToLocationsScreen()
	{

		if (CommonStuff.gameState.freePlayModeOn == true)
		{
			mainActivity.goToPage(FragmentConstants.FRAGMENT_MAIN_MENU);
			return;
		}

		if (postGameDialogHasBeenDisplayed == true)
		{
			new MainGameLocationCompleteTask(mainActivity).execute(scoreData.totalScore, stopWatch.getElapsedTimeSeconds());
		}
		else
		{
			if (CommonStuff.gameState.hintUsed == true)
			{
				DialogMsgBoxGeneral.newInstance(
						getResources().getString(R.string.string_maingame_confirm),
						getResources().getString(R.string.string_maingame_lose_hints) + GeneralConstants.newLine +
						getResources().getString(R.string.string_maingame_lose_hints2),
						false, true, DialogConstants.GO_TO_LOCATIONS)
						.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

				return;
			}
			else
			{
				mainActivity.goToPage(FragmentConstants.FRAGMENT_LOCATIONS);
			}
		}
	}


	// Run a runnable on UI thread
	public void runOnGameUiThread(Runnable runnable)
	{

		mainActivity.runOnUiThread(runnable);
	}


	/* ==============================================================================================================================================================
	/																	Dialogs
	/ =============================================================================================================================================================*/

	// Callback for ActivityHintData
	public void setMapState()
	{

		mapFragment.setCurrentMapState();
	}


	// Show Post Game Dialog
	public void showPostGameDialog()
	{

		ScoreClass scoreClass = new ScoreClass();

		if (postGameDialogHasBeenDisplayed == false)
			scoreData = scoreClass.calculateScore(mapFragment.getDistanceBetweenGuessAndAnswer(), stopWatch.getElapsedTimeSeconds(), stopWatch.getElapsedTimeMinutes());

		dialogPostGame = new DialogPostGame(mainActivity, scoreData);
		dialogPostGame.setCancelable(false);
		dialogPostGame.setCanceledOnTouchOutside(false);
		postGameDialogHasBeenDisplayed = true;
		dialogPostGame.show();

		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true && CommonStuff.gameState.tutorialDialogsSeen[10] == false)
		{
			mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog11(mainActivity.getApplicationContext()), DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[10] = true;
			return;
		}
	}


	// Shows Description Info Dialog
	private void showDescriptionInfoDialog()
	{

		if (CommonStuff.gameState.currentLocationRow.description.length() > 0)
		{
			DialogPostGameInfo dialogInfo = new DialogPostGameInfo(mainActivity, CommonStuff.gameState.currentLocationRow.description);
			dialogInfo.show();
		}
		else
			showPostGameDialog();
	}

	/* ==============================================================================================================================================================
	/																	Hints
	/ =============================================================================================================================================================*/

	// Called when hint starts
	private void startHint()
	{

		switch (CommonStuff.gameState.currentSelectedHint.id)
		{
			case HintNameConstants.CAMERA_ZOOM :
				linearLayoutLeft.setVisibility(View.GONE);
				buttonHints.setVisibility(View.GONE);
				buttonBack.setVisibility(View.GONE);
				buttonMiddle.setVisibility(View.VISIBLE);
				buttonMiddle.setText(getResources().getString(R.string.string_common_return));
				buttonHideHints.setVisibility(View.GONE);
				break;

			case HintNameConstants.HOT_COLD_TIMER_10 :
			case HintNameConstants.HOT_COLD_TIMER_20 :
			case HintNameConstants.HOT_COLD_TIMER_30 :
			case HintNameConstants.ARROW_2 :
			case HintNameConstants.ARROW_4 :
			case HintNameConstants.ARROW_6 :
			case HintNameConstants.SECOND_GUESS_1 :
			case HintNameConstants.SECOND_GUESS_2 :
			case HintNameConstants.SECOND_GUESS_3 :
				buttonNavDrawer.setVisibility(View.GONE);
				buttonHints.setVisibility(View.GONE);
				buttonBack.setVisibility(View.GONE);
				buttonZoomIn.setVisibility(View.GONE);
				buttonZoomOut.setVisibility(View.GONE);
				buttonMapStreet.setVisibility(View.GONE);
				buttonHideHints.setVisibility(View.GONE);
				textViewHint.setVisibility(View.GONE);
				startAnimationHintPause();
				break;

			default :
				buttonNavDrawer.setVisibility(View.GONE);
				buttonHints.setVisibility(View.GONE);
				buttonBack.setVisibility(View.GONE);
				buttonHideHints.setVisibility(View.GONE);
		}
	}

	// Called when hint is finished
	private void finishHint()
	{

		int name = CommonStuff.gameState.currentSelectedHint.id;
		switch (name)
		{
			case HintNameConstants.CAMERA_ZOOM :
				linearLayoutLeft.setVisibility(View.VISIBLE);
				buttonHints.setVisibility(View.VISIBLE);
				buttonBack.setVisibility(View.VISIBLE);
				buttonMiddle.setVisibility(View.GONE);
				buttonMiddle.setText("");
				if (mapFragment.needToShowHideHintsButton == true) buttonHideHints.setVisibility(View.VISIBLE);
				break;

			case HintNameConstants.HOT_COLD_TIMER_10 :
			case HintNameConstants.HOT_COLD_TIMER_20 :
			case HintNameConstants.HOT_COLD_TIMER_30 :
				CommonStuff.gameState.hotColdHintIsRunning = false;
			case HintNameConstants.ARROW_2 :
			case HintNameConstants.ARROW_4 :
			case HintNameConstants.ARROW_6 :
			case HintNameConstants.SECOND_GUESS_1 :
			case HintNameConstants.SECOND_GUESS_2 :
			case HintNameConstants.SECOND_GUESS_3 :
				CommonStuff.gameState.interactiveHintIsRunning = false;
				buttonNavDrawer.setVisibility(CommonStuff.gameState.freePlayModeOn == true ? View.GONE : View.VISIBLE);
				buttonHints.setVisibility(View.VISIBLE);
				buttonBack.setVisibility(View.VISIBLE);
				if (mapFragment.needToShowHideHintsButton == true) buttonHideHints.setVisibility(View.VISIBLE);
				mapFragment.setMapViewSettings(true, true);
				break;

			default :
				buttonNavDrawer.setVisibility(CommonStuff.gameState.freePlayModeOn == true ? View.GONE : View.VISIBLE);
				buttonHints.setVisibility(View.VISIBLE);
				buttonBack.setVisibility(View.VISIBLE);
				if (mapFragment.needToShowHideHintsButton == true) buttonHideHints.setVisibility(View.VISIBLE);
		}
	}

	// After the Ready, Go textView animation
	private void afterAnimation()
	{

		textViewHintStart.setVisibility(View.GONE);
		buttonZoomIn.setVisibility(View.VISIBLE);
		buttonZoomOut.setVisibility(View.VISIBLE);
		buttonMapStreet.setVisibility(View.VISIBLE);

		switch (CommonStuff.gameState.currentSelectedHint.id)
		{
			case HintNameConstants.HOT_COLD_TIMER_10 :
			case HintNameConstants.HOT_COLD_TIMER_20 :
			case HintNameConstants.HOT_COLD_TIMER_30 :
			case HintNameConstants.ARROW_2 :
			case HintNameConstants.ARROW_4 :
			case HintNameConstants.ARROW_6 :
			case HintNameConstants.SECOND_GUESS_1 :
			case HintNameConstants.SECOND_GUESS_2 :
			case HintNameConstants.SECOND_GUESS_3 :
				CommonStuff.gameState.interactiveHintIsRunning = true;
				break;

			default :
				mapFragment.setMapViewSettings(true, true);
				break;
		}

		animationIsRunning = false;
	}


	// Set textViewHint visibility
	public void setHintTextViewVisible(boolean visible)
	{

		if (visible == true)
			textViewHint.setVisibility(View.VISIBLE);
		else
		{
			textViewHint.setText("");
			textViewHint.setVisibility(View.GONE);
		}
	}

	// Update hint text view
	public void updateHintText(Spanned text)
	{

		textViewHint.setText(text);
	}

}
