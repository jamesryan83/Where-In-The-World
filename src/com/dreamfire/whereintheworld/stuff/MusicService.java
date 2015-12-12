package com.dreamfire.whereintheworld.stuff;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dreamfire.whereintheworld.R;

public class MusicService extends Service implements OnErrorListener, OnCompletionListener
{
    private final IBinder binder = new ServiceBinder();
    MediaPlayer player;

    private int lastPlayedTrack;
    private int nextTrack;
    private int[] musicFiles;

    public MusicService()
    {
    	musicFiles = new int[8];
    	musicFiles[0] = R.raw.elevated;
    	musicFiles[1] = R.raw.cappuccino;
    	musicFiles[2] = R.raw.cruisin_la_barrio;
    	musicFiles[3] = R.raw.cuban_spirit;
    	musicFiles[4] = R.raw.dining_out_tonight;
    	musicFiles[5] = R.raw.last_dance;
    	musicFiles[6] = R.raw.shooting_the_breeze;
    	musicFiles[7] = R.raw.come_dine_with_me;
    }


    public class ServiceBinder extends Binder
    {
     	 public MusicService getService()
    	 {
    		return MusicService.this;
    	 }
    }


    @Override
    public IBinder onBind(Intent arg0)
    {
    	return binder;
    }


    @Override
    public void onCreate()
    {
	    super.onCreate();

	    player = MediaPlayer.create(this, musicFiles[0]);
	    player.setOnErrorListener(this);
	    player.setOnCompletionListener(this);

	    if (player!= null)
		{
			player.setLooping(false);
			player.setVolume(100,100);
		}
	}


    @Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
        //player.start();
    	startNewTrack(CommonStuff.currentTrack);
        return START_STICKY;
	}


    @Override
	public void onCompletion(MediaPlayer mp)
	{
    	nextTrack(true);
    }


    public void nextTrack(boolean random)
    {
    	CommonStuff.trackPosition = 0;

    	if (random == true)
    	{
    		Random randomNumber = new Random();

    		int count = 0;
    		while (count < 20)
    		{
    			nextTrack = randomNumber.nextInt(musicFiles.length - 1);
    			if (nextTrack != lastPlayedTrack)
    				break;
    			count++;
    		}
    	}
    	else
    	{
    		nextTrack = (nextTrack + 1) == musicFiles.length ? 0 : nextTrack + 1;
    	}

    	startNewTrack(nextTrack);

    	lastPlayedTrack = nextTrack;
    }

    public void previousTrack()
    {
    	nextTrack = (nextTrack - 1) < 0 ? musicFiles.length - 1 : --nextTrack;

    	startNewTrack(nextTrack);

    	lastPlayedTrack = nextTrack;
    }


    private void startNewTrack(int trackNumber)
    {
    	try
		{
    		CommonStuff.currentTrack = trackNumber;
			AssetFileDescriptor afd = getResources().openRawResourceFd(musicFiles[CommonStuff.currentTrack]);
			player.reset();
			player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			player.prepare();
			player.seekTo(CommonStuff.trackPosition);
			player.start();
		}
		catch (Exception e)
		{
			try
			{
				AssetFileDescriptor afd = getResources().openRawResourceFd(musicFiles[0]);
				player.reset();
				player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				afd.close();
				player.prepare();
				player.start();
			}
			catch (Exception e2)
			{
				Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
				player.stop();
				player.release();
				player = null;
				Log.d("exception", e2.getMessage() + "\n" + e2.getStackTrace());
			}
		}
    }


	/*public void pauseMusic()
	{
		if (player != null && player.isPlaying())
		{
			player.pause();
			length=player.getCurrentPosition();

		}
	}*/


	/*public void resumeMusic()
	{
		if (player != null && player.isPlaying()==false)
		{
			player.seekTo(length);
			player.start();
		}
	}*/


	public void stopMusic()
	{
		if (player != null)
		{
			try
			{
				CommonStuff.trackPosition = player.getCurrentPosition();
				player.stop();
				player.release();
			} finally
			{
				player = null;
			}
		}
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (player != null)
		{
			try
			{
				player.stop();
				player.release();
			} finally
			{
				player = null;
			}
		}
	}


	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		Toast.makeText(this, "Music Player Failed", Toast.LENGTH_SHORT).show();

		if (player != null)
		{
			try
			{
				player.stop();
				player.release();
			} finally
			{
				player = null;
			}
		}
		return false;
	}



}