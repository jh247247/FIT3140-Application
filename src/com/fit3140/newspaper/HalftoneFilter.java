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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.graphics.Matrix;

/**
 * This class applies a halftoning filter to the passed in
 * image. Shouldn't be too complicated but should also be flexible
 * enough to allow for application of any angle of grid and any
 * halftoning image type.
 *
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

  // Parent typecasted to this so that we can actually call some stuff
  // on it.
  private Filter.FilterCallBack m_parent;


  // These are just here because they were easy to write.
  // Should give ideas on how to implement the more advance halftoning
  // later on. Uncomment so that they are actually defined when we
  // need them.
  //protected double m_gridAngle;
  //protected enum m_shapeType {CIRCLE, DIAMOND, RECTANGLE}


  private class gridSizeHandler implements OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar,
				  int progress,
				  boolean fromUser) {
      setGridSize(progress);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

  }

  private class gridAngleHandler implements OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar,
				  int progress,
				  boolean fromUser) {
      setGridAngle(progress);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

  }

  /**
   * This method is called when the fragment is attached to an
   * activity. Fortunately the activity also passes in itself to
   * this. This means that we can override it and use the passed in
   * activity to attach a callback to the Activity.
   *
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

  @Override
  public View onCreateView(LayoutInflater inflater,
			   ViewGroup container,
			   Bundle savedInstanceState) {

	  View ret = inflater.inflate(R.layout.fragment_filter_halftone,
			  container, false);

	  m_gridSizeText =
	    (TextView)ret.findViewById(R.id.grid_size_text);
          m_gridSizeBar =
	    (SeekBar)ret.findViewById(R.id.grid_size_seekbar);

	  m_gridAngleText =
	    (TextView)ret.findViewById(R.id.grid_angle_text);
          m_gridAngleBar =
	    (SeekBar)ret.findViewById(R.id.grid_angle_seekbar);


          m_gridSizeBar.setOnSeekBarChangeListener(new gridSizeHandler());
	  m_gridAngleBar.setOnSeekBarChangeListener(new
	  gridAngleHandler());

	  // make sure the default settings are sane.
	  setGridSize(0);
	  setGridAngle(0);

          // Inflate the layout for this fragment
	  return ret;
  }


  /**
2   * This method should set the grid size for the halftoning when the
   * user changes it in the displayed fragment.
   *
   */
  protected void setGridSize(int gridsize) {
    m_gridSize = gridsize+MIN_FILTER_GRIDSIZE;
    m_gridSizeText.setText(String.valueOf(gridsize+MIN_FILTER_GRIDSIZE));
  }

  protected void setGridAngle(int gridangle) {
    m_gridAngle = gridangle;
    m_gridAngleText.setText(String.valueOf(gridangle));
  }
  protected Bitmap rotateImageAround(Bitmap img, int angle, int x, int y) {
    if(angle == 0){
      return img;
    }
    Matrix m = new Matrix();
    m.setRotate(angle,x,y);

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
  protected Bitmap rotateImageCenter(Bitmap img, int angle) {
    return rotateImageAround(img, angle,
			     img.getWidth()/2,
			     img.getHeight());
  }

  protected Bitmap downScaleImage(Bitmap img, int scaleFactor) {
    return Bitmap.createScaledBitmap(img, img.getWidth()/scaleFactor,
				     img.getHeight()/scaleFactor, true);
  }

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

	c.drawCircle(x*m_gridSize + m_gridSize / 2,
		     y*m_gridSize + m_gridSize / 2,
		     dotRadius, black);
      }
    }
    img.recycle();
    return halftoneImg;
  }

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


    // recycle refs to save memory.
    Bitmap imgDownScaled = downScaleImage(img, m_gridSize);


    Bitmap imgRotated = rotateImageCenter(imgDownScaled, m_gridAngle);
    Bitmap imgHalftoned = halftoneImage(imgRotated);

    Bitmap imgRotated2 = rotateImageCenter(imgHalftoned, -m_gridAngle);

    Bitmap imgCut = cutOutCenterBitmap(imgRotated2, w, h);

    m_parent.filterFinishedCallback(imgCut);
  }

  /**
   * This method is supposed to return a string to put into the
   * tabbar. Handy, since we actually need this.
   *
   */
  @Override
  public String getFilterName() {
    return "Halftone";
  }

}
