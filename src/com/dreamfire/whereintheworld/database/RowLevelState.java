package com.dreamfire.whereintheworld.database;

public class RowLevelState
{
	public long bestScore;
	public boolean passed;		// some locations completed
	public boolean completed;	// all locations completed

	public RowLevelState(long bestScore, boolean passed, boolean completed)
	{
		this.bestScore = bestScore;
		this.passed = passed;
		this.completed = completed;
	}
}
