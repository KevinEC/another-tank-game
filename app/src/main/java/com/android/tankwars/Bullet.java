package com.android.tankwars;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Bullet extends GameObject {

    private static int ID = 0;
    private int id;


    Bullet(float x, float y, float direction){
        super(x,y, 4.0f,4.0f);
        id = ID; ID++;
        this.direction = direction;
        speed = 500.0f;
        color = new Paint();
        color.setColor(Color.argb(255, 255, 100, 100));
    }

    @Override
    public void collision(GameObject otherObject) {
        speed = 0;
        if(otherObject.getClass() == Obstacle.class)
        {

        }
        toBeRemoved.add(this);
    }

    public void update(long fps){
        translate(fps);
    }

    public int getId() {
        return id;
    }


}
