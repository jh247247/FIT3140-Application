package com.jack.nowlayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.content.Context;
import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.io.File;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.widget.ImageView;

import android.util.Log;

public class MainActivity extends Activity {
    /*This is where all the constants for this activity live.*/
    /* Intent IDs */
    private static final int ACTIVITY_SELECT_IMAGE=1;
    private static final int ACTIVITY_CAPTURE_IMAGE=2;

    /* Other constants */


    /*This is all the changing data lives, ones that should be saved upon
      shutdown. I don't know whether or not there should be a database
      for this or  something.*/

    // This variable saves the previous location of the data loaded.
    // Planned to be used for loading the full res image when needed.
    private Uri m_prevImageLoc;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // This is testing stuff for template layout inflating.
        // I have yet to decide the best way to implement this dynamically
        //Context context = getApplicationContext();
        //LinearLayout container = (LinearLayout) findViewById(R.id.mainLayout);
        //LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(context);
        //View test = inflater.inflate(R.layout.card_action, null);
        //container.addView(test);
        //View test2 = inflater.inflate(R.layout.card_info, null);
        //container.addView(test2);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // Handle intents from other apps
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
		addImageToUI((Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM));
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            // What do we do with multiple images?!
            if (type.startsWith("image/")) {
                //handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }
    }

    // TODO: read the android guidelines for saving state etc.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("MainActivity", "onCreateOptionsMenu called!");
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v("MainActivity", "onOptionsItemSelected called!");
        // Handle presses on the action bar items
        Context ctx = getApplicationContext();
        switch (item.getItemId()) {
        case R.id.action_imageFromCamera:
            Log.v("MainActivity", "Calling intent for capturing image from camera");
            //Timestamp filenames mean they won't be overwritten and they are
            //sorted chronologically... you know, 'cause that's so important.
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            String timestamp = sdf.format(date);

            File file = new File(ctx.getExternalFilesDir(null)
                                 + "/" + timestamp + ".jpg");
            Log.v("MainActivity", "ImagePath: " + file.getAbsolutePath());
            Uri outputFileUri = Uri.fromFile(file);

            // Since we can't get the proper path from the result
            // call, we need to store it at to load later instead.
            // Hacky, but it should work.
            m_prevImageLoc = outputFileUri;

            Intent cameraIntent = new
                Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                Log.v("MainActivity", "Found an app to take picture.");
                startActivityForResult(cameraIntent,
                                       ACTIVITY_CAPTURE_IMAGE);
            }
            return true;
        case R.id.action_settings:
            Log.v("MainActivity", "Calling up settings...");
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, "This is a test action!",
                                         duration);
            toast.show();
            return true;

        case R.id.action_imageFromFile:
            Log.v("MainActivity", "Calling intent to load image from file.");
            Intent imageIntent = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.
                                            Images.Media.EXTERNAL_CONTENT_URI);
            if (imageIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(imageIntent, ACTIVITY_SELECT_IMAGE);
            }
            return true;

        default:
            Log.wtf("MainActivity", "How did we end up in the default case...");
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
        case ACTIVITY_SELECT_IMAGE:
            Log.v("MainActivity", "Intent to load image from file finished.");
            if(resultCode == RESULT_OK){
                Log.v("MainActivity", "Result was good.");
                // get data needed for loading
                Context ctx = getApplicationContext();
                Uri selectedImage = imageReturnedIntent.getData();

                // store Uri so that image can be loaded again later.
                // maybe a new class should be added that stores
                // everything we need for loading the full resolution
                // image (or halftoned image) later.
                m_prevImageLoc = selectedImage;

		// does what it says on the tin.
		addImageToUI(selectedImage);
            }
            break;
        case ACTIVITY_CAPTURE_IMAGE:
            Log.v("MainActivity", "Finished intent for loading image from camera");
            if(resultCode == RESULT_OK){
                Log.v("MainActivity", "Result was good.");
                // this where the stored URI comes into play, since it
                // means that we can now load the image from file.
                // This is kind of a hack since the image is probably
                // given up in the intent somehow, but this way is how
                // it works in kit kat apparently.

                addImageToUI(m_prevImageLoc);
            }
            break;
        }
    }

    private void addImageToUI(Uri uri) {
    	if(uri == null) return; // cbf.
        Context ctx = getApplicationContext();
        Bitmap img = ImageUtils.loadImageScaledToScreenWidth(uri, ctx);

        final int WIDTH = img.getWidth(), HEIGHT = img.getHeight();
        Bitmap greyImg = Bitmap.createBitmap(WIDTH, HEIGHT, Config.ARGB_8888);

	Canvas c = new Canvas(greyImg);
	Paint paint = new Paint();
	ColorMatrix cm = new ColorMatrix();
	cm.setSaturation(0);
	ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	paint.setColorFilter(f);
	c.drawBitmap(img, 0, 0, paint);
	// proof of concept drawing
	Paint black = new Paint(Color.BLACK);
	c.drawCircle(WIDTH/2, HEIGHT/2, 100, black);

        LinearLayout container = (LinearLayout)
            findViewById(R.id.mainLayout);
        // Should also put a ViewHolder or something here so
        // we can modify the view later on.
        View imageTest = ImageUtils.getCardImage(greyImg, ctx);
        container.addView(imageTest);
    }

    public int greytone(int r, int g, int b) {
    	//A common algorithm used by image editors:
    	int value = (r * 3 / 10) + (g * 59 / 100) + (b * 11 / 100);
		return value;
    }
}
