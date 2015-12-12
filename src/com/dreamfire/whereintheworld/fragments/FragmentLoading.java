package com.dreamfire.whereintheworld.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.stuff.CommonStuff;

public class FragmentLoading extends AbstractFragment
{
	private ImageView image;
	private RelativeLayout layout;
	private Animation animation;

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.dialog_progress, container, false);

		layout = (RelativeLayout) view.findViewById(R.id.dialog_progress_layout);
		image = (ImageView) view.findViewById(R.id.dialog_progress_imageview_circle);

		animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.progress_screen);

		setBackgroundColor(CommonStuff.gameState.lightThemeEnabled);

		return view;
	}

	public void setBackgroundColor(boolean lightTheme)
	{
		if (lightTheme == true)
		{
			layout.setBackgroundColor(getResources().getColor(R.color.color_white));
			image.setImageResource(R.drawable.image_loading_dark);
			image.startAnimation(animation);
		}
		else
		{
			layout.setBackgroundColor(getResources().getColor(R.color.color_gray_dark));
			image.setImageResource(R.drawable.image_loading_light);
			image.startAnimation(animation);
		}
	}

	@Override public void afterOnClick() { }
}
