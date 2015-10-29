package com.example.dave.glass_aero;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    public static String loadShaderResource(final Context context,
                                      final int resourceId)
    {
        final InputStream inputStream = context.getResources().openRawResource(
                resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(
                inputStream);
        final BufferedReader bufferedReader = new BufferedReader(
                inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
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
    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        //GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        beforeBuffer           = makeSquare(-.8f, .9f, .8f, .15f);
        afterBuffer            = makeSquare(-.8f, -.15f, .8f, -.9f);
        textureBuffer          = makeTextureSquare();
        String vertexSource    = loadShaderResource(context, R.raw.vertex_shader);
        String fragmentSource  = loadShaderResource(context, R.raw.fragment_shader);
        String undistortSource = loadShaderResource(context, R.raw.undistort_shader);

        vertexShader           = loadShader(GLES20.GL_VERTEX_SHADER,
                                            vertexSource);
        fragmentShader         = loadShader(GLES20.GL_FRAGMENT_SHADER,
                                            fragmentSource);
        undistortShader        = loadShader(GLES20.GL_FRAGMENT_SHADER,
                                            undistortSource);
        shaderProgram          = GLES20.glCreateProgram();
        undistortProgram       = GLES20.glCreateProgram();

        originalTextureHandle = loadTexture(context,
                R.drawable.test_pattern);


        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        GLES20.glAttachShader(undistortProgram, vertexShader);
        GLES20.glAttachShader(undistortProgram, undistortShader);
        GLES20.glLinkProgram(undistortProgram);

        String blah = GLES20.glGetShaderInfoLog(undistortShader);

        // get handles for shaders
        positionHandle1          = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        textureUniformHandle1    = GLES20.glGetUniformLocation(shaderProgram, "uTexture");
        textureCoordinateHandle1 = GLES20.glGetAttribLocation(shaderProgram, "aTexture");
        colorHandle1             = GLES20.glGetUniformLocation(shaderProgram, "uColor");

        positionHandle2          = GLES20.glGetAttribLocation(undistortProgram, "aPosition");
        textureUniformHandle2    = GLES20.glGetUniformLocation(undistortProgram, "uTexture");
        textureCoordinateHandle2 = GLES20.glGetAttribLocation(undistortProgram, "aTexture");
        colorHandle2             = GLES20.glGetUniformLocation(undistortProgram, "uColor");
    }


    public void drawSquare(int shader, FloatBuffer buf) {

    }
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(shaderProgram);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle1);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle1);


        // get handle to fragment shader's vColor member


        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle1, 1, color, 0);


        // Draw the before rectangle
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, originalTextureHandle);
        GLES20.glUniform1i(textureUniformHandle1, 0);
        GLES20.glUniform1i(textureUniformHandle2, 0);
        GLES20.glVertexAttribPointer(positionHandle1, 3,
                GLES20.GL_FLOAT, false,
                stride, beforeBuffer);


        GLES20.glVertexAttribPointer(textureCoordinateHandle1, 2,
                GLES20.GL_FLOAT, false,
                8, textureBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        // Draw the after rectangle
        GLES20.glUseProgram(undistortProgram);
        GLES20.glUniform4fv(colorHandle2, 1, color, 0);
        GLES20.glEnableVertexAttribArray(positionHandle2);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle2);
        GLES20.glVertexAttribPointer(positionHandle2, 3,
                GLES20.GL_FLOAT, false,
                stride, afterBuffer);
        GLES20.glVertexAttribPointer(textureCoordinateHandle2, 2,
                GLES20.GL_FLOAT, false,
                8, textureBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(positionHandle2);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }


    private FloatBuffer makeSquare(float minx, float miny, float maxx, float maxy) {
        ByteBuffer bb = ByteBuffer.allocateDirect(stride * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buf = bb.asFloatBuffer();
        buf.put(new float[]{minx, maxy, 0.0f,
                            maxx, maxy, 0.0f,
                            minx, miny, 0.0f,
                            maxx, miny, 0.0f,});
        buf.position(0);
        return buf;
    }

    private FloatBuffer makeTextureSquare() {
        ByteBuffer bb = ByteBuffer.allocateDirect(8 * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buf = bb.asFloatBuffer();
        buf.put(new float[]{0.0f, 1.0f,
                            1.0f, 1.0f,
                            0.0f, 0.0f,
                            1.0f, 0.0f});
        buf.position(0);
        return buf;
    }

    float color[] = {0.0f, .2f, 0.0f, 1.0f};
    private final int stride=12;
    private FloatBuffer beforeBuffer;
    private FloatBuffer afterBuffer;
    private FloatBuffer textureBuffer;
    private Context context;
    private int shaderProgram;
    private int undistortProgram;
    private int vertexShader;
    private int fragmentShader;
    private int undistortShader;
    private int originalTextureHandle;

    private int positionHandle1;
    private int colorHandle1;
    private int textureUniformHandle1;
    private int textureCoordinateHandle1;
    private int positionHandle2;
    private int colorHandle2;
    private int textureUniformHandle2;
    private int textureCoordinateHandle2;
}

