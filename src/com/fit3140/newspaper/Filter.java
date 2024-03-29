package com.fit3140.newspaper;

import android.graphics.Bitmap;

/**
 * This class is supposed to be a base for all filters made for the
 * processing of images. It would be smart to have a generic image
 * class to hold the image used in the application, but that can be
 * worried about later on.
 *
 * @author 	<a href="mailto:jmhos3@student.monash.edu">Jack Hosemans</a>
 * 			<a href="mailto:tjpar4@student.monash.edu">Thomas Parasiuk</a>
 * @modified	May 2014
 */
public abstract
  class Filter extends android.app.Fragment {
  /**
   * This is a class that the parent is typecasted to. Mainly it is
   * used for saying that we finished filtering an image.
   */
  public interface FilterCallBack {
    /**
     * This is the only thing from the parent that this child is
     * supposed to have access to.
     */
    public void filterFinishedCallback(Bitmap filteredImage);
  }

  /**
   * This method is supposed to take in an image and return the class
   * defined filter to it.
   * 
   * @param img The image to apply the filter to.
   */
  public abstract void apply(Bitmap img);

  /**
   * This method is supposed to return the filter name.
   * Should we worry about different languages?
   */

  public abstract String getFilterName();
}
