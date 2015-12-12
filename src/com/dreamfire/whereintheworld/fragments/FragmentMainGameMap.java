package com.dreamfire.whereintheworld.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamfire.whereintheworld.ActivityMain;
import com.dreamfire.whereintheworld.R;
import com.dreamfire.whereintheworld.constants.DialogConstants;
import com.dreamfire.whereintheworld.constants.FragmentConstants;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.constants.HintNameConstants;
import com.dreamfire.whereintheworld.constants.MainGameUiStateConstants;
import com.dreamfire.whereintheworld.constants.MapStateConstants;
import com.dreamfire.whereintheworld.database.RowLocationData;
import com.dreamfire.whereintheworld.dialogs.DialogMsgBoxGeneral;
import com.dreamfire.whereintheworld.stuff.CommonStuff;
import com.dreamfire.whereintheworld.stuff.MapCalculationsClass;
import com.dreamfire.whereintheworld.stuff.MarkerGenerator;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

// This is the map inside FragmentMainGame
public class FragmentMainGameMap extends SupportMapFragment implements IFragment, OnMapClickListener, OnMarkerClickListener, OnMapReadyCallback
{
	private final String MARKER_GUESS = "markerGuess";
	private final String MARKER_ANSWER = "markerAnswer";
	private final String MARKER_OPPOSITE_SIDE_EARTH = "markerOppositeSideEarth";
	private final String MARKER_ARROW = "markerArrow";
	private final String MARKER_RANDOM = "markerRandom";
	private final String MARKER_SECOND_GUESS = "markerSecondGuess";
	private final String MARKER_HOT_COLD_TIMER = "markerHotColdTimer";
	private final String MARKER_CAMERA_ZOOM = "markerCameraZoom";

	private final int TIMER_PERIOD_FOR_POLYLINE = 20; // in milliseconds
	private final int POLYLINE_SEGMENTS = 100;
	private int polylineSegmentsCount = 0;
	public boolean guessMarkerExists;
	private double distanceBetweenGuessAndAnswer;

	private ActivityMain mainActivity;
	private FragmentMainGame activityGame;

	private GoogleMap mapView;
	private MapCalculationsClass mapCalculationsClass;

	private ArrayList<LatLng> polylinePointsList;
	private LatLng[] polylinePoints;
	private Polyline polylineGuessToAnswer;
	private ArrayList<Polyline> polylineCircleAroundEarth;

	private Timer timer;
	private CountDownTimer countDownTimer;

	public int mapState;

	private Multimap<String, Marker> markerMap;
	private ArrayList<GroundOverlay> groundOverlayList;


	// For hints
	private final int CAMERA_ZOOM_HINT_ZOOM_LEVEL = 10;
	private int hintCounterArrows;
	private int hintCounterSecondGuessesRemaining;
	private boolean hintsVisible = false;
	public boolean needToShowHideHintsButton = false;
	private LatLng cameraZoomOriginalLatLng;
	private float cameraZoomOriginalZoom;




	/* ==============================================================================================================================================================
	/																	Startup
	/ =============================================================================================================================================================*/

