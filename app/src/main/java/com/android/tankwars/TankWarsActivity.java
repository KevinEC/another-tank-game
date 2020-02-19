package com.android.tankwars;


import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class TankWarsActivity extends Activity {

    TankWarsView tankWarsView;
    ArrayMap<String, View> controls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        // add view for controls
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        View controlsView = LayoutInflater.from(this).inflate(R.layout.controls, null, false);
        addContentView(controlsView, lp);

        controls = new ArrayMap<>();
        controls.put("left", findViewById(R.id.controls_left));
        controls.put("right",findViewById(R.id.controls_right));
        controls.put("up", findViewById(R.id.controls_up));
        controls.put("down", findViewById(R.id.controls_down));
        controls.put("fire", findViewById(R.id.controls_fire));
        controls.put("joystick", findViewById(R.id.joystick));
        controls.put("controlArea", findViewById(R.id.movement_controls));


        tankWarsView = new TankWarsView(this, size.x, size.y, controls);
        setContentView(tankWarsView);
        addContentView(controlsView, lp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        tankWarsView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tankWarsView.pause();
    }
}