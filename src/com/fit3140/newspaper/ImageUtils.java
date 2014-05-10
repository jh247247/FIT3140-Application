// This data structure is supposed to do simple image stuff.
// i.e: loading, saving and other things that really should not belong
// in an activity
package com.fit3140.newspaper;

import android.app.Activity;
import android.widget.ImageView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.Toast;


// This class is meant to be a helper class to load images and whatnot.
// In most cases though, I don't know what kind of monstrosity it
// could become if misused.

public class ImageUtils {
    private static final int IMAGE_LOAD_DOWNSCALE_FACTOR = 2;

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
     * Inflates a card into a given parent, using a bitmap as the data
     * to display.
     *
     * Note that for events to work the inflated view needs an
     * Activity as well as a parent.
     *
     * @param img image to add to view
     * @param ctx The current context of the application
     * @param act The current activity on the display
     * @param parent The parent view to inflate the card into.
     * @return The inflated view.
     */
    public static View getCardImage(Bitmap img, Context ctx,
                                    Activity act, ViewGroup parent) {
        // Have to add the image to the layout somehow.
        View imageCard = LayoutInflater.from(act).inflate(R.layout.card_image,
        		parent, true);
        
        ImageView imageInCard = (ImageView)
            imageCard.findViewById(R.id.card_image);
        imageInCard.setImageBitmap(img);
        return imageCard;
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
     * Loads an image scaled to the current screen width (for UI speed purposes)
     *
     * @param img Uri of the image to load.
     * @param ctx context of the application.
     * @return Bitmap scaled to the width of the screen.
     */
    public static Bitmap loadImageScaledToScreenWidth(Uri img, Context ctx) {
        DisplayMetrics metrics = new DisplayMetrics();
        // casting is okay in this case, since we should really get what we want...
        ((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        BitmapFactory.Options dims =
            ImageUtils.getImageDimsFromUri(img, ctx);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = dims.outWidth*
            IMAGE_LOAD_DOWNSCALE_FACTOR/metrics.widthPixels;
        return ImageUtils.convertUriToBitmap(img,
                                             ctx, opt);
    }

    /**
     * Takes in a bitmap and applies a simple halftoning algorithm to
     * it.
     * Note that this currently consumes a lot of memory. Optimization
     * by reducing buffering would be for the best.
     *
     * @param img image to apply halftoning to.
     * @param grid grid size to use for halftoning
     * @return A halftoned image
     */
    public static Bitmap makeHalftoneImage(Bitmap img, int grid) {
      return null;
    }

    /**
     * saves the given image into the applications private app directory.
     *
     * @param img bitmap to save
     * @param ctx the application context
     * @return The uri to the saved image
     */
    public static Uri saveImagePrivate(Bitmap img, Context ctx) {
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
            Log.w("ImageUtils.saveImage", "OutputStream threw exception: " + e);
	    return null;
        }

        Uri imgUri = Uri.fromFile(file);

        return imgUri;

    }
    /**
     * Saves the given image to the android public directory for images
     *
     * @param img bitmap to save
     * @param ctx the application context
     * @return The uri to the saved image
     */

    public static Uri saveImagePublic(Bitmap img, Context ctx) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String timestamp = sdf.format(date);

        File file = new
            File(Environment.getExternalStoragePublicDirectory(
            		Environment.DIRECTORY_PICTURES)
            		+ "/" + timestamp + ".jpg");
        try {
            FileOutputStream outStream = new FileOutputStream(file);
            img.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
            outStream.close();
        } catch (Exception e) {
            Log.w("ImageUtils.saveImage", "OutputStream threw exception: " + e);
        }

        Toast.makeText(ctx, file.getAbsolutePath(),
                       Toast.LENGTH_SHORT).show();

        Uri imgUri = Uri.fromFile(file);

        return imgUri;

    }
}
