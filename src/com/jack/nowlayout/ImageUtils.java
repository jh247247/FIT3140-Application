// This data structure is supposed to do simple image stuff.
// i.e: loading, saving and other things that really should not belong
// in an activity
package com.jack.nowlayout;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.util.Log;
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
import android.view.WindowManager;


// This class is meant to be a helper class to load images and whatnot.
// In most cases though, I don't know what kind of monstrosity it
// could become if misused.

public class ImageUtils {
    private static final int IMAGE_LOAD_DOWNSCALE_FACTOR = 2;

    // note that bitmapfactory does not care if options is null.
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

    public static View getCardImage(Bitmap img, Context ctx) {
        // Have to add the image to the layout somehow.
        LayoutInflater inflater = (LayoutInflater)
            LayoutInflater.from(ctx);

        View imageCard = inflater.inflate(R.layout.card_image, null);
        ImageView imageInCard = (ImageView)
            imageCard.findViewById(R.id.card_image);
        imageInCard.setImageBitmap(img);
        return imageCard;
    }

    // I would like to make this not need the context, but whatever.
    public static BitmapFactory.Options getImageDimsFromUri(Uri img,
                                                            Context ctx) {
        BitmapFactory.Options ret = new BitmapFactory.Options();
        ret.inJustDecodeBounds = true;
        convertUriToBitmap(img, ctx, ret);

        return ret;
    }

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

}
