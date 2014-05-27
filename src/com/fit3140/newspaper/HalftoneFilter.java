package com.fit3140.newspaper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.graphics.Matrix;

/**
 * Filter subclass for making halftoned images.
 * Handles halftone grids of varying angles, sizes and shapes.
 * 
 * @author 	<a href="mailto:jmhos3@student.monash.edu">Jack Hosemans</a>
 * 			<a href="mailto:tjpar4@student.monash.edu">Thomas Parasiuk</a>
 * @modified	May 2014
 */
public class HalftoneFilter extends Filter
{
  private static final int MIN_FILTER_GRIDSIZE = 5;

  // This is a setting for basic halftoning, other implementations
  // come later.
  private int m_gridSize;
  private SeekBar m_gridSizeBar;
  private TextView m_gridSizeText;

  private int m_gridAngle;
  private SeekBar m_gridAngleBar;
  private TextView m_gridAngleText;

  private Spinner m_shapeSpinner;
  // This is the type of shape to use.
  private int m_shapeType;
  private final static int SHAPE_CIRCLE = 0;
  // ASSUMPTION: user will not want to change ratio of side len for
  // rect. will make things too difficult anyway. keep it a square.
  private final static int SHAPE_RECTANGLE = 1;
  private final static int SHAPE_DIAMOND = 2;


  // Parent typecasted to this so that we can actually call some stuff
  // on it.
  private Filter.FilterCallBack m_parent;

/**
 * Class for handling changes in the grid size bar.
 * 
 * @author Jack Hosemans and Thomas Parasiuk
 * @modified	May 2014
 */
  private class gridSizeHandler implements OnSeekBarChangeListener {
	  
    /**
     * Sets the grid size every time the bar's value changes.
     * 
     * @param seekBar	The bar itself. Not used.
     * @param progress	The current value of the bar.
     * @param fromUser	Whether the change was caused by the user or not.
     * 					Not used.
     */
	@Override
    public void onProgressChanged(SeekBar seekBar,
				  int progress,
				  boolean fromUser) {
      setGridSize(progress);
    }


	/**
	 * Not used.
	 */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    /**
     * Not used.
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

  }

  
  /**
   * Class for handling changes in the grid angle bar.
   * 
   * @author Jack Hosemans and Thomas Parasiuk
   * @modified	May 2014
   */
  private class gridAngleHandler implements OnSeekBarChangeListener {
	  
	/**
	 * Sets the grid size every time the bar's value changes.
	 * 
	 * @param seekBar	The bar itself. Not used.
	 * @param progress	The current value of the bar.
	 * @param fromUser	Whether the change was caused by the user or not.
	 * 					Not used.
	 */
    @Override
    public void onProgressChanged(SeekBar seekBar,
				  int progress,
				  boolean fromUser) {
      setGridAngle(progress);
    }

    /**
     * Not used.
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    /**
     * Not used.
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

  }
  
  
  /**
   * Drop-down menu class used for selecting halftone shapes.
   * 
   * @author Jack Hosemans and Thomas Parasiuk
   * @modified	May 2014
   */
  
  private class shapeSpinnerHandler implements OnItemSelectedListener {
	/** 
	 * Called whenever an item is selected. Used to set the halftone shape.
	 * 
	 * @param parent Not used.
	 * @param view Not used.
	 * @param pos The position of the item that's been selected.
	 * @param id Not used.
	 */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
      // pos relates directly to the defined constants. Is gud.
      Log.v("shapeSpinnerHandler","Selection: " + pos);
      m_shapeType = pos;
    }

    /**
     * Not used.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
  }

  /**
   * This method is called when the fragment is attached to an
   * activity. Fortunately the activity also passes in itself to
   * this. This means that we can override it and use the passed in
   * activity to attach a callback to the Activity.
   */
  @Override
  public void onAttach(Activity activity) {
	  // do whatever the inherited function is supposed to do.
	  super.onAttach(activity);

	  // try to typecast the Activity to the callback interface declared
	  // in the Filter class. This gets called upon completion of the filtering.
	  try {
		  m_parent = (Filter.FilterCallBack) activity;
	  } catch (ClassCastException e) {
		  throw new ClassCastException(activity.toString() +
				  " must implement Filter.FilterCallBack");
	  }
  }

