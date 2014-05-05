package com.fit3140.newspaper;

import android.app.Fragment;
import android.graphics.Bitmap;

/**
 * This class is supposed to be a base for all filters made for the
 * processing of images. It would be smart to have a generic image
 * class to hold the image used in the application, but that can be
 * worried about later on.
 *
 */
abstract
  class Filter extends android.app.Fragment {
  /**
   * This method is supposed to take in an image and return the class
   * defined filter to it.
   * @arg img The image to apply the filter to.
   * @return image that has the defined filter to it.
   */
  public abstract Bitmap apply(Bitmap img);

}
