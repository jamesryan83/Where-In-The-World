package com.dreamfire.whereintheworld.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dreamfire.whereintheworld.constants.GeneralConstants;

public class MyTextView extends TextView
{
	public MyTextView(Context context)
    {
        super(context);
        setFont(null);
    }

	public MyTextView(Context context, AttributeSet attrs)
	{
        super(context, attrs);
        setFont(attrs);
    }

	public MyTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setFont(attrs);
	}

    private void setFont(AttributeSet attrs)
    {
        if (isInEditMode() == false)
        {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), GeneralConstants.FONT_NAME);
            setTypeface(tf);
        }
    }
}
