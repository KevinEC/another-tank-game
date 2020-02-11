package com.android.tankwars;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Bullet {

    private static int ID = 0;

    private int id;
    private float x, y;

    private float width, height;

    private float mDirection;

    private float speed;


    private RectF rect;

    Bullet(float x, float y, float direction){
        id = ID; ID++;
        this.x = x; this.y = y;
        mDirection = direction;

        width = height = 4.0f;
        speed = 500.0f;

        rect = new RectF();
        setRect();
    }

    public void update(long fps){
        translate(fps);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(rect, new Paint(Color.RED));
    }

    public int getId() {
        return id;
    }

    public float getX(){ return x; }
    public float getY(){ return y; }
    public RectF getRect(){ return rect; }
    private void setRect() {
        rect.top = this.y;
        rect.bottom = this.y + height;
        rect.left = this.x;
        rect.right = this.x + width;
    }

    private void translate(long fps) {
        if(mDirection == 0.0f) {
            setCoordinate("x", getX() + speed/fps);
        }
        else if(mDirection == 180.0f) {
            setCoordinate("x", getX() - speed/fps);
        }
        else if(mDirection == 90.0f) {
            setCoordinate("y", getY() + speed/fps);
        }
        else if(mDirection == 270.0f) {
            setCoordinate("y", getY() - speed/fps);
        }
    }

    private void setCoordinate(String coordinate, float value) {
        Log.d("bullets", "coordinate " + coordinate + " set to: "  + value);
        if(coordinate.equals("x")) x = value;
        else y = value;
        setRect();
    }

}
