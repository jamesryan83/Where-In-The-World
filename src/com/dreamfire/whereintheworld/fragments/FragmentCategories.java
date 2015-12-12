package com.dreamfire.whereintheworld.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.stuff.AsyncTasks.CategoriesDataTask;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.TutorialStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.viewpagerindicator.CirclePageIndicator;


public class FragmentCategories extends AbstractFragment implements OnPageChangeListener
{
	private ImageView imageViewArrowLeft, imageViewArrowRight;

	public ViewPager viewPager;
	private CirclePageIndicator circlePageIndicator;


	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_categories, container, false);

		imageViewArrowLeft = (ImageView) view.findViewById(R.id.categories_imageview_next_arrow_left);
		imageViewArrowRight = (ImageView) view.findViewById(R.id.categories_imageview_next_arrow_right);

		imageViewArrowLeft.setOnClickListener(this);
		imageViewArrowRight.setOnClickListener(this);

		viewPager = (ViewPager) view.findViewById(R.id.categories_viewpager);
		circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.categories_circlepageindicator);
		circlePageIndicator.setOnPageChangeListener(this);

		updateUi();

		return view;
	}


	// Update the UI
	@Override
	public void updateUi()
	{

		super.updateUi();

		imageViewArrowLeft.setVisibility(View.INVISIBLE);
		imageViewArrowRight.setVisibility(View.INVISIBLE);

		if (CommonStuff.gameState.lightThemeEnabled == true)
			circlePageIndicator.setFillColor(getResources().getColor(R.color.color_yellow));
		else
			circlePageIndicator.setFillColor(getResources().getColor(R.color.color_purple));

		try
		{
			Glide.with(this).load(R.drawable.image_common_next_arrow_left).into(imageViewArrowLeft);
			Glide.with(this).load(R.drawable.image_common_next_arrow_right).into(imageViewArrowRight);
		}
		catch (Exception e) {}

		new CategoriesDataTask(mainActivity.getApplicationContext()).execute();
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

	// after asynctask
	@Override
	public void setupUiAfterAsync()
	{
		super.setupUiAfterAsync();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Categories");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		viewPager.setAdapter(new FragmentPagerAdapterCategories(mainActivity.getSupportFragmentManager(),
				CommonStuff.gameState.difficultyStateData[CommonStuff.gameState.currentDifficultyIndex].numCategoriesPerDifficulty));
		circlePageIndicator.setViewPager(viewPager);

		viewPager.setCurrentItem(CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex]);

		mainActivity.setTextViewTitle(CommonStuff.gameState.categoryNames[CommonStuff.gameState.currentCategoryIndex[CommonStuff.gameState.currentDifficultyIndex]].replace("_", " "));

		setLeftRightArrows();

		// Tutorial
		if (CommonStuff.gameState.tutorialModeOn == true)
		{
			mainActivity.showTutorialDialog(TutorialStuff.getTutorialDialog4(mainActivity.getApplicationContext()), DialogConstants.NO_CALLBACK);
			CommonStuff.gameState.tutorialDialogsSeen[3] = true;
		}
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
		else if (index == CommonStuff.gameState.difficultyStateData[CommonStuff.gameState.currentDifficultyIndex].numCategoriesPerDifficulty - 1)
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
	/																	Listeners
	/ =============================================================================================================================================================*/

	// OnClick
	@Override
	public void onClick(View v)
	{

		super.onClick(v);

		int index = viewPager.getCurrentItem();

		if (tempViewId == R.id.categories_imageview_next_arrow_right)
		{
			v.startAnimation(animationClick);
			if (index < CommonStuff.gameState.categoryNames.length - 1)
				viewPager.setCurrentItem(index + 1, true);
		}

		if (tempViewId == R.id.categories_imageview_next_arrow_left)
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

		CommonStuff.setCurrentCategoryIndex(mainActivity.getApplicationContext(), position);
		mainActivity.setTextViewTitle(CommonStuff.gameState.categoryNames[position].replace("_", " "));
		setLeftRightArrows();
	}


	// Unused
	@Override public void onPageScrollStateChanged(int arg0) { }
	@Override public void onPageScrolled(int arg0, float arg1, int arg2) { }
	@Override public void afterOnClick() { }

}