  /**
   * Called when the view is created.
   * Inflates the view and then sets up the seekbars, text, etc.
   */
  @Override
  public View onCreateView(LayoutInflater inflater,
			   ViewGroup container,
			   Bundle savedInstanceState) {

	  View ret = inflater.inflate(R.layout.fragment_filter_halftone,
			  container, false);

	  // get data from the grid size
	  m_gridSizeText =
	    (TextView)ret.findViewById(R.id.grid_size_text);
          m_gridSizeBar =
	    (SeekBar)ret.findViewById(R.id.grid_size_seekbar);

	  // get data from the grid angle
	  m_gridAngleText =
	    (TextView)ret.findViewById(R.id.grid_angle_text);
          m_gridAngleBar =
	    (SeekBar)ret.findViewById(R.id.grid_angle_seekbar);

	  // have to setup the stuff in the spinner so that there is
	  // something to display.
	  Spinner m_shapeSpinner = (Spinner) ret.findViewById(R.id.halftone_shape_spinner);
	  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
									       R.array.halftone_types_array,
									       R.layout.card_spinner_item);
	  adapter.setDropDownViewResource(R.layout.card_spinner_dropdown);
	  // Apply the adapter to the spinner
	  m_shapeSpinner.setAdapter(adapter);

	  m_shapeSpinner.setOnItemSelectedListener(new shapeSpinnerHandler());
          m_gridSizeBar.setOnSeekBarChangeListener(new gridSizeHandler());
	  m_gridAngleBar.setOnSeekBarChangeListener(new gridAngleHandler());

	  // make sure the default settings are sane.
	  setGridSize(0);
	  setGridAngle(0);

          // Inflate the layout for this fragment
	  return ret;
  }


  /**
   * This method should set the grid size for the halftoning when the
   * user changes it in the displayed fragment.
   * 
   * Note that the bar's minimum value is actually 0, this method is
   * where the minimum is added to make the range 5-100.
   *
   * @param	The new value of the grid size bar.
   */
  protected void setGridSize(int gridsize) {
    m_gridSize = gridsize+MIN_FILTER_GRIDSIZE;
    m_gridSizeText.setText(String.valueOf(gridsize+MIN_FILTER_GRIDSIZE));
  }

  /**
   * This method sets the grid angle, taking in an integer to set it
   * to. Also updates the text ui element to reflect the change.
   * 
   * @param gridangle The value, in degrees, to set the angle to.
   */
  protected void setGridAngle(int gridangle) {
    m_gridAngle = gridangle;
    m_gridAngleText.setText(String.valueOf(gridangle));
  }


  /**
   * Rotates the given bitmap around the center of the image by the
   * given angle (in degrees).
   * 
   * Note that the original image is recycled if the rotation is successful.
   * 
   * @param img The bitmap to rotate.
   * @param angle The angle to rotate image by.
   * @return 	The rotated image
   */
  protected Bitmap rotateImageCenter(Bitmap img, int angle) {
    if(angle == 0){
      return img;
    }
    Matrix m = new Matrix();
    m.setRotate(angle,
		img.getWidth()/2,
		img.getHeight());

    try {
      Bitmap newImg = Bitmap.createBitmap(
                                          img, 0, 0, img.getWidth(), img.getHeight(), m, true);
      if (img != newImg) {
        img.recycle();
        img = newImg;
      }
    } catch (OutOfMemoryError ex) {
      throw ex;
    }
    return img;
  }

  /**
   * Downscales an image by a given factor.
   * 
   * @param img The bitmap to scale
   * @param scaleFactor The amount to scale by.
   * @return 	The scaled image.
   */
  protected Bitmap downScaleImage(Bitmap img, int scaleFactor) {
    return Bitmap.createScaledBitmap(img, img.getWidth()/scaleFactor,
				     img.getHeight()/scaleFactor, true);
  }

