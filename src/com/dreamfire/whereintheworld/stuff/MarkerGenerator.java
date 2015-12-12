package com.dreamfire.whereintheworld.stuff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.customviews.MyTextView;

@SuppressLint("InflateParams")
public class MarkerGenerator
{
	private final Context context;

	private ViewGroup viewGroup;
	private MyTextView textView1;

	private LinearLayout linearLayout;


	public MarkerGenerator(Context context)
	{
		this.context = context;
	}

	// Get guess marker
	public Bitmap makeGuessMarker(String text)
	{
		return makeMarker(text, R.layout.map_marker_guess, R.id.map_marker_layout_guess, R.id.map_marker_textview_guess);
	}

	// Get second guess marker
	public Bitmap makeSecondGuessMarker(String text)
	{
		return makeMarker(text, R.layout.map_marker_second_guess, R.id.map_marker_layout_second_guess, R.id.map_marker_textview_second_guess);
	}


	// Guess marker
	private Bitmap makeMarker(String text, int layoutId, int linearLayoutId, int textViewId)
	{

		viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
		linearLayout = (LinearLayout) viewGroup.findViewById(linearLayoutId);
		textView1 = (MyTextView) linearLayout.findViewById(textViewId);

		textView1.setText(text);

		int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		linearLayout.measure(measureSpec, measureSpec);

		int measuredWidth = linearLayout.getMeasuredWidth();
		int measuredHeight = linearLayout.getMeasuredHeight();

		linearLayout.layout(0, 0, measuredWidth, measuredHeight);

		Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);

		Canvas canvas = new Canvas(bitmap);

		linearLayout.draw(canvas);
		return bitmap;
	}

	// Arrow icon
	public Bitmap makeMarker(float rotation)
	{

		viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.map_marker_hint_arrow, null);
		linearLayout = (LinearLayout) viewGroup.findViewById(R.id.map_marker_layout_hint_arrow);

		int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		linearLayout.measure(measureSpec, measureSpec);

		int layoutSize = Math.max(linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());

		linearLayout.layout(0, 0, layoutSize, layoutSize);

		Bitmap bitmap = Bitmap.createBitmap(layoutSize, layoutSize, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);

		Canvas canvas = new Canvas(bitmap);

		canvas.rotate(rotation, bitmap.getWidth() / 2, bitmap.getHeight() / 2);

		linearLayout.draw(canvas);
		return bitmap;
	}
}
