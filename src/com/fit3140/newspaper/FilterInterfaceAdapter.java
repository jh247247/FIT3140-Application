package com.fit3140.newspaper;

import android.support.v13.app.FragmentPagerAdapter;
import android.app.FragmentManager;
import android.app.Fragment;
import android.util.Log;


import java.util.ArrayList;

public class FilterInterfaceAdapter extends FragmentPagerAdapter {
  ArrayList<Fragment> m_fragmentList;

  public FilterInterfaceAdapter(FragmentManager fm) {
    super(fm);

    m_fragmentList = new ArrayList<Fragment>();
    // have to add fragments...
    m_fragmentList.add(new HalftoneFilter());
    m_fragmentList.add(new CaptionFilter());

  }

  @Override
  public int getCount() {
    return m_fragmentList.size();
  }

  @Override
  public Fragment getItem(int position) {
    return m_fragmentList.get(position);
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return ((Filter)m_fragmentList.get(position)).getFilterName();
  }
}
