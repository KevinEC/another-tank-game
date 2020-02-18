package com.android.tankwars;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

public class PlayerTank extends GameObject{

    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;
    public final int FIRE = 5;

    private TankWarsActivity activity;

    //private RectF rect;
    private Bitmap bitmap;
    private Bitmap bitmapOrigin;
    private float bitmapWidth;
    private float bitmapHeight;

    //private float width, height;

    private float mScreenX, mScreenY;

    //private float x, y;

    //private float tankSpeed;

    private ArrayList<Bullet> playerBullets;

    private boolean moving = false;
    private boolean parseMovementInput = true;
    // int representing the current rotation.
    // Initial rotation UP.
    private int playerInput = UP;

    private boolean firing = false;
    private long fireCooldown = 150;
    private  long fireCooldownFinish;
    private long cooldown = 0;

    public PlayerTank(Context context, int screenX, int screenY) {
        super(screenX / 2, screenY / 2, screenX / 10, screenY / 10);
        activity = (TankWarsActivity) context;
        mScreenX = screenX; mScreenY = screenY;
        bitmapWidth = width; bitmapHeight = height;
        x = screenX / 2;
        y = screenY / 2;

        speed = 350;
        playerBullets = new ArrayList<>();
        color = new Paint();
        color.setColor(Color.argb(255, 20, 200, 40));

        bitmapOrigin = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank2);
        // Scale bitmap to tank size
        // tuck away a copy of the original rotate position
        bitmapOrigin = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);
        // set current bitmap
        bitmap = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);
    }

    @Override
    public void collision(GameObject otherObject) {
        bouncePlayerBack();
    }

    public void update(long fps) {
        if (parseMovementInput && moving) {
            translate(fps);
        }
        if(firing && cooldown == 0) fire();
        updateFireCooldown();

        // update player bullets
        updateBullets(fps);
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

    public void setMoving(boolean moving) { this.moving = moving; }

    public void setFireInput(boolean state) {
        firing = state;
    }

    public void setRotation(int rotation) {
        // if the new rotation value is different from the previous one
        if (rotation != this.rotation) {
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
            this.rotation = rotation;
            setDirectionVector();
        }
    }

    private void bouncePlayerBack() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DecelerateInterpolator easeOut = new DecelerateInterpolator(3.0f);

                final float xEndValue = getX() + (-1*directionVector.first) * 30;
                final float yEndValue = getY() + (-1*directionVector.second) * 30;

                Log.d("Collision", "directionVector: " + directionVector);
                Log.d("Collision", "xEndValue: " + xEndValue + " yEndValue: " + yEndValue);

                final ValueAnimator xAnim = ValueAnimator.ofFloat(getX(), xEndValue);
                final ValueAnimator yAnim = ValueAnimator.ofFloat(getY(), yEndValue);
                xAnim.setInterpolator(easeOut);
                yAnim.setInterpolator(easeOut);

                xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float xValue = (float) xAnim.getAnimatedValue();
                        float yValue = (float) yAnim.getAnimatedValue();
                        Log.d("Collision", "(xValue,yValue): (" + xValue + ", " + yValue + ")");
                        setCoordinate(new Pair<>(xValue, yValue));
                    }
                });
                xAnim.addListener(new ValueAnimator.AnimatorListener() {
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

                yAnim.start();
                xAnim.start();
            }
        });
    }

    private void fire() {
        Pair bulletXY = calcBulletCoordinate();
        Bullet newBullet = new Bullet((float) bulletXY.first, (float) bulletXY.second, rotation);
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

        if (rotation == 0) {
            bulletY = y + height / 2;
            bulletX = x + width;
        } else if (rotation == 180) {
            bulletY = y + height / 2;
            bulletX = x;
        } else if (rotation == 90) {
            bulletX = x + width / 2;
            bulletY = y + height;
        } else {
            bulletX = x + width / 2;
            bulletY = y;
        }

        return new Pair<>(bulletX, bulletY);
    }
}
