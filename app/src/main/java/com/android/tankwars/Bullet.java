package com.android.tankwars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Bullet {

    private float x, y;

    private float width, height;

    private float mDirection;

    private float speed;


    private RectF rect;

    Bullet(float x, float y, float direction){
        this.x = x; this.y = y;
        mDirection = direction;

        width = height = 4.0f;
        rect = new RectF();

        rect.top = this.y;
        rect.bottom = this.y + height;
        rect.left = this.x;
        rect.right = this.x + width;
    }

    public void update(){

    }

    public void draw(Canvas canvas) {
        canvas.drawRect(rect, new Paint(Color.RED));
    }

    public float getX(){ return x; }
    public float getY(){ return y; }
    public RectF getRect(){ return rect; }

}
