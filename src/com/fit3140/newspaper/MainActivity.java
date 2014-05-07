package com.fit3140.newspaper;

import android.app.FragmentManager;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.view.View;
import android.view.MenuInflater;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import java.io.File;
import android.graphics.Bitmap;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.view.ViewGroup;

import android.util.Log;

/**
 * MainActivity is the activity that android first launches when being
 * started by the user.
 *
 * @author <a href="mailto:jmhos3@student.monash.edu">Jack Hosemans</a>
 * @version 1.0
 */
public class MainActivity extends Activity implements
					     View.OnClickListener, Filter.FilterCallBack{
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
  public void filterFinishedCallback(Bitmap filteredImage) {
    Log.v("MainActivity", "Image used callback!");
    Context ctx = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(ctx, "This is from the callback!",
                                 duration);
    toast.show();
  }

    /**
     * onCreate is the method called to recreate the activity when it
     * is killed by either the user or the android task manager.
     *
     * It takes in the state that the app was in last time, and should
     * restore the state depending on what data is in there. Currently
     * it does not do that.
     *
     * @param param savedInstanceState is the state that the app was
     * in when it was closed. Data should be restored from there.
     * @return None
     */


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

	FragmentManager man = getFragmentManager();
	Filter testFilter = (Filter)man.findFragmentById(R.id.testFragment);
	testFilter.apply(null);

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

    /**
     * onCreateOptionsMenu is called when the menu button is pressed
     * (on devices that have it) or when the overflow button in the
     * actionbar is pressed.
     *
     * @param menu is the menu view that (should be) inflated when
     * this method is called
     * @return true when the menu is displayed and false when it is not
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v("MainActivity", "onCreateOptionsMenu called!");
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called whenever an option item is selected.
     *
     * @param item the item that was selected
     * @return false if the menu is to persist, true if it is to be destroyed
     */
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

    /**
     * Called whenever a launched activity returns with or without data.
     *
     * @param requestCode code that the activity was called with
     * @param resultCode code that the activity returned with
     * @param data Data that was returned from the activity
     * @return None
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
        case ACTIVITY_SELECT_IMAGE:
            Log.v("MainActivity", "Intent to load image from file finished.");
            if(resultCode == RESULT_OK){
                Log.v("MainActivity", "Result was good.");
                Uri selectedImage = data.getData();

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

    // This is a class level reference to the latest half-tone image.
    Bitmap m_halfToneImage = null;

    /**
     * Loads an image from a Uri, scales it to the screen and adds it
     * to the main LinearLayout.
     *
     * @param uri Uri of the image to add
     * @return Noneh
     */
    private void addImageToUI(Uri uri) {
        if(uri == null) return; // cbf.
        Context ctx = getApplicationContext();
	Toast.makeText(ctx, "Loading image...",
		       Toast.LENGTH_SHORT).show();
        Bitmap img = ImageUtils.convertUriToBitmap(uri, ctx, null);

	Toast.makeText(ctx, "Halftoning image...",
		       Toast.LENGTH_SHORT).show();
	m_halfToneImage = ImageUtils.makeHalftoneImage(img, 20);

        Uri halfUri = ImageUtils.saveImagePrivate(m_halfToneImage, ctx);
	m_prevImageLoc = halfUri; // have to do something with this uri...

        Bitmap displayImg = ImageUtils.loadImageScaledToScreenWidth(halfUri, ctx);

        LinearLayout container = (LinearLayout)
            findViewById(R.id.mainLayout);
        // Should also put a ViewHolder or something here so
        // we can modify the view later on.
        View imageTest = ImageUtils.getCardImage(displayImg, ctx, this,
						 (ViewGroup)findViewById(R.id.mainLayout));
	Toast.makeText(ctx, "Done!",
		       Toast.LENGTH_SHORT).show();
    }
    /**
     * Save the processed image from the UI.
     * TODO
     *
     * @param v The view that caused the click event
     * @return None
     */
    public void onClickSave(View v){
	Context ctx = getApplicationContext();
	// make the button actually do something.
	ImageUtils.saveImagePublic(m_halfToneImage,ctx);
    }

    /**
     * onClickShare is called whenever the share button is pressed in
     * the imageCard.
     *
     * @param view The view that caused the click event
     * @return None
     */

    public void onClickShare(View view) {
    	Log.v("anon Onclicklistener", "Managed to get into the onclick listener");
    	Intent share = new Intent(Intent.ACTION_SEND);
    	share.setType("image/jpeg");
    	share.putExtra(Intent.EXTRA_STREAM, m_prevImageLoc);;
    	startActivity(Intent.createChooser(share, "Share Image"));
    }

    @Override
    public void onClick(View v) {

    }
}
