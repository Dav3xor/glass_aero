package com.example.dave.glass_aero;

import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by dave on 10/20/15.
 */

public class MyGLRenderer implements GLSurfaceView.Renderer {

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        //GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        beforeBuffer   = makeSquare(-.8f, .8f, .8f, .2f);
        afterBuffer    = makeSquare(-.8f, -.2f, .8f, -.8f);
        vertexShader   = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
        shaderProgram  = GLES20.glCreateProgram();

        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);
    }



    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(shaderProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, 3,
                                     GLES20.GL_FLOAT, false,
                                     12, beforeBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }


    private FloatBuffer makeSquare(float minx, float miny, float maxx, float maxy) {
        ByteBuffer bb = ByteBuffer.allocateDirect(12 * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buf = bb.asFloatBuffer();
        buf.put(new float[]{minx, maxy, 0.0f,
                            maxx, maxy, 0.0f,
                            minx, miny, 0.0f,
                            maxx, miny, 0.0f});
        buf.position(0);
        return buf;
    }

    private final String vertexShaderSource =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderSource =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    float color[] = {0.0f, 1.0f, 0.0f, 1.0f};

    private FloatBuffer beforeBuffer;
    private FloatBuffer afterBuffer;

    private int shaderProgram;
    private int vertexShader;
    private int fragmentShader;
    private int colorHandle;
    private int positionHandle;
}
