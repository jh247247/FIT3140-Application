package com.jack.nowlayout;

import android.app.Activity;
import android.os.Bundle;
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
import java.util.List;

public class MainActivity extends Activity {
    /*This is where all the constants for this activity live.*/
    private static final int ACTIVITY_SELECT_IMAGE=1;
    /*This is all the changing data lives, ones that should be saved upon
      shutdown. I don't know whether or not there should be a database
      for this or  something.*/
    private List<Bitmap> m_imageBuffer;
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
	m_imageBuffer = new List<Bitmap>();
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
        case R.id.action_settings:
            Context ctx = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, "This is a test action!",
                                         duration);
            toast.show();
            return true;

        case R.id.action_select:
            Intent i = new Intent(Intent.ACTION_PICK,
                                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
        case ACTIVITY_SELECT_IMAGE:
            if(resultCode == RESULT_OK){
		Uri selectedImage = imageReturnedIntent.getData();
		Context ctx = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(ctx, selectedImage.getPath(),
					     duration);
		toast.show();
		// NOTE: at this stage, image can be downscaled on the fly.
		// might not be a good idea to do this as we would be
		// bandwidth limited...
		InputStream imageStream = null;
		try {
		    imageStream = getContentResolver().openInputStream(selectedImage);
		}
		catch (Exception e) {
		    // What should I do here?!
		    return;
		}
                m_imageBuffer.append(BitmapFactory.decodeStream(imageStream));
            }
        }
    }

}
