package com.dreamfire.whereintheworld.dialogs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class DialogCredits extends DialogFragment implements OnClickListener, AnimationListener
{
	private MyButton buttonOk;
	private Animation animationClick;
	private ImageView imageView;

	private int tempViewId;

	// newInstance
	public static DialogCredits newInstance()
	{
		DialogCredits dialog = new DialogCredits();
		return dialog;
	}


	// OnCreate
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
	}


	// OnCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.dialog_credits, container);

		buttonOk = (MyButton) view.findViewById(R.id.dialog_credits_button_ok);
		imageView = (ImageView) view.findViewById(R.id.dialog_credits_imageview_logo);

		try
		{
			Glide.with(this).load(R.drawable.image_x_company_logo).into(imageView);
		}
		catch (Exception e) {}

		buttonOk.setOnClickListener(this);

		animationClick = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.button);
		animationClick.setAnimationListener(this);

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Credits Dialog");
		tracker.send(new HitBuilders.AppViewBuilder().build());

		return view;
	}




	// OnClick
	@Override
	public void onClick(View v)
	{

		CommonStuff.playClickSound(getActivity().getApplicationContext());

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

		switch (tempViewId)
		{
			case R.id.dialog_credits_button_ok :
				dismiss();
				break;
		}
	}


	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }
}
