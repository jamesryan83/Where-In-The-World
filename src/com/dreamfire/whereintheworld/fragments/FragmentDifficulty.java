package com.dreamfire.whereintheworld.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dreamfire.whereintheworld.stuff.AsyncTasks.DifficultyTask;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class FragmentDifficulty extends AbstractFragment
{
	private MyButton buttonEasy, buttonNormal, buttonHard, buttonExpert;
	private MyTextView textViewEasy, textViewNormal, textViewHard, textViewExpert;
	private RelativeLayout layoutEasy, layoutNormal, layoutHard, layoutExpert, layoutLock;
	private ImageView imageViewLock;


	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_difficulty, container, false);

		textViewEasy = (MyTextView) view.findViewById(R.id.difficulty_textview_completed_easy);
		textViewNormal = (MyTextView) view.findViewById(R.id.difficulty_textview_completed_normal);
		textViewHard = (MyTextView) view.findViewById(R.id.difficulty_textview_completed_hard);
		textViewExpert = (MyTextView) view.findViewById(R.id.difficulty_textview_completed_expert);
		buttonEasy = (MyButton) view.findViewById(R.id.difficulty_button_easy);
		buttonNormal = (MyButton) view.findViewById(R.id.difficulty_button_normal);
		buttonHard = (MyButton) view.findViewById(R.id.difficulty_button_hard);
		buttonExpert = (MyButton) view.findViewById(R.id.difficulty_button_expert);
		layoutEasy = (RelativeLayout)  view.findViewById(R.id.difficulty_layout_easy);
		layoutNormal = (RelativeLayout)  view.findViewById(R.id.difficulty_layout_normal);
		layoutHard = (RelativeLayout)  view.findViewById(R.id.difficulty_layout_hard);
		layoutExpert = (RelativeLayout)  view.findViewById(R.id.difficulty_layout_expert);
		layoutLock = (RelativeLayout)  view.findViewById(R.id.difficulty_layout_expert_lock);
		imageViewLock = (ImageView) view.findViewById(R.id.difficulty_image_lock);

		layoutLock.setOnClickListener(this);

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
			Glide.with(this).load(R.drawable.image_common_lock).into(imageViewLock);
		}
		catch (Exception e) {}

		new DifficultyTask(mainActivity.getApplicationContext()).execute();
	}


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			Glide.with(this).load(R.drawable.image_common_lock).into(imageViewLock);
		}
		catch (Exception e) {}
	}


	// set the textviews
	@Override
	public void setupUiAfterAsync()
	{

		super.setupUiAfterAsync();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Difficulty");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		buttonEasy.setOnClickListener(this);
		buttonNormal.setOnClickListener(this);
		buttonHard.setOnClickListener(this);
		buttonExpert.setOnClickListener(this);

		textViewEasy.setText(getCompletedString(0));
		textViewNormal.setText(getCompletedString(1));
		textViewHard.setText(getCompletedString(2));
		textViewExpert.setText(getCompletedString(3));

		if (CommonStuff.gameState.expertModeUnlocked == false)
			layoutLock.setVisibility(View.VISIBLE);
		else
			layoutLock.setVisibility(View.GONE);

		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true)
		{
			//mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog3(mainActivity.getApplicationContext()), DialogConstants.NO_CALLBACK);
			mainActivity.showTutorialDialog(new SpannableString(getResources().getString(R.string.string_tutorial_difficulty)), DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[2] = true;
		}
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// OnCLick
	@Override
	public void onClick(View v)
	{

		super.onClick(v);

		int index = 0;
		switch(tempViewId)
		{
			case R.id.difficulty_button_easy : layoutEasy.startAnimation(animationClick); index = 0; break;
			case R.id.difficulty_button_normal : layoutNormal.startAnimation(animationClick); index = 1; break;
			case R.id.difficulty_button_hard : layoutHard.startAnimation(animationClick); index = 2; break;
			case R.id.difficulty_button_expert : layoutExpert.startAnimation(animationClick); index = 3; break;

			case R.id.difficulty_layout_expert_lock :
				if (CommonStuff.gameState.expertModeUnlocked == false)
				{
					DialogMsgBoxGeneral.newInstance(getResources().getString(R.string.string_difficulty_lock_title), getResources()
							.getString(R.string.string_difficulty_lock_message), false, false, DialogConstants.NO_CALLBACK)
							.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
					return;
				}
				break;
		}

		CommonStuff.setCurrentDifficultyIndex(index);
	}


	// After onClick
	@Override
	public void afterOnClick()
	{

		mainActivity.goToPage(FragmentConstants.FRAGMENT_CATEGORIES);
	}


	/* ==============================================================================================================================================================
	/																	Other Methods
	/ =============================================================================================================================================================*/


	// Returns a Spanned for the String "Completed X%"
	private Spanned getCompletedString(int index)
	{

		return Html.fromHtml(
				getResources().getString(R.string.string_difficulty_completed) +
				GeneralConstants.startFontTagOrange +
				Math.round(CommonStuff.gameState.difficultyPercentComplete[index]) + "%" +
				GeneralConstants.endFontTag);
	}


}
