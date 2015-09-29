package com.vinci.dtp;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;


import com.android.graphics.CanvasView;
import com.vinci.dtp.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Paint;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	private ImageView image;
	private CanvasView canvasView;
	private Button clearbtn;
	private Button submitbtn;
	private TextView output;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.image = (ImageView) super.findViewById(R.id.image);
		this.canvasView = (CanvasView) super.findViewById(R.id.canvasView);
		this.clearbtn = (Button) super.findViewById(R.id.clearbtn);
		this.submitbtn = (Button) super.findViewById(R.id.submitbtn);
		this.output = (TextView) super.findViewById(R.id.output);
		
	//setup canvas
		canvasView.setPaintStrokeWidth(8F);
		canvasView.setBlur(2F);
		canvasView.setLineCap(Paint.Cap.ROUND);
		canvasView.setMode(CanvasView.Mode.DRAW);
		canvasView.setDrawer(CanvasView.Drawer.PEN);
		
		clearbtn.setOnClickListener(this);
		submitbtn.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i("DTP", "OpenCV loaded successfully");
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};

	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
	}

	@Override
	public void onClick(View v) {
		
		if(v == clearbtn){
			canvasView.clear();
		} else {
			computeScore();
		}
	}

	private void computeScore() {
		
		final ProgressDialog dialog = ProgressDialog.show(this, "", "Calculating...", true);
		
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				
				Bitmap drawing = canvasView.getBitmap();
				image.buildDrawingCache();
				Bitmap imageBmap = image.getDrawingCache();
				
				Mat imageMat = new Mat();
				Mat drawingMat = new Mat();
				Utils.bitmapToMat(imageBmap, imageMat);
				Utils.bitmapToMat(drawing, drawingMat);
				
				long t = System.currentTimeMillis();
				
				float result = 0;
				result = ChamLib.getScore(drawingMat.getNativeObjAddr(), imageMat.getNativeObjAddr());
				
				
				t = System.currentTimeMillis() - t;
				
				return String.format("Score is = %f in %d ms.", result, t);
				
			}
			
			@Override
			protected void onPostExecute(String result) {
				MainActivity.this.output.setText(result);
				dialog.dismiss();
			}
			
			
		}.execute();
		
	}


}
