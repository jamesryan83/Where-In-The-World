package com.dreamfire.whereintheworld.stuff;

import android.graphics.Color;
import android.graphics.Point;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;



public class MapCalculationsClass
{
	private final double RADIUS_OF_EARTH = 6372.797; //km


	// camera factors
	public int startZoom = 3;
	public int finishZoom = 6;
	public double speedFactor = 1.5;


	/* ==============================================================================================================================================================
	/																	Map Calculations for Hints
	/ =============================================================================================================================================================*/


	// Returns a LatLng on the opposite side of the earth
	public LatLng getOppositeSideOfEarth(LatLng point)
	{

		return new LatLng(-point.latitude, point.longitude >= 0 ? point.longitude + 180 : point.longitude - 180);
	}


	// Returns angle between 2 LatLongs
	public double getAngleBetweenPoints(LatLng start, LatLng finish, GoogleMap map)
	{

		Projection projection = map.getProjection();

		Point point1 = projection.toScreenLocation(start);
		Point point2 = projection.toScreenLocation(finish);

		double angle = Math.toDegrees(Math.atan2((point2.y - point1.y), (point2.x - point1.x)));

		return angle;
	}


	// Returns 4 points around the earth for a given latitude
	public LatLng[] get4pointsAroundEarth(LatLng start)
	{

		LatLng[] points = new LatLng[4];

		double longitude = start.longitude;
		for (int i = 0; i < points.length; i++)
		{
			longitude += 90;

			if (longitude > 180) longitude -= 360;

			points[i] = new LatLng(start.latitude, longitude);
		}

		return points;
	}

	// Get linearly interpolated value
	public double getLinearInterpolation(double x0, double x, double x1, double y0, double y1)
	{

		return y0 + ((y1 - y0) * ((x - x0) / (x1 - x0)));
	}


	/* ==============================================================================================================================================================
	/																	Other Map Calculations
	/ =============================================================================================================================================================*/


	// http://www.movable-type.co.uk/scripts/latlong.html
	// http://snipplr.com/view/2531/calculate-the-distance-between-two-coordinates-latitude-longitude/
	// Convert two lat/longs into a distance in km/miles
	public double getDistance(Marker markerStart, Marker markerFinish)
	{
		return getDistance(markerStart.getPosition(), markerFinish.getPosition());
	}

	// As above overloaded
	public double getDistance(LatLng start, LatLng finish)
	{

		double degreesToRadians = Math.PI / 180;

		double rLat1 = start.latitude * degreesToRadians;
		double rLong1 = start.longitude * degreesToRadians;
		double rLat2 = finish.latitude * degreesToRadians;
		double rLong2 = finish.longitude * degreesToRadians;

		double dLat = rLat2 - rLat1;
		double dLong = rLong2 - rLong1;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLong / 2) * Math.sin(dLong / 2) * Math.cos(rLat1) * Math.cos(rLat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = c * RADIUS_OF_EARTH;

		return d;
	}


	// Returns a LatLng[] of interpolated points between a start and finish point
	public LatLng[] getInterpolatedPoints(LatLng start, LatLng finish, int segments)
	{

		LatLng[] newPoints = new LatLng[segments + 1];

		double startLat = start.latitude;
		double startLong = start.longitude;
		double finishLat = finish.latitude;
		double finishLong = finish.longitude;

		double incrementLat;
		double incrementLong;
		double directionLat = 0;
		double directionLong = 0;

		double totalDifferenceLat;
		double totalDifferenceLong;
		double totalSeparationLong;

		double[] answerLat = new double[segments + 1];
		double[] answerLong = new double[segments + 1];

		totalDifferenceLat = Math.abs(startLat - finishLat);

		totalDifferenceLong = Math.abs(startLong - finishLong);
		if (totalDifferenceLong > 180) totalDifferenceLong = 360 - Math.abs(startLong - finishLong);

		totalSeparationLong = Math.max(startLong, finishLong) - Math.min(startLong, finishLong);

		incrementLat = totalDifferenceLat / segments;
		incrementLong = totalDifferenceLong / segments;


		// direction polyline should go in - Lat
		if (startLat > finishLat)
			directionLat = -1.0;
		else if (startLat <= finishLat)
			directionLat = 1.0;


		// direction polyline should go in - long
		if (startLong > finishLong)
		{
			if (totalSeparationLong > 180)
				directionLong = 1.0;
			else if (totalSeparationLong <= 180)
				directionLong = -1.0;
		}
		else if (startLong <= finishLong)
		{
			if (totalSeparationLong > 180)
				directionLong = -1.0;
			else if (totalSeparationLong <= 180)
				directionLong = 1.0;
		}


		// calculate points - lat
		for (int i = 0; i < (segments + 1); i++)
			answerLat[i] = startLat + incrementLat * directionLat * i;


		// calculate points - long
		for (int i = 0; i < (segments + 1); i++)
		{
			answerLong[i] = startLong + incrementLong * directionLong * i;

			if (answerLong[i] > 180)
				answerLong[i] -= 360;
			else if (answerLong[i] < -180)
				answerLong[i] += 360;
		}


		// Put points into LatLng[]
		for (int i = 0; i < (segments + 1); i++)
			newPoints[i] = new LatLng(answerLat[i], answerLong[i]);


		return newPoints;
	}


	private int[] hueValues = new int[] { 180, 160, 140, 120, 100, 80, 60, 40, 30, 20, 10, 0 };
	private int[] distanceValues = new int[] { 17000, 15000, 13000, 10000, 7500, 5000, 2000, 1000, 500, 100, 50, 10, 0 };

	// Returns a hue color for the timer hint
	public int getHueForTimerHint(double distance)
	{

		for (int i = 0; i < distanceValues.length - 1; i++)
		{
			if (distance <= distanceValues[i] && distance >= distanceValues[i + 1])
				return Color.HSVToColor(150, new float[] {hueValues[i], 1, 1});
		}

		return 0;
	}


	/* ==============================================================================================================================================================
	/																	Camera Factors
	/ =============================================================================================================================================================*/

	// Calculate camera movement factors
	public void setCameraSpeedZoom(double distance)
	{

		if (distance > 6000)
		{
			startZoom = 3; finishZoom = 15; speedFactor = 1.5;
		}
		else if (distance <= 6000 && distance > 3000)
		{
			startZoom = 4; finishZoom = 15; speedFactor = 1.3;
		}
		else if (distance <= 3000 && distance > 1000)
		{
			startZoom = 5; finishZoom = 15; speedFactor = 1.3;
		}
		else if (distance <= 1000 && distance > 500)
		{
			startZoom = 6; finishZoom = 15; speedFactor = 1.2;
		}
		else
		{
			startZoom = 8; finishZoom = 15; speedFactor = 1.1;
		}
	}


}
