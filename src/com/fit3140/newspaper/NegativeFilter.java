package com.fit3140.newspaper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

/**
 * Filter subclass for making negatived greyscale images.
 * 
 * @author 	<a href="mailto:jmhos3@student.monash.edu">Jack Hosemans</a>
 * 			<a href="mailto:tjpar4@student.monash.edu">Thomas Parasiuk</a>
 * @modified	May 2014
 */
public class NegativeFilter extends Filter {

	private Filter.FilterCallBack m_parent;
	
	/**
	 * Called when the fragment is attached to an activity.
	 * Also used to set up callbacks.
	 */
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			m_parent = (Filter.FilterCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement Filter.FilterCallBack");
		}
	}

	/**
	 * Called when the view is created.
	 * The negative filter has no options, so it just gets inflated and then
	 * it's done.
	 */
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View ret = inflater.inflate(R.layout.fragment_filter_negative,
				container, false);

		return ret;
	}

	/**
	 * Applies a negative filter to the given image.
	 * Once it's done it calls back with the negative image.
	 *
	 * @param	img	The given image.
	 */

	@Override
	public void apply(Bitmap img) {
	  if(img == null) {
            Log.w("NegativeFilter","Trying to negative a null image!");
            // so for this case, we just never call the callback. Genius!
            return;
          }
	  Bitmap bmpGrayscale = Bitmap.createBitmap(img.getWidth(),
						    img.getHeight(),
						    Bitmap.Config.ARGB_8888);
	  Canvas c = new Canvas(bmpGrayscale);
	  Paint paint = new Paint();
	  // take grayscale, invert.
	  float[] mat = new float[] {-0.3f, -0.59f, -0.11f, 0, 255,
				     -0.3f, -0.59f, -0.11f, 0, 255,
				     -0.3f, -0.59f, -0.11f, 0, 255,
				     0, 0, 0, 1, 0};
          ColorMatrix cm = new ColorMatrix(mat);
	  ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);


	  paint.setColorFilter(f);
	  c.drawBitmap(img, 0, 0, paint);
	  m_parent.filterFinishedCallback(bmpGrayscale);
	}

  /**
   * This method is supposed to return a string to put into the
   * tab bar. Handy, since we actually need this.
   */
  @Override
  public String getFilterName() {
    return "Negative";
  }
}
