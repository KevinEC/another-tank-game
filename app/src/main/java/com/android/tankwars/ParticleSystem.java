package com.android.tankwars;

import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.IntegerRes;

import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem {

    private ArrayList<Particle> particles = new ArrayList<>();

    private float x,y;

    // values used for transformation
    private boolean transform;
    private float transformationRotation;
    private Pair transformationRotationOrigin;

    private Pair<Float, Float> directionRange;
    private Pair<Float,Float> spinRange;
    private Pair<Float,Float> sizeRange;
    private Pair<Float,Float> distanceRange;
    private Pair<Float,Float> speedRange;
    private Pair<Float, Float> lifetimeRange;
    private Pair<Integer, Integer> spawnRate;

    private int color;

    private boolean spawnParticles = false;

    private Random random = new Random();
    private float setTransformationRotationTranslation;

    ParticleSystem(float x, float y, Pair<Float, Float> directionRange, Pair<Float, Float> spinRange, Pair<Float, Float> sizeRange, Pair<Float, Float> distanceRange, Pair<Float, Float> speedRange,Pair<Float, Float> lifetimeRange, int spawnRate, int color) {
        this.x = x; this.y = y;
        this.directionRange = directionRange;
        this.spinRange = spinRange;
        this.sizeRange = sizeRange;
        this.distanceRange = distanceRange;
        this.speedRange = speedRange;
        this.lifetimeRange = lifetimeRange;
        this.spawnRate = new Pair<>(0, spawnRate);
        this.color = color;

        transform = false;
        spawnParticles = true;
    }

    public void update(long fps) {
        if(spawnParticles)
            spawnParticles(fps);
    }

    private void spawnParticles(long fps){
        int numParticles = getRandomInt(spawnRate);
        Pair<Float, Float> coordinates = new Pair<>(0f, 0f);
        if(transform)
            coordinates = calcTransformedCoordinates();
        for (int i = 0; i < numParticles; i++) {
            if(transform)
                new Particle(coordinates.first, coordinates.second, getRandomFloat(spinRange), getRandomFloat(sizeRange), getRandomFloat(directionRange), getRandomFloat(distanceRange), getRandomFloat(speedRange), getRandomFloat(lifetimeRange), color);
            else
                new Particle(x, y, getRandomFloat(spinRange), getRandomFloat(sizeRange), getRandomFloat(directionRange), getRandomFloat(distanceRange), getRandomFloat(speedRange), getRandomFloat(lifetimeRange), color);
        }
    }

    private Float getRandomFloat(Pair<Float, Float> range) {
        float distance = range.second - range.first;
        return range.first + random.nextFloat() * distance;
    }

    private Integer getRandomInt(Pair<Integer, Integer> range) {
        float distance = range.second - range.first;
        return 1 + (int)(range.first + random.nextFloat() * distance);
    }

    public void setSpawnParticles(boolean spawnParticles) {
        this.spawnParticles = spawnParticles;
    }

    public void setDirectionRange(Pair<Float, Float> range) {
        directionRange = range;
    }

    public void setCoordinates(Pair coordinates) {
        x = (float) coordinates.first;
        y = (float) coordinates.second;
    }

    public void setTransformationRotation(float transformationRotation) {
        this.transformationRotation = transformationRotation;
    }

    public void setTransformationRotationOrigin(Pair coordinates) {
        this.transformationRotationOrigin = coordinates;
    }

    public void setTransformationRotationTranslation(float translation) {
        this.setTransformationRotationTranslation = translation;
    }

    public void setTransform(boolean state) {
        this.transform = state;
    }

    private Pair<Float, Float> calcTransformedCoordinates() {
        float unitX = (float) Math.cos(Math.toRadians(transformationRotation));
        float unitY = (float) Math.sin(Math.toRadians(transformationRotation));

        float newX  = (float) transformationRotationOrigin.first + unitX*setTransformationRotationTranslation;
        float newY  = (float) transformationRotationOrigin.second + unitY*setTransformationRotationTranslation;

        return new Pair<>(newX, newY);
    }

}
