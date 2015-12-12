package com.dreamfire.whereintheworld.fragments;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.stuff.CommonStuff;

public abstract class AbstractFragment extends Fragment implements IFragment, OnClickListener, AnimationListener
{

	ActivityMain mainActivity;
	Animation animationClick;
	int tempViewId;


	// Update the ui
	@Override
	public void updateUi()
	{
		mainActivity = (ActivityMain) getActivity();

		// Button click animation

	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(getActivity());

		tryToLoadAnimation();
	}


	// OnClick
	@Override
	public void onClick(View v)
	{
		CommonStuff.playClickSound(mainActivity.getApplicationContext());
		tempViewId = v.getId();
	}


	private void tryToLoadAnimation()
	{
		try
		{
			if (animationClick == null)
			{
				if (mainActivity == null)
					mainActivity = (ActivityMain) getActivity();

				animationClick = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.button);
				animationClick.setAnimationListener(this);
			}
		}
		catch (Exception e)
		{
			try
			{
				if (animationClick == null)
				{
					animationClick = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.button);
					animationClick.setAnimationListener(this);
				}
			}
			catch (Exception e2) { }
		}
	}


	// After Animation
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

	@Override
	public void setupUiAfterAsync()
	{
		if (mainActivity == null)
			mainActivity = (ActivityMain) getActivity();
	}

	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }

}
