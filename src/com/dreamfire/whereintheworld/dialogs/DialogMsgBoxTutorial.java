package com.dreamfire.whereintheworld.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
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

public class DialogMsgBoxTutorial extends Dialog implements OnClickListener, AnimationListener
{
	private Activity callingActivity;
	private String title;
	private SpannableString message;
	private int callback;

	private MyButton buttonOk;
	private MyTextView textViewTitle, textViewMessage;

	private Animation animationClick;

	private int tempViewId;


	public DialogMsgBoxTutorial(Activity callingActivity, String title, SpannableString message, int callback)
	{
		super(callingActivity);

		this.callingActivity = callingActivity;
		this.title = title;
		this.message = message;
		this.callback = callback;
	}


	// OnCreate
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.dialog_msgbox_tutorial);

		int[] widthAndHeight = CommonStuff.getScreenWidthHeight(callingActivity.getApplicationContext());
		getWindow().setLayout((int) (widthAndHeight[0] * 0.8), (int) (widthAndHeight[1] * 0.8));

		buttonOk = (MyButton) findViewById(R.id.dialog_msgbox_tutorial_button_ok);
		textViewTitle = (MyTextView) findViewById(R.id.dialog_msgbox_tutorial_textview_title);
		textViewMessage = (MyTextView) findViewById(R.id.dialog_msgbox_tutorial_textview_message);

		textViewTitle.setText(title);
		textViewMessage.setText(message);

		buttonOk.setOnClickListener(this);

		animationClick = AnimationUtils.loadAnimation(callingActivity.getApplicationContext(), R.anim.button);
		animationClick.setAnimationListener(this);


	}



	// OnClick
	@Override
	public void onClick(View v)
	{

		CommonStuff.playClickSound(callingActivity.getApplicationContext());

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
			case R.id.dialog_msgbox_tutorial_button_ok :
				((ActivityMain)callingActivity).callbackForDialogMsgBoxTutorial(callback);
				dismiss();
				break;
		}
	}


	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }
}
