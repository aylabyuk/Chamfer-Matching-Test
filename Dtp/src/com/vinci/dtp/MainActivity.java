package com.vinci.dtp;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.vinci.dtp.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	private ImageView image;
	private ImageView drawing;
	private TextView output;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.image = (ImageView) super.findViewById(R.id.image);
		this.drawing = (ImageView) super.findViewById(R.id.drawing);
		this.output = (TextView) super.findViewById(R.id.score);
		Button button = (Button) super.findViewById(R.id.computeBtn);
		button.setOnClickListener(this);
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

public void onClick(View view) {
		
		final ProgressDialog dialog = ProgressDialog.show(this, "", "Calculating...", true);

		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
			
				
				long t = System.currentTimeMillis();
				
				image.buildDrawingCache();
				Bitmap bmap1 = image.getDrawingCache();
				Mat imageMat = new Mat();
				Utils.bitmapToMat(bmap1, imageMat);
				
				drawing.buildDrawingCache();
				Bitmap bmap2 = drawing.getDrawingCache();
				Mat drawingMat = new Mat();
				Utils.bitmapToMat(bmap2, drawingMat);
				
				float result = 0;
				result = ChamLib.getScore(imageMat.getNativeObjAddr(), drawingMat.getNativeObjAddr());
				
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