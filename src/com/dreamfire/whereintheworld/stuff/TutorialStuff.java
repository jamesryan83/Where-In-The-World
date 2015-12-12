package com.dreamfire.whereintheworld.stuff;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;

public class TutorialStuff
{
	// Start
	public static SpannableString getTutorialDialog1(Context context)
	{
		return new SpannableString(context.getResources().getString(R.string.string_tutorial_mainmenu1) +
			GeneralConstants.newLine +
			GeneralConstants.newLine +
			context.getResources().getString(R.string.string_tutorial_mainmenu2) +
			GeneralConstants.newLine +
			context.getResources().getString(R.string.string_tutorial_mainmenu3));
	}

	// Categories
	public static SpannableString getTutorialDialog4(Context context)
	{
		Drawable[] drawableArray = new Drawable[1];
		drawableArray[0] = getTextDrawable(context, R.drawable.image_common_token, 50, 50);

		return getSpannableString(drawableArray, context.getResources().getString(R.string.string_tutorial_categories));
	}

	// Locations
	public static SpannableString getTutorialDialog6(Context context)
	{
		Drawable[] drawableArray = new Drawable[2];
		drawableArray[0] = getTextDrawable(context, R.drawable.image_tutorial_required_points, 182, 46);
		drawableArray[1] = getTextDrawable(context, R.drawable.image_common_token, 50, 50);

		return getSpannableString(drawableArray, context.getResources().getString(R.string.string_tutorial_locations));
	}


	// Main Game 1
	public static SpannableString getTutorialDialog7(Context context)
	{
		Drawable[] drawableArray = new Drawable[1];
		drawableArray[0] = getTextDrawable(context, R.drawable.image_tutorial_switch_to_map, 50, 50);

		return getSpannableString(drawableArray, context.getResources().getString(R.string.string_tutorial_main_game1));
	}

	// Main Game 2
	public static SpannableString getTutorialDialog8(Context context)
	{
		Drawable[] drawableArray = new Drawable[3];
		drawableArray[0] = getTextDrawable(context, R.drawable.image_tutorial_switch_to_street, 50, 50);
		drawableArray[1] = getTextDrawable(context, R.drawable.image_tutorial_guess_marker, 83, 50);
		drawableArray[2] = getTextDrawable(context, R.drawable.image_tutorial_accept, 94, 50);

		return getSpannableString(drawableArray, context.getResources().getString(R.string.string_tutorial_main_game2));
	}

	// Main Game 3
	public static SpannableString getTutorialDialog9(Context context)
	{
		Drawable[] drawableArray = new Drawable[1];
		drawableArray[0] = getTextDrawable(context, R.drawable.image_tutorial_hints, 50, 50);

		return getSpannableString(drawableArray, context.getResources().getString(R.string.string_tutorial_main_game3));
	}

	// Hints
	public static SpannableString getTutorialDialog10(Context context)
	{
		Drawable[] drawableArray = new Drawable[1];
		drawableArray[0] = getTextDrawable(context, R.drawable.image_common_token, 50, 50);

		return getSpannableString(drawableArray, context.getResources().getString(R.string.string_tutorial_hints));
	}

	// Post Game
	public static SpannableString getTutorialDialog11(Context context)
	{
		Drawable[] drawableArray = new Drawable[3];
		drawableArray[0] = getTextDrawable(context, R.drawable.image_tutorial_view_map, 175, 50);
		drawableArray[1] = getTextDrawable(context, R.drawable.image_tutorial_post_next, 175, 50);
		drawableArray[2] = getTextDrawable(context, R.drawable.image_tutorial_wiki, 175, 50);

		return getSpannableString(drawableArray, context.getResources().getString(R.string.string_tutorial_post_game));
	}

	// Last tutorial dialog
	public static SpannableString getTutorialDialog12(Context context)
	{
		return new SpannableString(context.getResources().getString(R.string.string_tutorial_last));
	}


	/* ===============================================================================================================================================================
	/																	Spannable Text for Tutorial Dialogs
	/ ==============================================================================================================================================================*/

	// Returns a Drawable
	private static Drawable getTextDrawable(Context context, int imageResourceId, int width, int height)
	{
		Drawable drawable = context.getResources().getDrawable(imageResourceId);
		drawable.setBounds(0, 0, width, height);
		return drawable;
	}


	// Returns a spannableString with the * characters replaced with Drawables
	private static SpannableString getSpannableString(Drawable[] drawables, String text)
	{
		SpannableString spannableString = new SpannableString(text);

		int count = 0;
		for (int i = 0; i < spannableString.length(); i++)
			if (spannableString.charAt(i) == '*')
				spannableString.setSpan(new ImageSpan(drawables[count++], ImageSpan.ALIGN_BOTTOM), i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		return spannableString;
	}
}
