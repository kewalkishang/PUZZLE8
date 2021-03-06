package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private ArrayList<PuzzleTile> tiles;
    private int steps;
    private PuzzleBoard previousBoard;


    PuzzleBoard(Bitmap bitmap, int parentWidth) {

        tiles = new ArrayList<>();

        int numTile = 0;
        Bitmap sqBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);

        for (int i = 0; i < NUM_TILES; i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                Bitmap sclBitmap = Bitmap.createBitmap(sqBitmap, (parentWidth / NUM_TILES) * j, (parentWidth / NUM_TILES) * i, (parentWidth / NUM_TILES), (parentWidth / NUM_TILES));
                PuzzleTile puzzleTile = new PuzzleTile(sclBitmap, numTile);
                numTile++;
                tiles.add(puzzleTile);
            }
        }
        tiles.set((NUM_TILES * NUM_TILES) - 1, null);


        // if not copy constructor keep this as zero(not needed)
        steps = 0;

    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();

        //to count the steps
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {

        int nullTile = 0;
        ArrayList<PuzzleBoard> validBoard = new ArrayList<>();

        //find the null tile
        while (tiles.get(nullTile) != null) {
            nullTile++;
        }

        //loop through NEIGHBOUR_COORDS to find the valid moves
        for (int j = 0; j < 4; j++) {
            int validMove = XYtoIndex(NEIGHBOUR_COORDS[j][0], NEIGHBOUR_COORDS[j][1]) + nullTile;

            if (validMove > -1 && validMove < 9) {
                PuzzleBoard copy = new PuzzleBoard(this);
                copy.swapTiles(nullTile, validMove);
                validBoard.add(copy);
            }
        }

        //return the array of valid moves
        return validBoard;

    }

    public int priority() {

        int manhattanValue = 0;

        int originalX;
        int originalY;

        int presentX;
        int presentY;

        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {

            //get the present positions
            presentX = i / NUM_TILES;
            presentY = i % NUM_TILES;

            //get the actual positions
            if (tiles.get(i) != null) {
                originalX = tiles.get(i).getNumber() / NUM_TILES;
                originalY = tiles.get(i).getNumber() % NUM_TILES;
            } else {
                originalX = 8 / NUM_TILES;
                originalY = 8 % NUM_TILES;
            }

            //add the difference to the manhattan value
            manhattanValue += Math.abs(originalX - presentX);
            manhattanValue += Math.abs(originalY - presentY);
        }


        //finally add it to the number of steps taken to reach the present position
        manhattanValue += steps;

        return manhattanValue;
    }



    public void setSteps (int x) {
        steps = x;
    }

    public void setPreviousToNull () {
        previousBoard = null;
    }

    public PuzzleBoard getPreviousBoard () {
        return previousBoard;
    }


}