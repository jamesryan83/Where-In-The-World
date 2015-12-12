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

import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.stuff.CommonStuff;

public class DialogMsgBoxGeneral extends DialogFragment implements OnClickListener, AnimationListener
{
	private static final String TITLE = "dialog_title";
	private static final String MESSAGE = "dialog_message";
	private static final String SHOW_TOKENS_BUTTON = "dialog_showTokensButton";
	private static final String SHOW_CANCEL_BUTTON = "dialog_showCancelButton";
	private static final String CALLBACK = "dialog_callback";

	private MyButton buttonOk, buttonBuyTokens, buttonCancel;
	private MyTextView textViewTitle, textViewMessage;

	private Animation animationClick;

	private int tempViewId;

	// newInstance
	public static DialogMsgBoxGeneral newInstance(String title, String message, boolean showTokensButton, boolean showCancelButton, int callback)
	{

		DialogMsgBoxGeneral dialog = new DialogMsgBoxGeneral();
		Bundle bundle = new Bundle();
		bundle.putString(TITLE, title);
		bundle.putString(MESSAGE, message);
		bundle.putBoolean(SHOW_TOKENS_BUTTON, showTokensButton);
		bundle.putBoolean(SHOW_CANCEL_BUTTON, showCancelButton);
		bundle.putInt(CALLBACK, callback);
		dialog.setArguments(bundle);
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

		View view = inflater.inflate(R.layout.dialog_msgbox_general, container);

		buttonOk = (MyButton) view.findViewById(R.id.dialog_msgbox_general_button_ok);
		buttonBuyTokens = (MyButton) view.findViewById(R.id.dialog_msgbox_general_button_buy_tokens);
		buttonCancel = (MyButton) view.findViewById(R.id.dialog_msgbox_general_button_cancel);
		textViewTitle = (MyTextView) view.findViewById(R.id.dialog_msgbox_general_textview_title);
		textViewMessage = (MyTextView) view.findViewById(R.id.dialog_msgbox_general_textview_message);


		if (getArguments().getString(TITLE) == null || getArguments().getString(TITLE).length() == 0)
			textViewTitle.setVisibility(View.GONE);
		else
			textViewTitle.setText(getArguments().getString(TITLE));

		textViewMessage.setText(getArguments().getString(MESSAGE));

		buttonOk.setOnClickListener(this);
		buttonBuyTokens.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);

		buttonBuyTokens.setVisibility(getArguments().getBoolean(SHOW_TOKENS_BUTTON) == true ? View.VISIBLE : View.GONE);
		buttonCancel.setVisibility(getArguments().getBoolean(SHOW_CANCEL_BUTTON) == true ? View.VISIBLE : View.GONE);

		animationClick = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.button);
		animationClick.setAnimationListener(this);

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

		try
		{
			switch (tempViewId)
			{
				case R.id.dialog_msgbox_general_button_ok :
					((ActivityMain)getActivity()).callbackForDialogMsgBoxGeneral(getArguments().getInt(CALLBACK));
					dismiss();
					break;

				case R.id.dialog_msgbox_general_button_buy_tokens :
					((ActivityMain)getActivity()).goToPage(FragmentConstants.FRAGMENT_TOKENS);
					dismiss();
					break;

				default :
					dismiss();
			}
		}
		catch (Exception e) {}
	}


	// Unused
	@Override public void onAnimationStart(Animation animation) { }
	@Override public void onAnimationRepeat(Animation animation) { }
}
