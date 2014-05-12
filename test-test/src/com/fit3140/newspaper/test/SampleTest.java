/**
 * Sample test to show how jUnit works.
 * 
 * (The following is a note for Jack, burn after readin- err, delete it before
 * we submit the app once you read it)
 * 
 * Tests are created like this:
 * You make a constructor and a setup function like the ones here.
 * I don't really know what the constructor does precisely, but whatever.
 * setUp is run before every test. If a group of tests are so closely related
 * that you can set them up in a very similar way, put them in the same junit
 * class/file. Otherwise, put them in seperate files.
 * Every other method is a test that should have minimal extra setup and then
 * assertTrue a few things that are being tested.
 * 
 * Tests are ran like this:
 * Uhhh, go in to eclipse, click "Run" -> "Debug As" -> "Android jUnit Test".
 * You're going to have to look up how to do it on your thing, probably.
 * 
 * I'll look in to ways of making more tests, right now all I know is how to
 * touch the center of things.
 */

package com.fit3140.newspaper.test;

import com.fit3140.newspaper.MainActivity;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class SampleTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private SeekBar gridbar;
	private TextView gridtext;
	private MainActivity main;
	
	public SampleTest() {
		super(MainActivity.class);
	}

	protected void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			Log.w("SampleTest", "setUp threw exception: " + e);
			e.printStackTrace();
		}
		
		//A kinda janky example of how to get one particular thing in the
		//application and access it during testing.
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
		assertTrue("grid_size_text did not show 51.", 
				(gridtext.getText().equals("51")));
	}
	
	public void testOrientation() {
		//Same thing but in landscape mode.
		main.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		TouchUtils.clickView(this,  gridbar);
		assertTrue("grid_size_text did not tap correctly in landscape mode.",
				(gridtext.getText().equals("51")));
	}

}
