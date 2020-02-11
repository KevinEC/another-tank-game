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


    }

    @Override
    public void collision() {

    }

    public void update(long fps){
        translate(fps);
    }

    public int getId() {
        return id;
    }


}
