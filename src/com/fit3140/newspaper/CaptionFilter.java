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

public class CaptionFilter extends Filter {

	private Filter.FilterCallBack m_parent;
	private EditText m_edittext;

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

		View ret = inflater.inflate(R.layout.fragment_filter_caption,
				container, false);
		m_edittext = (EditText) ret.findViewById(R.id.caption_text);

		return ret;
	}

	/**
	 * Applies a caption to the given image.
	 * Once it's done it calls back with the new captioned image.
	 * 
	 * @param	img	The given image.
	 */

	@Override
	public void apply(Bitmap img) {
	  if(img == null) {
            Log.w("CaptionFilter","Trying to caption a null image!");
            // so for this case, we just never call the callback. Genius!
            return;
          }
	  String caption = m_edittext.getText().toString();
	  TextPaint text = new TextPaint();
	  final int WIDTH = img.getWidth();

	  text.setColor(Color.BLACK);
	  text.setTextSize(img.getHeight() / 8);

	  StaticLayout layout = new StaticLayout(caption, text, WIDTH,
						 Layout.Alignment.ALIGN_CENTER, 1, 0, true);
	  //A small space is included for a separator between the image and the
	  //caption.
	  int captionHeight = layout.getHeight() + 8;

	  Bitmap captionbmp = Bitmap.createBitmap(WIDTH, captionHeight,
						  Bitmap.Config.ARGB_8888);
	  Canvas captionc = new Canvas(captionbmp);
	  layout.draw(captionc);

	  final int HEIGHT = img.getHeight() + captionHeight;

	  Bitmap captionedImg = Bitmap.createBitmap(WIDTH, HEIGHT,
						    Bitmap.Config.ARGB_8888);
	  Canvas c = new Canvas(captionedImg);

	  Paint white = new Paint();
	  white.setColor(Color.WHITE);

	  c.drawRect(0, 0, WIDTH, HEIGHT, white);
	  c.drawBitmap(img, 0, 0, null);
	  c.drawBitmap(captionbmp, 0, HEIGHT - captionHeight +
		       4, null);

	  m_parent.filterFinishedCallback(captionedImg);
	}

  /**
   * This method is supposed to return a string to put into the
   * tab bar. Handy, since we actually need this.
   */
  @Override
  public String getFilterName() {
    return "Caption";
  }
}
