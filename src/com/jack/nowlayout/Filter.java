package com.fit3140.halftoneApp;

import Android.app.Fragment;
import Android.graphics.Bitmap;

class Filter extends Android.app.Fragment {
  /**
   * This method is supposed to take in an image and return the class
   * defined filter to it.
   * @arg img The image to apply the filter to.
   * @return image that has the defined filter to it.
   */
  public abstract Bitmap apply(Bitmap img);

}
