package com.fit3140.newspaper;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.app.FragmentManager;
import android.app.Fragment;
import android.util.Log;
import android.widget.TextView;
import android.view.Gravity;


import java.util.ArrayList;
/**
 * Image Viewer class for Old Newspaper App.
 * Displays images alongside the filtered images made from it.
 * 
 * @author 	<a href="mailto:jmhos3@student.monash.edu">Jack Hosemans</a>
 * 			<a href="mailto:tjpar4@student.monash.edu">Thomas Parasiuk</a>
 * @modified	May 2014
 */
public class ImageViewer extends FragmentStatePagerAdapter {
	/**
	 * Class that is used and displayed when there are no images in the
	 * viewer.
	 * 
	 * @author Jack Hosemans and Thomas Parasiuk
	 * @modified	May 2014
	 */
  public static class NoImages extends Fragment {
	  
    @Override
    public View onCreateView(LayoutInflater inflater,
			   ViewGroup container,
			   Bundle savedInstanceState) {
      TextView text =  new TextView(getActivity());
      text.setText("No Image");
      text.setGravity(Gravity.CENTER);
      Log.v("ImageViewer.NoImages", "Built view to display.");
      return text;
    }
  }

  ArrayList<Fragment> m_fragmentList;

  
  /**
   * Constructor for image viewers.
   */
  public ImageViewer(FragmentManager fm) {
    super(fm);

    m_fragmentList = new ArrayList<Fragment>();
  }

  
  /**
   * Gets the amount of image fragments stored by the image viewer.
   * 
   * @return	The amount of image fragments stored.
   */
  @Override
  public int getCount() {
    Log.v("ImageViewer","Have to get the count of the images");
    if(m_fragmentList.size() == 0) {
      Log.v("ImageViewer","Returning size 1 for dummy view");
      return 1;
    } else {
      Log.v("ImageViewer","Returning size: " + m_fragmentList.size());
      return m_fragmentList.size();
    }

  }

  /**
   * Gets the the image fragment at the given position.
   * 
   * @param position The position in the viewer to get the fragment from.
   * @return	The fragment at that position.
   */
  @Override
  public Fragment getItem(int position) {
    if(m_fragmentList.size() == 0){
      Log.v("ImageViewer","Returning no image text.");
      return new NoImages();
    } else {
      Log.v("ImageViewer","Returning position: " + position);
      return m_fragmentList.get(position);
    }
  }

  /**
   * Adds a new image to the viewer.
   * 
   * @param imgLoc The path of the image as a string.
   */
  
  public void addImage(String imgLoc) {
    m_fragmentList.add((Fragment)ImageFragment.newInstance(imgLoc));
    Log.v("ImageViewer.addImageFragment","size: " + m_fragmentList.size());
    notifyDataSetChanged();
  }

  /**
   * Clears all images from the viewer. Called when a new source image
   * is loaded in.
   */
  public void clearAllImages() {
    m_fragmentList = new ArrayList<Fragment>();
    notifyDataSetChanged();
  }
}
