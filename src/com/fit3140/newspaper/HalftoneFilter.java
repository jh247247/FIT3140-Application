package com.fit3140.newspaper;

import android.app.Activity;
import android.app.Fragment;
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

/**
 * This class applies a halftoning filter to the passed in
 * image. Shouldn't be too complicated but should also be flexible
 * enough to allow for application of any angle of grid and any
 * halftoning image type.
 *
 */

public class HalftoneFilter extends Filter implements OnSeekBarChangeListener
{
  // This is a setting for basic halftoning, other implementations
  // come later.
  private int m_gridSize;
  private SeekBar m_gridSizeBar;
  private TextView m_gridSizeText;

  // Parent typecasted to this so that we can actually call some stuff
  // on it.
  private Filter.FilterCallBack m_parent;


  // These are just here because they were easy to write.
  // Should give ideas on how to implement the more advance halftoning
  // later on. Uncomment so that they are actually defined when we
  // need them.
  //protected double m_gridAngle;
  //protected enum m_shapeType {CIRCLE, DIAMOND, RECTANGLE}

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

	  m_gridSizeText = (TextView)ret.findViewById(R.id.grid_size_text);
	  m_gridSizeBar = (SeekBar)ret.findViewById(R.id.grid_size_seekbar);

	  //FIXME
	  m_gridSizeText.setText("0");
	  m_gridSizeBar.setOnSeekBarChangeListener(this);

	  // Inflate the layout for this fragment
	  return ret;
  }


  /**
   * This method should set the grid size for the halftoning when the
   * user changes it in the displayed fragment.
   *
   */
  protected void setGridSize(int gridsize) {
    m_gridSize = gridsize;
    m_gridSizeText.setText(String.valueOf(gridsize));
  }

  /**
   * This gets called whenever any child seekbar gets changed.
   *
   */
  @Override
    public void onProgressChanged(SeekBar seekBar,
				  int progress,
				  boolean fromUser) {
    // currently placeholder. Can be changed.
    setGridSize(progress);
  }

  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  public void onStopTrackingTouch(SeekBar seekBar) {

  }

  /**
   * This should apply the halftoning filter to the image by making
   * several threads that process each line(?) or block of the
   * image. Should be faster/smarter/better than the other basic implementation.
   *
   */
  @Override
  public Bitmap apply (Bitmap img) {
    final int WIDTH = img.getWidth(), HEIGHT = img.getHeight();

    Bitmap halftoneImg = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(halftoneImg);
    Paint black = new Paint(),
      white = new Paint();
    black.setColor(Color.BLACK);
    white.setColor(Color.WHITE);
    int[] pixels = new int[m_gridSize * m_gridSize];
    int red, green, blue;

    float totalValue, maxValue, averageValue;
    float dotRadius;

    c.drawRect(0, 0, WIDTH, HEIGHT, white);

    for (int x = 0; x < WIDTH; x += m_gridSize) {
      for (int y = 0; y < HEIGHT; y += m_gridSize) {
	totalValue = 0;
	maxValue = 0;

	img.getPixels(pixels, 0, m_gridSize, x, y,
		      Math.min(m_gridSize, WIDTH - x), Math.min(m_gridSize, HEIGHT - y));
	for (int i = 0; i < pixels.length; i++) {
	  red = Color.red(pixels[i]);
	  green = Color.green(pixels[i]);
	  blue = Color.blue(pixels[i]);
	  totalValue += (0.3f * red + 0.59f * green + 0.11f * blue) / 255.0f;
	  maxValue += 1.0f;
	}

	//With this line, averageValue now represents "the
	//percentage of blackness in the m_gridSize square"
	averageValue = 100.0f * (1.0f - (totalValue / maxValue));

	//This function roughly maps out to making the area
	//of the dots equal to averageValue% of the area of
	//the m_gridSize square.
	dotRadius = (float) (Math.sqrt(averageValue + 8) - 2) * m_gridSize * 2 / 25;
	if (averageValue > 90.0f) {
	  dotRadius += (float) Math.pow(averageValue - 90.0f, 2) / 100;
	}

	c.drawCircle(x + m_gridSize / 2, y + m_gridSize / 2, dotRadius, black);
      }
    }

    return halftoneImg;
  }
}
