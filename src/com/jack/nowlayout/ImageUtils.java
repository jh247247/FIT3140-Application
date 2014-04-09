// This data structure is supposed to do simple image stuff.
// i.e: loading, saving and other things that really should not belong
// in an activity
package com.jack.nowlayout;

import android.app.Activity;
import android.widget.ImageView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.ViewGroup;


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

    public static View getCardImage(Bitmap img, Context ctx,
				    Activity act, ViewGroup parent) {
        // Have to add the image to the layout somehow.
        LayoutInflater inflater = (LayoutInflater)
            LayoutInflater.from(ctx);

        View imageCard = inflater.from(act).inflate(R.layout.card_image,
						    parent, true);
        ImageView imageInCard = (ImageView)
            imageCard.findViewById(R.id.card_image);
        imageInCard.setImageBitmap(img);
        return imageCard;
    }


    public void saveImage(View view) {

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
    
    public static Bitmap makeHalftoneImage(Bitmap img, int grid) {
    	final int WIDTH = img.getWidth(), HEIGHT = img.getHeight();
    	
    	Bitmap halftoneImg = Bitmap.createBitmap(WIDTH, HEIGHT, Config.ARGB_8888);
    	Canvas c = new Canvas(halftoneImg);
    	Paint black = new Paint(),
    			white = new Paint();
    	black.setColor(Color.BLACK);
    	white.setColor(Color.WHITE);
    	int[] pixels = new int[grid * grid];
    	float[] hsv = new float[3];
    	
    	float totalValue, maxValue, averageValue;
    	float dotRadius;

    	c.drawRect(0, 0, WIDTH, HEIGHT, white);
    	
    	for (int x = 0; x < WIDTH; x += grid) {
    		for (int y = 0; y < HEIGHT; y += grid) {
    			totalValue = 0;
    			maxValue = 0;
    			
    			img.getPixels(pixels, 0, grid, x, y,
    					Math.min(grid, WIDTH - x), Math.min(grid, HEIGHT - y));
    			for (int i = 0; i < pixels.length; i++) {
    				Color.colorToHSV(pixels[i], hsv);
    				totalValue += hsv[2];
    				maxValue += 1.0f;
    			}
    			
    			averageValue = 1.0f - (totalValue / maxValue);
    			
    			//This is some formula I came up with in Assignment 1
    			dotRadius = (float) (Math.sqrt(averageValue + 0.08) - 0.02) * grid * 0.8f;
    			if (averageValue > 0.9f) {
    				dotRadius += (float) Math.pow(averageValue - 0.9f, 2) / 100;
    			}
    			
    	    	c.drawCircle(x + grid / 2, y + grid / 2, dotRadius, black);
    		}
    	}
    
		return halftoneImg;
    }
    
    public static Uri saveImage(Bitmap img, Context ctx) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String timestamp = sdf.format(date);

        File file = new File(ctx.getExternalFilesDir(null)
                             + "/" + timestamp + ".jpg");
        try {
        	FileOutputStream outStream = new FileOutputStream(file);
        	img.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        	outStream.close();
        } catch (Exception e) {
        	Log.w("ImageUtils.saveImage", "OutputStream threw exception: " + e);
        }
        
        Uri imgUri = Uri.fromFile(file);
        
        return imgUri;
        
    }

}
