/**
 * Halftone Filter tests
 */

package com.fit3140.newspaper.test;

import com.fit3140.newspaper.ImageUtils;
import com.fit3140.newspaper.MainActivity;
import com.fit3140.newspaper.R;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HalftoneTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Button applyButton;
	private MainActivity main;
	private ImageView m_tempImageRef;
	
	private Paint black = new Paint();
	private Paint white = new Paint();

	public HalftoneTest() {
		super(MainActivity.class);
	}

	protected void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			Log.w("HalftoneTest", "setUp threw exception: " + e);
			e.printStackTrace();
		}

		main = getActivity();
		applyButton = (Button) main.findViewById(
				com.fit3140.newspaper.R.id.apply_filter);
		
		black.setColor(Color.BLACK);
		white.setColor(Color.WHITE);
	}
	
	/**
	 * Not actually a test. Uses the UI thread to put an image in to the UI, because tests
	 * aren't allowed to do that normally.
	 * 
	 * @param 	img	The image to put in to the UI. It's made final to appease eclipse and because
	 * 				it's not going to get changed in any tests anyway.
	 */
	
	public void putImageInUI(final Bitmap img) {
	    main.runOnUiThread(
	            new Runnable()
	            {
	                @Override
	                public void run()
	                {
	            		LinearLayout container = (LinearLayout) main.findViewById(R.id.outputArea);
	            		View imageTest = ImageUtils.getCardImage(img, main.getApplicationContext(), main,
	            				(ViewGroup) main.findViewById(R.id.outputArea));
	            		m_tempImageRef = (ImageView)imageTest.findViewById(R.id.card_image);
	                }
	            }
	        );
	}
	
	/**
	 * Tests that the filter is actually being applied when the tests are ran. If this fails, the
	 * results of the other two tests are meaningless.
	 */
	
	public void testApplyButton() {
		Bitmap testImg = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(testImg);
	    
	    c.drawRect(0, 0, 10, 10, white);
	    c.drawRect(0, 10, 10, 20, black);
	    c.drawRect(10, 0, 20, 10, black);
	    c.drawRect(10, 10, 20, 20, white);
	    
	    putImageInUI(testImg);
	    
	    TouchUtils.tapView(this, applyButton);
	    
		Bitmap outputImg = ((BitmapDrawable) m_tempImageRef.getDrawable()).getBitmap();
		boolean hasChanged = false;
		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 20; y++) {
				//                                              != is used as XOR here
				if (outputImg.getPixel(x, y) == Color.BLACK && ((x > 9) != (y > 9))) {
					hasChanged = true;
				}
			}
		}
		assertTrue("The halftone filter was not applied in these tests - ignore following results.", hasChanged);
	}
	
	/**
	 * Tests that if a white image is input in to the halftone filter, a white image is also output.
	 */
	
	public void testWhiteImage() {
		Bitmap whiteImg = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
		Canvas wc = new Canvas(whiteImg);
	    
	    wc.drawRect(0, 0, 20, 20, white);
		
	    putImageInUI(whiteImg);
		
		TouchUtils.tapView(this, applyButton);
		
		Bitmap outputImg = ((BitmapDrawable) m_tempImageRef.getDrawable()).getBitmap();
		boolean allWhite = true;
		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 20; y++) {
				if (outputImg.getPixel(x, y) != Color.WHITE) {
					allWhite = false;
				}
			}
		}
		assertTrue("The white image was altered by the filter when it shouldn't've been.", allWhite);
	}
	
	/**
	 * Tests that if a black image is input in to the halftone filter, a black image is also output.
	 */

	public void testBlackImage() {
		Bitmap blackImg = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
	    Canvas bc = new Canvas(blackImg);
	    
	    bc.drawRect(0, 0, 20, 20, black);
		
	    putImageInUI(blackImg);
	    
		TouchUtils.tapView(this, applyButton);
		
		Bitmap outputImg = ((BitmapDrawable) m_tempImageRef.getDrawable()).getBitmap();
		boolean allBlack = true;
		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 20; y++) {
				if (outputImg.getPixel(x, y) != Color.BLACK) {
					allBlack = false;
				}
			}
		}
		assertTrue("The black image was altered by the filter when it shouldn't've been.", allBlack);
	}
}
