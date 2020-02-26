package com.android.tankwars;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class Bullet extends GameObject {

    private static int ID = 0;
    private PlayerTank player;
    private int id;
    private int distanceTravelled = 0;
    private int maxDistance = 800;
    private float minFadeOutSpeed = 150.0f;

    private ParticleSystem explosion = null;
    private int explosionLifetime;

    Bullet(float x, float y, float rotation, PlayerTank player){
        super(x,y, 20.0f,20.0f);
        id = ID; ID++;
        this.player = player;
        this.rotation = rotation;
        setDirectionVector();
        speed = 250.0f;
        setColor(Color.argb(255, 255, 100, 100));
        explosionLifetime = 7;
    }

    @Override
    public void collision(GameObject otherObject) {
        // Collision with opponent bullet
        if(otherObject instanceof Bullet && !((Bullet) otherObject).getPlayer().equals(this.getPlayer())) {
            speed = 0;
            if(explosion == null)
                initExplosion();
            setCollided(true);
            explosion.setSpawnParticles(true);
        }
        // Collision with Obstacle
        else if(otherObject instanceof Obstacle){
            speed = 0;
            setCollided(true);
            if(explosion == null)
                initExplosion();
            explosion.setSpawnParticles(true);
        }
        // Collision with opponent tank
        else if(otherObject instanceof PlayerTank && !this.getPlayer().equals(otherObject)){
            speed = 0;
            setCollided(true);
            if(explosion == null)
                initExplosion();
            explosion.setSpawnParticles(true);
        }
    }

    private void initExplosion() {
        Pair<Float, Float> directionRange = new Pair(0.0f, 360.0f);
        Pair<Float, Float> spinRange = new Pair(20.0f, 60.0f);
        Pair<Float, Float> sizeRange = new Pair(4.0f, 12.0f);
        Pair<Float, Float> distanceRange = new Pair(70.0f, 420.0f);
        Pair<Float, Float> speedRange = new Pair(200.0f, 320.0f);
        Pair<Float, Float> lifetimeRange = new Pair(30.0f, 80.0f);
        int spawnrate = 3;
        int color = Color.argb(255, 200, 140, 40);

        explosion = new ParticleSystem(getRect().centerX(), getRect().centerY(),directionRange,spinRange, sizeRange, distanceRange, speedRange, lifetimeRange, spawnrate, color);
    }

    public void update(long fps){
        translate(fps);
        accelerate();
        bulletReach(fps);
        explode(fps);
    }

    private void accelerate() {
        speed = (float) Math.pow(speed, 1.015f);
        if(distanceTravelled > maxDistance / 3)
            speed = (float) Math.pow(speed, 1.0001f);
        if(distanceTravelled > (maxDistance * 3 )/ 4)
            speed *= 0.6;
        if(distanceTravelled >= maxDistance - 20.0f)
            speed = minFadeOutSpeed;
    }

    private void explode(long fps){
        if(getCollided() && explosionLifetime > 0){
            explosionLifetime -= 1;
            explosion.update(fps);
        }
        if(explosionLifetime <= 0) {
            removeBullet();
        }
    }

    public int getId() {
        return id;
    }

    private void bulletReach(long fps){
        distanceTravelled += speed / fps;
        if(distanceTravelled >= maxDistance)
            removeBullet();
    }

    private void removeBullet() {
        toBeRemoved.add(this);
        speed = 0;
    }

    public PlayerTank getPlayer() {
        return player;
    }
}