  /**
   * Applies a halftone filter to the given image using the current
   * settings.
   * 
   * @param img The image to be halftoned.
   * @return	The halftoned image.
   */
  protected Bitmap halftoneImage(Bitmap img) {
    final int WIDTH = img.getWidth(), HEIGHT = img.getHeight();
    Bitmap halftoneImg = Bitmap.createBitmap(WIDTH*m_gridSize,
                                             HEIGHT*m_gridSize,
                                             Bitmap.Config.RGB_565);
    Canvas c = new Canvas(halftoneImg);
    Paint black = new Paint(),
      white = new Paint();
    black.setColor(Color.BLACK);
    white.setColor(Color.WHITE);
    int raw, red, green, blue;

    float totalValue, maxValue, averageValue;
    float dotRadius;

    c.drawRect(0, 0, halftoneImg.getWidth(),
	       halftoneImg.getHeight(), white);

    for (int x = 0; x < WIDTH; x++) {
      for (int y = 0; y < HEIGHT; y++) {
	raw = img.getPixel(x,y);

	red = Color.red(raw);
	green = Color.green(raw);
	blue = Color.blue(raw);

	totalValue = (0.3f * red + 0.59f * green + 0.11f * blue) / 255.0f;
	maxValue = 1.0f;


	//With this line, averageValue now represents "the
	//percentage of blackness in the m_gridSize square"
	averageValue = 100.0f * (1.0f - (totalValue / maxValue));

	//This function roughly maps out to making the area
	//of the dots equal to averageValue% of the area of
	//the m_gridSize square.
	dotRadius = (float) (Math.sqrt(averageValue + 4) - 2) * m_gridSize * 2 / 25;

	int xCenter = x*m_gridSize + m_gridSize / 2;
	int yCenter = y*m_gridSize + m_gridSize / 2;
	if (m_shapeType == SHAPE_CIRCLE) {
	  c.drawCircle(xCenter, yCenter,
                       dotRadius, black);
	} else {

  	  c.drawRect(xCenter - dotRadius,
                     yCenter - dotRadius,
		     xCenter + dotRadius,
                     yCenter + dotRadius,
		     black);
	}
      }
    }
    img.recycle();
    return halftoneImg;
  }

  /**
   * Removes any border around the image caused by rotating it.
   * 
   * @param img The image to be cropped.
   * @param w	The width that the image should be.
   * @param h	The height that the image should be.
   * @return	The cropped image.
   */
  
  protected Bitmap cutOutCenterBitmap(Bitmap img, int w, int h){
    int wDiff = img.getWidth() - w;
    int hDiff = img.getHeight() - h;
    if(wDiff == 0 && hDiff == 0) {
      return img;
    }
    Log.v("HaltoneFilter","wDiff: " + wDiff + " hDiff: " + hDiff);
    Log.v("HaltoneFilter","w: " + img.getWidth() + " h: " +
	  img.getHeight());
    if(wDiff < 0 || hDiff < 0) {
      Log.e("HalftoneFilter","One or more of the difference is < 0!");
      return img;
    }
    Bitmap retVal =  Bitmap.createBitmap(img,
                                         wDiff/2, hDiff/2,
                                         w,h);
    if(retVal == null) {
      Log.e("HalftoneFilter","Downscaling the image failed!");
      return img;
    }
    img.recycle();
    return retVal;
  }
  
  /**
   * This should apply the halftoning filter to the image by making
   * several threads that process each line(?) or block of the
   * image. Should be faster/smarter/better than the other basic implementation.
   *
   * @param	The image that the filter will be applied to.
   */
  @Override
  public void apply (Bitmap img) {
    if(img == null) {
      Log.w("HalftoneFilter","Trying to halftone a null image!");
      // so for this case, we just never call the callback. Genius!
      return;
    }

    int w = img.getWidth();
    int h = img.getHeight();
    int rotAngle = m_gridAngle;
    // diamond = square + 45 degrees
    if(m_shapeType == SHAPE_DIAMOND) {
      Log.v("HalftoneFilter","Angle increase 45 degrees!");
      rotAngle += 45;
    }


      Bitmap imgDownScaled = downScaleImage(img, m_gridSize);


    // Tertiary op is for when the shape selected is a diamond.
    // pretty much square halftoning, but rotated 45 degrees for a
    // "diamond" shape.
    Bitmap imgRotated = rotateImageCenter(imgDownScaled,
					  rotAngle);
    Bitmap imgHalftoned = halftoneImage(imgRotated);

    Bitmap imgRotated2 = rotateImageCenter(imgHalftoned,
					   -rotAngle);

    Bitmap imgCut = cutOutCenterBitmap(imgRotated2, w, h);

    m_parent.filterFinishedCallback(imgCut);
  }

  /**
   * This method is supposed to return a string to put into the
   * tabbar. Handy, since we actually need this.
   */
  @Override
  public String getFilterName() {
    return "Halftone";
  }

}
