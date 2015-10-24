package com.example.dave.glass_aero;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.opengl.GLSurfaceView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyGLSurfaceView mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }
}
