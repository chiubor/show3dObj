package tw.edu.ntpc.show3dobj.gl.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

public class TextureUtil {

    /**
     * @param context
     * @param drawableId
     * @return
     */
    public static int getTextureIdByDrawableId(Context context, int drawableId)// textureId
    {
        //生成紋理ID
        int[] textures = new int[1];
        GLES20.glGenTextures
                (
                        1,          //產生的紋理id的數量
                        textures,     //紋理id的陣列
                        0          //偏移量
                );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        //通過輸入流載入圖片===============begin===================
        InputStream is = context.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通過輸入流載入圖片===============end=====================
        GLUtils.texImage2D
                (
                        GLES20.GL_TEXTURE_2D, //紋理類型
                        0,
                        GLUtils.getInternalFormat(bitmapTmp),
                        bitmapTmp, //紋理圖像
                        GLUtils.getType(bitmapTmp),
                        0   //紋理邊框尺寸
                );
        bitmapTmp.recycle();          //紋理載入成功後釋放圖片
        return textureId;
    }


    /**
     * @param bmp
     * @return
     */
    public static int getTextureIdByBitmap(Bitmap bmp)// textureId
    {
        // 生成紋理ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, // 產生的紋理id的數量
                textures, // 紋理id的陣列
                0      // 偏移量
        );
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        // 實際載入紋理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // 紋理類型
                0, // 紋理的層次，0表示基本圖像層，可以理解為直接貼圖
                bmp, // 紋理圖像
                0 // 紋理邊框尺寸
        );
        bmp.recycle(); // 紋理載入成功後釋放圖片

        return textureId;
    }

}
