package com.dreamfire.whereintheworld.database;


public class Hint
{
	public int id;
	public String name;
	public boolean purchased;
	public boolean repeatable;
	public boolean used;
	public int tokenCost;
	public String hintTitle;
	public String hintMessage;
	public int imageResourceId;
	public String flagCountryName;

	public Hint(int id, String name, int tokenCost, boolean repeatable)
	{
		this.id = id;
		this.name = name;
		purchased = false;
		this.repeatable = repeatable;
		this.tokenCost = tokenCost;
		used = false;
	}
}
