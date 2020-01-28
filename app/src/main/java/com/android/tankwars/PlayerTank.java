package com.android.tankwars;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class PlayerTank {
    TankWarsActivity activity;

    RectF rect;
    private Bitmap bitmap;
    private Bitmap bitmapOrigin;

    private float width, height;

    private float mScreenX, mScreenY;

    private float x, y;

    private float tankSpeed;

    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;
    public final int FIRE = 5;

    private boolean moving = false;
    private boolean parseMovementInput = true;
    // int representing the current direction.
    // Initial direction UP.
    private int tankDirection = UP;
    private int tankRotation = 0;

    public PlayerTank(Context context, int screenX, int screenY) {
        activity = (TankWarsActivity) context;
        mScreenX = screenX;
        mScreenY = screenY;
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
        if (moving) {
            switch (tankDirection) {
                case LEFT:
                    handlePlayerMovement("x", -tankSpeed / fps);
                    setTankDirection(180);
                    break;
                case RIGHT:
                    handlePlayerMovement("x", +tankSpeed / fps);
                    setTankDirection(0);
                    break;
                case UP:
                    handlePlayerMovement("y", -tankSpeed / fps);
                    setTankDirection(270);
                    break;
                case DOWN:
                    handlePlayerMovement("y", +tankSpeed / fps);
                    setTankDirection(90);
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

    public void setCoordinate(String coordinate, float value){
        if(coordinate.equals("x")) x = value;
        else if(coordinate.equals("y")) y = value;
        else Log.e("setCoordinate", "invalid coordinate. Only x and y are valid identifiers");
    }
    public float getCoordinate(String coordinate){
        if(coordinate.equals("x")) return getX();
        else return getY();
    }

    public void setDirectionState(int state) {
        tankDirection = state;
    }

    public void setMoving(boolean state) {
        moving = state;
    }

    private void handlePlayerMovement(String coordinate, float direction) {
        float originCoordinate;

        if(coordinate.equals("x")) originCoordinate = x;
        else originCoordinate = y;

        if (parseMovementInput) {
            if (screenEdgeDetected()) {
                Log.d("Collision", "edge detected");
                bouncePlayerBack(coordinate, direction);
            } else {
                float newCoordinate = calcCoordinate(originCoordinate, direction);
                setCoordinate(coordinate, newCoordinate);
            }
        }
    }

    private float calcCoordinate(float coordinate, float direction) {
        return coordinate + direction;
    }

    private void setTankDirection(int rotation) {
        // if the new rotation value is different from the previous one
        if (rotation != tankRotation) {
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmapOrigin, 0, 0, bitmapOrigin.getWidth(), bitmapOrigin.getHeight(), rotationMatrix, true);
            tankRotation = rotation;
        }
    }

    private boolean screenEdgeDetected() {
        boolean onEdge = false;
        // right
        if (x + getRect().width() > mScreenX) {
            onEdge = true;
        }
        //left
        else if (x < 0) {
            onEdge = true;
        }
        //top
        else if (y + getRect().height() > mScreenY) {
            onEdge = true;
        }
        //bottom
        else if (y - getRect().height() < 0) {
            onEdge = true;
        }
        return onEdge;
    }

    private void bouncePlayerBack(final String coordinate, final float direction) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("Collision", "bouncePlayerBack method start");
                DecelerateInterpolator easeOut = new DecelerateInterpolator(1.0f);

                float startValue = getCoordinate(coordinate);
                float endValue = getCoordinate(coordinate) + ((-1) * Math.signum(direction)) * 20;
                Log.d("Collision", "animating from: " + startValue + " to " + endValue);

                final ValueAnimator wallAnimator = ValueAnimator.ofFloat(startValue, endValue);

                wallAnimator.setInterpolator(easeOut);


                wallAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) wallAnimator.getAnimatedValue();
                        setCoordinate(coordinate, value);
                        Log.d("Collision", "updating " + coordinate + " to: " + value);
                    }
                });
                wallAnimator.addListener(new ValueAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        parseMovementInput = false;
                        Log.d("Collision", "animation started");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parseMovementInput = true;
                        Log.d("Collision", "animation ended. Final values x: " + x + "y: " + y);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        parseMovementInput = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                Log.d("Collision", "new Thread run");
                wallAnimator.start();
            }
        });
    }
}
