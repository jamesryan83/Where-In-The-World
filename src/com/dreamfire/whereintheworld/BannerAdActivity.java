package com.dreamfire.whereintheworld;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


public class BannerAdActivity
{
	public static InterstitialAd interstitialAd;

	public static void setupAdvert(final ActivityMain activityMain)
	{
		interstitialAd = new InterstitialAd(activityMain.getApplicationContext());
		interstitialAd.setAdUnitId("ca-app-pub-1473593695248108/6159980075");

		interstitialAd.setAdListener(new AdListener()
		{
			@Override
			public void onAdLoaded()
			{
			}

			@Override
			public void onAdClosed()
			{
				activityMain.finish();
			}

			@Override
			public void onAdFailedToLoad(int errorCode)
			{
			}
		});

		interstitialAd.loadAd(new AdRequest.Builder().build());
	}

	public static void showAd()
	{
		if (interstitialAd != null && interstitialAd.isLoaded() == true) // crash fix
			interstitialAd.show();
	}
}
