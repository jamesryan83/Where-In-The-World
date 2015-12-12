package com.dreamfire.whereintheworld.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.stuff.CommonStuff;

public class DialogPostGameInfo extends Dialog implements OnClickListener, AnimationListener
{
	private Activity callingActivity;

	private MyButton buttonOk;
	private MyTextView textViewDescription;

	private String description;

	private Animation animationClick;


	public DialogPostGameInfo(Activity callingActivity, String description)
	{
		super(callingActivity);

		this.callingActivity = callingActivity;
		this.description = description;
	}


	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.dialog_post_game_info);

		int[] widthAndHeight = CommonStuff.getScreenWidthHeight(callingActivity.getApplicationContext());
		getWindow().setLayout((int) (widthAndHeight[0] * 0.8), (int) (widthAndHeight[1] * 0.8));

		buttonOk = (MyButton) findViewById(R.id.dialog_post_game_info_button_ok);
		textViewDescription = (MyTextView) findViewById(R.id.dialog_post_game_info_textview_description);

		textViewDescription.setText(description);

		animationClick = AnimationUtils.loadAnimation(callingActivity.getApplicationContext(), R.anim.button);
		animationClick.setAnimationListener(this);

		buttonOk.setOnClickListener(this);
	}


	private int tempViewId;

	@Override
	public void onClick(View v)
	{

		CommonStuff.playClickSound(callingActivity.getApplicationContext());

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

		((ActivityMain)callingActivity).dialogPostGameInfoClicked(tempViewId);

		dismiss();
	}


	// onBackPressed
	@Override
	public void onBackPressed()
	{
		afterOnClick();
	}


	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }
}
