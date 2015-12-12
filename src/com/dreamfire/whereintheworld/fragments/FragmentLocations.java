package com.dreamfire.whereintheworld.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.CheckLevelCompletedTask;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.ResetLocationsTask;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.TutorialStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class FragmentLocations extends AbstractFragment
{
	private MyTextView[] textViewScoreLocation;
	private MyTextView textViewScoreRequired, textViewScoreCurrent;
	private MyTextView textViewScoreRequiredStatic, textViewScoreCurrentStatic;
	private MyTextView textViewLevelComplete;
	private RelativeLayout layoutCurrentPoints, layoutRequiredPoints;

	private RelativeLayout layoutButton1, layoutButton2, layoutButton3, layoutButton4, layoutButton5, layoutButton6;

	private ImageView[] imageViewLocation;
	private MyButton[] buttonLocation;
	private ImageButton buttonReset;

	private int selectedLocationIndex = 0;


	/* =============================================================================================================================================================
	/																Startup
	/ =============================================================================================================================================================*/

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		imageViewLocation = new ImageView[6];
		buttonLocation = new MyButton[6];
		textViewScoreLocation = new MyTextView[6];

		View view = inflater.inflate(R.layout.fragment_locations, container, false);

		textViewScoreRequired = (MyTextView) view.findViewById(R.id.locations_textview_required_points);
		textViewScoreCurrent = (MyTextView) view.findViewById(R.id.locations_textview_current_points);
		textViewScoreRequiredStatic = (MyTextView) view.findViewById(R.id.locations_textview_required_points_static);
		textViewScoreCurrentStatic = (MyTextView) view.findViewById(R.id.locations_textview_current_points_static);
		textViewLevelComplete = (MyTextView) view.findViewById(R.id.locations_textview_level_complete);
		layoutCurrentPoints = (RelativeLayout) view.findViewById(R.id.locations_layout_current_points);
		layoutRequiredPoints = (RelativeLayout) view.findViewById(R.id.locations_layout_required_points);
		buttonReset = (ImageButton) view.findViewById(R.id.locations_button_location_reset);

		textViewScoreLocation[0] = (MyTextView) view.findViewById(R.id.locations_button_layout_1).findViewById(R.id.locations_textview_score);
		textViewScoreLocation[1] = (MyTextView) view.findViewById(R.id.locations_button_layout_2).findViewById(R.id.locations_textview_score);
		textViewScoreLocation[2] = (MyTextView) view.findViewById(R.id.locations_button_layout_3).findViewById(R.id.locations_textview_score);
		textViewScoreLocation[3] = (MyTextView) view.findViewById(R.id.locations_button_layout_4).findViewById(R.id.locations_textview_score);
		textViewScoreLocation[4] = (MyTextView) view.findViewById(R.id.locations_button_layout_5).findViewById(R.id.locations_textview_score);
		textViewScoreLocation[5] = (MyTextView) view.findViewById(R.id.locations_button_layout_6).findViewById(R.id.locations_textview_score);

		layoutButton1 = (RelativeLayout) view.findViewById(R.id.locations_layout_button_1);
		layoutButton2 = (RelativeLayout) view.findViewById(R.id.locations_layout_button_2);
		layoutButton3 = (RelativeLayout) view.findViewById(R.id.locations_layout_button_3);
		layoutButton4 = (RelativeLayout) view.findViewById(R.id.locations_layout_button_4);
		layoutButton5 = (RelativeLayout) view.findViewById(R.id.locations_layout_button_5);
		layoutButton6 = (RelativeLayout) view.findViewById(R.id.locations_layout_button_6);

		imageViewLocation[0] = (ImageView) view.findViewById(R.id.locations_button_layout_1).findViewById(R.id.locations_imageview_complete);
		imageViewLocation[1] = (ImageView) view.findViewById(R.id.locations_button_layout_2).findViewById(R.id.locations_imageview_complete);
		imageViewLocation[2] = (ImageView) view.findViewById(R.id.locations_button_layout_3).findViewById(R.id.locations_imageview_complete);
		imageViewLocation[3] = (ImageView) view.findViewById(R.id.locations_button_layout_4).findViewById(R.id.locations_imageview_complete);
		imageViewLocation[4] = (ImageView) view.findViewById(R.id.locations_button_layout_5).findViewById(R.id.locations_imageview_complete);
		imageViewLocation[5] = (ImageView) view.findViewById(R.id.locations_button_layout_6).findViewById(R.id.locations_imageview_complete);

		buttonLocation[0] = (MyButton) view.findViewById(R.id.locations_button_location_1);
		buttonLocation[1] = (MyButton) view.findViewById(R.id.locations_button_location_2);
		buttonLocation[2] = (MyButton) view.findViewById(R.id.locations_button_location_3);
		buttonLocation[3] = (MyButton) view.findViewById(R.id.locations_button_location_4);
		buttonLocation[4] = (MyButton) view.findViewById(R.id.locations_button_location_5);
		buttonLocation[5] = (MyButton) view.findViewById(R.id.locations_button_location_6);

		for (MyButton b : buttonLocation)
			b.setOnClickListener(this);

		buttonReset.setOnClickListener(this);

		layoutRequiredPoints.setVisibility(View.GONE);
		textViewLevelComplete.setVisibility(View.GONE);

		updateUi();

		return view;
	}


	// Update the Ui
	@Override
	public void updateUi()
	{

		super.updateUi();

		try
		{
			Glide.with(this).load(R.drawable.image_common_reset).into(buttonReset);
		}
		catch (Exception e) {}

		new CheckLevelCompletedTask(mainActivity).execute();

		String[] levelNames = getResources().getStringArray(R.array.string_array_common_level_names);

		if (CommonStuff.gameState.lightThemeEnabled == true)
		{
			layoutCurrentPoints.setBackgroundColor(getResources().getColor(R.color.color_yellow));
			layoutRequiredPoints.setBackgroundColor(getResources().getColor(R.color.color_yellow));
			textViewScoreCurrent.setTextColor(getResources().getColor(R.color.color_gray_2));
			textViewScoreRequired.setTextColor(getResources().getColor(R.color.color_gray_2));
			textViewScoreCurrentStatic.setTextColor(getResources().getColor(R.color.color_red));
			textViewScoreRequiredStatic.setTextColor(getResources().getColor(R.color.color_red));

			mainActivity.setTextViewTitle(CommonStuff.gameState.categoryNames[CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex]].replace("_", " ") +
					" <small><small>" + GeneralConstants.startFontTagRed + levelNames[CommonStuff.gameState.currentLevelIndex] + GeneralConstants.endFontTag + "</small></small>");
		}
		else
		{
			layoutCurrentPoints.setBackgroundColor(getResources().getColor(R.color.color_purple));
			layoutRequiredPoints.setBackgroundColor(getResources().getColor(R.color.color_purple));
			textViewScoreCurrent.setTextColor(getResources().getColor(R.color.color_yellow));
			textViewScoreRequired.setTextColor(getResources().getColor(R.color.color_yellow));
			textViewScoreCurrentStatic.setTextColor(getResources().getColor(R.color.color_white));
			textViewScoreRequiredStatic.setTextColor(getResources().getColor(R.color.color_white));

			mainActivity.setTextViewTitle(CommonStuff.gameState.categoryNames[CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex]].replace("_", " ") +
					" <small><small>" + GeneralConstants.startFontTagWhite + levelNames[CommonStuff.gameState.currentLevelIndex] + GeneralConstants.endFontTag + "</small></small>");
		}

		for (int i = 0; i < buttonLocation.length; i++)
			enableButton(imageViewLocation[i], true);

		for (MyTextView t : textViewScoreLocation)
			t.setText(getResources().getString(R.string.string_locations_score_colon) + "");

		textViewScoreCurrent.setText("");
		textViewScoreRequired.setText("");


	}


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			Glide.with(this).load(R.drawable.image_common_reset).into(buttonReset);
		}
		catch (Exception e) {}
	}


	// Update Ui after async
	@Override
	public void setupUiAfterAsync()
	{

		super.setupUiAfterAsync();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Locations");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		if (CommonStuff.gameState.levelStateData != null && CommonStuff.gameState.levelStateData[CommonStuff.gameState.currentLevelIndex].completed == true)
		{
			layoutRequiredPoints.setVisibility(View.GONE);
			textViewLevelComplete.setText(getResources().getString(R.string.string_common_level_complete));
			textViewLevelComplete.setVisibility(View.VISIBLE);
		}
		else if (CommonStuff.gameState.levelStateData != null && CommonStuff.gameState.levelStateData[CommonStuff.gameState.currentLevelIndex].passed == true)
		{
			layoutRequiredPoints.setVisibility(View.GONE);
			textViewLevelComplete.setText(getResources().getString(R.string.string_common_level_passed));
			textViewLevelComplete.setVisibility(View.VISIBLE);
		}
		else
		{
			layoutRequiredPoints.setVisibility(View.VISIBLE);
			textViewLevelComplete.setVisibility(View.GONE);
		}

		for (int i = 0; i < textViewScoreLocation.length; i++)
			textViewScoreLocation[i].setText(getResources().getString(R.string.string_locations_score_colon) + CommonStuff.gameState.locationStateData[i].currentScore);

		for (int i = 0; i < buttonLocation.length; i++)
			enableButton(imageViewLocation[i], CommonStuff.gameState.locationStateData[i].completed == true ? false : true);

		textViewScoreCurrent.setText(CommonStuff.getCurrentTotalScore() + "");
		textViewScoreRequired.setText("" + GeneralConstants.levelRequiredScores[CommonStuff.gameState.currentDifficultyIndex][CommonStuff.gameState.currentLevelIndex]);


		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true && CommonStuff.gameState.tutorialDialogsSeen[10] == true && CommonStuff.gameState.tutorialDialogsSeen[11] == false)
		{
			mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog12(mainActivity.getApplicationContext()), DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[11] = true;
			CommonStuff.gameState.tutorialModeOn = false;
		}

		if (CommonStuff.gameState.tutorialModeOn == true && CommonStuff.gameState.tutorialDialogsSeen[5] == false)
		{
			new ResetLocationsTask(mainActivity).execute();
			mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog6(mainActivity.getApplicationContext()), DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[5] = true;
		}
	}





	/* ==============================================================================================================================================================
	/																Other Methods
	/ =============================================================================================================================================================*/

	// Enable/disable button
	private void enableButton(ImageView image, boolean enable)
	{

		if (enable == true)
			Glide.with(this).load(R.drawable.image_locations_enabled).into(image);
		else
			Glide.with(this).load(R.drawable.image_locations_disabled).into(image);
	}


	/* ==============================================================================================================================================================
	/																Listeners
	/ =============================================================================================================================================================*/

	// OnClick Listener
	@Override
	public void onClick(View v)
	{

		if (CommonStuff.gameState.currentLocations == null)
			return;

		super.onClick(v);

		switch (tempViewId)
		{
			case R.id.locations_button_location_1 : selectedLocationIndex = 0; layoutButton1.startAnimation(animationClick); break;
			case R.id.locations_button_location_2 : selectedLocationIndex = 1; layoutButton2.startAnimation(animationClick); break;
			case R.id.locations_button_location_3 : selectedLocationIndex = 2; layoutButton3.startAnimation(animationClick); break;
			case R.id.locations_button_location_4 : selectedLocationIndex = 3; layoutButton4.startAnimation(animationClick); break;
			case R.id.locations_button_location_5 : selectedLocationIndex = 4; layoutButton5.startAnimation(animationClick); break;
			case R.id.locations_button_location_6 : selectedLocationIndex = 5; layoutButton6.startAnimation(animationClick); break;
			case R.id.locations_button_location_reset : buttonReset.startAnimation(animationClick); break;
		}
	}


	// After onClick
	@Override
	public void afterOnClick()
	{

		if (tempViewId == R.id.locations_button_location_reset)
			DialogMsgBoxGeneral.newInstance(getResources().getString(R.string.string_navdrawer_confirm), getResources()
					.getString(R.string.string_navdrawer_reset_locations), false, true, DialogConstants.RESET_LOCATIONS)
					.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

		else if (CommonStuff.gameState.locationStateData[selectedLocationIndex].completed == true)
			DialogMsgBoxGeneral.newInstance("", getResources()
					.getString(R.string.string_locations_already_completed), false, false, DialogConstants.NO_CALLBACK)
					.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

		else
		{
			CommonStuff.setCurrentLocationIndex(selectedLocationIndex);
			mainActivity.goToPage(FragmentConstants.FRAGMENT_MAIN_GAME, true);
		}
	}


}
