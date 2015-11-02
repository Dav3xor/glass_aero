package com.example.dave.glass_aero;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by dave on 10/30/15.
 */
public class TextureSquare {
    public void setupDraw() {
        GLES20.glUseProgram(shaderProgram);

        GLES20.glUniform1i(textureUniformHandle, 0);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
    }
    public void finishDraw() {
        GLES20.glVertexAttribPointer(positionHandle, 3,
                                     GLES20.GL_FLOAT, false,
                                     stride, vertices);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2,
                                     GLES20.GL_FLOAT, false,
                                     textureStride, textureVertices);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    public void setVertices(float minx, float miny, float maxx, float maxy) {
        vertices = makeSquare(minx, miny, maxx, maxy);
    }

    public TextureSquare(final Context context, int vertexShaderID, int fragmentShaderID) {
        // I couldn't figure out how to make both of these as parts of one big array
        // in the Android OpenGL api, like I would normally do in the C api, so they're
        // separated here.
        vertices = makeSquare(0.0f, 0.0f, 1.0f, 1.0f);
        textureVertices = makeTextureSquare();

        vertexShader    = loadShader(GLES20.GL_VERTEX_SHADER,
                                     loadShaderResource(context, vertexShaderID));
        fragmentShader  = loadShader(GLES20.GL_FRAGMENT_SHADER,
                                     loadShaderResource(context, fragmentShaderID));
        shaderProgram   = GLES20.glCreateProgram();

        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        // for when running in the debugger...
        String blah = GLES20.glGetShaderInfoLog(fragmentShader);

        positionHandle          = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        textureUniformHandle    = GLES20.glGetUniformLocation(shaderProgram, "uTexture");
        textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexture");
    }

    protected static int loadShader(int type, String shaderCode){

        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    protected static String loadShaderResource(final Context context,
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

    private FloatBuffer vertices;
    private FloatBuffer textureVertices;
    private int stride = 12;
    private int textureStride = 8;
    private int positionHandle;
    private int textureCoordinateHandle;
    private int textureUniformHandle;
    private int fragmentShader;
    private int vertexShader;
    protected int shaderProgram;

}
