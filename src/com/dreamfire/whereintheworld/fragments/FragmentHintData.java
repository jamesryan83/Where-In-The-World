package com.dreamfire.whereintheworld.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.HintNameConstants;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.database.Hint;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class FragmentHintData extends AbstractFragment
{
	private MyTextView textViewTitle, textViewDescription;
	private Hint hint;
	private ImageView imageViewImage;
	private ImageButton buttonBack;
	private View clickView;


	/* ==============================================================================================================================================================
	/																	Setup
	/ =============================================================================================================================================================*/

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_hint_data, container, false);

		textViewTitle = (MyTextView) view.findViewById(R.id.fragment_hint_data_textview_title);
		textViewDescription = (MyTextView) view.findViewById(R.id.fragment_hint_data_textview_description);
		buttonBack = (ImageButton) view.findViewById(R.id.fragment_hint_data_button_back);
		imageViewImage = (ImageView) view.findViewById(R.id.fragment_hint_data_imageview);
		clickView = view.findViewById(R.id.fragment_hint_data_view);

		buttonBack.setOnClickListener(this);
		clickView.setOnClickListener(this);

		updateUi();

		return view;
	}


	// update the ui
	@Override
	public void updateUi()
	{

		super.updateUi();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Hint Data");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		this.hint = CommonStuff.gameState.currentSelectedHint;

		try
		{
			Glide.with(this).load(R.drawable.image_common_back_arrow).into(buttonBack);
		}
		catch (Exception e) {}

		textViewTitle.setText(hint.hintTitle);
		textViewDescription.setText(Html.fromHtml(hint.hintMessage));

		// Add image for hint
		if (hint.id == HintNameConstants.FLAG)
			mainActivity.loadBitmapHintDataFlag(hint.flagCountryName, imageViewImage);
		else if (hint.imageResourceId != 0)
		{
			try
			{
				Glide.with(this).load(hint.imageResourceId).into(imageViewImage);
			}
			catch (Exception e) {}
		}
	}



	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			Glide.with(this).load(R.drawable.image_common_back_arrow).into(buttonBack);
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

		if (tempViewId == R.id.fragment_hint_data_button_back)
			v.startAnimation(animationClick);

		else if (tempViewId == R.id.fragment_hint_data_view)
			mainActivity.fragmentHintDataDismissed();
	}


	// After onClick
	@Override
	public void afterOnClick()
	{

		mainActivity.fragmentHintDataDismissed();
	}


}
