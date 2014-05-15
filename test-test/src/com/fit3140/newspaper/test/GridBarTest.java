/**
 * Grid bar tests.
 */

package com.fit3140.newspaper.test;

import com.fit3140.newspaper.MainActivity;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class GridBarTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private SeekBar gridbar;
	private TextView gridtext;
	private MainActivity main;
	
	public GridBarTest() {
		super(MainActivity.class);
	}

	protected void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			Log.w("GridBarTest", "setUp threw exception: " + e);
			e.printStackTrace();
		}
		
		main = getActivity();
		gridbar = (SeekBar) main.findViewById(
				com.fit3140.newspaper.R.id.grid_size_seekbar);
		gridtext = (TextView) main.findViewById(
				com.fit3140.newspaper.R.id.grid_size_text);
	}
	
	public void testGridbar() {
		//Touch center of bar
		TouchUtils.tapView(this, gridbar);
		//Show this message if this condition does not evaluate to TRUE.
		assertTrue("grid_size_text does not update correctly when grid_size_seekbar is tapped.", 
				(gridtext.getText().equals("54")));
	}
	
	public void testOrientation() {
		//Same thing but in landscape mode.
		main.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		TouchUtils.clickView(this,  gridbar);
		assertTrue("grid_size_text does not update correctly in landscape mode.",
				(gridtext.getText().equals("54")));
	}

}
