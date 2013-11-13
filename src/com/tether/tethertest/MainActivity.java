package com.tether.tethertest;

import tether.Tether;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String TAG = "tethertest";
	
	private String TETHER_ADDRESS = "00:06:66:4E:3E:CE";
	
	private double X;
	private double Y;
	private double Z;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		X = 0.0;
		Y = 0.0;
		Z = 0.0;
		
		class TCallbacks implements Tether.TetherCallbacks {
			
			public void connected() {
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 tetherConnected();
				    }
				});
			}
			
			public void disconnected() {
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 tetherDisconnected();
				    }
				});
			}
			
			public void positionUpdate(double newX, double newY, double newZ) {
				X = newX;
				Y = newY;
				Z = newZ;
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 tetherPositionUpdate(X, Y, Z);
				    }
				});
			}
		}
		
		Tether.makeTether(TETHER_ADDRESS, new TCallbacks());
		Log.v(TAG, "my tether: " + Tether.getTether(TETHER_ADDRESS));
	}
	
	public void tetherConnected() {
		Log.v(TAG, "Tether connected!");
		TextView statusText = (TextView)findViewById(R.id.statusText);
		statusText.setText("Status: Connected");
	}
	public void tetherDisconnected() {
		Log.w(TAG, "Tether disconnected!");
		TextView statusText = (TextView)findViewById(R.id.statusText);
		statusText.setText("Status: Disconnected");
	}
	
	public void tetherPositionUpdate(double newX, double newY, double newZ) {

		Log.v(TAG, "New positions. X: " + X + ", Y: " + Y + ", Z: " + Z);
		TextView positionText = (TextView)findViewById(R.id.positionText);
		positionText.setText(
				"X: " + String.format("%+.2f", X) + 
				"cm, Y: " + String.format("%+.2f", Y) + 
				"cm, Z: " + String.format("%+.2f", Z) + "cm");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startTetherButtonPressed(View v) {
		Log.v(TAG, "Start tether button pressed");
		Tether.getTether(TETHER_ADDRESS).start();
		TextView tv = (TextView)findViewById(R.id.startStop);
		tv.setText("Tether started.");
	}
	
	public void stopTetherButtonPressed(View v) {
		Log.v(TAG, "Stop tether button pressed");
		Tether.getTether(TETHER_ADDRESS).stop();
		TextView tv = (TextView)findViewById(R.id.startStop);
		tv.setText("Tether stopped.");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Tether.getTether(TETHER_ADDRESS).stop();
	}
}

