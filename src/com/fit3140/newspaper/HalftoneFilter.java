package com.fit3140.newspaper;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * This class applies a halftoning filter to the passed in
 * image. Shouldn't be too complicated but should also be flexible
 * enough to allow for application of any angle of grid and any
 * halftoning image type.
 *
 */

class HalftoneFilter extends Filter implements OnSeekBarChangeListener
{
  // This is a setting for basic halftoning, other implementations
  // come later.
  private int m_gridSize;
  private SeekBar m_gridSizeBar;
  private TextView m_gridSizeText;


  // These are just here because they were easy to write.
  // Should give ideas on how to implement the more advance halftoning
  // later on. Uncomment so that they are actually defined when we
  // need them.
  //protected double m_gridAngle;
  //protected enum m_shapeType {CIRCLE, DIAMOND, RECTANGLE}

  @Override
  public View onCreateView(LayoutInflater inflater,
			   ViewGroup container,
			   Bundle savedInstanceState) {

    View ret = inflater.inflate(R.layout.fragment_filter_halftone,
				container,
				false);

    m_gridSizeText = (TextView)ret.findViewById(R.id.grid_size_text);
    m_gridSizeBar = (SeekBar)ret.findViewById(R.id.grid_size_seekbar);

    //Fixme
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
    return null;
  }
}
