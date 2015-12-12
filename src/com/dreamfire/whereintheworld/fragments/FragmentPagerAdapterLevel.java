package com.dreamfire.whereintheworld.fragments;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class FragmentPagerAdapterLevel extends FragmentStatePagerAdapter
{
	private int count;

	public FragmentPagerAdapterLevel(FragmentManager f, int count)
	{
		super (f);
		this.count = count;
	}

	@Override
	public Fragment getItem(int position)
	{
		return new FragmentLevelsFragment(position);
	}

	@Override
	public int getCount()
	{
		return count;
	}

}
