package com.dreamfire.whereintheworld;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dreamfire.whereintheworld.constants.GeneralConstants;
import com.dreamfire.whereintheworld.stuff.CommonStuff;

public class ActivitySplash extends Activity implements OnClickListener
{
	private int SPLASH_DISPLAY_TIME = 3000;

	private View splashView;
	private ImageView imageView;
	private Handler handler;
	private SplashRunnable splashRunnable;

	// Main
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);

		imageView = (ImageView) findViewById(R.id.splash_imageview_company_logo);
		splashView = findViewById(R.id.splash_view_company_logo);
		splashView.setOnClickListener(this);

		Animation animation = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.zoom_in_bounce);

		try
		{
			Glide.with(this).load(R.drawable.image_x_company_logo).animate(animation).into(imageView);
		}
		catch (Exception e) {}

		splashRunnable = new SplashRunnable();

		handler = new Handler();
		handler.postDelayed(splashRunnable, SPLASH_DISPLAY_TIME);
	}


	/* ==============================================================================================================================================================
	/																	Splash screen thread
	/ =============================================================================================================================================================*/

	private class SplashRunnable implements Runnable
	{
		@Override
		public void run()
		{
			endSplashActivity();
		}
	}

	private void endSplashActivity()
	{

		handler.removeCallbacks(splashRunnable);
		CommonStuff.runActivity(ActivitySplash.this, ActivitySplash2.class, GeneralConstants.NO_INTENT_FLAG);
		finish();
	}


	/* ==============================================================================================================================================================
	/																	Listeners
	/ =============================================================================================================================================================*/

	@Override
	public void onClick(View v)
	{

		CommonStuff.playClickSound(this.getApplicationContext());

		if (v.getId() == R.id.splash_view_company_logo)
			endSplashActivity();
	}

	@Override
	public void onBackPressed()
	{

		endSplashActivity();
	}
}
