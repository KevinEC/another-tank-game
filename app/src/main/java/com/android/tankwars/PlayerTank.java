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

    private int playerID;

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

    private ParticleSystem leftTrail, rightTrail;

    public PlayerTank(Context context, int screenX, int screenY, int playerID) {
        super(screenX / 2, screenY / 2, screenY / 10, screenY / 10);
        activity = (TankWarsActivity) context;
        mScreenX = screenX; mScreenY = screenY;
        bitmapWidth = width; bitmapHeight = height;

        speedFactor = 0.0f;
        speed = 0.0f * speedFactor;
        playerBullets = new ArrayList<>();
        setColor(Color.argb(255, 20, 200, 40));

        bitmapOrigin = BitmapFactory.decodeResource(context.getResources(), R.drawable.tank2);
        // Scale bitmap to tank size
        // tuck away a copy of the original rotate position
        bitmapOrigin = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);
        // set current bitmap
        bitmap = Bitmap.createScaledBitmap(bitmapOrigin, (int) (width), (int) (height), false);

        this.playerID = playerID;
        leftTrail = initTrail();
        rightTrail = initTrail();
    }

    private ParticleSystem initTrail(){
        int spread = 40;
        Pair<Float, Float> directionRange = new Pair(rotation-spread, rotation+spread);
        Pair<Float, Float> spinRange = new Pair(20.0f, 90.0f);
        Pair<Float, Float> sizeRange = new Pair(6.0f, 10.0f);
        Pair<Float, Float> distanceRange = new Pair(70.0f, 220.0f);
        Pair<Float, Float> speedRange = new Pair(20.0f, 50.0f);
        Pair<Float, Float> lifetimeRange = new Pair(20.0f, 80.0f);
        int spawnrate = 1;
        int color = Color.argb(255, 80, 40, 30);

        ParticleSystem sys = new ParticleSystem(getRect().centerX(), getRect().centerY(),directionRange,spinRange, sizeRange, distanceRange, speedRange, lifetimeRange, spawnrate, color);
        sys.setTransform(true);
        return sys;
    }

    @Override
    public void collision(GameObject otherObject) {
        setCollided(true);
        if(otherObject instanceof Bullet && ((Bullet) otherObject).getPlayer().equals(this)) {
            Log.d("collision", "collided with own bullet");
        }
        else {
            bouncePlayerBack();
        }
    }

    public void update(long fps) {
        if (parseMovementInput && moving) {
            translate(fps);
            leftTrail.update(fps);
            rightTrail.update(fps);
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

    public void setMoving(boolean moving) {
        this.moving = moving;
        setTrail(moving, rightTrail, true);
        setTrail(moving, leftTrail, false);
    }

    private void setTrail(boolean state, ParticleSystem trail, boolean rightTrail) {
        trail.setSpawnParticles(state);
        trail.setDirectionRange(new Pair<>(getRotation() + 180 - 20, getRotation() + 180 + 20));
        trail.setCoordinates(new Pair<>(getRect().left, getRect().bottom));
        if(rightTrail)
            trail.setTransformationRotation(getRotation() + 225);
        else
            trail.setTransformationRotation(getRotation() + 135);
        trail.setTransformationRotationOrigin(new Pair<>(getRect().centerX(), getRect().centerY()));
        float rectW = getWidth() / 2.0f;
        float diagonal = (float) Math.sqrt((2.0f*Math.pow(rectW, 2)));
        trail.setTransformationRotationTranslation(diagonal);
    }

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
        Bullet newBullet = new Bullet((float) bulletXY.first, (float) bulletXY.second, rotation, this);
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
        speedFactor = factor / 10.0f; // [0,10]
        setMoving(speedFactor > 0);
        // weird math stuff for easing
        speed = 100.0f + (float) Math.pow(speed, speedFactor / 10.0f);
        // max speed
        if(speed > 500.0f)
            speed = 500.0f;
    }
}
