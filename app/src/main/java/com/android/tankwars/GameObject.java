package com.android.tankwars;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

public abstract class GameObject{

    // for debugging
    RectF tempThisRect, tempOtherRect;

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
    protected static ArrayList<GameObject> toBeAdded = new ArrayList<>();

    GameObject(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        coordinateVector = new Pair<>(x,y);
        this.width = width;
        this.height = height;
        rect = new RectF();
        setRect();
        rotation = 0.0f;
        setDirectionVector();

        // This is to avoid ConcurrentModificationException.
        toBeAdded.add(this);
    }
    public abstract void collision(GameObject otherObject);

    public abstract void update(long fps);

    public boolean checkCollision(GameObject otherObject){
        return this.rect.intersect(otherObject.getRect());
    }

    protected void translate(long fps){

        float newX = getX() + (float) getDirectionVector().first * (speed/fps);
        float newY = getY() + (float) getDirectionVector().second * (speed/fps);
        setCoordinate(new Pair<>(newX, newY));

        for (GameObject object : allGameObjects) {
            // don't compare to self
            if(!object.equals(this) && checkCollision(object)) {
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
        Log.d("Joystick", "directionVector: " + directionVector);
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

    public float getRotation() {
        return rotation;
    }
}
