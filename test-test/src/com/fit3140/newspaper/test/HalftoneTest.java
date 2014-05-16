/**
 * Halftone Filter tests
 */

package com.fit3140.newspaper.test;

import java.io.ByteArrayOutputStream;

import com.fit3140.newspaper.ImageFragment;
import com.fit3140.newspaper.ImageViewer;
import com.fit3140.newspaper.MainActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.MediaStore.Images;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.Button;

public class HalftoneTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity main;
	private ImageViewer imgViewer;
	private ViewPager imageViewerPager;
	private Button applyButton;
	private ImageFragment currentImage;
	private Bitmap currentBitmap;
	
	private Paint black = new Paint();
	private Paint white = new Paint();
	private Paint blue = new Paint();

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
		imgViewer = new ImageViewer(main.getFragmentManager());
		imageViewerPager = (ViewPager) main.findViewById(
				com.fit3140.newspaper.R.id.imagePager);
		
		black.setColor(Color.BLACK);
		white.setColor(Color.WHITE);
		blue.setColor(Color.BLUE);
	}
	
	/**
	 * Puts an image in to the UI directly - bypassing the need to access the gallery or camera.
	 * 
	 * @param 	img		The image to put in to the UI.
	 */
	
	public void putImageInUI(Bitmap img) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(main.getContentResolver(), img, "TEST",
				"Image automatically created for testing.");
		imgViewer.addImage(path);
	}
	
	/**
	 * Gets the bitmap that the imageViewerPager's currently displaying and puts it
	 * in to currentBitmap.
	 */
	
	public void getImageFromUI() {
		currentImage = (ImageFragment)imgViewer.getItem(imageViewerPager.getCurrentItem());
		currentBitmap = currentImage.getBitmap();
	}
	
	/**
	 * Tests that the tests can actually read images and correctly identify whether they're identical or not.
	 * If this test fails, all other tests are meaningless.
	 */
	
	public void testImageReading() {
		Bitmap testImg = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(testImg);
	    
	    c.drawRect(0, 0, 20, 20, blue);
	    
	    putImageInUI(testImg);
	    //Note that the apply button is not clicked - the image doesn't change.
	    getImageFromUI();
	    
		boolean hasChanged = false;
		for (int x = 0; x < currentBitmap.getWidth(); x++) {
			for (int y = 0; y < currentBitmap.getHeight(); y++) {
				if (currentBitmap.getPixel(x, y) != Color.BLUE) {
					hasChanged = true;
				}
			}
		}
		assertTrue("The test failed to read the image properly.", !hasChanged);
	}
	
	/**
	 * Tests that the filter is actually being applied when the tests are ran. If this fails, the
	 * results of the white and black image tests are meaningless.
	 */
	
	public void testApplyButton() {
		Bitmap testImg = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(testImg);
	    
	    c.drawRect(0, 0, 20, 20, blue);
	    
	    putImageInUI(testImg);
	    TouchUtils.clickView(this, applyButton);
	    getImageFromUI();
	    
		boolean hasChanged = false;
		for (int x = 0; x < currentBitmap.getWidth(); x++) {
			for (int y = 0; y < currentBitmap.getHeight(); y++) {
				if (currentBitmap.getPixel(x, y) != Color.BLUE) {
					hasChanged = true;
				}
			}
		}
		assertTrue("The halftone filter was not applied correctly.", hasChanged);
	}
	
	/**
	 * Tests that if a white image is input in to the halftone filter, a white image is also output.
	 * Has an underscore to make it test after the first two.
	 */
	
	public void test_WhiteImage() {
		Bitmap whiteImg = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
		Canvas wc = new Canvas(whiteImg);
	    
	    wc.drawRect(0, 0, 20, 20, white);
		
	    putImageInUI(whiteImg);
	    TouchUtils.clickView(this, applyButton);
		getImageFromUI();
		
		boolean allWhite = true;
		for (int x = 0; x < currentBitmap.getWidth(); x++) {
			for (int y = 0; y < currentBitmap.getHeight(); y++) {
				if (currentBitmap.getPixel(x, y) != Color.WHITE) {
					allWhite = false;
				}
			}
		}
		assertTrue("The white image was altered by the filter when it shouldn't've been.", allWhite);
	}
	
	/**
	 * Tests that if a black image is input in to the halftone filter, a black image is also output.
	 * Has an underscore to make it test after the first two.
	 */

	public void test_BlackImage() {
		Bitmap blackImg = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
	    Canvas bc = new Canvas(blackImg);
	    
	    bc.drawRect(0, 0, 20, 20, black);
		
	    putImageInUI(blackImg);
	    TouchUtils.clickView(this, applyButton);
		getImageFromUI();
		
		boolean allBlack = true;
		for (int x = 0; x < currentBitmap.getWidth(); x++) {
			for (int y = 0; y < currentBitmap.getHeight(); y++) {
				if (currentBitmap.getPixel(x, y) != Color.BLACK) {
					allBlack = false;
				}
			}
		}
		assertTrue("The black image was altered by the filter when it shouldn't've been.", allBlack);
	}
}
