package com.android.tankwars;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public abstract class GameObject{

    protected float x, y;
    protected float width, height;
    protected RectF rect;

    protected float direction;
    protected float speed;

    protected Paint color;

    protected static ArrayList<GameObject> allGameObjects = new ArrayList<>();
    protected static ArrayList<GameObject> toBeRemoved = new ArrayList<>();

    GameObject(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        rect = new RectF();
        setRect();

        allGameObjects.add(this);
    }
    public abstract void collision(GameObject otherObject);

    public abstract void update(long fps);

    public boolean checkCollision(RectF otherRect){
        return this.rect.intersect(otherRect);
    }

    protected void translate(long fps){
        if(direction == 0.0f) {
            setCoordinate("x", getX() + speed/fps);
        }
        else if(direction == 180.0f) {
            setCoordinate("x", getX() - speed/fps);
        }
        else if(direction == 90.0f) {
            setCoordinate("y", getY() + speed/fps);
        }
        else if(direction == 270.0f) {
            setCoordinate("y", getY() - speed/fps);
        }

        Log.d("GameObject", "collection size: " + allGameObjects.size());

        for (GameObject object : allGameObjects) {
            // don't compare to self
            if(!object.equals(this) && checkCollision(object.getRect())) {
                collision(object);
            }
        }
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    protected void setCoordinate(String coordinate, float value) {
        if(coordinate.equals("x")) x = value;
        else y = value;
        setRect();
    }

    public RectF getRect() {
        return rect;
    }

    protected void setRect() {
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;
    }
}
