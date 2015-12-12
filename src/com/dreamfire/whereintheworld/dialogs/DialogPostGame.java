package com.dreamfire.whereintheworld.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.ScoreData;

public class DialogPostGame extends Dialog implements OnClickListener, OnItemClickListener, AnimationListener
{
	private Activity activity;

	private MyButton buttonViewMap, buttonWiki, buttonLocations;
	private MyTextView textViewTitle, textViewTotalScore;
	private MyTextView textViewItemDistanceTitle, textViewItemTimeTakenTitle, textViewItemBonusTokensTitle, textViewItemDistanceScoreTitle, textViewItemDistanceBonusTitle, textViewItemTimeBonusTitle;
	private MyTextView textViewItemDistanceValue, textViewItemTimeTakenValue, textViewItemBonusTokensValue, textViewItemDistanceScoreValue, textViewItemDistanceBonusValue, textViewItemTimeBonusValue;

	private RelativeLayout layoutBonusTokens;

	private Animation animationClick;

	private ScoreData scoreData;

	public DialogPostGame(Activity context, ScoreData scoreData)
	{
		super(context);

		this.activity = context;
		this.scoreData = scoreData;
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.dialog_post_game);

		int[] widthAndHeight = CommonStuff.getScreenWidthHeight(activity.getApplicationContext());
		getWindow().setLayout((int) (widthAndHeight[0] * 0.9), (int) (widthAndHeight[1] * 0.9));

		buttonViewMap = (MyButton) findViewById(R.id.dialog_post_game_button_view_map);
		buttonWiki = (MyButton) findViewById(R.id.dialog_post_game_button_wiki);
		buttonLocations = (MyButton) findViewById(R.id.dialog_post_game_button_locations);

		textViewTitle = (MyTextView) findViewById(R.id.dialog_post_game_textView_title);
		textViewTotalScore = (MyTextView) findViewById(R.id.dialog_post_game_textView_final_score);

		textViewItemDistanceTitle = (MyTextView) findViewById(R.id.dialog_post_game_item_distance).findViewById(R.id.dialog_post_game_item_textview_title);
		textViewItemTimeTakenTitle = (MyTextView) findViewById(R.id.dialog_post_game_item_time_taken).findViewById(R.id.dialog_post_game_item_textview_title);
		textViewItemBonusTokensTitle = (MyTextView) findViewById(R.id.dialog_post_game_item_bonus_tokens).findViewById(R.id.dialog_post_game_item_textview_title);
		textViewItemDistanceScoreTitle = (MyTextView) findViewById(R.id.dialog_post_game_item_distance_score).findViewById(R.id.dialog_post_game_item_textview_title);
		textViewItemDistanceBonusTitle = (MyTextView) findViewById(R.id.dialog_post_game_item_distance_bonus).findViewById(R.id.dialog_post_game_item_textview_title);
		textViewItemTimeBonusTitle = (MyTextView) findViewById(R.id.dialog_post_game_item_time_bonus).findViewById(R.id.dialog_post_game_item_textview_title);

		textViewItemDistanceValue = (MyTextView) findViewById(R.id.dialog_post_game_item_distance).findViewById(R.id.dialog_post_game_item_textview_value);
		textViewItemTimeTakenValue = (MyTextView) findViewById(R.id.dialog_post_game_item_time_taken).findViewById(R.id.dialog_post_game_item_textview_value);
		textViewItemBonusTokensValue = (MyTextView) findViewById(R.id.dialog_post_game_item_bonus_tokens).findViewById(R.id.dialog_post_game_item_textview_value);
		textViewItemDistanceScoreValue = (MyTextView) findViewById(R.id.dialog_post_game_item_distance_score).findViewById(R.id.dialog_post_game_item_textview_value);
		textViewItemDistanceBonusValue = (MyTextView) findViewById(R.id.dialog_post_game_item_distance_bonus).findViewById(R.id.dialog_post_game_item_textview_value);
		textViewItemTimeBonusValue = (MyTextView) findViewById(R.id.dialog_post_game_item_time_bonus).findViewById(R.id.dialog_post_game_item_textview_value);

		layoutBonusTokens = (RelativeLayout) findViewById(R.id.dialog_post_game_layout_bonus_tokens);

		if (CommonStuff.gameState.freePlayModeOn == true)
		{
			layoutBonusTokens.setVisibility(View.GONE);
			buttonWiki.setVisibility(View.GONE);
		}

		buttonViewMap.setOnClickListener(this);
		buttonWiki.setOnClickListener(this);
		buttonLocations.setOnClickListener(this);

		textViewTitle.setText(scoreData.locationName);

		textViewItemDistanceTitle.setText(activity.getResources().getString(R.string.string_dialogpostgame_distance));
		textViewItemTimeTakenTitle.setText(activity.getResources().getString(R.string.string_dialogpostgame_timetaken));
		textViewItemBonusTokensTitle.setText(activity.getResources().getString(R.string.string_dialogpostgame_bonustokens));
		textViewItemDistanceScoreTitle.setText(activity.getResources().getString(R.string.string_dialogpostgame_distancescore));
		textViewItemDistanceBonusTitle.setText(activity.getResources().getString(R.string.string_dialogpostgame_distancebonus));
		textViewItemTimeBonusTitle.setText(activity.getResources().getString(R.string.string_dialogpostgame_timebonus));

		if (CommonStuff.gameState.freePlayModeOn == true)
			buttonLocations.setText(activity.getResources().getString(R.string.string_common_return));
		else
			buttonLocations.setText(activity.getResources().getString(R.string.string_dialogpostgame_next));

		String unit = CommonStuff.gameState.kilometresEnabled == true ? " km" : " mi";
		double distance = CommonStuff.gameState.kilometresEnabled == true ? scoreData.distance : scoreData.distance * GeneralConstants.KM_TO_MILES;
		textViewItemDistanceValue.setText(String.format("%.2f", distance) + unit);
		textViewItemTimeTakenValue.setText(scoreData.timeTaken);
		textViewItemBonusTokensValue.setText("+" + scoreData.bonusTokens);
		textViewItemDistanceScoreValue.setText("+" + scoreData.distanceScore);
		textViewItemDistanceBonusValue.setText("+" + scoreData.distanceBonus);
		textViewItemTimeBonusValue.setText("+" + scoreData.timeBonus);

		textViewTotalScore.setText(Html.fromHtml(
				activity.getResources().getString(R.string.string_dialogpostgame_totalscore1) +
				GeneralConstants.startFontTagOrange +
				scoreData.totalScore +
				GeneralConstants.endFontTag +
				activity.getResources().getString(R.string.string_dialogpostgame_totalscore2)));

		animationClick = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.button);
		animationClick.setAnimationListener(this);
	}


	private int tempViewId;

	@Override
	public void onClick(View v)
	{

		CommonStuff.playClickSound(activity.getApplicationContext());

		tempViewId = v.getId();

		v.startAnimation(animationClick);
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
				afterOnClick();
			}
		}, GeneralConstants.ANIMATION_PAUSE_DURATION);
	}


	// After onClick
	private void afterOnClick()
	{

		if (tempViewId == R.id.dialog_post_game_button_wiki)
		{
			Uri uri = Uri.parse(scoreData.wikiLink);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			activity.startActivity(intent);
		}

		((ActivityMain)activity).dialogPostGameClicked(tempViewId);

		dismiss();
	}


	// Do nothing when back is pressed
	@Override
	public void onBackPressed()
	{

		((ActivityMain)activity).dialogPostGameClicked(R.id.dialog_post_game_button_view_map);

		dismiss();
	}


	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }
	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
}
