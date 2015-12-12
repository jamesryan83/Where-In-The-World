package com.dreamfire.whereintheworld.database;

public class RowLocationData
{
	public double latitude;
	public double longitude;
	public double heading;
	public double tilt;

	public String name;
	public String description;
	public String suburb;
	public String city;
	public String country;
	public String wikiurl;

	public String hint_continent;
	public String hint_capital_city;
	public String hint_area;
	public String hint_population;
	public String hint_gdp;
	public String hint_currency;
	public String hint_languages;

	public String hint_written_1;

	public boolean played;
	public long best_score;
	public long best_time;
	public String guid;

	@Override
	public int hashCode()
	{

		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{

		if (this == obj)return true;
		if (obj == null)return false;
		if (getClass() != obj.getClass())return false;

		RowLocationData other = (RowLocationData) obj;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		return true;
	}
}
