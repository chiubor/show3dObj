package tw.edu.ntpc.show3dobj.gl.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {
    /**
     * 根據檔名,從Assets中取圖片
     *
     * @param context
     * @param fileName 例如:game_bg.png
     * @return
     */
    public static Bitmap getBitmapFromAsset(Context context, String fileName) {
        Bitmap bmp = null;
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        AssetManager asm = context.getAssets();
        if (asm == null) {
            return bmp;
        }
        InputStream is = null;
        try {
            is = asm.open(fileName);
            bmp = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bmp;
    }


}
