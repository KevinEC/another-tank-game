package com.android.tankwars;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

public class PlayerTank extends GameObject{

    private TankWarsActivity activity;

    private Bitmap bitmap;
    private Bitmap bitmapOrigin;
    private float bitmapWidth;
    private float bitmapHeight;

    private float mScreenX, mScreenY;

    private ArrayList<Bullet> playerBullets;

    private boolean moving = false;
    private boolean parseMovementInput = true;

    private boolean firing = false;
    private long fireCooldown = 150;
    private  long fireCooldownFinish;
    private long cooldown = 0;
    private float speedFactor;

    public PlayerTank(Context context, int screenX, int screenY) {
        super(screenX / 2, screenY / 2, screenY / 10, screenY / 10);
        activity = (TankWarsActivity) context;
        mScreenX = screenX; mScreenY = screenY;
        bitmapWidth = width; bitmapHeight = height;
        x = screenY / 2;
        y = screenY / 2;

        speedFactor = 0.0f;
        speed = 0.0f * speedFactor;
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
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ArrayList<Bullet> getPlayerBullets() {
        return playerBullets;
    }

    public void setMoving(boolean moving) { this.moving = moving; }

    public void setFireInput(boolean state) {
        firing = state;
    }

    private void bouncePlayerBack() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DecelerateInterpolator easeOut = new DecelerateInterpolator(3.0f);

                final float xEndValue = getX() + (-1*directionVector.first) * 30;
                final float yEndValue = getY() + (-1*directionVector.second) * 30;

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

        // 100 pixels worth of padding implemented to go around the poor collision detection on
        // rotated rectangles
        /* TODO
        *  Possible Solution to this could be to make the Tank Square.
        * */
        bulletX = getRect().centerX() + getDirectionVector().first * (width+100.0f)/2.0f;
        bulletY = getRect().centerY() + getDirectionVector().second * (height+100.0f)/2.0f;

        return new Pair<>(bulletX, bulletY);
    }

    public void setSpeedFactor(int factor) {
        speedFactor = factor / 100.0f;
        setMoving(speedFactor > 0);
        speed = 200.0f * speedFactor;
    }
}