	@Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2)
	{
        View v = super.onCreateView(arg0, arg1, arg2);

		mainActivity = (ActivityMain) getActivity();

		CommonStuff.gameState.hotColdHintIsRunning = false;
		activityGame = (FragmentMainGame) ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME);
		guessMarkerExists = false;

		mapState = MapStateConstants.NORMAL;

		markerMap = ArrayListMultimap.create();
		groundOverlayList = new ArrayList<GroundOverlay>();
		polylineCircleAroundEarth = new ArrayList<Polyline>();

		mapCalculationsClass = new MapCalculationsClass();

		getMapAsync(this);  // crash fix
		/*mapView = this.getMap(); mapView.setOnMapClickListener(this);
		mapView.setOnMarkerClickListener(this);

		setMapViewSettings(true, true);
		mapView.getUiSettings().setZoomControlsEnabled(false);
		mapView.getUiSettings().setCompassEnabled(false);
		mapView.getUiSettings().setRotateGesturesEnabled(false);
		mapView.getUiSettings().setTiltGesturesEnabled(false);
		mapView.getUiSettings().setMyLocationButtonEnabled(false);

		// Add answer marker to map and set it to invisible
		addMarkerToMap(MARKER_ANSWER, new MarkerOptions()
			.position(new LatLng(CommonStuff.gameState.currentLocationRow.latitude, CommonStuff.gameState.currentLocationRow.longitude))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.image_map_marker_answer)));

		getMarkerSingle(MARKER_ANSWER).setVisible(false);*/

		return v;
	}




	// onHiddenChanged
	@Override
	public void onHiddenChanged(boolean hidden)
	{

		super.onHiddenChanged(hidden);

		if (hidden == false)
			activityGame = (FragmentMainGame) ActivityMain.fragmentList.get(FragmentConstants.FRAGMENT_MAIN_GAME);
		else
		{
			if (countDownTimer != null)
				countDownTimer.cancel();
		}
	}


	/* ==============================================================================================================================================================
	/																	Polyline Stuff
	/ =============================================================================================================================================================*/

	// Adds the polyline to the map
	public void drawPolyline()
	{

		distanceBetweenGuessAndAnswer = getDistanceBetweenGuessAndAnswer();

		polylinePoints = mapCalculationsClass.getInterpolatedPoints(getMarkerLatLng(MARKER_GUESS), getMarkerLatLng(MARKER_ANSWER), POLYLINE_SEGMENTS);
		polylineGuessToAnswer = mapView.addPolyline(new PolylineOptions().width(5).color(Color.argb(150, 0, 177, 222)));

		mapCalculationsClass.setCameraSpeedZoom(distanceBetweenGuessAndAnswer);

		// Guess answer marker camera animations
		polylinePointsList = new ArrayList<LatLng>();
		polylinePointsList.add(polylinePoints[0]);
		polylineSegmentsCount = 1;


		// Polyline in a timer - Part 1
		mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(getMarkerLatLng(MARKER_GUESS), mapCalculationsClass.startZoom), 1000, new CancelableCallback()
		{
			@Override
			public void onFinish()
			{
				timer = new Timer();
				timer.schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						if (polylineSegmentsCount >= POLYLINE_SEGMENTS) timer.cancel();
						activityGame.runOnGameUiThread(timerRunnable);
					}
				}, 0, TIMER_PERIOD_FOR_POLYLINE);

				// Part 2
				int cameraDuration = (int) (POLYLINE_SEGMENTS * TIMER_PERIOD_FOR_POLYLINE * mapCalculationsClass.speedFactor);
				mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(getMarkerLatLng(MARKER_ANSWER), mapCalculationsClass.finishZoom), cameraDuration, new CancelableCallback()
				{
					// Part 3
					@Override public void onFinish()
					{
						activityGame.startTextViewAnswerAnimation();
					}
					@Override public void onCancel(){}
				});
			}

			@Override
			public void onCancel(){}
		});
	}


	// Runnable for polyline timer - extends the polyline
	private Runnable timerRunnable = new Runnable()
	{
		@Override
		public void run()
		{

			if (polylineSegmentsCount <= POLYLINE_SEGMENTS)
			{
				double distanceCalc = polylineSegmentsCount * (distanceBetweenGuessAndAnswer / POLYLINE_SEGMENTS);
				activityGame.updateTextViewAnswerText(distanceCalc);
				polylinePointsList.add(polylinePoints[polylineSegmentsCount]);
				polylineGuessToAnswer.setPoints(polylinePointsList);
				polylineSegmentsCount++;
			}

			if (polylineSegmentsCount == POLYLINE_SEGMENTS)
				addAnswerMarker();
		}
	};


	/*==============================================================================================================================================================
	/																	Guess and Answer Marker stuff
	/ =============================================================================================================================================================*/

	// Zooms to the marker - moves to guessMarker before guess, answerMarker after guess unless at answerMarker in which case guessMarker, if that makes sense
	public void zoomToGuessMarker()
	{

		if (guessMarkerExists == true)
		{

			LatLng moveToLatLng;

			if (activityGame.guessHasBeenAccepted == true)
			{
				double answerLat = CommonStuff.round(getMarkerLatLng(MARKER_ANSWER).latitude, 6);
				double answerLng = CommonStuff.round(getMarkerLatLng(MARKER_ANSWER).longitude, 6);
				double cameraLat = CommonStuff.round(mapView.getCameraPosition().target.latitude, 6);
				double cameraLng = CommonStuff.round(mapView.getCameraPosition().target.longitude, 6);

				if (cameraLat == answerLat && cameraLng == answerLng)
					moveToLatLng = getMarkerLatLng(MARKER_GUESS);
				else
					moveToLatLng = getMarkerLatLng(MARKER_ANSWER);
			}
			else
				moveToLatLng = getMarkerLatLng(MARKER_GUESS);

			CameraUpdate camera = CameraUpdateFactory.newLatLng(moveToLatLng);
			mapView.animateCamera(camera);
		}
	}

	// Adds the guess marker to the map
	private void addGuessMarkerToMap(float x, float y)
	{

		Projection projection = mapView.getProjection();
		Point point = new Point((int) x, (int) y - 20);
		LatLng pos = projection.fromScreenLocation(point);

		addGuessMarkerToMap(pos);
	}

	// Adds a marker to the map
	private void addGuessMarkerToMap(LatLng position)
	{

		if (guessMarkerExists == false) // allow only 1 marker
		{
			addMarkerToMap(MARKER_GUESS, new MarkerOptions().position(position).anchor(0.27f, 1f)
					.icon(BitmapDescriptorFactory.fromBitmap(new MarkerGenerator(mainActivity.getApplicationContext())
					.makeGuessMarker(activityGame.getResources().getString(R.string.string_fragmentmap_guessmarker)))));

			activityGame.setUiControlStates(MainGameUiStateConstants.GUESS_MARKER_ADDED);
		}
		else
			activityGame.setUiControlStates(MainGameUiStateConstants.GUESS_MARKER_REMOVED);
	}

	// Adds the answer marker to the map
	private void addAnswerMarker()
	{

		setMapViewSettings(false, false);
		getMarkerSingle(MARKER_ANSWER).setVisible(true);
	}


	// Removes the guessMarker from the map
	public void removeGuessMarker()
	{

		guessMarkerExists = false;
		removeMarkerFromMap(MARKER_GUESS);
	}


	// Returns the distance between the guess and answer markers
	public double getDistanceBetweenGuessAndAnswer()
	{

		distanceBetweenGuessAndAnswer = mapCalculationsClass.getDistance(getMarkerSingle(MARKER_GUESS), getMarkerSingle(MARKER_ANSWER));
		return distanceBetweenGuessAndAnswer;
	}


	/* ==============================================================================================================================================================
	/																	Map Stuff
	/ =============================================================================================================================================================*/

	// Zoom in on map
	public void zoomIn()
	{

		CameraUpdate update = CameraUpdateFactory.zoomIn() ;
		mapView.moveCamera(update);
	}

	// Zoom out on map
	public void zoomOut()
	{

		/*if (mapView.getCameraPosition().zoom == 2)
		{

		}
		else*/
			mapView.moveCamera(CameraUpdateFactory.zoomOut());
	}

	// Sets touchZoom, and scroll controls on map
	public void setMapViewSettings(boolean touchZoomEnabled, boolean scrollEnabled)
	{

		mapView.getUiSettings().setZoomGesturesEnabled(touchZoomEnabled);
		mapView.getUiSettings().setScrollGesturesEnabled(scrollEnabled);
	}

	// Draws a polyline on the map between 2 points
	private void drawStraightLineBetweenPoints(LatLng start, LatLng finish, int color)
	{

		ArrayList<LatLng> points = new ArrayList<LatLng>();
		points.add(start);
		points.add(finish);
		polylineCircleAroundEarth.add(mapView.addPolyline(new PolylineOptions().width(5).color(color)));
		polylineCircleAroundEarth.get(polylineCircleAroundEarth.size() - 1) .setPoints(points);
	}


	// Hide the hints on the map
	public void hideHints()
	{

		for (Entry<String, Marker> e : markerMap.entries())
		{
			if (e.getKey().equals(MARKER_ANSWER) == false && e.getKey().equals(MARKER_GUESS) == false)
				e.getValue().setVisible(hintsVisible);
		}

		for (GroundOverlay g : groundOverlayList)
			g.setVisible(hintsVisible);

		for (Polyline p : polylineCircleAroundEarth)
			p.setVisible(hintsVisible);

		if (hintsVisible == true) hintsVisible = false; else hintsVisible = true;
	}


	// ****************** Marker Methods *********************

	// Get a single marker from MarkerMap
	private Marker getMarkerSingle(String markerName)
	{

		return markerMap.get(markerName).iterator().next();
	}

	// Add a marker to MapView and MarkerMap
	private void addMarkerToMap(String markerName, MarkerOptions options)
	{

		markerMap.put(markerName, mapView.addMarker(options));
	}

	// Remove a marker from the MapView and the MarkerMap
	private void removeMarkerFromMap(String markerName)
	{

		Collection<Marker> markers = markerMap.get(markerName);

		for (Marker m : markers)
			m.remove();

		markerMap.removeAll(markerName);
	}

	// Returns the LatLng of a marker
	private LatLng getMarkerLatLng(String markerName)
	{

		return getMarkerSingle(markerName).getPosition();
	}

	// Returns the name of the Marker
	private String getMarkerName(Marker marker)
	{

		for (Map.Entry<String, Marker> m : markerMap.entries())
			if (m.getValue().equals(marker))
				return m.getKey();

		return "";
	}


	/* ==============================================================================================================================================================
	/																	Hints
	/ =============================================================================================================================================================*/

	// Arrows
	private void addArrowStart(int numArrows)
	{

		hintCounterArrows = numArrows;
		mapState = MapStateConstants.HINT_ARROW_ENABLED;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);
		activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_STARTED);
		activityGame.updateHintText(Html.fromHtml(activityGame.getResources().getString(R.string.string_fragmentmap_arrowsremaining) + hintCounterArrows));
	}

	// Add an arrow to the map
	private void addArrowMarker(float x, float y)
	{

		needToShowHideHintsButton = true;
		activityGame.buttonHideHints.setVisibility(View.VISIBLE);

		Projection projection = mapView.getProjection();
		Point point = new Point((int) x, (int) y - 30);
		LatLng pos = projection.fromScreenLocation(point);

		float angle = (float) mapCalculationsClass.getAngleBetweenPoints(pos, getMarkerLatLng(MARKER_ANSWER), mapView);

		addMarkerToMap(MARKER_ARROW, new MarkerOptions().position(pos).anchor(0.5f, 0.5f).icon(
				BitmapDescriptorFactory.fromBitmap(new MarkerGenerator(mainActivity.getApplicationContext()).makeMarker(angle))));

		hintCounterArrows--;
		activityGame.updateHintText(Html.fromHtml(activityGame.getResources().getString(R.string.string_fragmentmap_arrowsremaining) + hintCounterArrows));
		if (hintCounterArrows == 0)
		{
			mapState = MapStateConstants.NORMAL;
			activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_FINISHED);
			activityGame.setHintTextViewVisible(false);
		}
	}

	private final double POSITIVE_LATITUDE_LIMIT = 79;
	private final double NEGATIVE_LATITUDE_LIMIT = -53;
	private final double CIRCLE_HINT_ADJUSTMENT_FACTOR = 0.4;

	// Circle
	private void drawCircle(String size)
	{

		LatLng position = getMarkerLatLng(MARKER_ANSWER);

		needToShowHideHintsButton = true;
		mapState = MapStateConstants.NORMAL;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);

		int radius = 0;
		if (size.equals("big"))
			radius = 2000000;
		else if (size.equals("medium"))
			radius = 1000000;
		else
			radius = 500000;

		Random random = new Random();

		double randomAngle = random.nextInt(90);

		double latitudeLimit = position.latitude >= 0 ? POSITIVE_LATITUDE_LIMIT : NEGATIVE_LATITUDE_LIMIT;

		double pullInX = (position.latitude / latitudeLimit) * CIRCLE_HINT_ADJUSTMENT_FACTOR;
		double pullDownY = (position.latitude / latitudeLimit) * CIRCLE_HINT_ADJUSTMENT_FACTOR;

		double lengthX = random.nextDouble() - pullInX;
		double lengthY = random.nextDouble() - pullDownY;

		double signX = (random.nextBoolean() == true ? -1 : 1);
		double signY = (random.nextBoolean() == true ? -1 : 1);

		double randomX = lengthX * Math.cos(Math.toRadians(randomAngle)) * signX;
		double randomY = lengthY * Math.sin(Math.toRadians(randomAngle)) * signY;

		randomX = mapCalculationsClass.getLinearInterpolation(-1, randomX, 1, 1, 0);
		randomY = mapCalculationsClass.getLinearInterpolation(-1, randomY, 1, 1, 0);

		GroundOverlayOptions options = new GroundOverlayOptions();
		options.anchor((float) randomX, (float) randomY);
		options.image(BitmapDescriptorFactory.fromResource(R.drawable.image_map_hint_circle));
		options.position(position, radius * 2, radius * 2);
		options.transparency(0.8f);

		groundOverlayList.add(mapView.addGroundOverlay(options));

		mapView.animateCamera(CameraUpdateFactory.newLatLng(position));
	}

	/*0,0
	 *-----+-----+-----+-----*
	 |     |     |     |     |
	 |     |     |     |     |
	 +-----+-----+-----+-----+
	 |     |     |   X |     |   (U, V) = (0.7, 0.6)
	 |     |     |     |     |
	 *-----+-----+-----+-----* 1,1*/



	// Opposite side of earth
	private void oppositeSideOfEarth()
	{

		needToShowHideHintsButton = true;
		mapState = MapStateConstants.NORMAL;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);

		addMarkerToMap(MARKER_OPPOSITE_SIDE_EARTH, new MarkerOptions()
			.position(mapCalculationsClass.getOppositeSideOfEarth(getMarkerLatLng(MARKER_ANSWER)))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.image_map_marker_opposite_side)));

		mapView.animateCamera(CameraUpdateFactory.newLatLng(getMarkerSingle(MARKER_OPPOSITE_SIDE_EARTH).getPosition()));
	}


	// Circle around earth
	private void circleAroundEarth()
	{

		needToShowHideHintsButton = true;
		mapState = MapStateConstants.NORMAL;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);

		LatLng[] points = mapCalculationsClass.get4pointsAroundEarth(getMarkerLatLng(MARKER_ANSWER));

		drawStraightLineBetweenPoints(points[0], points[1], Color.MAGENTA);
		drawStraightLineBetweenPoints(points[1], points[2], Color.MAGENTA);
		drawStraightLineBetweenPoints(points[2], points[3], Color.MAGENTA);
		drawStraightLineBetweenPoints(points[3], points[0], Color.MAGENTA);

		mapView.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(points[0].latitude, mapView.getCameraPosition().target.longitude)));
	}


	// Random Markers where 1 is the answer
	private void addRandomMarkersToMap(int numMarkers)
	{

		mapState = MapStateConstants.HINT_MARKERS_ENABLED;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);
		activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_STARTED);

		ArrayList<RowLocationData> locations = CommonStuff.databaseLocations.getRandomLocationsFromAllTables(numMarkers, true);

		for (int i = 0; i < locations.size(); i++)
		{
			addMarkerToMap(MARKER_RANDOM, new MarkerOptions()
				.position(new LatLng(locations.get(i).latitude, locations.get(i).longitude))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.image_map_marker_markers_hint)));
		}

		Random random = new Random();
		int point = random.nextInt(numMarkers);
		mapView.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(locations.get(point).latitude, locations.get(point).longitude)));
	}
	// Removes random markers added in method above
	private void removeRandomMarkersFromMap(Marker marker)
	{

		addGuessMarkerToMap(marker.getPosition());
		removeMarkerFromMap(MARKER_RANDOM);
		mapState = MapStateConstants.NORMAL;
		activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_FINISHED);
	}


	// Second Guess
	private void secondGuess(int numGuesses)
	{

		mapState = MapStateConstants.HINT_SECOND_GUESS_ENABLED;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);
		activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_STARTED);

		hintCounterSecondGuessesRemaining = numGuesses;
		activityGame.updateHintText(Html.fromHtml(activityGame.getResources().getString(R.string.string_fragmentmap_guessesremaining) + hintCounterSecondGuessesRemaining));
	}
	// Add second guess marker
	private void addSecondGuessMarker(float x, float y)
	{

		if (hintCounterSecondGuessesRemaining > 0)
		{
			needToShowHideHintsButton = true;
			activityGame.buttonHideHints.setVisibility(View.VISIBLE);

			Projection projection = mapView.getProjection();
			Point point = new Point((int) x, (int) y - 30);
			LatLng pos = projection.fromScreenLocation(point);

			hintCounterSecondGuessesRemaining--;

			double distance = mapCalculationsClass.getDistance(pos, getMarkerLatLng(MARKER_ANSWER));

			if (CommonStuff.gameState.kilometresEnabled == false) distance *= GeneralConstants.KM_TO_MILES;

			String unit = CommonStuff.gameState.kilometresEnabled == true ? "km" : "mi";
			addMarkerToMap(MARKER_SECOND_GUESS, new MarkerOptions().position(pos).anchor(0.27f, 1f)
					.icon(BitmapDescriptorFactory.fromBitmap(new MarkerGenerator(mainActivity.getApplicationContext())
					.makeSecondGuessMarker(String.format("%.2f" + unit, distance)))));

			activityGame.updateHintText(Html.fromHtml(activityGame.getResources().getString(R.string.string_fragmentmap_guessesremaining) + hintCounterSecondGuessesRemaining));

			if (hintCounterSecondGuessesRemaining == 0)
			{
				mapState = MapStateConstants.NORMAL;
				activityGame.setHintTextViewVisible(false);
				activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_FINISHED);
			}
		}
	}


	// Hot Cold Timer
	private void hotColdTimer()
	{

		needToShowHideHintsButton = true;
		setMapViewSettings(false, true);
		mapState = MapStateConstants.HINT_HOT_COLD_TIMER_ENABLED;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);
		activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_STARTED);
	}
	// Start timer
	private void hotColdTimerStartTimer()
	{

		long time = 0;

		if (CommonStuff.gameState.currentSelectedHint.id == HintNameConstants.HOT_COLD_TIMER_10)
			time = 10000;
		else if (CommonStuff.gameState.currentSelectedHint.id == HintNameConstants.HOT_COLD_TIMER_20)
			time = 20000;
		else
			time = 30000;



		countDownTimer = new CountDownTimer(time, 1000)
		{
			@Override
			public void onTick(long millisUntilFinished)
			{
				activityGame.updateHintText(Html.fromHtml(activityGame.getResources().getString(R.string.string_fragmentmap_timeleft) +
						String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished))));
			}

			@Override
			public void onFinish()
			{

				activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_FINISHED);
				activityGame.setHintTextViewVisible(false);
				setMapViewSettings(true, true);
				mapState = MapStateConstants.NORMAL;
			}

		}.start();
	}
	// Add a hot/cold timer marker
	public void addHotColdTimerMarker(float x, float y)
	{

		if (activityGame.animationIsRunning == true || CommonStuff.gameState.hotColdHintIsRunning == false)
			return;

		Projection projection = mapView.getProjection();
		Point point = new Point((int) x, (int) y - 20);
		LatLng pos = projection.fromScreenLocation(point);

		double distance = mapCalculationsClass.getDistance(pos, getMarkerLatLng(MARKER_ANSWER));
		Bitmap markerBitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(markerBitmap);
		Drawable shape = getResources().getDrawable(R.drawable.hint_circle_hot_cold_timer);
		shape.setBounds(0, 0, markerBitmap.getWidth(), markerBitmap.getHeight());
		shape.setColorFilter(mapCalculationsClass.getHueForTimerHint(distance), Mode.MULTIPLY);
		shape.draw(canvas);

		addMarkerToMap(MARKER_HOT_COLD_TIMER, new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));
	}




	// Camera Zoom
	private void addCameraZoomHint()
	{

		mapState = MapStateConstants.HINT_CAMERA_ZOOM_ENABLED;
		activityGame.setUiControlStates(MainGameUiStateConstants.SWITCH_TO_MAP);
		activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_STARTED);

		cameraZoomOriginalLatLng = mapView.getCameraPosition().target;
		cameraZoomOriginalZoom = mapView.getCameraPosition().zoom;

		mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(getMarkerLatLng(MARKER_ANSWER), CAMERA_ZOOM_HINT_ZOOM_LEVEL));

		addMarkerToMap(MARKER_CAMERA_ZOOM, new MarkerOptions().position(getMarkerLatLng(MARKER_ANSWER)).anchor(0.5f, 0.5f).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.image_map_marker_camera_zoom)));
		setMapViewSettings(false, false);
	}
	// Remove the cameraZoom hint and return to normal
	public void removeCameraZoomHint()
	{

		mapState = MapStateConstants.NORMAL;
		removeMarkerFromMap(MARKER_CAMERA_ZOOM);
		mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraZoomOriginalLatLng, cameraZoomOriginalZoom));
		activityGame.setUiControlStates(MainGameUiStateConstants.MAP_HINT_FINISHED);
		setMapViewSettings(true, true);
	}




	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	// Called from dispatchTouchEvent in ActivityMain
	private void onDispatchTouchEvent(float x, float y)
	{

		if (activityGame.guessHasBeenAccepted == false)
		{
			if (CommonStuff.gameState.interactiveHintIsRunning == true)
			{
				// Add various hint markers to map
				switch (CommonStuff.gameState.currentSelectedHint.id)
				{
					case HintNameConstants.ARROW_2 :
					case HintNameConstants.ARROW_4 :
					case HintNameConstants.ARROW_6 :
						addArrowMarker(x, y);
						break;
					case HintNameConstants.SECOND_GUESS_1 :
					case HintNameConstants.SECOND_GUESS_2 :
					case HintNameConstants.SECOND_GUESS_3 :
						addSecondGuessMarker(x, y);
						break;
				}
			}
			else
			{
				// Add guess marker to map
				if (activityGame.isOnMapView == true)
				{
					if (guessMarkerExists == true)
						activityGame.setUiControlStates(MainGameUiStateConstants.GUESS_MARKER_REMOVED);
					else
						addGuessMarkerToMap(x, y);
				}
			}
		}
	}


	// Map Click
	@Override
	public void onMapClick(LatLng position)
	{

		if (activityGame.animationIsRunning == true)
			return;

		if (mapState == MapStateConstants.HINT_CAMERA_ZOOM_ENABLED)
			return;

		if (mapState == MapStateConstants.HINT_MARKERS_ENABLED)
		{
			DialogMsgBoxGeneral.newInstance("", activityGame.getResources()
					.getString(R.string.string_fragmentmap_selectmarker), false, false, DialogConstants.NO_CALLBACK)
					.show(getFragmentManager(), GeneralConstants.DIALOG_MSGBOX_TAG);
			return;
		}

		if (CommonStuff.gameState.canAddMapMarker == true)
		{
			onDispatchTouchEvent(CommonStuff.gameState.mapMarkerPoint.x, CommonStuff.gameState.mapMarkerPoint.y);
			return;
		}
	}


	// Marker Click
	@Override
	public boolean onMarkerClick(Marker marker)
	{

		if (mapState == MapStateConstants.HINT_CAMERA_ZOOM_ENABLED)
			return true;

		if (mapState == MapStateConstants.HINT_MARKERS_ENABLED)
		{
			if (getMarkerName(marker).equals(MARKER_RANDOM))
				removeRandomMarkersFromMap(marker);
			return true;
		}

		if (CommonStuff.gameState.canAddMapMarker == true)
		{
			onDispatchTouchEvent(CommonStuff.gameState.mapMarkerPoint.x, CommonStuff.gameState.mapMarkerPoint.y);
			return true;
		}

		return true; // return true stop zooming to marker location
	}


	/* ==============================================================================================================================================================
	/																	Map State
	/ =============================================================================================================================================================*/

	// Set the state of the map for the map click listener
	public void setCurrentMapState()
	{

		if (CommonStuff.gameState.currentSelectedHint == null) return;

		CommonStuff.setHintUsed(CommonStuff.gameState.currentSelectedHint.id);

		CommonStuff.gameState.hintUsed = true;

		switch (CommonStuff.gameState.currentSelectedHint.id)
		{
			case HintNameConstants.CONTINENT :
			case HintNameConstants.AREA :
			case HintNameConstants.POPULATION :
			case HintNameConstants.GDP :
			case HintNameConstants.CURRENCY :
			case HintNameConstants.LANGUAGES :
			case HintNameConstants.CAPITAL_CITY :
			case HintNameConstants.COUNTRY :
			case HintNameConstants.CITY :
			case HintNameConstants.WRITTEN_HINT_1 :
			case HintNameConstants.FLAG :
			case HintNameConstants.MISSING_LETTERS_25_PCT :
			case HintNameConstants.MISSING_LETTERS_50_PCT :
			case HintNameConstants.MISSING_LETTERS_75_PCT :
			case HintNameConstants.ANAGRAM : mapState = MapStateConstants.NORMAL; break;

			case HintNameConstants.CIRCLE_SMALL :  drawCircle("small");  break;
			case HintNameConstants.CIRCLE_MEDIUM : drawCircle("medium"); break;
			case HintNameConstants.CIRCLE_BIG :    drawCircle("big");    break;

			case HintNameConstants.ARROW_2 : addArrowStart(2); break;
			case HintNameConstants.ARROW_4 : addArrowStart(4); break;
			case HintNameConstants.ARROW_6 : addArrowStart(6); break;

			case HintNameConstants.CIRCLE_AROUND_EARTH : circleAroundEarth(); break;

			case HintNameConstants.OPPOSITE_SIDE_EARTH : oppositeSideOfEarth(); break;

			case HintNameConstants.MARKERS_2 : addRandomMarkersToMap(2); break;
			case HintNameConstants.MARKERS_4 : addRandomMarkersToMap(4); break;
			case HintNameConstants.MARKERS_6 : addRandomMarkersToMap(6); break;

			case HintNameConstants.SECOND_GUESS_1 : secondGuess(1); break;
			case HintNameConstants.SECOND_GUESS_2 : secondGuess(2); break;
			case HintNameConstants.SECOND_GUESS_3 : secondGuess(3); break;

			case HintNameConstants.HOT_COLD_TIMER_10 :
			case HintNameConstants.HOT_COLD_TIMER_20 :
			case HintNameConstants.HOT_COLD_TIMER_30 :
				hotColdTimer();
				break;

			case HintNameConstants.CAMERA_ZOOM : addCameraZoomHint(); break;

			default :
				mapState = MapStateConstants.NORMAL;
				break;
		}
	}

	// after the Ready, Go animation
	public void afterReadyGo()
	{

		int name = CommonStuff.gameState.currentSelectedHint.id;

		switch(name)
		{
			case  HintNameConstants.ARROW_2 :
			case  HintNameConstants.ARROW_4 :
			case  HintNameConstants.ARROW_6 :
				activityGame.setHintTextViewVisible(true);
				break;

			case  HintNameConstants.HOT_COLD_TIMER_10 :
			case  HintNameConstants.HOT_COLD_TIMER_20 :
			case  HintNameConstants.HOT_COLD_TIMER_30 :
				activityGame.setHintTextViewVisible(true);
				CommonStuff.gameState.hotColdHintIsRunning = true;
				hotColdTimerStartTimer();
				break;

			case  HintNameConstants.SECOND_GUESS_1 :
			case  HintNameConstants.SECOND_GUESS_2 :
			case  HintNameConstants.SECOND_GUESS_3 :
				activityGame.setHintTextViewVisible(true);
				break;

			default :
				break;
		}
	}



	// Unused
	@Override public void updateUi() { }
	@Override public void setupUiAfterAsync() { }
	@Override public void afterOnClick() { }



	// loading map callback - crash fix
	@Override
	public void onMapReady(GoogleMap map)
	{
		this.mapView = map;

		mapView.setOnMapClickListener(this);
		mapView.setOnMarkerClickListener(this);

		setMapViewSettings(true, true);
		mapView.getUiSettings().setZoomControlsEnabled(false);
		mapView.getUiSettings().setCompassEnabled(false);
		mapView.getUiSettings().setRotateGesturesEnabled(false);
		mapView.getUiSettings().setTiltGesturesEnabled(false);
		mapView.getUiSettings().setMyLocationButtonEnabled(false);

		// Add answer marker to map and set it to invisible
		addMarkerToMap(MARKER_ANSWER, new MarkerOptions()
			.position(new LatLng(CommonStuff.gameState.currentLocationRow.latitude, CommonStuff.gameState.currentLocationRow.longitude))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.image_map_marker_answer)));

		getMarkerSingle(MARKER_ANSWER).setVisible(false);

	}

}