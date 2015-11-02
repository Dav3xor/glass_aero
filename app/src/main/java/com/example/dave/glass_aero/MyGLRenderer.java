package com.example.dave.glass_aero;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dave on 10/20/15.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {
    public MyGLRenderer(final Context activityContext){
        context = activityContext;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        //GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        // first create a simple textured square renderer for the
        // 'before' image.
        linear                 = new LinearSquare(context);

        // and an undistorted image for after.
        undistort              = new UnDistort(context);

        //put the original bitmap at the top of the screen
        linear.setVertices(-.8f, .85f, .8f, .1f);
        // and the undistorted image underneath.
        undistort.setVertices(-.8f, -.15f, .8f, -.9f);


        undistort.setFocalLength(1100.0f, 800.0f);
        undistort.setOpticalCenter(512.0f, 384.0f);
        undistort.setImageSize(1024.0f, 768.0f);
        undistort.setDistortionCoefficients(0.5f, 0.1f, 0.1f, 0.0f);
        undistort.setTangentialCoefficients(0.0000f, -0.0003f);

        originalTextureHandle = loadTexture(context,
                R.drawable.test_pattern);




    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // draw the before image
        linear.Draw(originalTextureHandle);

        // for a demo, make a highly distored image (for fun)
        Long longtime = ((System.currentTimeMillis())%1000000);
        Double curtime = longtime.doubleValue()/1000.0;

        // fun house mirror mode, comment these out if you want the simple demo
        //undistort.setFocalLength((float) (712.0 + Math.cos(curtime)*500.0),
        //                         (float) (584.0 + Math.sin(curtime)*300.0));

        //undistort.setOpticalCenter((float) (512.0 + Math.sin(curtime) * 10.0),
        //                           (float) (384.0 + Math.cos(curtime) * 10.0));

        //undistort.setDistortionCoefficients((float) (Math.sin(curtime) * .8),
        //                                    (float) (Math.cos(curtime) * .8),
        //                                    (float) (Math.sin(curtime) * .5),
        //                                    (float) (Math.cos(curtime) * .5));
        //undistort.setTangentialCoefficients((float) Math.sin(curtime)*.0002f,
        //                                    (float) Math.cos(curtime)*.0002f);

        undistort.Draw(originalTextureHandle);



    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


    private UnDistort undistort;
    private LinearSquare linear;

    private Context context;

    private int originalTextureHandle;




}

