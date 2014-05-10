package com.fit3140.newspaper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

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
	 * Unfinished implementation of captioning images. Takes the image
	 * and puts a white bar underneath it for a hypothetical caption.
	 */
	
	@Override
	public Bitmap apply(Bitmap img) {
		final int WIDTH = img.getWidth(), HEIGHT = img.getHeight() + 20;

		Bitmap captionedImg = Bitmap.createBitmap(WIDTH, HEIGHT,
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(captionedImg);
		Paint black = new Paint(),
				white = new Paint();
		black.setColor(Color.BLACK);
		white.setColor(Color.WHITE);

		c.drawRect(0, 0, WIDTH, HEIGHT, white);
		c.drawBitmap(img, 0, 0, null);
		
		return captionedImg;
	}

}
