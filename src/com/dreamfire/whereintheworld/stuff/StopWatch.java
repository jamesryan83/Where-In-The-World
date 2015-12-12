package com.dreamfire.whereintheworld.stuff;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class StopWatch
{
	private long startTime = 0;
    private long stopTime = 0;
    private long pauseTime = 0;
    private boolean running = false;


    public void start()
    {
    	pauseTime = 0;
        startTime = System.currentTimeMillis();
        running = true;
    }

    /*public void pause()
    {
    	pauseTime += (System.currentTimeMillis() - startTime);
    	running = false;
    }

    public void unPause()
    {
    	startTime = System.currentTimeMillis();
    	running = true;
    }*/


    public void stop()
    {
        stopTime = pauseTime + System.currentTimeMillis();
        running = false;
    }


    // Elaspsed time in milliseconds
    public long getElapsedTime()
    {
        long elapsed;
        if (running == true)
        	elapsed = (System.currentTimeMillis() - startTime);
        else
            elapsed = (stopTime - startTime);

        return elapsed;
    }


    // Elaspsed time in seconds
    public long getElapsedTimeSeconds()
    {
        long elapsed;
        if (running == true)
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        else
            elapsed = ((stopTime - startTime) / 1000);

        return elapsed;
    }

    // Elapsed time in minutes
    public String getElapsedTimeMinutes()
    {
    	long milliseconds = getElapsedTime();

    	int seconds = (int) (milliseconds / 1000) % 60;
    	int minutes = (int) ((milliseconds / (1000*60)) % 60);

    	return String.format("%d:%02d", minutes, seconds);
    }
}
