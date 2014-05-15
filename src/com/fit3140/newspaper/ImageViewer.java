package com.fit3140.newspaper;

import android.graphics.Bitmap;
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

public class ImageViewer extends FragmentStatePagerAdapter {
  private class NoImages extends Fragment {
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

  public ImageViewer(FragmentManager fm) {
    super(fm);

    m_fragmentList = new ArrayList<Fragment>();
  }

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

  public void addImage(String imgLoc) {
    m_fragmentList.add((Fragment)Image.newInstance(imgLoc));
    Log.v("ImageViewer.addImage","size: " + m_fragmentList.size());
    notifyDataSetChanged();
  }
}
