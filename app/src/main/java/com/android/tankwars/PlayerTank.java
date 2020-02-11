package com.android.tankwars;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;

public class PlayerTank {

    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;
    public final int FIRE = 5;

    private TankWarsActivity activity;

    private RectF rect;
    private Bitmap bitmap;
    private Bitmap bitmapOrigin;
    private float bitmapWidth;
    private float bitmapHeight;

    private float width, height;

    private float mScreenX, mScreenY;

    private float x, y;

    private float tankSpeed;

    private ArrayList<Bullet> playerBullets;

    private boolean moving = false;
    private boolean parseMovementInput = true;
    // int representing the current direction.
    // Initial direction UP.
    private int playerInput = UP;
    private int tankRotation = 0;
    private int direction;

    private boolean firing = false;
    private long fireCooldown = 150;
    private  long fireCooldownFinish;
    private long cooldown = 0;

    public PlayerTank(Context context, int screenX, int screenY) {
        activity = (TankWarsActivity) context;

        mScreenX = screenX;
        mScreenY = screenY;
        rect = new RectF();
        bitmapWidth = width = screenX / 10;
        bitmapHeight = height = screenY / 10;
        x = screenX / 2;
        y = screenY / 2;
        tankSpeed = 350;
        playerBullets = new ArrayList<>();


        bitmapOrigin = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank2);
        // Scale bitmap to tank size
        // tuck away a copy of the original rotate position
        bitmapOrigin = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);
        // set current bitmap
        bitmap = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);
    }

    public void update(long fps) {
        // only one of these should be able to happen
        if (moving) {
            switch (playerInput) {
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
            setRect();
        }
        if(firing && cooldown == 0) fire();
        updateFireCooldown();

        // update player bullets
        updateBullets(fps);
    }

    // Getters & Setters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public RectF getRect() {
        return rect;
    }

    private void setRect() {
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ArrayList<Bullet> getPlayerBullets() {
        return playerBullets;
    }

    private void updateBullets(long fps){
        for (Bullet bullet : playerBullets) {
            bullet.update(fps);
        }
    }

    public void setCoordinate(String coordinate, float value) {
        if (coordinate.equals("x")) x = value;
        else if (coordinate.equals("y")) y = value;
        else Log.e("error", "setCoordinate. Invalid coordinate. Only x and y are valid identifiers");
    }

    public float getCoordinate(String coordinate) {
        if (coordinate.equals("x")) return getX();
        else return getY();
    }

    public void setMoving(boolean moving) { this.moving = moving; }

    public void setPlayerInput(int state) {
        playerInput = state;
    }

    public void setFireInput(boolean state) {
        firing = state;
    }

    private void handlePlayerMovement(String coordinate, float direction) {
        float originCoordinate;
        // Determine which coordinate to bg handled
        if (coordinate.equals("x")) originCoordinate = x;
        else originCoordinate = y;

        // Surface collision logic
        if (parseMovementInput) {
            if (screenEdgeDetected()) {
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
            // swap width and height according to rotation
            if (rotation == 0 || rotation == 180) {
                width = bitmapWidth;
                height = bitmapHeight;
            } else {
                width = bitmapHeight;
                height = bitmapWidth;
            }
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
        //bottom
        else if (y + getRect().height() > mScreenY) {
            onEdge = true;
        }
        //top
        else if (y < 0) {
            onEdge = true;
        }
        return onEdge;
    }

    private void bouncePlayerBack(final String coordinate, final float direction) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DecelerateInterpolator easeOut = new DecelerateInterpolator(1.0f);
                float startValue = getCoordinate(coordinate);
                float endValue = getCoordinate(coordinate) + ((-1) * Math.signum(direction)) * 20;

                final ValueAnimator wallAnimator = ValueAnimator.ofFloat(startValue, endValue);
                wallAnimator.setInterpolator(easeOut);

                wallAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) wallAnimator.getAnimatedValue();
                        setCoordinate(coordinate, value);
                    }
                });
                wallAnimator.addListener(new ValueAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        parseMovementInput = false;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parseMovementInput = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        parseMovementInput = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

                wallAnimator.start();
            }
        });
    }

    private void fire() {
        Pair bulletXY = calcBulletCoordinate();
        Bullet newBullet = new Bullet((float) bulletXY.first, (float) bulletXY.second, tankRotation);
        playerBullets.add(newBullet);

        startFireCooldown();
    }

    private void startFireCooldown(){
        fireCooldownFinish = System.currentTimeMillis() + fireCooldown;
        cooldown = fireCooldown;
    }

    private void updateFireCooldown(){
        if(cooldown > 0)
            cooldown = fireCooldownFinish - System.currentTimeMillis();
        else {
            cooldown = 0;
        }
    }

    private Pair<Float, Float> calcBulletCoordinate() {
        float bulletX, bulletY;

        if (tankRotation == 0) {
            bulletY = y + height / 2;
            bulletX = x + width;
        } else if (tankRotation == 180) {
            bulletY = y + height / 2;
            bulletX = x;
        } else if (tankRotation == 90) {
            bulletX = x + width / 2;
            bulletY = y + height;
        } else {
            bulletX = x + width / 2;
            bulletY = y;
        }

        return new Pair<>(bulletX, bulletY);
    }


}
