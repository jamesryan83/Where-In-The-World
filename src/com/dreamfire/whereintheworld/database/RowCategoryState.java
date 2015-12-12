package com.dreamfire.whereintheworld.database;

public class RowCategoryState
{
	public boolean locked;
	public boolean completed;

	public RowCategoryState(boolean locked, boolean completed)
	{
		this.locked = locked;
		this.completed = completed;
	}
}
