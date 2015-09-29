package com.snayak.puzzle;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    int imageForPuzzle = R.drawable.duck;//default

    int[] imageIds = {
            R.drawable.duck,
            R.drawable.grumpycat,
            R.drawable.puss_boots,
            R.drawable.gollum
    };

    public int currScore = 0;
    //public int topScore = 0;

    private Handler handler = new Handler();

    //initialize to a wrong index to catch errors on first click
    public int last_tile_index = 10;

    View.OnClickListener lastMoveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showLastMoveHighLight();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Called in the same thread as where the handler was created.
                    //If the handler was created in main thread, then
                    //we can safely update the GUI from here.
                    removeHighlight();
                }
            }, 1000);

        }
    };

    View.OnClickListener onChangeImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeImage();
        }
    };

    public void changeImage(){
        Random generator = new Random();
        int randomImageId = this.imageForPuzzle;
        do {
            randomImageId = imageIds[generator.nextInt(imageIds.length)];
        } while(randomImageId == this.imageForPuzzle);
        //set the new random image as long as it is not the previous one
        this.imageForPuzzle = randomImageId;
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        buildGrid(gridLayout);
        resetScore();
    }


    //Declaring the onClick listener for all the tiles
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // set listeners for all the TextViews in the Grid
            ImageView imgView = (ImageView) view;
            //skip listener if empty cell is touched
            Bitmap currentBitMap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(currentBitMap.getWidth(), currentBitMap.getHeight(), Bitmap.Config.ARGB_8888);
            if (!currentBitMap.sameAs(emptyBitmap)) {
                // myBitmap is not empty/blank
                assess(imgView.getId());
            }
        }
    };


    //Declaring the onClick listener for the New Game Button
    View.OnClickListener newGamelistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
            buildGrid(gridLayout);
            //updateScores();
            //TextView best_score = (TextView) findViewById(R.id.best_score_num);
            //reset scores
            resetScore();
        }
    };

    public void resetScore() {
        //TextView best_score = (TextView) findViewById(R.id.best_score_num);
        TextView score = (TextView) findViewById(R.id.current_score_num);
        this.currScore = 0;
        //this.topScore = 0;
        score.setText(this.currScore + "");
        //best_score.setText(this.topScore + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //this.topScore = 0;

        //build the grid of images
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        Random randomNumberGenerator = new Random();

        buildGrid(gridLayout);


        //TextView best_score = (TextView) findViewById(R.id.best_score_num);
        TextView score = (TextView) findViewById(R.id.current_score_num);
        score.setText(this.currScore + "");
        //best_score.setText(this.topScore + "");

        //set listener on the new game button
        Button newGameButton = (Button) findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(newGamelistener);

        //set listener on the button
        Button lastMoveButton = (Button) findViewById(R.id.last_move_button);
        lastMoveButton.setOnClickListener(lastMoveListener);

        //set listener on the buttonchange_image_button
        Button changeImageButton = (Button) findViewById(R.id.change_image_button);
        changeImageButton.setOnClickListener(onChangeImageListener);
    }


    /**
     *
     * @param layout
     */
    public void buildGrid(GridLayout layout) {
        //clear old tiles if any
        layout.removeAllViews();

        Random randomNumberGenerator = new Random();

        this.currScore = 0;

        //build the grid of images
        final GridLayout gridLayout = layout;

        List<Bitmap> bitmapList = createImageTiles(imageForPuzzle, layout);

        int n = 9;

        for (int i = 0; i < 9; i++) {
            ImageView imgView = new ImageView(this);
            imgView.setId(i);
            //set the image bitmap
            imgView.setTag(imageForPuzzle, i);
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

        //set image to empty
        ImageView emptyCell = (ImageView) gridLayout.getChildAt(randomNum);

        emptyCell.setImageBitmap(Bitmap.createBitmap(pixelToDp(100),
                pixelToDp(100), Bitmap.Config.ARGB_8888));
    }


    @Override
    public void onStart() {
        super.onStart();

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
     * @param imgResource is the resource identifier that
     *                    should be passed in
     * @return List<Bitmap>>
     */
    private List<Bitmap> createImageTiles(int imgResource, GridLayout layout) {
        List<Bitmap> bitmapList = new ArrayList<Bitmap>();

        int height = layout.getLayoutParams().height;
        int width = layout.getLayoutParams().height;


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
     *
     * @param id
     * @return
     */
    public void assess(int id) {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);

        int index = 0;
        for (int i = 0; i < 9; i++) {
            ImageView imgView = (ImageView) gridLayout.getChildAt(i);
            if (imgView.getId() == id) {
                index = i;
                break;
            }
        }

        int x = index % 3;
        int y = index / 3;

        Log.d("GameActivity.java", "Co-ordinates : " + x + ", " + y);

        //Check adjacent cells for empty slots
        if (y - 1 >= 0) {
            //top element
            int destIndex = x + 3 * (y - 1);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if (destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        if (y + 1 < 3) {
            //bottom element
            int destIndex = x + 3 * (y + 1);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if (destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        if (x - 1 >= 0) {
            //left element
            int destIndex = (x - 1) + 3 * (y);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if (destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        if (x + 1 < 3) {
            //right element
            int destIndex = (x + 1) + 3 * (y);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if (destBitmap.sameAs(emptyBitmap)) {
                animate(index, destIndex);
            }
        }

        boolean showToast = true;
        //Toast Message if win
        for (int i = 0; i < 9; i++) {
            ImageView imgView = (ImageView) gridLayout.getChildAt(i);
            if ((int) imgView.getTag(imageForPuzzle) == i) {
                showToast = false;
                break;
            }
        }
        if (showToast) {
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
        //TextView best_score = (TextView) findViewById(R.id.best_score_num);
        TextView score = (TextView) findViewById(R.id.current_score_num);
        score.setText(this.currScore + "");
        //best_score.setText(this.topScore + "");

        //update last_tile_index with source index
        last_tile_index = destIndex;
    }

    public void showLastMoveHighLight() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        //check if last tile index is less than 3x3 indices, else show toast
        if(this.last_tile_index < 8) {
            ImageView imgView = (ImageView) gridLayout.getChildAt(last_tile_index);
            imgView.setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);
        } else {
            Toast.makeText(GameActivity.this, "You haven't made a move yet!", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeHighlight() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        if(this.last_tile_index < 8) {
            ImageView imgView = (ImageView) gridLayout.getChildAt(last_tile_index);
            imgView.setColorFilter(Color.TRANSPARENT);
        }
    }


}
