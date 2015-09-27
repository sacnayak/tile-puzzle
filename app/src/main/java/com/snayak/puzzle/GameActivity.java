package com.snayak.puzzle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public int currScore = 0;
    public int topScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Declaring the onClick listener for all the tiles
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set listeners for all the TextViews in the Grid
                ImageView imgView = (ImageView) view;
                //skip listener if empty cell is touched
                //TODO fix this to the right logic
                Bitmap currentBitMap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                Bitmap emptyBitmap = Bitmap.createBitmap(currentBitMap.getWidth(), currentBitMap.getHeight(), Bitmap.Config.ARGB_8888);
                if (!currentBitMap.sameAs(emptyBitmap)) {
                    // myBitmap is not empty/blank
                    assess(imgView.getId());
                }
            }
        };

        //build the grid of images
        final GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        List<Bitmap> bitmapList = createImageTiles(R.drawable.duck);

        Random randomNumberGenerator = new Random();
        int n = 9;

        for(int i=0;i<9;i++) {
            ImageView imgView = new ImageView(this);
            imgView.setId(i);
            //set the image bitmap
            imgView.setTag(R.drawable.duck, i);
            //generate a random number from [0, 9)
            int randomNum = randomNumberGenerator.nextInt(n);
            imgView.setImageBitmap(bitmapList.get(randomNum));
            bitmapList.remove(randomNum);
            n--;

            imgView.setPadding(pixelToDp(2), pixelToDp(2), pixelToDp(2), pixelToDp(2));
            imgView.setLayoutParams(new ViewGroup.LayoutParams(pixelToDp(100), pixelToDp(100)));
            imgView.setOnClickListener(listener);

            gridLayout.addView(imgView);
        }


        //generate a random number from [0, 9)
        int randomNum = randomNumberGenerator.nextInt(9);

        //set text to empty
        ImageView emptyCell = (ImageView) gridLayout.getChildAt(randomNum);
        emptyCell.setImageBitmap(Bitmap.createBitmap(pixelToDp(100), pixelToDp(100), Bitmap.Config.ARGB_8888));

        TextView best_score = (TextView) findViewById(R.id.best_score_num);
        TextView score = (TextView) findViewById(R.id.current_score_num);
        score.setText(this.currScore + "");
        best_score.setText(this.topScore + "");

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
     * @param imgResource is the resource identifier that
     *                 should be passed in
     * @return List<Bitmap>>
     */
    private List<Bitmap> createImageTiles(int imgResource) {
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();
        //create a bitmap out of the image
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgResource);

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
            ImageView imgView = (ImageView) gridLayout.getChildAt(i);
            if(imgView.getId() == id) {
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
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if(destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        if(y+1<3) {
            //bottom element
            int destIndex = x + 3*(y+1);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if(destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        if(x-1 >= 0) {
            //left element
            int destIndex = (x-1) + 3*(y);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if(destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        if(x+1 < 3) {
            //right element
            int destIndex = (x+1) + 3*(y);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if(destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        boolean showToast = true;
        //Toast Message if win
        for(int i=0;i<9;i++) {
            ImageView imgView = (ImageView) gridLayout.getChildAt(i);
            if((int) imgView.getTag(R.drawable.duck) == i) {
                showToast = false;
                break;
            }
        }
        if(showToast) {
            Toast.makeText(GameActivity.this, "Congratulations! You have won! Click New Game " +
                    "to Play another one", Toast.LENGTH_SHORT).show();
        }
    }

    public void animate(int srcIndex, int destIndex) {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);

        ImageView imgViewSrc = (ImageView) gridLayout.getChildAt(srcIndex);
        ImageView imgViewDest = (ImageView) gridLayout.getChildAt(destIndex);

        Bitmap srcBitmap = ((BitmapDrawable) imgViewSrc.getDrawable()).getBitmap();
        Bitmap emptyBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        imgViewDest.setImageBitmap(srcBitmap);
        imgViewSrc.setImageBitmap(emptyBitmap);

        //update the scores
        this.currScore++;
        TextView best_score = (TextView) findViewById(R.id.best_score_num);
        TextView score = (TextView) findViewById(R.id.current_score_num);
        score.setText(this.currScore + "");
        best_score.setText(this.topScore + "");
    }

}
