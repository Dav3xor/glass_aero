package com.example.dave.glass_aero; /**
 * Created by dave on 10/31/15.
 */
import android.content.Context;
import android.opengl.GLES20;

import com.example.dave.glass_aero.R;
import com.example.dave.glass_aero.TextureSquare;

/**
 * Created by dave on 10/31/15.
 */

public class LinearSquare extends TextureSquare {
    public void Draw(final int textureHandle) {
        super.setupDraw();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        super.finishDraw();


    }

    public LinearSquare(final Context context) {
        // hmmm, using R.(...) here is probably bogus, but
        // I'm not too hip on reusability in Android/Java
        super(context, R.raw.vertex_shader, R.raw.fragment_shader);

    }


}