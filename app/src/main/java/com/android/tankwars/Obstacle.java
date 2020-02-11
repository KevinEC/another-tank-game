package com.android.tankwars;

import android.graphics.RectF;

public class Obstacle {

    private float x,y;
    private float width,height;
    private RectF rect;

    Obstacle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        rect = new RectF();
        setRect();
    }

    public void update(long fps){

    }

    private void setRect(){
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;
    }
    public RectF getRect(){ return rect; }
    private float getX(){ return x; }
    private float getY(){ return y; }

}
