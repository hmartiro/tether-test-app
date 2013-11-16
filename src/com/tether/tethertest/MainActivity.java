package com.tether.tethertest;

import tether.Tether;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// Log tag
	private static String TAG = "tether.test";
	
	// Address of the device
	private String TETHER_ADDRESS = "00:06:66:4E:3E:CE";
	
	// Tether object
	private Tether tether;
	
	// Handler for this Tether
	private static final class TetherHandler extends Handler {
		
		private final MainActivity activity;
		private TetherHandler(MainActivity a) { activity = a; }
		
		@Override
		public void handleMessage(Message msg) {
			
			Bundle b = msg.getData();
			
			switch (msg.what) {
				case Tether.CONNECTED:
					activity.tetherConnected();
					break;
				case Tether.DISCONNECTED:
					activity.tetherDisconnected();
					break;
				case Tether.POSITION_UPDATE:
					double X = b.getDouble("X");
					double Y = b.getDouble("Y");
					double Z = b.getDouble("Z");
					activity.tetherPositionUpdated(X, Y, Z);
					break;
				case Tether.AOK:
					activity.showToast(b.getString("INFO"));
					break;
				case Tether.ERROR:
					activity.showToast(b.getString("INFO"));
					break;
				default:
					Log.w(TAG, "Received unprocessed message id from libtether: " + msg.what);
					break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Don't turn off the screen
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Create a Tether object and set the Handler
		tether = new Tether(TETHER_ADDRESS);
		tether.setHandler(new TetherHandler(this));
		
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
	
	public void tetherPositionUpdated(double X, double Y, double Z) {
		Log.v(TAG, "New positions. X: " + X + ", Y: " + Y + ", Z: " + Z);
		TextView positionText = (TextView)findViewById(R.id.positionText);
		positionText.setText(
				"X: " + String.format("%+.2f", X) + 
				"cm, Y: " + String.format("%+.2f", Y) + 
				"cm, Z: " + String.format("%+.2f", Z) + "cm");
	}
	
	public void showToast(String str) {
	    Toast toast = Toast.makeText(getApplicationContext(),
    	        str, Toast.LENGTH_LONG);
    	toast.show();
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
		TextView tv = (TextView)findViewById(R.id.startStop);
		tv.setText("Tether started.");
	}
	
	public void stopTetherButtonPressed(View v) {
		Log.v(TAG, "Stop tether button pressed");
		tether.stop();
		TextView tv = (TextView)findViewById(R.id.startStop);
		tv.setText("Tether stopped.");
	}
	
	public void sendCommandButtonPressed(View v) {
		EditText et = (EditText)findViewById(R.id.sendCommandField);
		tether.sendCommand(et.getText().toString());
		et.setText("");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		tether.stop();
	}
}

