package com.fit3140.newspaper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.graphics.Matrix;
import java.util.Arrays;

public class GaussianBlurFilter extends Filter {
  private static final int MIN_FILTER_RADIUS = 2;
  private static final int MIN_FILTER_SIGMA = 1;

  private static final int RED_MASK = 0xff0000;
  private static final int RED_MASK_SHIFT = 16;
  private static final int GREEN_MASK = 0x00ff00;
  private static final int GREEN_MASK_SHIFT = 8;
  private static final int BLUE_MASK = 0x0000ff;

  private static int m_radius;
  private SeekBar m_radiusBar;
  private TextView m_radiusText;

  private static int m_sigma;
  private SeekBar m_sigmaBar;
  private TextView m_sigmaText;


  // Parent typecasted to this so that we can actually call some stuff
  // on it.
  private Filter.FilterCallBack m_parent;

    private class radiusHandler implements OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar,
				  int progress,
				  boolean fromUser) {
      setRadius(progress);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

  }

  private class sigmaHandler implements OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar,
				  int progress,
				  boolean fromUser) {
      setSigma(progress);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }

  }

  /**
   * This method is called when the fragment is attached to an
   * activity. Fortunately the activity also passes in itself to
   * this. This means that we can override it and use the passed in
   * activity to attach a callback to the Activity.
   *
   */
  @Override
  public void onAttach(Activity activity) {
    // do whatever the inherited function is supposed to do.
    super.onAttach(activity);

    // try to typecast the Activity to the callback interface declared
    // in the Filter class. This gets called upon completion of the filtering.
    try {
      m_parent = (Filter.FilterCallBack) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() +
				   " must implement Filter.FilterCallBack");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
			   ViewGroup container,
			   Bundle savedInstanceState) {

    View ret = inflater.inflate(R.layout.fragment_filter_gaussian,
				container, false);

    // get data from the grid size
    m_radiusText =
      (TextView)ret.findViewById(R.id.radius_text);
    m_radiusBar =
      (SeekBar)ret.findViewById(R.id.radius_seekbar);

    // get data from the grid angle
    m_sigmaText =
      (TextView)ret.findViewById(R.id.sigma_text);
    m_sigmaBar =
      (SeekBar)ret.findViewById(R.id.sigma_seekbar);

    m_radiusBar.setOnSeekBarChangeListener(new radiusHandler());
    m_sigmaBar.setOnSeekBarChangeListener(new sigmaHandler());

    // make sure the default settings are sane.
    setRadius(0);
    setSigma(0);

    // Inflate the layout for this fragment
    return ret;
  }


  /**
   * Sets the blur radius with an offset.
   */

  protected void setRadius(int radius) {
    m_radius = radius + MIN_FILTER_RADIUS;
    m_radiusText.setText(String.valueOf(m_radius));
  }

  /**
   * Sets the std deviation of the kernel generation with an offset.
   */

  protected void setSigma(int sigma) {
    m_sigma = sigma + MIN_FILTER_SIGMA;
    m_sigmaText.setText(String.valueOf(m_sigma));
  }

  /**
   * This method generates a 1D gaussian kernel scaled appropriately
   * to be used for convolution in the filter. The sum of the kernel
   * must be 256.
   * @param radius of the filter
   * @return array of the gaussian kernel.
   */
  private int[] generateGaussianKernel(int radius, int sigma) {
    // have to generate the weights, the length of this is 2*radius+1.
    // weight has to be 256.
    double[] tmp = new double[2*radius+1];
    int[] ret =  new int[2*radius+1];
    double total = 0;
    for(int i = -radius; i <= radius; i++) {
      tmp[radius+i] = (double)(Math.exp(-(i*i)/(2*sigma*sigma))/Math.sqrt(2*Math.PI*sigma));
      total += tmp[radius+i];
    }
    int newTotal = 0;
    // scale so that the sum is roughly 256
    for(int i = 0; i < ret.length; i++) {
      ret[i] = (int)(tmp[i]/(total/256));
      newTotal += ret[i];
    }



      // try to fudge some values so that the sum of the array is
    // actually 256 otherwise shade of the image can change
    // just add/subtract the difference from the center value,
    // hopefully it won't change too much...
    // integer math is just so much faster though...
    ret[radius] += 256-newTotal;

    return ret;
  }

  // Note that the following code has been based off code from
  // the aosp project. http://goo.gl/fQE0t8
  /**
   * Convolves the input matrix with the filter. Transposes it and
   * puts it into the output array. Takes in the
   *
   */
  private void filterTranspose(int[] in, int[] out, int[] filter,
			  int radius,
			  int w,
			  int h) {
    int inPos = 0;
    int widthMask = w-1; // this has to be a power of 2.
    for (int y = 0; y < h; ++y) {
      // Cap the alpha value to the max. Not used.
      int alpha = 0xff;
      // Compute output values for the row.
      int outPos = y;
      for (int x = 0; x < w; ++x) {
	int red = 0;
	int green = 0;
	int blue = 0;
	// convolve for each pixel.
	for (int i = -radius; i <= radius; ++i) {
	  int argb = in[inPos + (widthMask & (x + i))];
	  int weight = filter[i+radius];
	  red += weight *((argb & RED_MASK) >> RED_MASK_SHIFT);
	  green += weight *((argb & GREEN_MASK) >> GREEN_MASK_SHIFT);
	  blue += weight *(argb & BLUE_MASK);
	}
	// Output the current pixel.
	out[outPos] = (alpha << 24) | ((red >> 8) << RED_MASK_SHIFT)
	  | ((green >> 8) << GREEN_MASK_SHIFT)
	  | (blue >> 8);
	outPos += h;
      }
      inPos += w;
    }
  }

  /**
   * Rounds a given number up to the next power of 2.
   * @param in "normal" number to round up.
   * @return number rounded up to the next power of 2.
   */
  private int roundToPowerOf2(int in) {
    int out = 1;
    while(out < in) {
      out = out << 1;
    }
    return out;
  }

  public void apply (Bitmap img) {
    if(img == null) {
      Log.w("GaussianBlurFilter","Trying to halftone a null image!");
      // so for // TODO: his case, we just never call the callback. Genius!
      return;
    }

    int w = img.getWidth();
    int h = img.getHeight();
    // only have to scale width
    int scaleW = roundToPowerOf2(w);
    int scaleH = roundToPowerOf2(h);
    Log.v("GuassianHalftone","Original image dims: " + w + " x " + h);
    Log.v("GuassianHalftone","Scaled image dims: " + scaleW + " x " + scaleH);


    Bitmap out = Bitmap.createScaledBitmap(img,
					   scaleW,
					   scaleH,
					   false);

    // image is up to twice as big. Have to recycle to have any hope
    // of continuing.
    img.recycle();

    // get the array of integers to speed things up later.
    int[] in = new int[scaleW*scaleH];
    int[] tmp = new int[scaleW*scaleH];
    out.getPixels(in, 0, scaleW, 0, 0, scaleW, scaleH);

    int[] kernel = generateGaussianKernel(m_radius, m_sigma);
    Log.v("GuassianHalftone","Guassian kernel: " + Arrays.toString(kernel));

    // Fun fact, gaussian blur is actually a nice seperable
    // function. That means that we can potentially multithread this
    // too if needed.
    filterTranspose(in, tmp, kernel, m_radius, scaleW, scaleH);
    filterTranspose(tmp, in, kernel, m_radius, scaleH, scaleW);
    out.recycle();
    Bitmap tmpBitmap = Bitmap.createBitmap(in, scaleW, scaleH, Bitmap.Config.ARGB_8888);

    out = Bitmap.createScaledBitmap(tmpBitmap, w,h, false);
    m_parent.filterFinishedCallback(out);

  }

  /**
   * This method is supposed to return a string to put into the
   * tabbar. Handy, since we actually need this.
   *
   */
  @Override
  public String getFilterName() {
    return "Gaussian Blur";
  }

}
