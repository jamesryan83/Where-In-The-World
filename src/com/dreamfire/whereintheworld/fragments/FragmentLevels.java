package com.dreamfire.whereintheworld.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.LevelDataTask;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.viewpagerindicator.CirclePageIndicator;

public class FragmentLevels extends AbstractFragment implements OnPageChangeListener
{
	private ImageView imageViewArrowLeft, imageViewArrowRight;

	private ViewPager viewPager;
	private CirclePageIndicator circlePageIndicator;


	/* ==============================================================================================================================================================
	/																Startup
	/ =============================================================================================================================================================*/

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_levels, container, false);

		imageViewArrowLeft = (ImageView) view.findViewById(R.id.level_imageview_next_arrow_left);
		imageViewArrowRight = (ImageView) view.findViewById(R.id.level_imageview_next_arrow_right);

		imageViewArrowLeft.setOnClickListener(this);
		imageViewArrowRight.setOnClickListener(this);

		viewPager = (ViewPager) view.findViewById(R.id.level_viewpager);
		circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.level_circlepageindicator);
		circlePageIndicator.setOnPageChangeListener(this);

		updateUi();

		return view;
	}


	// Update the Ui
	@Override
	public void updateUi()
	{

		super.updateUi();

		new LevelDataTask(mainActivity.getApplicationContext()).execute();
	}


	// after asynctask
	@Override
	public void setupUiAfterAsync()
	{

		super.setupUiAfterAsync();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Levels");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		try
		{
			Glide.with(this).load(R.drawable.image_common_next_arrow_left).into(imageViewArrowLeft);
			Glide.with(this).load(R.drawable.image_common_next_arrow_right).into(imageViewArrowRight);
		}
		catch (Exception e) {}

		viewPager.setAdapter(new FragmentPagerAdapterLevel(getFragmentManager(), GeneralConstants.availableLevels.length));
		circlePageIndicator.setViewPager(viewPager);

		if (CommonStuff.gameState.lightThemeEnabled == true)
			circlePageIndicator.setFillColor(getResources().getColor(R.color.color_yellow));
		else
			circlePageIndicator.setFillColor(getResources().getColor(R.color.color_purple));


		viewPager.setCurrentItem(CommonStuff.gameState.currentLevelIndex);

		setLeftRightArrows();

		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true)
		{
			mainActivity.showTutorialDialog(new SpannableString(getResources().getString(R.string.string_tutorial_levels)), DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[4] = true;
		}
	}


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			Glide.with(this).load(R.drawable.image_common_next_arrow_left).into(imageViewArrowLeft);
			Glide.with(this).load(R.drawable.image_common_next_arrow_right).into(imageViewArrowRight);
		}
		catch (Exception e) {}
	}


	/* ==============================================================================================================================================================
	/																	Other Methods
	/ =============================================================================================================================================================*/

	// Show/Hide the left/right arrows on each side of screen
	private void setLeftRightArrows()
	{

		int index = viewPager.getCurrentItem();

		if (index == 0)
		{
			imageViewArrowLeft.setVisibility(View.INVISIBLE);
			imageViewArrowRight.setVisibility(View.VISIBLE);
		}
		else if (index == GeneralConstants.availableLevels.length - 1)
		{
			imageViewArrowLeft.setVisibility(View.VISIBLE);
			imageViewArrowRight.setVisibility(View.INVISIBLE);
		}
		else
		{
			imageViewArrowLeft.setVisibility(View.VISIBLE);
			imageViewArrowRight.setVisibility(View.VISIBLE);
		}
	}


	/* ==============================================================================================================================================================
	/																Listeners
	/ =============================================================================================================================================================*/

	// OnClick
	@Override
	public void onClick(View v)
	{

		super.onClick(v);

		int index = viewPager.getCurrentItem();

		if (tempViewId == R.id.level_imageview_next_arrow_right)
		{
			v.startAnimation(animationClick);
			if (index < GeneralConstants.availableLevels.length - 1)
				viewPager.setCurrentItem(index + 1, true);
		}

		if (tempViewId == R.id.level_imageview_next_arrow_left)
		{
			v.startAnimation(animationClick);
			if (index > 0)
				viewPager.setCurrentItem(index - 1, true);
		}

		setLeftRightArrows();
	}


	// OnPageSelected
	@Override
	public void onPageSelected(int position)
	{

		CommonStuff.setCurrentLevelIndex(position);
		setLeftRightArrows();
	}


	// Unused
	@Override public void onPageScrollStateChanged(int arg0) { }
	@Override public void onPageScrolled(int arg0, float arg1, int arg2) { }
	@Override public void afterOnClick() { }


}
