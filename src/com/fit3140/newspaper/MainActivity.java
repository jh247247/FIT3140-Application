package com.fit3140.newspaper;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;
import android.view.View;
import android.view.MenuInflater;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import java.io.File;
import android.graphics.Bitmap;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.support.v4.view.ViewPager;
//ActivityInfo is used to force emulators in to landscape mode if necessary.
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.widget.Button;
import java.io.IOException;

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

  private static final int IMAGE_VIEWPAGER_IMAGE_MARGIN = -75;


  /* Other constants */

  /*This is all the changing data lives, ones that should be saved upon
    shutdown. I don't know whether or not there should be a database
    for this or  something.*/

  // This variable saves the previous location of the data loaded.
  // Planned to be used for loading the full res image when needed.
  private Uri m_prevImageLoc;

  // this gets set by the callback.
  private Bitmap m_filteredImage;

  // Adapters for the two swipy interfaces.
  private FilterInterfaceAdapter m_filterInterface;
  private ViewPager m_filterInterfacePager;
  private ImageViewer m_imageViewer;
  private ViewPager m_imageViewerPager;

  @Override
  public void filterFinishedCallback(Bitmap filteredImage) {
    Log.v("MainActivity", "Image used callback!");
    Context ctx = getApplicationContext();

    m_filteredImage = filteredImage;
    m_prevImageLoc = Image.saveImage(m_filteredImage, ctx, Image.PRIVATE);
    m_imageViewer.addImage(m_prevImageLoc.toString());
    m_imageViewerPager.setCurrentItem(m_imageViewer.getCount()-1);
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
    m_filterInterfacePager = (ViewPager)findViewById(R.id.filterPager);
    m_imageViewerPager = (ViewPager)findViewById(R.id.imagePager);
    m_imageViewer = new ImageViewer(getFragmentManager());
    m_filterInterface = new
      FilterInterfaceAdapter(getFragmentManager());

    m_filterInterfacePager.setAdapter(m_filterInterface);
    m_imageViewerPager.setAdapter(m_imageViewer);
    // this apparently does not have an equivalent in xml...
    m_imageViewerPager.setPageMargin(IMAGE_VIEWPAGER_IMAGE_MARGIN);



    // hide buttons to avoid sharing/saving null pointers.
    setButtonVisibility(Button.GONE);



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

    //Use this line to test landscape mode if the emulator/device is usually
    //forced in to portrait mode.
    //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
      Log.v("MainActivity", "Calling intent for capturing image from" +
	    "camera");

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

      // This is not accessible in any way, but we might need it
      // later.
      // Also not causing any harm by leaving it in.
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

  /**
   * Loads an image from a Uri, scales it to the screen and adds it
   * to the main LinearLayout.
   *
   * @param uri Uri of the image to add
   * @return None
   */
  private void addImageToUI(Uri uri) {
    if(uri == null) return; // cbf.
    Context ctx = getApplicationContext();
    Log.v("MainActivity", "Loading image from: " + uri.toString());

    m_imageViewer.addImage(uri.toString());
    ViewPager imagePager = (ViewPager)findViewById(R.id.imagePager);
    imagePager.setAdapter(m_imageViewer );

    setButtonVisibility(Button.VISIBLE);

    Toast.makeText(ctx, "Done!", Toast.LENGTH_SHORT).show();
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
    Uri path = saveCurrentImage();

    Log.v("MainActivity","Uri: " + path);
    if(path == null) {
      // wait a second...
      Log.e("Mainactivity.onClickSave","Saving current image returned null!");
      return;
    }
    Toast.makeText(this, "Image saved to:" + path.getPath(),
                   Toast.LENGTH_SHORT).show();

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

  /**
   * Applies the currently paged-to filter to whatever image is loaded in.
   *
   * @param view The view that caused the click event
   * @return None
   */

  public void onClickApply(View view) {
    Context ctx = getApplicationContext();
    Log.v("MainActivity.onClickApply","Applying filter to image.");
    Log.v("MainActivity.onClickApply","Current Item index: " +
	  m_imageViewerPager);


    Image currImg = getCurrentImage();
    if(currImg == null) {
      Log.w("MainActivity.onClickApply",
            "Image returned is not image!");
      Toast.makeText(this, "Need to load image first!",
                     Toast.LENGTH_SHORT).show();
      return;
    }

    // if we get here, the image returned is of the correct type.
    // that means we can typecast.
    Bitmap img = (currImg).getBitmap();


    Log.v("MainActivity.onClickApply","Image loaded.");

    Filter filter = (Filter)
      m_filterInterface.getItem(m_filterInterfacePager.getCurrentItem());
    Log.v("MainActivity.onClickApply","Applying...");
    filter.apply(img);



  }

  // have to keep this here apparently. Annoying.
  @Override
  public void onClick(View v) {

  }


  // this handles orientation change, because that sort of stuff is
  // kinda important...
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    LinearLayout oriLayout = (LinearLayout)
      findViewById(R.id.oriLayout);
    LinearLayout filterLayout = (LinearLayout)
      findViewById(R.id.filterLayout);
    LinearLayout.LayoutParams filterlp = (LinearLayout.LayoutParams)
      filterLayout.getLayoutParams();

    LinearLayout imageLayout = (LinearLayout)
      findViewById(R.id.imageLayout);
    LinearLayout.LayoutParams imagelp = (LinearLayout.LayoutParams)
      imageLayout.getLayoutParams();


    // Checks the orientation of the screen
    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    {

      oriLayout.setOrientation(LinearLayout.HORIZONTAL);

      filterlp.height = LinearLayout.LayoutParams.MATCH_PARENT;
      imagelp.height = LinearLayout.LayoutParams.MATCH_PARENT;

      filterlp.width = 0;
      imagelp.width = 0;
    } else if (newConfig.orientation ==
	       Configuration.ORIENTATION_PORTRAIT){


      oriLayout.setOrientation(LinearLayout.VERTICAL);
      filterlp.height = 0;
      imagelp.height = 0;

      filterlp.width = LinearLayout.LayoutParams.MATCH_PARENT;
      imagelp.width = LinearLayout.LayoutParams.MATCH_PARENT;
    }
  }

  // set the 'save' and 'share' button visibility.
  private void setButtonVisibility(int vis) {
    ((Button)findViewById(R.id.saveButton)).setVisibility(vis);
    ((Button)findViewById(R.id.shareButton)).setVisibility(vis);
  }


  // these method might be better off in their own class or
  // something.
  // adapter pattern maybe? Right now it is just code smell.
  private Uri saveCurrentImage() {
    Context ctx = getApplicationContext();


    Image currImg = getCurrentImage();
    if(currImg == null) {
      Log.w("MainActivity.onClickApply",
            "Image returned is not image!");
      Toast.makeText(this, "Need to load image first!",
                     Toast.LENGTH_SHORT).show();
      return null;
    }


    Uri outUri = null;
    try {
      outUri = currImg.copyImageToPublic();
    }
    catch (IOException e) {
      // TODO TOAST STUFF
      Log.e("MainActivity.saveCurrentImage","Cannot copy to public dir: " + e);
      Toast.makeText(this, "Image save failed: " + e,
                     Toast.LENGTH_SHORT).show();
    }
    Toast.makeText(this, "Image saved to: " + outUri.getPath(),
                   Toast.LENGTH_SHORT).show();
    return outUri;
  }

  private boolean checkCurrentImage(){
    // get a ref to the object to verify if we can actually typecast.
    Object imgObj =
      m_imageViewer.getItem(m_imageViewerPager.getCurrentItem());
    return (imgObj instanceof Image);
  }

  private Image getCurrentImage() {
    if(!checkCurrentImage()) {
      return null;
    }
    return (Image)m_imageViewer.getItem(m_imageViewerPager.getCurrentItem());

  }
}
