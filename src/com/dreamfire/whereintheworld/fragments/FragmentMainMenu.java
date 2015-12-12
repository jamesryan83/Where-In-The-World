package com.dreamfire.whereintheworld.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.TutorialStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class FragmentMainMenu extends AbstractFragment
{
	private MyButton buttonMainGame, buttonFreePlay, buttonTutorial;
	private ImageView imageViewLogo, imageViewLock;
	private LinearLayout layoutTitle;
	private RelativeLayout layoutLock;


	private Animation animationSlideInMainGame, animationSlideInFreePlay, animationSlideInTutorial, animationFadeInTitle;


	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

		buttonMainGame = (MyButton) view.findViewById(R.id.mainmenu_button_game);
		buttonFreePlay = (MyButton) view.findViewById(R.id.mainmenu_button_free_play);
		buttonTutorial = (MyButton) view.findViewById(R.id.mainmenu_button_tutorial);
		layoutTitle = (LinearLayout) view.findViewById(R.id.mainmenu_layout_title);
		layoutLock = (RelativeLayout) view.findViewById(R.id.mainmenu_layout_free_play_lock);
		imageViewLogo = (ImageView) view.findViewById(R.id.mainmenu_imageview_globe);
		imageViewLock = (ImageView) view.findViewById(R.id.mainmenu_imageview_lock);

		buttonMainGame.setOnClickListener(this);
		buttonFreePlay.setOnClickListener(this);
		buttonTutorial.setOnClickListener(this);
		layoutLock.setOnClickListener(this);

		// Animate buttons sliding in
		animationSlideInMainGame = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in_from_right);
		animationSlideInFreePlay = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in_from_right);
		animationSlideInTutorial = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_in_from_right);
		animationFadeInTitle = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in_500);

		animationSlideInFreePlay.setStartOffset(100);
		animationSlideInTutorial.setStartOffset(200);

		updateUi();

		return view;
	}



	// Update the Ui
	@Override
	public void updateUi()
	{
		super.updateUi();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Main Menu");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		CommonStuff.gameState.freePlayModeUnlocked = CommonStuff.databaseGameState.getIfFreePlayUnlocked();

		try
		{
			Glide.with(this).load(R.drawable.image_mainmenu_globe).into(imageViewLogo);
			Glide.with(this).load(R.drawable.image_common_lock).into(imageViewLock);
		}
		catch (Exception e) {}

		if (CommonStuff.gameState.freePlayModeUnlocked == true)
		{
			layoutLock.setVisibility(View.GONE);
			imageViewLock.setVisibility(View.GONE);
		}
		else
		{
			layoutLock.setVisibility(View.VISIBLE);
			imageViewLock.setVisibility(View.VISIBLE);
		}

		if (buttonMainGame == null)
		{
			getActivity().getSupportFragmentManager().popBackStack();
			mainActivity.goToPage(FragmentConstants.FRAGMENT_MAIN_MENU, true);
		}

		buttonMainGame.startAnimation(animationSlideInMainGame);
		buttonFreePlay.startAnimation(animationSlideInFreePlay);
		layoutLock.startAnimation(animationSlideInFreePlay);
		imageViewLock.startAnimation(animationSlideInFreePlay);
		buttonTutorial.startAnimation(animationSlideInTutorial);
		layoutTitle.startAnimation(animationFadeInTitle);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			Glide.with(this).load(R.drawable.image_mainmenu_globe).into(imageViewLogo);
			Glide.with(this).load(R.drawable.image_common_lock).into(imageViewLock);
		}
		catch (Exception e) {}
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// onClick
	@Override
	public void onClick(View v)
	{
		super.onClick(v);

		v.startAnimation(animationClick);

		if (v == layoutLock)
			buttonFreePlay.startAnimation(animationClick);
	}


	// After onClick
	@Override
	public void afterOnClick()
	{
		Tracker tracker = CommonStuff.tracker;

		switch (tempViewId)
		{
			case R.id.mainmenu_button_game :
				if (CommonStuff.gameState.isFirstRun == true)
				{
					onFirstRun(R.string.string_mainmenu_buton_try_tutorial);
					return;
				}

				CommonStuff.gameState.freePlayModeOn = false;
				CommonStuff.gameState.tutorialModeOn = false;
				mainActivity.goToPage(FragmentConstants.FRAGMENT_DIFFICULTY);
				break;

			case R.id.mainmenu_button_free_play :
				if (CommonStuff.gameState.isFirstRun == true)
				{
					onFirstRun(R.string.string_mainmenu_buton_try_tutorial2);
					return;
				}

				tracker.send(new HitBuilders.EventBuilder()
	            	.setCategory("Button Click Event")
	            	.setAction("Pressed Free Play Button")
	            	.build());

				CommonStuff.gameState.freePlayModeOn = true;
				CommonStuff.gameState.currentFreePlayTokens = GeneralConstants.startingTokensFreePlay;
				mainActivity.goToPage(FragmentConstants.FRAGMENT_MAIN_GAME, true);
				break;

			case R.id.mainmenu_layout_free_play_lock :
				if (CommonStuff.gameState.freePlayModeUnlocked == false)
				{
					DialogMsgBoxGeneral.newInstance("", getResources()
							.getString(R.string.string_mainmenu_lock_message), false, false, DialogConstants.NO_CALLBACK)
							.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
				}
				break;

				// Tutorial
			case R.id.mainmenu_button_tutorial :

				tracker.send(new HitBuilders.EventBuilder()
	            	.setCategory("Button Click Event")
	            	.setAction("Pressed Tutorial Button")
	            	.build());

				CommonStuff.gameState.isFirstRun = false;
				CommonStuff.gameState.tutorialDialogsSeen = new boolean[12];
				for (int i = 0; i < CommonStuff.gameState.tutorialDialogsSeen.length; i++)
					CommonStuff.gameState.tutorialDialogsSeen[i] = false;

				mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog1(mainActivity.getApplicationContext()), DialogConstants.TUTORIAL_1);
				CommonStuff.gameState.tutorialDialogsSeen[0] = true;
				break;
		}
	}


	private void onFirstRun(int stringResource)
	{
		CommonStuff.gameState.isFirstRun = false;

		DialogMsgBoxGeneral.newInstance("", getResources()
				.getString(stringResource), false, false, DialogConstants.NO_CALLBACK)
				.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
	}

}
