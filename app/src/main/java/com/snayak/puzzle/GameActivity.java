package com.snayak.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        final GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set listeners for all the TextViews in the Grid
                TextView textView = (TextView) view;
                //skip listener if empty cell is touched
                if(textView.getText() != "") {
                    assess(view.getId());
                }
            }
        };

        for(int i=0;i<9;i++) {
            TextView textView = new TextView(this);
            textView.setId(i);
            textView.setText("Cell " + i);
            textView.setPadding(pixelToDp(30), pixelToDp(30), pixelToDp(30), pixelToDp(30));
            textView.setTextSize(14);
            textView.setLayoutParams(new ViewGroup.LayoutParams(pixelToDp(100), pixelToDp(100)));
            textView.setOnClickListener(listener);
            gridLayout.addView(textView);
        }
        //generate a random number from [0, 9)
        Random randomNumberGenerator = new Random();
        int randomNum = randomNumberGenerator.nextInt(9);

        //set text to empty
        TextView emptyTextView = (TextView) gridLayout.getChildAt(randomNum);
        emptyTextView.setText("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param drawable is the resource identifier that
     *                 should be passed in
     * @return List<Bitmap>>
     */
    private List<Bitmap> createImageTiles(int drawable) {
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();
        //create a bitmap out of the image
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);

        //create scaled bitmap according to size of layout
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

        //create the tiles in the order of L to R
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 0, 0, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 100, 0, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 200, 0, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 0, 100, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 100, 100, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 200, 100, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 0, 200, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 100, 200, 100, 100));
        bitmapList.add(Bitmap.createBitmap(scaledBitmap, 200, 200, 100, 100));

        /*
         * return tiles with 1d indices to matrix scale below
         * [0,1,2
         *  3,4,5
         *  6,7,8]
         */
        return bitmapList;
    }

    private int pixelToDp(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    /**
     * check if the cell clicked is valid
     * @param id
     * @return
     */
    public void assess(int id) {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);

        int index = 0;
        for(int i=0;i<9;i++) {
            TextView textView = (TextView) gridLayout.getChildAt(i);
            if(textView.getId() == id) {
                index = i;
                break;
            }
        }

        int x = index % 3;
        int y = index / 3;

        Log.d("GameActivity.java", "Co-ordinates : " + x  + ", "+ y);

        //Check adjacent cells for empty slots
        if(y-1 >= 0) {
            //top element
            int destIndex = x + 3*(y-1);
            TextView textView = (TextView) gridLayout.getChildAt(destIndex);
            if(textView.getText() == "") {
                animate(index, destIndex);
            }
        }

        if(y+1<3) {
            //bottom element
            int destIndex = x + 3*(y+1);
            TextView textView = (TextView) gridLayout.getChildAt(destIndex);
            if(textView.getText() == "") {
                animate(index, destIndex);
            }
        }

        if(x-1 >= 0) {
            //left element
            int destIndex = (x-1) + 3*(y);
            TextView textView = (TextView) gridLayout.getChildAt(destIndex);
            if(textView.getText() == "") {
                animate(index, destIndex);
            }
        }

        if(x+1 < 3) {
            //right element
            int destIndex = (x+1) + 3*(y);
            TextView textView = (TextView) gridLayout.getChildAt(destIndex);
            if(textView.getText() == "") {
                animate(index, destIndex);
            }
        }

    }

    public void animate(int srcIndex, int destIndex) {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);

        TextView textViewSrc = (TextView) gridLayout.getChildAt(srcIndex);
        TextView textViewDest = (TextView) gridLayout.getChildAt(destIndex);

        textViewDest.setText(textViewSrc.getText());
        textViewSrc.setText("");

    }

}
