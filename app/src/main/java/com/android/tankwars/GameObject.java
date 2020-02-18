package com.android.tankwars;

import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Pair;

import java.util.ArrayList;

public abstract class GameObject{

    protected long fps;
    protected float x, y;
    protected float width, height;
    protected RectF rect;

    protected float rotation;
    protected float speed;
    protected Pair<Float, Float> directionVector;
    protected Pair<Float, Float> coordinateVector;

    protected Paint color;

    protected static ArrayList<GameObject> allGameObjects = new ArrayList<>();
    protected static ArrayList<GameObject> toBeRemoved = new ArrayList<>();

    GameObject(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        coordinateVector = new Pair<>(x,y);
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
        if(rotation == 0.0f) {
            setCoordinate("x", getX() + speed/fps);
        }
        else if(rotation == 180.0f) {
            setCoordinate("x", getX() - speed/fps);
        }
        else if(rotation == 90.0f) {
            setCoordinate("y", getY() + speed/fps);
        }
        else if(rotation == 270.0f) {
            setCoordinate("y", getY() - speed/fps);
        }

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
        coordinateVector = new Pair<>(x,y);
        setRect();
    }

    protected void setCoordinate(Pair<Float, Float> value) {
        x = value.first;
        y = value.second;
        coordinateVector = value;
        setRect();
    }

    public float getCoordinate(String coordinate) {
        if (coordinate.equals("x")) return getX();
        else return getY();
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        setDirectionVector();
    }

    protected void setDirectionVector(){
        float x = (float) Math.cos(Math.toRadians(rotation));
        float y = (float) Math.sin(Math.toRadians(rotation));
        directionVector = new Pair(x,y);
    }

    public Pair<Float,Float> getDirectionVector(){
        return directionVector;
    }

    public void setFps(long fps) {
        this.fps = fps;
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
