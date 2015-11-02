package com.example.dave.glass_aero;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

/**
 * Created by dave on 10/30/15.
 *
 * Alright, here's my undistort class.  I've made a few assumptions:
 *
 * 1. It currently renders to whatever the current context is -- if you want to
 *    render to a texture, you'll have to set that up yourself.
 *
 * 2. If you want to do the newCameraMatrix argument of the OpenCV undistort function,
 *    you can run this a second time on the original output with the different matrix.
 *
 * 3. I couldn't get the OpenCV camera calibration to work on my Mac.  I have plugged in
 *    reasonable values to the function, and it seems to work as advertised, but if I
 *    have something inverted, or mirrored, or...   I don't know for sure.
 *
 *    I'm going to try installing OpenCV on my desktop Linux box, maybe I can get it to
 *    work there.
 */



public class UnDistort extends TextureSquare{
    public void Draw(final int textureHandle) {
        super.setupDraw();

        GLES20.glUniform2fv(focalLengthHandle, 1, focalLength, 0);
        GLES20.glUniform2fv(opticalCenterHandle, 1, opticalCenter, 0);
        GLES20.glUniform4fv(distortionCoefficientsHandle, 1, distortionCoefficients, 0);
        GLES20.glUniform2fv(imageSizeHandle, 1, imageSize, 0);
        GLES20.glUniform2fv(tangentialCoefficientsHandle, 1, tangentialCoefficients, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        super.finishDraw();


    }

    public UnDistort(final Context context) {
        // hmmm, using R.(...) here is probably not a great idea, but
        // I'm not too hip on best practices Android/Java.
        super(context, R.raw.vertex_shader, R.raw.undistort_shader);

        focalLengthHandle            = GLES20.glGetUniformLocation(shaderProgram, "focalLength");
        opticalCenterHandle          = GLES20.glGetUniformLocation(shaderProgram, "opticalCenter");
        distortionCoefficientsHandle = GLES20.glGetUniformLocation(shaderProgram, "distortionCoefficients");
        tangentialCoefficientsHandle = GLES20.glGetUniformLocation(shaderProgram, "tangentialCoefficients");
        imageSizeHandle              = GLES20.glGetUniformLocation(shaderProgram, "imageSize");
    }
    public void setFocalLength(float x, float y) {
        focalLength[0] = x;
        focalLength[1] = y;
    }

    public void setOpticalCenter(float x, float y) {
        opticalCenter[0] = x;
        opticalCenter[1] = y;
    }

    public void setImageSize(float x, float y) {
        imageSize[0] = x;
        imageSize[1] = y;
    }

    public void setDistortionCoefficients(float c1, float c2, float c3, float c4) {
        distortionCoefficients[0] = c1;
        distortionCoefficients[1] = c2;
        distortionCoefficients[2] = c3;
        distortionCoefficients[3] = c4;
    }

    public void setTangentialCoefficients(float x, float y) {
        tangentialCoefficients[0] = x;
        tangentialCoefficients[1] = y;
    }

    // set these to reasonable defaults that should display something...
    private float focalLength[]            = {512.0f, 384.0f};
    private float opticalCenter[]          = {512.0f, 384.0f};
    private float distortionCoefficients[] = {-0.35109f, -0.02393f, 0.00335f, -0.00449f};
    private float tangentialCoefficients[] = {0.0f, 0.0f};
    private float imageSize[]              = {1024.0f, 768.0f};





    private int focalLengthHandle;
    private int opticalCenterHandle;
    private int distortionCoefficientsHandle;
    private int tangentialCoefficientsHandle;
    private int imageSizeHandle;
}
