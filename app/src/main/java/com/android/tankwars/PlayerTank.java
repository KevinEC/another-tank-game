package com.android.tankwars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Button;

public class PlayerTank {

    RectF rect;
    private Bitmap bitmap;
    private Bitmap bitmapOrigin;

    private float width, height;

    private float x, y;

    private float tankSpeed;

    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;
    public final int FIRE = 5;

    private boolean moving = false;
    // int representing the current direction.
    // Initial direction UP.
    private int tankDirection = UP;
    private int tankRotation = 0;

    public PlayerTank(Context context, int screenX, int screenY) {
        rect = new RectF();
        width = screenX / 10;
        height = screenY / 10;
        x = screenX / 2;
        y = screenY / 2;
        tankSpeed = 350;


        bitmapOrigin = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank2);

        // Scale bitmap to tank size
        // tuck away a copy of the original rotate position
        bitmapOrigin = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);
        // set current bitmap
        bitmap = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);
    }

    public void update(long fps) {
        //Log.d("TANKU: ", "moving in PlayerTank" + moving);
        if (moving) {
            switch (tankDirection) {
                case LEFT:
                    x = moveTank(x, - tankSpeed / fps);
                    setBitmap(180);
                    break;
                case RIGHT:
                    x = moveTank(x, + tankSpeed / fps);
                    setBitmap(0);
                    break;
                case UP:
                    y = moveTank(y, - tankSpeed / fps);
                    setBitmap(270);
                    break;
                case DOWN:
                    y = moveTank(y, + tankSpeed / fps);
                    setBitmap(90);
                    break;
            }

            // update rect according to the new coordinates
            rect.top = y;
            rect.bottom = y + height;
            rect.left = x;
            rect.right = x + width;
        }
    }

    // Getters & Setters
    public RectF getRect() {
        return rect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setDirectionState(int state) { tankDirection = state; }

    public void setMoving(boolean state) {
        moving = state;
    }

    private float moveTank(float coordinate, float direction) {
        return coordinate + direction;
    }

    private void setBitmap(int rotation) {
        if(rotation != tankRotation) {
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmapOrigin,0,0, bitmapOrigin.getWidth(), bitmapOrigin.getHeight(), rotationMatrix, true);
            tankRotation = rotation;
        }
    }
}
