package com.fit3140.newspaper;

import android.support.v13.app.FragmentPagerAdapter;
import android.app.FragmentManager;
import android.app.Fragment;

import java.util.ArrayList;

/**
 * Handles all of the filter subclasses to display them in side by side
 * in a pager in the app.
 * 
 * @author 	<a href="mailto:jmhos3@student.monash.edu">Jack Hosemans</a>
 * 			<a href="mailto:tjpar4@student.monash.edu">Thomas Parasiuk</a>
 * @modified	May 2014
 */
public class FilterInterfaceAdapter extends FragmentPagerAdapter {
  ArrayList<Fragment> m_fragmentList;

  /**
   * Constructor for FilterInterfaceAdapter.
   * Adds in all the filter fragments currently supported by the application.
   */
  public FilterInterfaceAdapter(FragmentManager fm) {
    super(fm);

    m_fragmentList = new ArrayList<Fragment>();
    // have to add fragments...
    m_fragmentList.add(new HalftoneFilter());
    m_fragmentList.add(new CaptionFilter());
    m_fragmentList.add(new NegativeFilter());
    m_fragmentList.add(new GaussianBlurFilter());

  }

  /**
   * Returns the amount of filters in the list.
   */
  @Override
  public int getCount() {
    return m_fragmentList.size();
  }

  /**
   * Returns the filter fragment at the given position.
   * 
   * @param	The given position.
   */
  @Override
  public Fragment getItem(int position) {
    return m_fragmentList.get(position);
  }

  /**
   * Returns the name of the fragment at the given position.
   * 
   * @param	The given position.
   */
  @Override
  public CharSequence getPageTitle(int position) {
    return ((Filter)m_fragmentList.get(position)).getFilterName();
  }
}
