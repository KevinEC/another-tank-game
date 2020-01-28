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

public class TankWarsActivity extends Activity {

    TankWarsView tankWarsView;
    ArrayMap<String, Button> controls;

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
        controls.put("left", (Button)findViewById(R.id.controls_left));
        controls.put("right", (Button)findViewById(R.id.controls_right));
        controls.put("up", (Button)findViewById(R.id.controls_up));
        controls.put("down", (Button)findViewById(R.id.controls_down));
        controls.put("fire", (Button)findViewById(R.id.controls_fire));

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