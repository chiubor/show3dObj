package tw.edu.ntpc.show3dobj.gl.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 載入頂點Shader與片元Shader的工具類
 */
public class ShaderUtil {
    //載入制定shader的方法
    public static int loadShader
    (
            int shaderType, //shader的類型  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
            String source   //shader的脚本字串
    ) {
        //建立一個新shader
        int shader = GLES20.glCreateShader(shaderType);
        //若建立成功則載入shader
        if (shader != 0) {
            //載入shader的原始碼
            GLES20.glShaderSource(shader, source);
            //編譯shader
            GLES20.glCompileShader(shader);
            //存放編譯成功shader數量的陣列
            int[] compiled = new int[1];
            //取得Shader的編譯情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {//若編譯失敗則顯示錯誤日誌並删除此shader
                Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    //建立shader程序的方法
    public static int createProgram(String vertexSource, String fragmentSource) {
        //載入頂點著色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        //載入片元著色器
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        //建立程序
        int program = GLES20.glCreateProgram();
        //若程序建立成功則向程序中加入頂點著色器與片元著色器
        if (program != 0) {
            //向程序中加入頂點著色器
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            //向程序中加入片元著色器
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //鏈接程序
            GLES20.glLinkProgram(program);
            //存放鏈接成功program狀態的陣列
            int[] linkStatus = new int[1];
            //取得program的鏈接狀態
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            //若鏈接失敗則報錯並删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    //檢查每一步操作是否有錯誤的方法
    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("ES20_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    //從sh脚本中載入shader内容的方法
    public static String loadFromAssetsFile(String fname, Resources r) {
        String result = null;
        try {
            InputStream in = r.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
