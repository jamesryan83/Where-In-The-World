package com.dreamfire.whereintheworld.database;

public class RowDifficultyState
{
	public boolean completed;
	public int numCategoriesPerDifficulty;

	public RowDifficultyState(boolean completed, int numCategoriesPerDifficulty)
	{
		this.completed = completed;
		this.numCategoriesPerDifficulty = numCategoriesPerDifficulty;
	}
}
