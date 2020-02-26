package com.android.tankwars;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

public abstract class GameObject implements Comparable<GameObject>{

    protected float x, y;
    protected int z;
    protected float width, height;
    protected RectF rect;

    protected float rotation;
    protected float speed;
    protected Pair<Float, Float> directionVector;
    protected Pair<Float, Float> coordinateVector;

    private boolean collidable = true;
    private boolean collided = false;

    private Paint color;

    protected static ArrayList<GameObject> allGameObjects = new ArrayList<>();
    protected static ArrayList<GameObject> toBeRemoved = new ArrayList<>();
    protected static ArrayList<GameObject> toBeAdded = new ArrayList<>();

    GameObject(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        z = 0;
        coordinateVector = new Pair<>(x,y);
        this.width = width;
        this.height = height;
        color = new Paint();
        rect = new RectF();
        setRect();
        rotation = 0.0f;
        setDirectionVector();

        // This is to avoid ConcurrentModificationException.
        toBeAdded.add(this); // add to start of array instead for bullets TODO
    }
    public abstract void collision(GameObject otherObject);

    public abstract void update(long fps);

    public void draw(Canvas canvas){
        canvas.drawRect(getRect(), getColor());
    }

    public boolean checkCollision(GameObject otherObject){
        return this.collidable && otherObject.collidable && this.rect.intersect(otherObject.getRect());
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
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

    public Pair getCoordinates() {
        return coordinateVector;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        setDirectionVector();
    }

    public float getRotation() {
        return rotation;
    }

    protected void setDirectionVector(){
        float x = (float) Math.cos(Math.toRadians(rotation));
        float y = (float) Math.sin(Math.toRadians(rotation));
        directionVector = new Pair(x,y);
    }

    public Pair<Float,Float> getDirectionVector(){
        return directionVector;
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

    protected void setColor(int argb) {
        this.getColor().setColor(argb);
    }
    public Paint getColor() {
        return color;
    }

    protected boolean getCollided() { return collided; }

    protected void setCollided(boolean collided) { this.collided = collided; }

    protected void setCollidable(boolean collidable) { this.collidable = collidable; }

    public int getZ() {
        return z;
    }

    protected void setZ(int z){
        this.z = z;
    }

    @Override
    public int compareTo(GameObject otherObject) {
        // utilize built in Integer compare as z is simply an int
        return Integer.compare(this.z, otherObject.getZ());
    }
}
