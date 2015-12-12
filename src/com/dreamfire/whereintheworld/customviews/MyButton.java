package com.dreamfire.whereintheworld.customviews;



import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.dreamfire.whereintheworld.constants.GeneralConstants;

public class MyButton extends Button
{
	public MyButton(Context context)
	{
        super(context);
        init();
    }

    public MyButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public MyButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
    	if (isInEditMode() == false)
        {
    		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), GeneralConstants.FONT_NAME);
    		setTypeface(tf);
        }

    	setSoundEffectsEnabled(false);
    }

}
