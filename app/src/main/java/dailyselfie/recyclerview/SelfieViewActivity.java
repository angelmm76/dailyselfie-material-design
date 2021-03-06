package dailyselfie.recyclerview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SelfieViewActivity extends AppCompatActivity {  // ListActivity {
	private AlarmManager mAlarmManager;
	private Intent mNotificationIntent;
	private PendingIntent mNotificationPendingIntent;

	private SelfieViewAdapter mAdapter;

	private CoordinatorLayout mCoordinatorLayout;

	private RecyclerView mRecyclerView;
	private SelfieRecyViewAdapter mRecyAdapter;
	private RecyclerView.LayoutManager mLayoutManager;

	private ArrayList<SelfieRecord> mSelfies = new ArrayList<SelfieRecord>();

	private static final String TAG = "Proyect-DailySelfie";
	private static final String FILE_NAME = "Daily_Selfie_data.txt";
		
	private static final long ALARM_INTERVAL = 2 * 60 * 1000L; // two minutes
	
	private String mCurrentPhotoPath;
	private SelfieRecord mSelectedSelfie;

	static private final int SHOW_SELFIE_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the app's user interface. This class is a ListActivity, 
		// so it has its own ListView. ListView's adapter should be a SelfieViewAdapter

//		mAdapter = new SelfieViewAdapter(getApplicationContext());
//		setListAdapter(mAdapter);
//
//		// Enable filtering when the user types in the virtual keyboard
//		getListView().setTextFilterEnabled(true);
//
//		// Set an setOnItemClickListener on the ListView
//		getListView().setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//				Log.i(TAG, "Item clicked");
//				Intent showIntent = new Intent(SelfieViewActivity.this, ShowSelfieActivity.class);
//				SelfieRecord selectedSelfie = (SelfieRecord) mAdapter.getItem(position);
//				String path = selectedSelfie.getFilePath();
//				showIntent.putExtra("path", path);
//				startActivity(showIntent);
//			}
//		});

		setContentView(R.layout.main);
		mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

		// FAB to take pictures
		FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Snackbar
//				Snackbar.make(v, "FAB float?", Snackbar.LENGTH_LONG).show();
				takePicture();
			}
		});

		// Set up the alarm
		mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// Create alarm
		//newAlarm();

		// ************* RECYCLER VIEW ***********
//		setContentView(R.layout.main);
		mRecyclerView = (RecyclerView)findViewById(R.id.recy_view);
		Log.i(TAG, "Recycler view: " + mRecyclerView.toString());
		// use this setting to improve performance if you know that changes
		// in content do not change the layout size of the RecyclerView
		mRecyclerView.setHasFixedSize(true);

		// Layout Manager
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		// Specify an adapter
//		mRecyAdapter = new SelfieRecyViewAdapter(mSelfies);
		// Adapter with on item click listener
		mRecyAdapter = new SelfieRecyViewAdapter(mSelfies, new SelfieRecyViewAdapter.OnItemClickListener() {
			@Override public void onItemClick(SelfieRecord item) {
				Log.i(TAG, "Item clicked (recycled view)");
				// Start activity that shows selfie
				Intent showIntent = new Intent(SelfieViewActivity.this, ShowSelfieActivity.class);
//				SelfieRecord selectedSelfie = (SelfieRecord) item;
//				String path = selectedSelfie.getFilePath();
				mSelectedSelfie = (SelfieRecord) item;
				String path = mSelectedSelfie.getFilePath();
				showIntent.putExtra("path", path);
				String rating = String.valueOf(mSelectedSelfie.getRating());
				showIntent.putExtra("rating", rating);
//				startActivity(showIntent);
				startActivityForResult(showIntent, SHOW_SELFIE_CODE);
			}
		});
		mRecyclerView.setAdapter(mRecyAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Load saved selfies, if necessary
//		if (mAdapter.getCount() == 0){
		if (mRecyAdapter.getItemCount() == 0){
			Log.i(TAG, "Loading items");
			loadItems();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Save selfies
		Log.i(TAG, "onDestroy. Saving items");
		saveItems();
		// Save mCurrentPhotoPath?????????????
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SHOW_SELFIE_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				float rating = Float.parseFloat(data.getStringExtra(data.EXTRA_TEXT));
				Log.i(TAG, "Activity result, rating: " + rating);
				mSelectedSelfie.setRating(rating);
//				mRecyAdapter.onBindViewHolder();  // rating not shown until call onBindviewholder
			}
		}
	}
	
	private void takePicture() {
	    
//		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//		// Create the File where the photo should go
//		File photoFile = null;
//		photoFile = createImageFile();
//
//		// Continue only if the File was successfully created
//
//		Log.i(TAG, "File created");
//		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//		startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//
//		String imageFileName = "Selfie_" + timeStamp + "_";

		// Add selfie to list
		Random r = new Random();
		int p = r.nextInt(9) + 1;
		mCurrentPhotoPath = String.valueOf(p);
		Log.i(TAG, "Current Photo path (takePicture): "+ mCurrentPhotoPath);
//		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alien1);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getBmpId(mCurrentPhotoPath));
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		// NullPointerException, bitmap=null, if photo rotated!!! mCurrentPhotoPath null onDestroy
		SelfieRecord selfie = new SelfieRecord(bitmap, timeStamp, mCurrentPhotoPath, 0);
//		mAdapter.add(selfie);
		mRecyAdapter.add(selfie);

		// Snackbar
		Snackbar.make(mCoordinatorLayout, "Selfie " + timeStamp + " taken", Snackbar.LENGTH_LONG)
//				.setActionTextColor(R.color.snackbar_action)   //0xff0033)
				.setAction("UNDO", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Log.i(TAG, "Snackbar action clicked!");
					}
				})
				.show();
	}
	
