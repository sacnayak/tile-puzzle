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

/**
 * Class for 3x3 tile-puzzle game
 *
 * @author snayak
 */
public class GameActivity extends AppCompatActivity {

    int imageForPuzzle = R.drawable.duck;//default image on-start of game

    public int currScore = 0;//the game score maintained at the activity level

    //initialize to a wrong index to catch error if Last Move was invoked on first click
    public int last_tile_index = 10;

    int[] drawableImageIds = {
            R.drawable.duck,
            R.drawable.grumpycat,
            R.drawable.puss_boots,
            R.drawable.gollum,
            R.drawable.got
    };//List of drawable id's available for display

    //Schedule a runnable to highlight cell for one second
    private Handler handler = new Handler();

    //Declaring listener for the Last Move button
    View.OnClickListener lastMoveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showLastMoveHighLight();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //remove the highlight after the timer second
                    removeHighlight();
                }
            }, 1000);

        }
    };

    //Declaring the onChangeListener for the Change Image button
    View.OnClickListener onChangeImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeGridImage();
        }
    };

    /**
     * Summary: Change the puzzle background image, and restart the game.
     * Invoked from the change image button click
     */
    public void changeGridImage() {
        Random generator = new Random();
        int randomImageId = this.imageForPuzzle;
        do {
            randomImageId = drawableImageIds[generator.nextInt(drawableImageIds.length)];
        } while (randomImageId == this.imageForPuzzle);
        //set the new random image as long as it is not the previous one
        this.imageForPuzzle = randomImageId;
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        buildGrid(gridLayout);
        resetScore();
    }


    //Declaring the onClick listener for all the tiles
    View.OnClickListener onTileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // set listeners for all the TextViews in the Grid
            ImageView imgView = (ImageView) view;
            //skip listener if empty cell is touched
            Bitmap currentBitMap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(currentBitMap.getWidth(), currentBitMap.getHeight(), Bitmap.Config.ARGB_8888);
            if (!currentBitMap.sameAs(emptyBitmap)) {
                // myBitmap is not empty/blank
                assessInput(imgView.getId());
            }
        }
    };


    //Declaring the onClick listener for the New Game Button. Clicking
    //a new game will restart a random puzzle with the existing images
    View.OnClickListener newGamelistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
            buildGrid(gridLayout);
            //reset scores
            resetScore();
        }
    };

    /**
     * Summary: Resets the TextView that displays the score of the current game
     * Invoked either due to New game of Change Image
     */
    public void resetScore() {
        //TextView best_score = (TextView) findViewById(R.id.best_score_num);
        TextView score = (TextView) findViewById(R.id.current_score_num);
        this.currScore = 0;
        //this.topScore = 0;
        score.setText(this.currScore + "");
        //best_score.setText(this.topScore + "");
    }

    /**
     * Summary of Steps:
     * Set View
     * Build GridLayout
     * Reset game variables
     * Add listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Lock screen orientation
        //FIXME cannot lock orientation. Have to fix this.
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
     * Summary: This function randomly regenerates the GridLayout
     * for a given input image
     * <p/>
     * Steps:
     * Crop and scale the images to required cell size
     * Randomly add images to each cell of the gridlayout
     * Remove a random child to create empty cell
     *
     * @param layout
     */
    public void buildGrid(GridLayout layout) {
        //clear old tiles if any
        layout.removeAllViews();

        Random randomNumberGenerator = new Random();
        this.currScore = 0;

        //build the grid of images
        List<Bitmap> bitmapList = createImageTiles(imageForPuzzle, layout);

        //constructing the images for each cell of the grid and adding listeners to them
        int n = 9;
        for (int i = 0; i < 9; i++) {
            ImageView imgView = new ImageView(this);
            imgView.setId(i);
            //set the image bitmap
            imgView.setTag(imageForPuzzle, i);

            //generate a random number from [0, 9) to insert into cell
            int randomNum = randomNumberGenerator.nextInt(n);
            imgView.setImageBitmap(bitmapList.get(randomNum));
            //remove inserted image and reorder list
            bitmapList.remove(randomNum);
            n--;

            imgView.setPadding(pixelToDp(2), pixelToDp(2), pixelToDp(2), pixelToDp(2));
            imgView.setLayoutParams(new ViewGroup.LayoutParams(pixelToDp(100), pixelToDp(100)));
            imgView.setOnClickListener(onTileClickListener);

            layout.addView(imgView);
        }

        //generate a random number from [0, 9) and remove a random cell
        int randomNum = randomNumberGenerator.nextInt(9);
        //set image to empty
        ImageView emptyCell = (ImageView) layout.getChildAt(randomNum);

        emptyCell.setImageBitmap(Bitmap.createBitmap(pixelToDp(100),
                pixelToDp(100), Bitmap.Config.ARGB_8888));
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
     * Takes an input image and cuts and scales it to a 3x3 cell grid
     *
     * @param imgResource is the resource identifier that
     *                    should be passed in
     * @return List<Bitmap>>
     */
    private List<Bitmap> createImageTiles(int imgResource, GridLayout layout) {
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

    /**
     * Utility method to convert pixel to dp
     *
     * @param dps
     * @return
     */
    private int pixelToDp(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    /**
     * Summary: Check if a click by the user results in a valid cell
     * movement to an empty slot. If a cell adjacent to an empty slot
     * is clicked, move the images to the empty spot
     *
     * @param id
     * @return
     */
    public void assessInput(int id) {
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
                animateCell(index, destIndex);
            }
        }

        if (y + 1 < 3) {
            //bottom element
            int destIndex = x + 3 * (y + 1);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if (destBitmap.sameAs(emptyBitmap)) {
                animateCell(index, destIndex);
            }
        }

        if (x - 1 >= 0) {
            //left element
            int destIndex = (x - 1) + 3 * (y);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if (destBitmap.sameAs(emptyBitmap)) {
                animateCell(index, destIndex);
            }
        }

        if (x + 1 < 3) {
            //right element
            int destIndex = (x + 1) + 3 * (y);
            ImageView imgView = (ImageView) gridLayout.getChildAt(destIndex);
            Bitmap destBitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
            Bitmap emptyBitmap = Bitmap.createBitmap(destBitmap.getWidth(), destBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            if (destBitmap.sameAs(emptyBitmap)) {
                animateCell(index, destIndex);
            }
        }

        //Show toast Message if win
        boolean showToast = true;

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

    /**
     * Summary:
     * Given a sourceIndex and a destIndex, move the ImageView from src
     * to destination
     * Increment the score
     * Update the last_tile_index for feature
     *
     * @param srcIndex
     * @param destIndex
     */
    public void animateCell(int srcIndex, int destIndex) {
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

    /**
     * Summary:
     * If invoked, shows the user the last tile he moved to the
     * empty cell.
     * If the user is yet to make a move, show an error alerting that users
     * hasn't made a move yet
     */
    public void showLastMoveHighLight() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        //check if last tile index is less than 3x3 indices, else show toast
        if (this.last_tile_index < 8) {
            ImageView imgView = (ImageView) gridLayout.getChildAt(last_tile_index);
            imgView.setColorFilter(Color.GREEN, PorterDuff.Mode.OVERLAY);
        } else {
            Toast.makeText(GameActivity.this, "You haven't made a move yet!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Removes highlight from the highlighted cell
     * after the timer of 1 second expires
     */
    public void removeHighlight() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.puzzle_grid);
        if (this.last_tile_index < 8) {
            ImageView imgView = (ImageView) gridLayout.getChildAt(last_tile_index);
            imgView.setColorFilter(Color.TRANSPARENT);
        }
    }


}
