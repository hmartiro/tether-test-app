package com.tether.tethertest;

import tether.Tether;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String TAG = "tethertest";
	
	private Tether tether;
	private String QUADROTOR_ADDRESS = "00:06:66:4E:3E:CE";
	
	private double X;
	private double Y;
	private double Z;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		X = 0.0;
		Y = 0.0;
		Z = 0.0;
		
		class TCallbacks implements Tether.TetherCallbacks {
			
			public void connected() {
				Log.v(TAG, "Tether connected!");
				
				runOnUiThread(new Runnable() {
				     public void run() {
						TextView statusText = (TextView)findViewById(R.id.statusText);
						statusText.setText("Status: Connected");
				    }
				});
			}
			public void disconnected() {
				Log.w(TAG, "Tether disconnected!");
				
				runOnUiThread(new Runnable() {
				     public void run() {
						TextView statusText = (TextView)findViewById(R.id.statusText);
						statusText.setText("Status: Disconnected");
				    }
				});
			}
			
			public void positionUpdate(double newX, double newY, double newZ) {
				
				X = newX;
				Y = newY;
				Z = newZ;
				Log.v(TAG, "New positions. X: " + X + ", Y: " + Y + ", Z: " + Z);
				
				runOnUiThread(new Runnable() {
				     public void run() {
						TextView positionText = (TextView)findViewById(R.id.positionText);
						positionText.setText("X: " + X + "cm, Y: " + Y + "cm, Z: " + Z + "cm");
				    }
				});
			}
		}
		
		tether = new Tether(QUADROTOR_ADDRESS, new TCallbacks());
		Log.v(TAG, "my tether: " + tether);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startTetherButtonPressed(View v) {
		Log.v(TAG, "Start tether button pressed");
		tether.start();
	}
	
	public void stopTetherButtonPressed(View v) {
		Log.v(TAG, "Stop tether button pressed");
		tether.stop();
	}
}

