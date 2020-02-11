package com.android.tankwars;

import android.content.Context;
import android.graphics.RectF;

public class Obstacle extends GameObject{

    private float x,y;
    private float width,height;
    private RectF rect;

    Obstacle(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        rect = new RectF();
        setRect();
    }

    @Override
    public void collision() {

    }

    public void update(long fps){

    }

}
