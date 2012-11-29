package com.kinvey.testdrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.kinvey.KCSClient;
import com.kinvey.KinveySettings;
import com.kinvey.MappedAppdata;
import com.kinvey.util.ScalarCallback;

/**
 * Activity that demonstrates the basics of Kinvey data storage.
 * 
 */
public class KinveyTestDriveActivity extends Activity {

	private static final String TAG = KinveyTestDriveActivity.class.getSimpleName();

	/**
	 * App key and secret used to authenticate with Kinvey in the initial
	 * handshake.
	 * TODO: create a backend on the console, then cut and paste the key and secret here
	 */
	private static final String KINVEY_APP_KEY = "your_app_key";
	private static final String KINVEY_APP_SECRET = "your_app_secret";

	/**
	 * Keep a handle on the client since it will be needed for most calls to Kinvey
	 */
	private static KCSClient mKinveyClient;

	/**
	 * Dummy collection name or our first data object
	 */
	private static final String COLLECTION_NAME = "testObjects";
	
	// some dummy data for our first attempt to persist and load data 
	private static TestEntity testObject = new TestEntity();
	static {
		// Assign it the unique ID of 12345 (If this value is nil, Kinvey will assign a unique ID)
	    // but we'll be loading by id later, so we're keeping it simple for now
		testObject.setObjectId("12345");
		testObject.setName("My first data!");
	}

	// UI references
	private TextView mOutput;
	private AlertDialog alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mOutput = (TextView) this.findViewById(R.id.text_output);
		
		// setup the client
		mKinveyClient = KCSClient.getInstance(this, new KinveySettings(KINVEY_APP_KEY, KINVEY_APP_SECRET));
		
		findViewById(R.id.save_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						save();
					}
				});

		findViewById(R.id.load_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						load();
					}
				});
		
	}

	/**
	 * Writes the dummy test object to the configured Kinvey backend.
	 */
	private void save() {
		MappedAppdata ma = mKinveyClient.mappeddata(TestEntity.class, COLLECTION_NAME);
		ma.save(testObject, new ScalarCallback<TestEntity>() {

			@Override
			public void onFailure(Throwable t) {
				String msg = String.format("Save failed%nerror: %s", t.getMessage());
				mOutput.setText(msg);
				Log.e(TAG, msg);
				showAlert(KinveyTestDriveActivity.this, "Saved failed", String.format("error:%n%s", t.getLocalizedMessage()));
			}
			
			@Override
			public void onSuccess(TestEntity object) {
				String msg = String.format("Saved worked!%nSaved: '%s'", object.getName());
				mOutput.setText(msg);
				Log.d(TAG, msg);
				
				showAlert(KinveyTestDriveActivity.this, "Saved worked!", String.format("Saved: '%s'", object.getName()));
			}
		});
	}

	/**
	 * Read the dummy test object from the configured Kinvey backend.
	 */
	private void load() {
		MappedAppdata ma = mKinveyClient.mappeddata(TestEntity.class, COLLECTION_NAME);
		TestEntity loaded = new TestEntity();
		ma.load(loaded, testObject.getObjectId(), new ScalarCallback<TestEntity>() {

			@Override
			public void onFailure(Throwable t) {
				String msg = String.format("Load failed%nerror: %s", t.getMessage());
				mOutput.setText(msg);
				Log.e(TAG, msg);
				
				showAlert(KinveyTestDriveActivity.this, "Load worked!", String.format("error:%n%s", t.getLocalizedMessage()));
			}
			
			@Override
			public void onSuccess(TestEntity object) {
				String msg = String.format("Load worked!%nLoaded: '%s'", object.getName());
				mOutput.setText(msg);
				Log.d(TAG, msg);
				
				showAlert(KinveyTestDriveActivity.this, "Load worked!", String.format("Loaded: '%s'", object.getName()));
			}
		});
	}

	/**
	 * Display a simple alert dialog with the given text and title.
	 */
	protected void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertDialog = alertBuilder.create();
		alertDialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Avoids window leaks, when changing from portrait to landscape mode.
	 */
	@Override
	protected void onPause() {
		if (alertDialog != null && alertDialog.isShowing())
			alertDialog.dismiss();
		
		super.onPause();
	}
}
