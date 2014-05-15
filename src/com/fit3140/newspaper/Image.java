package com.fit3140.newspaper;

import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.net.Uri;
import android.util.Log;
import android.content.Context;
import java.io.InputStream;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.os.Environment;
import java.nio.channels.FileChannel;
import java.io.IOException;


// probably should have come up with a better name for this class...
class Image extends android.app.Fragment {
  // get the image location from args etc.
  private static final String IMG_LOC = "imgLoc";
  private static final int IMAGE_LOAD_DOWNSCALE_FACTOR = 2;

  private Uri m_imgLoc;
  private Bitmap m_dispImg;
  // we want this to be a weak ref so that when android runs out of
  // mem, it can get a few meg back by freeing this.
  private ImageView m_imgView;

  public static Image newInstance(String imgLoc) {
    // needs to show a downscaled image if needed
    Image f = new Image();

    // pass in the args to the made fragment
    Bundle args = new Bundle();
    args.putCharSequence(IMG_LOC,imgLoc);
    f.setArguments(args);

    return f;

  }

  @Override
  public View onCreateView(LayoutInflater inflater,
			   ViewGroup container,
			   Bundle savedInstanceState) {

    Log.v("Image","Have to set up the UI for this fragment");
    View ret = inflater.inflate(R.layout.fragment_image,
				container, false);
    m_imgView =
      (ImageView)ret.findViewById(R.id.fragment_imgView);

    String imgLocStr =  (String)getArguments().getCharSequence(IMG_LOC,null);;
    if(imgLocStr == null) {
      Log.w("Image", "Somehow a null uri was passed in as an arg.");
      // what do we do???
      return null;
    }

    // set the member vars for later.
    m_imgLoc = Uri.parse(imgLocStr);

    m_dispImg = loadImageScaledToScreenWidth(m_imgLoc, getActivity());
    m_imgView.setImageBitmap(m_dispImg);
    // Inflate the layout for this fragment
    Log.v("Image","Great success!");
    return ret;
  }

  public Bitmap getBitmap() {
    Log.v("Image","Loading image");
    return convertUriToBitmap(m_imgLoc, getActivity(), null);
  }


  /**
   * Takes in a Uri and loads it into a bitmap that has some options
   * applied to it.
   *
   * @param loc location of the image given as a Uri
   * @param ctx context of the application, needed to look up the
   * location of the image from the Uri.
   * @param opt Options to apply to the image when loading. If no
   * options are required, pass in null instead.
   * @return The bitmap from the uri with options applied.
   */
  public static Bitmap convertUriToBitmap(Uri loc, Context ctx,
                                          BitmapFactory.Options opt) {
    InputStream imageStream = null;
    try {
      imageStream = ctx.getContentResolver().openInputStream(loc);
    }
    catch (Exception e) {
      Log.w("ImageUtils", "openInputStream() threw exception: " + e);
      return null;
    }
    return BitmapFactory.decodeStream(imageStream, null, opt);
  }




  /**
   * Loads an image scaled to the current screen width (for UI speed purposes)
   *
   * @param img Uri of the image to load.
   * @param ctx context of the application.
   * @return Bitmap scaled to the width of the screen.
   */
  private static Bitmap loadImageScaledToScreenWidth(Uri img, Context ctx) {
    DisplayMetrics metrics = new DisplayMetrics();
    // casting is okay in this case, since we should really get what we want...
    ((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

    BitmapFactory.Options dims = getImageDimsFromUri(img, ctx);

    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inSampleSize = dims.outWidth*
      IMAGE_LOAD_DOWNSCALE_FACTOR/metrics.widthPixels;
    return convertUriToBitmap(img,
					 ctx, opt);
  }

  /**
   * grabs the image dims from the Uri and returns the
   * BitmapFactory.Options that contains it
   *
   * @param img the Uri to the image
   * @param ctx the context of the application.
   * @return BitmapFactory.Options containing the dimensions of the
   * image given at location 'img'
   */
  // I would like to make this not need the context, but whatever.
  public static BitmapFactory.Options getImageDimsFromUri(Uri img,
							  Context ctx) {
    BitmapFactory.Options ret = new BitmapFactory.Options();
    ret.inJustDecodeBounds = true;
    convertUriToBitmap(img, ctx, ret);

    return ret;
  }

  /**
   * Saves the given image to the android public directory for images
   *
   * @param img bitmap to save
   * @param ctx the application context
   * @return The uri to the saved image
   */

  public static Uri saveImage(Bitmap img, Context ctx) {
    if (img == null) {
      Log.e("ImageUtils.saveImage",
	    "Input image is null!");
    }
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    String timestamp = sdf.format(date);

    File file = new File(ctx.getExternalFilesDir(null)
			 + "/" + timestamp + ".jpg");


    try {
      FileOutputStream outStream = new FileOutputStream(file);
      img.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
      outStream.close();
    } catch (Exception e) {
      Log.e("ImageUtils.saveImage", "OutputStream threw exception: " + e);
    }

    Uri imgUri = Uri.fromFile(file);

    return imgUri;
  }

  /**
   * copy file from source to destination
   *
   * @param src source
   * @param dst destination
   * @throws java.io.IOException in case of any problems
   */
  public Uri copyImageToPublic() throws IOException {
    File inFile = new File(m_imgLoc.getPath());

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    String timestamp = sdf.format(date);
    File outFile = new
      File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
           + "/" + timestamp + ".jpg");
    FileChannel inChannel = new FileInputStream(inFile).getChannel();
    FileChannel outChannel = new FileOutputStream(outFile).getChannel();
    try {
      inChannel.transferTo(0, inChannel.size(), outChannel);
    } finally {
      if (inChannel != null)
	inChannel.close();
      if (outChannel != null)
	outChannel.close();
    }
    return Uri.fromFile(outFile);
  }
}
