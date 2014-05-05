package com.fit3140.halftoneApp;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * This class applies a halftoning filter to the passed in
 * image. Shouldn't be too complicated but should also be flexible
 * enough to allow for application of any angle of grid and any
 * halftoning image type.
 *
 */

class HalftoneFilter extends Filter{
  @Override
  public View onCreateView(LayoutInflater inflater,
			   ViewGroup container,
			   Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    //return inflater.inflate(R.layout.example_fragment,
    //container,
    //false);
    return null; // TODO
  }
  /**
   * This method should set the grid size for the halftoning when the
   * user changes it in the displayed fragment.
   *
   */
  protected void setGridSize(int gridsize) {
    // TODO
  }

  @Override
  public Bitmap apply (Bitmap img) {
    return null;
  }
}
