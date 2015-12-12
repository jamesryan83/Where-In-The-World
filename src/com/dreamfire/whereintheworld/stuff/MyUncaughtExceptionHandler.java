package com.dreamfire.whereintheworld.stuff;

import java.lang.Thread.UncaughtExceptionHandler;

import com.dreamfire.whereintheworld.ActivityMain;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler
{
	public UncaughtExceptionHandler defaultUEH;

	ActivityMain mainActivity;

	public MyUncaughtExceptionHandler(ActivityMain mainActivity)
	{
		this.mainActivity = mainActivity;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex)
	{
		try
		{
			mainActivity.doUnbindService(); // stop music service
			defaultUEH.uncaughtException(thread, ex);
		} finally
		{
		    System.exit(10);
		}
	}

}