//	private File createImageFile() throws IOException {
//	    // Create an image file name
//	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//	    String imageFileName = "Selfie_" + timeStamp + "_";
//	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//	    if (!storageDir.exists()){
//	    	storageDir.mkdirs();
//	    }
//	    Log.i(TAG, "Storage dir: " + storageDir);
//	    File image = File.createTempFile(
//	        imageFileName,  /* prefix */
//	        ".jpg",         /* suffix */
//	        storageDir      /* directory */
//	    );
//
//	    mCurrentPhotoPath = image.getAbsolutePath();
//	    Log.i(TAG, "mCurrentphotopath (createFile): " + mCurrentPhotoPath);
//	    return image;
//	}

	private int getBmpId (String path) {
		Log.i(TAG, "Load bitmap, path: " + path);
		switch(path){
			case "1": return R.drawable.alien1;
			case "2": return R.drawable.alien2;
			case "3": return R.drawable.alien3;
			case "4": return R.drawable.alien4;
			case "5": return R.drawable.alien5;
			case "6": return R.drawable.alien6;
			case "7": return R.drawable.alien7;
			case "8": return R.drawable.alien8;
			case "9": return R.drawable.alien9;
			default:
				Log.i(TAG, "No path");
				return R.drawable.no_image;
		}
	}

	// Load stored ToDoItems
	private void loadItems() {
		BufferedReader reader = null;
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			reader = new BufferedReader(new InputStreamReader(fis));

			Bitmap bitmap = null;
			String date = null;
			String path = null;

			// Read date and path
			while (null != (date = reader.readLine())) {
				path = reader.readLine();
				Log.i(TAG, "Loaded path: " + path);
//				bitmap = BitmapFactory.decodeFile(path);
				bitmap = BitmapFactory.decodeResource(getResources(), getBmpId(path));
//				mAdapter.add(new SelfieRecord(bitmap, date, path));
				mRecyAdapter.add(new SelfieRecord(bitmap, date, path, 0));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Save Items to file
	private void saveItems() {
		PrintWriter writer = null;
		try {
			FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));

//			for (int idx = 0; idx < mAdapter.getCount(); idx++) {
			for (int idx = 0; idx < mRecyAdapter.getItemCount(); idx++) {
//				writer.println(mAdapter.getItem(idx));
				String itemDate = mRecyAdapter.getItem(idx).getDate();
				String itemPath = mRecyAdapter.getItem(idx).getFilePath();
				Log.i(TAG, "Saved path: " + itemPath + ". Saved date: " + itemDate);
				writer.println(itemDate);
				writer.println(itemPath);

			}
//			writer.write("");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}
	
	private void newAlarm() {
		// Create an Intent to broadcast to the AlarmNotificationReceiver
		mNotificationIntent = new Intent(SelfieViewActivity.this, AlarmNotificationReceiver.class);

		// Create an PendingIntent that holds the NotificationReceiverIntent
		mNotificationPendingIntent = PendingIntent.getBroadcast(
				SelfieViewActivity.this, 0, mNotificationIntent, 0);
		
		Log.i(TAG, "Alarm set");
		mAlarmManager.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + ALARM_INTERVAL, ALARM_INTERVAL,
				mNotificationPendingIntent);
		Toast.makeText(getApplicationContext(), "Alarm set", Toast.LENGTH_LONG).show();
	}

	private void cancelAlarm() {

		Log.i(TAG, "Alarm cancelled");
		mAlarmManager.cancel(mNotificationPendingIntent);
		Toast.makeText(getApplicationContext(), "Alarm cancelled", Toast.LENGTH_LONG).show();
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.launch_camera:
				takePicture();
				return true;
			case R.id.set_alarm:
				// Create alarm
				newAlarm();
				return true;
			case R.id.cancel_alarm:
				// Cancel alarm
				cancelAlarm();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
