package com.dreamfire.whereintheworld.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyButton;
import com.dreamfire.whereintheworld.dialogs.DialogCredits;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class FragmentSettings extends AbstractFragment
{
	private ImageButton checkBoxNotifications, checkBoxMusic, checkBoxLightTheme, checkBoxDarkTheme, checkBoxKilometres, checkBoxMiles;
	private MyButton buttonResetDatabase, buttonReferences, buttonCredits, buttonLegalNotices;



	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// Main
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		checkBoxNotifications = (ImageButton) view.findViewById(R.id.settings_checkbox_imagebutton_notifications);
		checkBoxMusic = (ImageButton) view.findViewById(R.id.settings_checkbox_imagebutton_music);
		checkBoxLightTheme = (ImageButton) view.findViewById(R.id.settings_checkbox_imagebutton_light_theme);
		checkBoxDarkTheme = (ImageButton) view.findViewById(R.id.settings_checkbox_imagebutton_dark_theme);
		checkBoxKilometres = (ImageButton) view.findViewById(R.id.settings_checkbox_imagebutton_kilometres);
		checkBoxMiles = (ImageButton) view.findViewById(R.id.settings_checkbox_imagebutton_miles);

		buttonResetDatabase = (MyButton) view.findViewById(R.id.settings_button_reset_database);
		buttonReferences = (MyButton) view.findViewById(R.id.settings_button_references);
		buttonCredits = (MyButton) view.findViewById(R.id.settings_button_credits);
		buttonLegalNotices = (MyButton) view.findViewById(R.id.settings_button_legal_notices);

		checkBoxNotifications.setOnClickListener(this);
		checkBoxMusic.setOnClickListener(this);
		checkBoxLightTheme.setOnClickListener(this);
		checkBoxDarkTheme.setOnClickListener(this);
		checkBoxKilometres.setOnClickListener(this);
		checkBoxMiles.setOnClickListener(this);

		buttonResetDatabase.setOnClickListener(this);
		buttonReferences.setOnClickListener(this);
		buttonCredits.setOnClickListener(this);
		buttonLegalNotices.setOnClickListener(this);

		updateUi();

		return view;
	}


	// update the ui
	@Override
	public void updateUi()
	{

		super.updateUi();

		Tracker tracker = CommonStuff.tracker;
		tracker.setScreenName("Fragment Settings");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		setCheckBoxImage(checkBoxNotifications, CommonStuff.gameState.notificationBarEnabled);
		setCheckBoxImage(checkBoxMusic, CommonStuff.gameState.musicEnabled);
		setCheckBoxImage(checkBoxLightTheme, CommonStuff.gameState.lightThemeEnabled);
		setCheckBoxImage(checkBoxDarkTheme, !CommonStuff.gameState.lightThemeEnabled);
		setCheckBoxImage(checkBoxKilometres, CommonStuff.gameState.kilometresEnabled);
		setCheckBoxImage(checkBoxMiles, !CommonStuff.gameState.kilometresEnabled);
	}


	/* ==============================================================================================================================================================
	/																	Other Methods
	/ =============================================================================================================================================================*/

	// Sets the image of the checkbox
	private void setCheckBoxImage(ImageButton button, boolean checked)
	{
		try
		{
			if (checked == true)
				Glide.with(this).load(R.drawable.image_common_checkbox_checked).into(button);
			else
				Glide.with(this).load(R.drawable.image_common_checkbox_unchecked).into(button);
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
		v.startAnimation(animationClick);
	}


	// afterOnClick
	@Override
	public void afterOnClick()
	{

		Tracker tracker = CommonStuff.tracker;

		boolean saveSettings = false;

		switch (tempViewId)
		{
			case R.id.settings_checkbox_imagebutton_notifications :
				if (CommonStuff.gameState.notificationBarEnabled == false)
				{
					CommonStuff.gameState.notificationBarEnabled = true;
					getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
					setCheckBoxImage(checkBoxNotifications, true);
				}
				else
				{
					CommonStuff.gameState.notificationBarEnabled = false;
					getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
					getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					setCheckBoxImage(checkBoxNotifications, false);
				}
				saveSettings = true;


				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("CheckBox Click Event")
		    	.setAction("Pressed Notifications Checkbox")
		    	.build());
				break;

			case R.id.settings_checkbox_imagebutton_music :
				if (CommonStuff.gameState.musicEnabled == true)
				{
					mainActivity.doUnbindService();
					CommonStuff.gameState.musicEnabled = false;
					setCheckBoxImage(checkBoxMusic, false);
				}
				else
				{
					mainActivity.startMusic();
					CommonStuff.gameState.musicEnabled = true;
					setCheckBoxImage(checkBoxMusic, true);
				}
				saveSettings = true;

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("CheckBox Click Event")
		    	.setAction("Pressed Music Checkbox")
		    	.build());
				break;

			case R.id.settings_checkbox_imagebutton_light_theme :
				CommonStuff.gameState.lightThemeEnabled = true;
				setCheckBoxImage(checkBoxLightTheme, true);
				setCheckBoxImage(checkBoxDarkTheme, false);
				saveSettings = true;
				mainActivity.setBackgroundTheme();

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("CheckBox Click Event")
		    	.setAction("Pressed Light Theme Checkbox")
		    	.build());
				break;

			case R.id.settings_checkbox_imagebutton_dark_theme :
				CommonStuff.gameState.lightThemeEnabled = false;
				setCheckBoxImage(checkBoxLightTheme, false);
				setCheckBoxImage(checkBoxDarkTheme, true);
				saveSettings = true;
				mainActivity.setBackgroundTheme();

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("CheckBox Click Event")
		    	.setAction("Pressed Dark Theme Checkbox")
		    	.build());
				break;

			case R.id.settings_checkbox_imagebutton_kilometres :
				CommonStuff.gameState.kilometresEnabled = true;
				setCheckBoxImage(checkBoxKilometres, true);
				setCheckBoxImage(checkBoxMiles, false);
				saveSettings = true;

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("CheckBox Click Event")
		    	.setAction("Pressed Kilometres Checkbox")
		    	.build());
				break;

			case R.id.settings_checkbox_imagebutton_miles :
				CommonStuff.gameState.kilometresEnabled = false;
				setCheckBoxImage(checkBoxKilometres, false);
				setCheckBoxImage(checkBoxMiles, true);
				saveSettings = true;

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("CheckBox Click Event")
		    	.setAction("Pressed Miles Checkbox")
		    	.build());
				break;

			case R.id.settings_button_reset_database :
				DialogMsgBoxGeneral.newInstance(getResources().getString(R.string.string_settings_reset_database),
						getResources().getString(R.string.string_settings_reset_database1) + GeneralConstants.newLine +
						getResources().getString(R.string.string_settings_reset_database2) + GeneralConstants.newLine +
						getResources().getString(R.string.string_settings_reset_database3) + GeneralConstants.newLine +
						getResources().getString(R.string.string_settings_reset_database4) + GeneralConstants.newLine +
						getResources().getString(R.string.string_settings_reset_database5),
						false, true, DialogConstants.RESET_DATABASE)
						.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed Reset Database Button")
		    	.build());
				break;

			case R.id.settings_button_references :
				DialogMsgBoxGeneral.newInstance("",
						getResources().getString(R.string.string_settings_references_context),
						false, false, DialogConstants.NO_CALLBACK)
						.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed References Button")
		    	.build());
				break;

			case R.id.settings_button_credits :
				DialogCredits.newInstance().show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed Credits Button")
		    	.build());
				break;

			case R.id.settings_button_legal_notices :
				String licenceInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getActivity());
				AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
				dialog.setTitle(getResources().getString(R.string.string_settings_legal));
				if (licenceInfo != null)
				{
					dialog.setMessage(licenceInfo);
					dialog.show();
				}

				tracker.send(new HitBuilders.EventBuilder()
		    	.setCategory("Button Click Event")
		    	.setAction("Pressed Legal Notices Button")
		    	.build());
				break;
		}

		if (saveSettings == true)
			CommonStuff.gameState.save(getActivity());
	}


}
