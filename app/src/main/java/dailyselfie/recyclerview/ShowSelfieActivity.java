package dailyselfie.recyclerview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;


public class ShowSelfieActivity extends Activity {

	private ImageView mImageView;
	private String mSelfiePath;
	private float mRating;

	private static final String TAG = "Show-DailySelfie";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.show_selfie_activity);
		mImageView = (ImageView) findViewById(R.id.imageView1);
		
		Intent intent = getIntent();
		mSelfiePath = intent.getExtras().getString("path");
		mRating = Float.parseFloat(intent.getExtras().getString("rating"));
		
		setPic();

		final RatingBar rBar = (RatingBar) findViewById(R.id.ratingBarShow);
		rBar.setRating(mRating);
		rBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
										boolean fromUser) {

				Log.i(TAG, "Rating changed");
				mRating = rating;
				rBar.setRating(mRating);

			}
		});

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mImageView.setImageDrawable(null);
	}

	@Override
	protected void onResume() {
		super.onResume();

//		mImageView.setImageDrawable(null);
		setPic();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		setResult();
	}
	
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true; // return image size only
//		BitmapFactory.decodeFile(mSelfiePath, bmOptions);
		BitmapFactory.decodeResource(getResources(), getBmpId(mSelfiePath), bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false; // return full image
		bmOptions.inSampleSize = scaleFactor;
//		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
//		Bitmap bitmap = BitmapFactory.decodeFile(mSelfiePath, bmOptions);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getBmpId(mSelfiePath), bmOptions);
//		Bitmap bitmap = loadBitmap(mSelfiePath);
//		The inBitmap flag is about reusing an existing Bitmap object for decoding a bitmap without
// 		having to allocate a new Bitmap object. Allocating a new Bitmap for decode can be unnecessarily
// 		expensive, so you now have the option to reuse a Bitmap when you know it's safe
		bmOptions.inBitmap = bitmap;  // ??? If the image takes too long do it in another thread

		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);
	
	}

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
}	
