package com.dreamfire.whereintheworld.database;

public class RowLocationState
{
	public String guid;
	public long currentScore;
	public boolean completed;

	public RowLocationState(String guid, long currentScore, boolean completed)
	{
		this.guid = guid;
		this.currentScore = currentScore;The
		this.completed = completed;
	}
}
