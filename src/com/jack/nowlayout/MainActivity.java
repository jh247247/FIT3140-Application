package com.jack.nowlayout;

import android.app.Activity;
import android.os.Bundle;
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
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import android.widget.ImageView;
import android.util.DisplayMetrics;

public class MainActivity extends Activity {
    /*This is where all the constants for this activity live.*/
    /* Intent IDs */
    private static final int ACTIVITY_SELECT_IMAGE=1;
    private static final int ACTIVITY_CAPTURE_IMAGE=2;

    /* Other constants */
    private static final int IMAGE_LOAD_DOWNSCALE_FACTOR = 2;

    /*This is all the changing data lives, ones that should be saved upon
      shutdown. I don't know whether or not there should be a database
      for this or  something.*/
    private ArrayList<Uri> m_imageLocBuffer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // This is testing stuff for template layout inflating.
        // I have yet to decide the best way to implement this dynamically
        Context context = getApplicationContext();
        LinearLayout container = (LinearLayout) findViewById(R.id.mainLayout);
        LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(context);
        View test = inflater.inflate(R.layout.card_action, null);
        container.addView(test);
        View test2 = inflater.inflate(R.layout.card_info, null);
        container.addView(test2);

        // We should restore state at this point, I think.
        // Have to look into how android talks to apps and how they do
        // states and stuff.
        m_imageLocBuffer = new ArrayList<Uri>();
    }

    // TODO: read the android guidelines for saving state etc.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_imageFromCamera:
            Intent cameraIntent = new
                Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent,
                                       ACTIVITY_CAPTURE_IMAGE);
            }
            return true;
        case R.id.action_settings:
            Context ctx = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, "This is a test action!",
                                         duration);
            toast.show();
            return true;

        case R.id.action_imageFromFile:

            Intent imageIntent = new Intent(Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    if (imageIntent.resolveActivity(getPackageManager()) != null) {
		startActivityForResult(imageIntent, ACTIVITY_SELECT_IMAGE);
	    }


        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
        case ACTIVITY_SELECT_IMAGE:
            if(resultCode == RESULT_OK){
                // get data needed for loading
                Context ctx = getApplicationContext();
                Uri selectedImage = imageReturnedIntent.getData();

                // store Uri so that image can be loaded again later.
                // maybe a new class should be added that stores
                // everything we need for loading the full resolution
                // image (or halftoned image) later.
                m_imageLocBuffer.add(selectedImage);

                // maybe this code could be put into a method, but seriously.
                // this is only test code for now, so we should worry later.
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                BitmapFactory.Options dims =
                    ImageUtils.getImageDimsFromUri(selectedImage, ctx);

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = dims.outWidth*
                    IMAGE_LOAD_DOWNSCALE_FACTOR/metrics.widthPixels;


                // load and add image to gui wrapped in a card.
                LinearLayout container = (LinearLayout)
                    findViewById(R.id.mainLayout);
                Bitmap img = ImageUtils.convertUriToBitmap(selectedImage,
                                                           ctx, opt);
                // Should also put a ViewHolder or something here so
                // we can modify the view later on.
                View imageTest = ImageUtils.getCardImage(img, ctx);
                container.addView(imageTest);
            }
        }
    }
}
