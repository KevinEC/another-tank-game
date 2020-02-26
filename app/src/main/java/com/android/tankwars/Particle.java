package com.android.tankwars;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;

import java.util.Random;

class Particle extends GameObject{

    private float spin;
    private float direction;
    private float distance;
    private float lifetime;

    private Random random = new Random();
    private float distanceTravelled;

    Particle(float x, float y, float spin, float size, float direction, float distance, float speed, float lifetime, int color) {
        super(x, y, size, size);
        this.speed = speed;
        rotation = random.nextFloat()*360.0f;
        setColor(color);
        setCollidable(false);

        this.direction = direction;
        this.distance = distance;
        this.spin = spin;
        this.lifetime = lifetime;
        setDirectionVector();
        setZ(-1);
    }

    @Override
    public void collision(GameObject otherObject) {

    }

    @Override
    public void update(long fps) {
        translate(fps);
        spin(fps);
        accelerate(fps);
        killParticle(fps);
    }

    @Override
    protected void setDirectionVector(){
        float x = (float) Math.cos(Math.toRadians(direction));
        float y = (float) Math.sin(Math.toRadians(direction));
        directionVector = new Pair(x,y);
    }

    private void spin(long fps) {
        rotation -= (spin * 0.9f) / fps;
    }

    private void accelerate(long fps) {
        speed -= (float) Math.pow(speed, 0.1);
        if(speed < 0)
            speed = 0;
    }

    private void killParticle(long fps) {
        distanceTravelled += speed / fps;
        lifetime -= 1;
        if(distanceTravelled >= distance || lifetime <= 0){
            toBeRemoved.add(this);
        }
    }
}
