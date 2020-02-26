package com.android.tankwars;

import android.graphics.Color;

public class Obstacle extends GameObject{

    Obstacle(float x, float y, float width, float height) {
        super(x, y, width, height);
        setColor(Color.argb(255, 80, 20, 80));
        setZ(-2);
    }

    @Override
    public void collision(GameObject otherObject) {
        setCollided(true);

    }

    public void update(long fps){

    }

}
