package com.dreamfire.whereintheworld.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.stuff.CommonStuff;



public class FragmentCategoriesFragment extends Fragment implements OnClickListener
{
	private int position;
	private ImageView imageView, imageViewLock, imageViewTick;
	private MyTextView textView;
	private RelativeLayout layoutLock;

	private ActivityMain mainActivity;


	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	// Constructor
	public FragmentCategoriesFragment(int position)
	{

		this.position = position;
	}


	// OnCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		mainActivity = (ActivityMain) getActivity();

		View view = inflater.inflate(R.layout.fragment_categories_fragment, container, false);
		imageView = (ImageView) view.findViewById(R.id.categories_fragment_image);
		imageViewLock = (ImageView) view.findViewById(R.id.categories_fragment_image_lock);
		imageViewTick = (ImageView) view.findViewById(R.id.categories_fragment_image_tick);
		textView = (MyTextView) view.findViewById(R.id.categories_fragment_textview);
		layoutLock = (RelativeLayout) view.findViewById(R.id.categories_fragment_layout_lock);

		imageView.setOnClickListener(this);

		textView.setText(
				getResources().getString(R.string.string_fragmentpagercategories_unlock1) +
				GeneralConstants.newLine +
				GeneralConstants.tokenCostPerCategory +
				getResources().getString(R.string.string_fragmentpagercategories_unlock2));

		try
		{
			// Set whether category is locked or not
			if (CommonStuff.gameState.categoryStateData[position].locked == true)
			{
				Glide.with(this).load(R.drawable.image_common_lock).into(imageViewLock);
				layoutLock.setVisibility(View.VISIBLE);
				imageViewTick.setVisibility(View.INVISIBLE);
			}
			else if (CommonStuff.gameState.categoryStateData[position].completed == true)
			{
				Glide.with(this).load(R.drawable.image_common_categories_tick).into(imageViewTick);
				layoutLock.setVisibility(View.INVISIBLE);
				imageViewTick.setVisibility(View.VISIBLE);
			}
			else
			{
				layoutLock.setVisibility(View.INVISIBLE);
				imageViewTick.setVisibility(View.INVISIBLE);
			}
		}
		catch (Exception e) {}

		mainActivity.loadBitmapCategory(CommonStuff.gameState.categoryNames[position], imageView);

		return view;
	}


	// onHiddenChanged
	@Override
	public void onHiddenChanged(boolean hidden)
	{

		super.onHiddenChanged(hidden);

		if (hidden == false)
			imageView.setImageDrawable(null);
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// OnClick
	@Override
	public void onClick(View v)
	{

		if (v == imageView)
			mainActivity.categoryImageClicked(position);
	}

}
