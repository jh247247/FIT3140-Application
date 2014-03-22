package com.jack.nowlayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.View;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.content.Context;
import android.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends Activity {
    private static final int ACTIVITY_SELECT_IMAGE=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Context context = getApplicationContext();
        LinearLayout container = (LinearLayout) findViewById(R.id.mainLayout);
        LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(context);
        View test = inflater.inflate(R.layout.card_action, null);
        container.addView(test);
        View test2 = inflater.inflate(R.layout.card_action, null);
        container.addView(test2);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
        case R.id.action_settings:
            Context ctx = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, "This is a test action!",
                                         duration);
            toast.show();
            return true;

        case R.id.action_select:
            Intent i = new Intent(Intent.ACTION_PICK,
                                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
        case ACTIVITY_SELECT_IMAGE      :
            if(resultCode == RESULT_OK){
		Context ctx = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(ctx, "The intent worked!",
					     duration);
		toast.show();
                //Uri selectedImage = imageReturnedIntent.getData();
                //InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                //Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
            }
        }
    }

    protected configureLoadCard(View)
}
