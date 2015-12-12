package com.dreamfire.whereintheworld.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.PurchaseHintTask;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.TutorialStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class FragmentHints extends AbstractFragment
{
	private MyButton[] buttonHint;
	private MyTextView[] textViewHintCost;
	private MyTextView[] textViewHintPurchased;
	private LinearLayout[] layoutHintPurchased;
	private LinearLayout[] layoutHintUnpurchased;
	private ImageView[] imageViewToken;
	private ImageView[] imageViewQuestionMark;

	private RelativeLayout[] layoutHint;

	private int selectedHintIndex = 0;


	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// CreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_hints, container, false);

		buttonHint = new MyButton[9];
		textViewHintCost = new MyTextView[9];
		textViewHintPurchased = new MyTextView[9];
		layoutHintPurchased = new LinearLayout[9];
		layoutHintUnpurchased = new LinearLayout[9];
		layoutHint = new RelativeLayout[9];
		imageViewToken = new ImageView[9];
		imageViewQuestionMark = new ImageView[9];

		layoutHintPurchased[0] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_1).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[1] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_2).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[2] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_3).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[3] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_4).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[4] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_5).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[5] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_6).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[6] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_7).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[7] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_8).findViewById(R.id.hints_button_layout_purchased);
		layoutHintPurchased[8] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_9).findViewById(R.id.hints_button_layout_purchased);

		layoutHintUnpurchased[0] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_1).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[1] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_2).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[2] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_3).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[3] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_4).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[4] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_5).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[5] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_6).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[6] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_7).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[7] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_8).findViewById(R.id.hints_button_layout_unpurchased);
		layoutHintUnpurchased[8] = (LinearLayout) view.findViewById(R.id.hints_activity_hint_9).findViewById(R.id.hints_button_layout_unpurchased);

		buttonHint[0] = (MyButton) view.findViewById(R.id.hints_activity_button_1);
		buttonHint[1] = (MyButton) view.findViewById(R.id.hints_activity_button_2);
		buttonHint[2] = (MyButton) view.findViewById(R.id.hints_activity_button_3);
		buttonHint[3] = (MyButton) view.findViewById(R.id.hints_activity_button_4);
		buttonHint[4] = (MyButton) view.findViewById(R.id.hints_activity_button_5);
		buttonHint[5] = (MyButton) view.findViewById(R.id.hints_activity_button_6);
		buttonHint[6] = (MyButton) view.findViewById(R.id.hints_activity_button_7);
		buttonHint[7] = (MyButton) view.findViewById(R.id.hints_activity_button_8);
		buttonHint[8] = (MyButton) view.findViewById(R.id.hints_activity_button_9);

		imageViewToken[0] = (ImageView) view.findViewById(R.id.hints_activity_hint_1).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[1] = (ImageView) view.findViewById(R.id.hints_activity_hint_2).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[2] = (ImageView) view.findViewById(R.id.hints_activity_hint_3).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[3] = (ImageView) view.findViewById(R.id.hints_activity_hint_4).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[4] = (ImageView) view.findViewById(R.id.hints_activity_hint_5).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[5] = (ImageView) view.findViewById(R.id.hints_activity_hint_6).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[6] = (ImageView) view.findViewById(R.id.hints_activity_hint_7).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[7] = (ImageView) view.findViewById(R.id.hints_activity_hint_8).findViewById(R.id.hints_button_imageview_token);
		imageViewToken[8] = (ImageView) view.findViewById(R.id.hints_activity_hint_9).findViewById(R.id.hints_button_imageview_token);

		imageViewQuestionMark[0] = (ImageView) view.findViewById(R.id.hints_activity_hint_1).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[1] = (ImageView) view.findViewById(R.id.hints_activity_hint_2).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[2] = (ImageView) view.findViewById(R.id.hints_activity_hint_3).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[3] = (ImageView) view.findViewById(R.id.hints_activity_hint_4).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[4] = (ImageView) view.findViewById(R.id.hints_activity_hint_5).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[5] = (ImageView) view.findViewById(R.id.hints_activity_hint_6).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[6] = (ImageView) view.findViewById(R.id.hints_activity_hint_7).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[7] = (ImageView) view.findViewById(R.id.hints_activity_hint_8).findViewById(R.id.hints_button_imageview_question_mark);
		imageViewQuestionMark[8] = (ImageView) view.findViewById(R.id.hints_activity_hint_9).findViewById(R.id.hints_button_imageview_question_mark);

		textViewHintCost[0] = (MyTextView) view.findViewById(R.id.hints_activity_hint_1).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[1] = (MyTextView) view.findViewById(R.id.hints_activity_hint_2).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[2] = (MyTextView) view.findViewById(R.id.hints_activity_hint_3).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[3] = (MyTextView) view.findViewById(R.id.hints_activity_hint_4).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[4] = (MyTextView) view.findViewById(R.id.hints_activity_hint_5).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[5] = (MyTextView) view.findViewById(R.id.hints_activity_hint_6).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[6] = (MyTextView) view.findViewById(R.id.hints_activity_hint_7).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[7] = (MyTextView) view.findViewById(R.id.hints_activity_hint_8).findViewById(R.id.hints_button_textview_hint_cost);
		textViewHintCost[8] = (MyTextView) view.findViewById(R.id.hints_activity_hint_9).findViewById(R.id.hints_button_textview_hint_cost);

		textViewHintPurchased[0] = (MyTextView) view.findViewById(R.id.hints_activity_hint_1).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[1] = (MyTextView) view.findViewById(R.id.hints_activity_hint_2).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[2] = (MyTextView) view.findViewById(R.id.hints_activity_hint_3).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[3] = (MyTextView) view.findViewById(R.id.hints_activity_hint_4).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[4] = (MyTextView) view.findViewById(R.id.hints_activity_hint_5).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[5] = (MyTextView) view.findViewById(R.id.hints_activity_hint_6).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[6] = (MyTextView) view.findViewById(R.id.hints_activity_hint_7).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[7] = (MyTextView) view.findViewById(R.id.hints_activity_hint_8).findViewById(R.id.hints_button_textview_purchased);
		textViewHintPurchased[8] = (MyTextView) view.findViewById(R.id.hints_activity_hint_9).findViewById(R.id.hints_button_textview_purchased);

		for (Button b : buttonHint)
			b.setOnClickListener(this);

		textViewHintCost[0].setText(GeneralConstants.tokensHintCostBest + "");
		textViewHintCost[1].setText(GeneralConstants.tokensHintCostBest + "");
		textViewHintCost[2].setText(GeneralConstants.tokensHintCostBest + "");
		textViewHintCost[3].setText(GeneralConstants.tokensHintCostMedium + "");
		textViewHintCost[4].setText(GeneralConstants.tokensHintCostMedium + "");
		textViewHintCost[5].setText(GeneralConstants.tokensHintCostMedium + "");
		textViewHintCost[6].setText(GeneralConstants.tokensHintCostWorst + "");
		textViewHintCost[7].setText(GeneralConstants.tokensHintCostWorst + "");
		textViewHintCost[8].setText(GeneralConstants.tokensHintCostWorst + "");

		layoutHint[0] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_1);
		layoutHint[1] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_2);
		layoutHint[2] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_3);
		layoutHint[3] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_4);
		layoutHint[4] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_5);
		layoutHint[5] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_6);
		layoutHint[6] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_7);
		layoutHint[7] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_8);
		layoutHint[8] = (RelativeLayout) view.findViewById(R.id.hints_activity_layout_9);

		for (RelativeLayout r : layoutHint)
			r.setOnClickListener(this);

		updateUi();

		return view;
	}


	// update the ui
	@Override
	public void updateUi()
	{

		super.updateUi();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Hints");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		int count = 8; // because layouts are backwards

		for (int i = 0; i < CommonStuff.gameState.currentHints.size(); i++)
		{
			if (CommonStuff.gameState.currentHints.get(i).purchased == true)
			{
				layoutHintPurchased[count].setVisibility(View.VISIBLE);
				layoutHintUnpurchased[count].setVisibility(View.GONE);
				textViewHintPurchased[count].setText(CommonStuff.gameState.currentHints.get(i).name);
			}
			else
			{
				layoutHintPurchased[count].setVisibility(View.GONE);
				layoutHintUnpurchased[count].setVisibility(View.VISIBLE);
			}

			count--;
		}

		try
		{
			for (int i = 0; i < imageViewQuestionMark.length; i++)
				Glide.with(this).load(R.drawable.image_common_question_mark).into(imageViewQuestionMark[i]);

			for (int i = 0; i < imageViewToken.length; i++)
			{
				if (CommonStuff.gameState.freePlayModeOn == true)
					Glide.with(this).load(R.drawable.image_common_token_freeplay).into(imageViewToken[i]);
				else
					Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken[i]);
			}
			}
		catch (Exception e) {}

		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true && CommonStuff.gameState.tutorialDialogsSeen[9] == false)
		{
			mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog10(mainActivity.getApplicationContext()),
					CommonStuff.gameState.tutorialBonusTokensGiven == false ? DialogConstants.TUTORIAL_10 : DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[9] = true;
			CommonStuff.gameState.tutorialBonusTokensGiven = true;
		}
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// onClick
	@Override
	public void onClick(View v)
	{

		super.onClick(v);

		selectedHintIndex = 0;
		switch(tempViewId)
		{
			case R.id.hints_activity_button_1 : selectedHintIndex = 8; layoutHint[0].startAnimation(animationClick); break;
			case R.id.hints_activity_button_2 : selectedHintIndex = 7; layoutHint[1].startAnimation(animationClick); break;
			case R.id.hints_activity_button_3 : selectedHintIndex = 6; layoutHint[2].startAnimation(animationClick); break;
			case R.id.hints_activity_button_4 : selectedHintIndex = 5; layoutHint[3].startAnimation(animationClick); break;
			case R.id.hints_activity_button_5 : selectedHintIndex = 4; layoutHint[4].startAnimation(animationClick); break;
			case R.id.hints_activity_button_6 : selectedHintIndex = 3; layoutHint[5].startAnimation(animationClick); break;
			case R.id.hints_activity_button_7 : selectedHintIndex = 2; layoutHint[6].startAnimation(animationClick); break;
			case R.id.hints_activity_button_8 : selectedHintIndex = 1; layoutHint[7].startAnimation(animationClick); break;
			case R.id.hints_activity_button_9 : selectedHintIndex = 0; layoutHint[8].startAnimation(animationClick); break;
		}
	}


	// After onClick
	@Override
	public void afterOnClick()
	{

		selectHint(selectedHintIndex);
	}


	/* ==============================================================================================================================================================
	/																	Other Methods
	/ =============================================================================================================================================================*/


	// Called when hint is clicked
	private void selectHint(int index)
	{

		if (CommonStuff.gameState.currentHints.get(index).purchased == true && CommonStuff.gameState.currentHints.get(index).repeatable == false)
		{
			DialogMsgBoxGeneral.newInstance("", CommonStuff.gameState.currentHints.get(index).hintTitle + getResources()
					.getString(R.string.string_hints_used_only_once), false, false, DialogConstants.NO_CALLBACK)
					.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

			return;
		}

		new PurchaseHintTask(mainActivity.getApplicationContext()).execute(index);
	}


	// after hint is purchased
	public void afterPurchaseHintTask(boolean purchased, int index)
	{

		if (purchased == false)
		{
			DialogMsgBoxGeneral.newInstance("", getResources()
					.getString(R.string.string_common_not_enough_tokens), CommonStuff.gameState.freePlayModeOn == true ? false : true, false, DialogConstants.NO_CALLBACK)
					.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
		}
		else
		{
			CommonStuff.setCurrentHint(index);
			mainActivity.goToPage(FragmentConstants.FRAGMENT_HINT_DATA, true);
		}
	}


}
