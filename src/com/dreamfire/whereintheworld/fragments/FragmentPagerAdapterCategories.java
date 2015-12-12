package com.dreamfire.whereintheworld.fragments;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class FragmentPagerAdapterCategories extends FragmentStatePagerAdapter
{
	private int count;

	public FragmentPagerAdapterCategories(FragmentManager f, int count)
	{
		super (f);
		this.count = count;
	}

	@Override
	public Fragment getItem(int position)
	{
		return new FragmentCategoriesFragment(position);
	}

	@Override
	public int getCount()
	{
		return count;
	}

}
