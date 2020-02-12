package com.android.tankwars;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Obstacle extends GameObject{

    Obstacle(float x, float y, float width, float height) {
        super(x, y, width, height);
        color = new Paint();
        color.setColor(Color.argb(255, 80, 20, 80));
    }

    @Override
    public void collision(GameObject otherObject) {

    }

    public void update(long fps){

    }

}
