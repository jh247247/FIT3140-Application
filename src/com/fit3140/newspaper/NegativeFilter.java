package com.fit3140.newspaper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.util.Log;

public class NegativeFilter extends Filter {

	private Filter.FilterCallBack m_parent;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			m_parent = (Filter.FilterCallBack) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement Filter.FilterCallBack");
		}
	}

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
	  final int WIDTH = img.getWidth(),
			  HEIGHT = img.getHeight();
	  int raw, red, green, blue, grey;

	  Bitmap negativedImg = Bitmap.createBitmap(WIDTH, HEIGHT,
						    Bitmap.Config.ARGB_8888);
	  Canvas c = new Canvas(negativedImg);
	  Paint p = new Paint();
	  
	  for (int x = 0; x < WIDTH; x++) {
		  for (int y = 0; y < HEIGHT; y++) {
			  
			  raw = img.getPixel(x, y);
			  red = Color.red(raw);
			  green = Color.green(raw);
			  blue = Color.blue(raw);

			  grey = (int) (0.3f * red + 0.59f * green + 0.11f * blue);
			  grey = 255 - grey;
			  
			  //Set paint to negatived grey color - fully opaque.
			  p.setARGB(255, grey, grey, grey);
			  //Draw a single pixel at (x, y) with this color.
			  c.drawPoint(x, y, p);
		  }
	  }

	  m_parent.filterFinishedCallback(negativedImg);
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
