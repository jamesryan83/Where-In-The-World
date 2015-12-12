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
import com.dreamfire.whereintheworld.customviews.MyTextView;
import com.dreamfire.whereintheworld.stuff.CommonStuff;



public class FragmentLevelsFragment extends Fragment implements OnClickListener
{
	private int position;
	private RelativeLayout layout;
	private MyTextView textViewLevel, textViewBestScore, textViewDifficulty, textViewLevelComplete;
	private ImageView imageView;
	private boolean levelUnlocked;

	private ActivityMain mainActivity;

	// Constructor
	public FragmentLevelsFragment(int position)
	{

		this.position = position;
	}

	// OnCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		mainActivity = (ActivityMain) getActivity();

		View view = inflater.inflate(R.layout.fragment_levels_fragment, container, false);

		layout = (RelativeLayout) view.findViewById(R.id.level_fragment_layout);
		textViewLevel = (MyTextView) view.findViewById(R.id.level_fragment_textview_level);
		textViewBestScore = (MyTextView) view.findViewById(R.id.level_fragment_textview_best_score);
		textViewDifficulty = (MyTextView) view.findViewById(R.id.level_fragment_textview_difficulty);
		textViewLevelComplete = (MyTextView) view.findViewById(R.id.level_fragment_textview_level_complete);
		imageView = (ImageView) view.findViewById(R.id.level_fragment_imageview_lock);

		layout.setOnClickListener(this);

		try
		{
			Glide.with(this).load(R.drawable.image_common_lock).into(imageView);
		}
		catch (Exception e) {}

		// Set text
		String[] levelNames = getResources().getStringArray(R.array.string_array_common_level_names);
		String[] difficultyNames = getResources().getStringArray(R.array.string_array_common_difficulty_names);

		textViewLevel.setText(levelNames[position]);
		textViewDifficulty.setText(difficultyNames[CommonStuff.gameState.currentDifficultyIndex]);


		// Set best score
		long bestScore = CommonStuff.gameState.levelStateData[position].bestScore;
		if (bestScore > 0)
		{
			textViewBestScore.setVisibility(View.VISIBLE);
			textViewBestScore.setText(getResources().getString(R.string.string_fragmentpagerlevel_bestscore) + bestScore);
		}
		else
		{
			textViewBestScore.setVisibility(View.GONE);
			textViewBestScore.setText("");
		}


		// Show "Level Completed" or not
		if (CommonStuff.gameState.levelStateData[position].completed == true)
		{
			textViewLevelComplete.setText(getResources().getString(R.string.string_common_level_complete));
			textViewLevelComplete.setVisibility(View.VISIBLE);
		}
		else if (CommonStuff.gameState.levelStateData[position].passed == true)
		{
			textViewLevelComplete.setText(getResources().getString(R.string.string_common_level_passed));
			textViewLevelComplete.setVisibility(View.VISIBLE);
		}
		else
			textViewLevelComplete.setVisibility(View.GONE);


		// Set whether level is locked or not
		if (position == 0)
		{
			imageView.setVisibility(View.INVISIBLE);
			levelUnlocked = true;
		}
		else
		{
			levelUnlocked = CommonStuff.gameState.levelStateData[position - 1].passed;
			if (levelUnlocked == false)
				imageView.setVisibility(View.VISIBLE);
			else
				imageView.setVisibility(View.INVISIBLE);
		}

		return view;
	}

	@Override
	public void onClick(View v)
	{

		if (v == layout)
			mainActivity.levelFragmentClicked(position, levelUnlocked);
	}

}
