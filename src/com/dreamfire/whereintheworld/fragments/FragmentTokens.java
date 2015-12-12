package com.dreamfire.whereintheworld.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class FragmentTokens extends AbstractFragment
{
	private MyButton buttonHint1, buttonHint2, buttonHint3, buttonHint4;

	private MyTextView textViewTokens1, textViewTokens2, textViewTokens3, textViewTokens4;
	private MyTextView textViewTokensPrice1, textViewTokensPrice2, textViewTokensPrice3, textViewTokensPrice4;
	private MyTextView textViewCurrentTokens, textViewPurchaseRemovesAds;
	private ImageView imageViewToken1, imageViewToken2, imageViewToken3, imageViewToken4;
	private RelativeLayout layoutToken1, layoutToken2, layoutToken3, layoutToken4;

	private int selectedTokenIndex = 0;

	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// Setup UI
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_tokens, container, false);

		buttonHint1 = (MyButton) view.findViewById(R.id.tokens_activity_button_1);
		buttonHint2 = (MyButton) view.findViewById(R.id.tokens_activity_button_2);
		buttonHint3 = (MyButton) view.findViewById(R.id.tokens_activity_button_3);
		buttonHint4 = (MyButton) view.findViewById(R.id.tokens_activity_button_4);

		textViewTokens1 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_1).findViewById(R.id.tokens_button_textview_tokens);
		textViewTokens2 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_2).findViewById(R.id.tokens_button_textview_tokens);
		textViewTokens3 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_3).findViewById(R.id.tokens_button_textview_tokens);
		textViewTokens4 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_4).findViewById(R.id.tokens_button_textview_tokens);

		textViewTokensPrice1 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_1).findViewById(R.id.tokens_button_textview_tokens_price);
		textViewTokensPrice2 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_2).findViewById(R.id.tokens_button_textview_tokens_price);
		textViewTokensPrice3 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_3).findViewById(R.id.tokens_button_textview_tokens_price);
		textViewTokensPrice4 = (MyTextView) view.findViewById(R.id.tokens_activity_hint_4).findViewById(R.id.tokens_button_textview_tokens_price);

		imageViewToken1 = (ImageView) view.findViewById(R.id.tokens_activity_hint_1).findViewById(R.id.tokens_button_imageview_token);
		imageViewToken2 = (ImageView) view.findViewById(R.id.tokens_activity_hint_2).findViewById(R.id.tokens_button_imageview_token);
		imageViewToken3 = (ImageView) view.findViewById(R.id.tokens_activity_hint_3).findViewById(R.id.tokens_button_imageview_token);
		imageViewToken4 = (ImageView) view.findViewById(R.id.tokens_activity_hint_4).findViewById(R.id.tokens_button_imageview_token);

		textViewCurrentTokens = (MyTextView) view.findViewById(R.id.tokens_activity_textview_tokens);
		textViewPurchaseRemovesAds = (MyTextView) view.findViewById(R.id.tokens_activity_textview_ads_message);

		layoutToken1 = (RelativeLayout)  view.findViewById(R.id.tokens_activity_layout_1);
		layoutToken2 = (RelativeLayout)  view.findViewById(R.id.tokens_activity_layout_2);
		layoutToken3 = (RelativeLayout)  view.findViewById(R.id.tokens_activity_layout_3);
		layoutToken4 = (RelativeLayout)  view.findViewById(R.id.tokens_activity_layout_4);

		buttonHint1.setOnClickListener(this);
		buttonHint2.setOnClickListener(this);
		buttonHint3.setOnClickListener(this);
		buttonHint4.setOnClickListener(this);

		layoutToken1.setOnClickListener(this);
		layoutToken2.setOnClickListener(this);
		layoutToken3.setOnClickListener(this);
		layoutToken4.setOnClickListener(this);

		textViewPurchaseRemovesAds.setText(Html.fromHtml(getResources().getString(R.string.string_tokens_remove_ads1) +
				GeneralConstants.breakTag + getResources().getString(R.string.string_tokens_remove_ads2)));

		updateUi();

		return view;
	}


	// update the ui
	@Override
	public void updateUi()
	{
		super.updateUi();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Tokens");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		try
		{
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken1);
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken2);
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken3);
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken4);
		}
		catch (Exception e) {}

		textViewTokens1.setText(GeneralConstants.availableTokens[0] + getResources().getString(R.string.string_tokens_tokens));
		textViewTokens2.setText(GeneralConstants.availableTokens[1] + getResources().getString(R.string.string_tokens_tokens));
		textViewTokens3.setText(GeneralConstants.availableTokens[2] + getResources().getString(R.string.string_tokens_tokens));
		textViewTokens4.setText(getResources().getString(R.string.string_tokens_infinite) + getResources().getString(R.string.string_tokens_tokens));

		textViewTokensPrice1.setText(CommonStuff.gameState.tokenPrices[0]);
		textViewTokensPrice2.setText(CommonStuff.gameState.tokenPrices[1]);
		textViewTokensPrice3.setText(CommonStuff.gameState.tokenPrices[2]);
		textViewTokensPrice4.setText(CommonStuff.gameState.tokenPrices[3]);

		setTokensTextView();
	}


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken1);
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken2);
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken3);
			Glide.with(this).load(R.drawable.image_common_token).into(imageViewToken4);
		}
		catch (Exception e) {}
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// OnClick
	@Override
	public void onClick(View v)
	{
		Tracker tracker = CommonStuff.tracker;

		super.onClick(v);

		selectedTokenIndex = 0;
		switch(tempViewId)
		{
			case R.id.tokens_activity_button_1 : selectedTokenIndex = 0;
				layoutToken1.startAnimation(animationClick);

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed Buy 1000 Tokens Button")
		    	.build());
				break;

			case R.id.tokens_activity_button_2 : selectedTokenIndex = 1;
				layoutToken2.startAnimation(animationClick);

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed Buy 5500 Tokens Button")
		    	.build());
				break;

			case R.id.tokens_activity_button_3 : selectedTokenIndex = 2;
				layoutToken3.startAnimation(animationClick);

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed Buy 11000 Tokens Button")
		    	.build());
				break;

			case R.id.tokens_activity_button_4 : selectedTokenIndex = 3;
				layoutToken4.startAnimation(animationClick);

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed Buy Infinite Tokens Button")
		    	.build());
				break;
		}
	}


	// After onClick
	@Override
	public void afterOnClick()
	{

		try
		{
			if (selectedTokenIndex == 0)
				mainActivity.iabHelper.launchPurchaseFlow(mainActivity, GeneralConstants.SKU_TOKENS_1000, GeneralConstants.RC_REQUEST, mainActivity.purchaseFinishedListener, "");
			else if (selectedTokenIndex == 1)
				mainActivity.iabHelper.launchPurchaseFlow(mainActivity, GeneralConstants.SKU_TOKENS_5500, GeneralConstants.RC_REQUEST, mainActivity.purchaseFinishedListener, "");
			else if (selectedTokenIndex == 2)
				mainActivity.iabHelper.launchPurchaseFlow(mainActivity, GeneralConstants.SKU_TOKENS_11000, GeneralConstants.RC_REQUEST, mainActivity.purchaseFinishedListener, "");
			else if (selectedTokenIndex == 3)
				mainActivity.iabHelper.launchPurchaseFlow(mainActivity, GeneralConstants.SKU_TOKENS_INFINITE, GeneralConstants.RC_REQUEST, mainActivity.purchaseFinishedListener, "");
		}
		catch (IllegalStateException e)
		{
			CommonStuff.myLog("IllegalStateException : ", e);
		}
	}


	/* ==============================================================================================================================================================
	/																	Other Methods
	/ =============================================================================================================================================================*/

	// Update the tokens textView
	public void setTokensTextView()
	{

		if (CommonStuff.gameState.infiniteTokens == false)
		{
			textViewCurrentTokens.setText(Html.fromHtml(getResources().getString(R.string.string_tokens_current) +
				GeneralConstants.startFontTagOrange + CommonStuff.gameState.currentTokens + GeneralConstants.endFontTag));
		}
		else
		{
			textViewCurrentTokens.setText(Html.fromHtml(getResources().getString(R.string.string_tokens_current) +
					GeneralConstants.startFontTagOrange + getResources().getString(R.string.string_tokens_infinite) + GeneralConstants.endFontTag));
		}
	}

}
