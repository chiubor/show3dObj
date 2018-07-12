package tw.edu.ntpc.show3dobj.gl.spirit;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import tw.edu.ntpc.show3dobj.gl.scene.LeGLBaseScene;
import tw.edu.ntpc.show3dobj.gl.utils.MatrixState;
import tw.edu.ntpc.show3dobj.gl.utils.ShaderUtil;
import tw.edu.ntpc.show3dobj.gl.utils.TextureUtil;

/**
 * 載入後的物體
 */
public class LeGLObjTextureSpirit extends LeGLBaseSpirit {
    int mProgram;//自定義彩現管線著色器程序id
    int muMVPMatrixHandle;//總變換矩陣引用
    int muMMatrixHandle;//位置、旋轉變換矩陣
    int maPositionHandle; //頂點位置屬性引用
    int maNormalHandle; //頂點法向量屬性引用
    int maLightLocationHandle;//光源位置屬性引用
    int maCameraHandle; //攝影機位置屬性引用
    int maTexCoorHandle; //頂點紋理坐標屬性引用
    int muColorHandle; // 頂點顏色
    int muOpacityHandle; // 材質中透明度
    String mVertexShader;//頂點著色器代码脚本
    String mFragmentShader;//片元著色器代码脚本

    FloatBuffer mVertexBuffer;//頂點坐標資料緩衝
    FloatBuffer mNormalBuffer;//頂點法向量資料緩衝
    FloatBuffer mTexCoorBuffer;//頂點紋理坐標資料緩衝

    // 材質中alpha
    protected float mAlpha;
    // 需轉化為紋理的圖片
    protected Bitmap mBmp;
    //
    int vCount = 0;
    /**
     *
     */
    // 紋理是否已載入
    protected boolean isInintFinsh = false;
    // 紋理id
    protected int textureId;


    public LeGLObjTextureSpirit(LeGLBaseScene scene, float[] vertices, float[] normals, float texCoors[], float alpha, Bitmap bmp) {
        //初始化頂點坐標著色資料
        initVertexData(vertices, normals, texCoors, alpha, bmp);
        //初始化shader
        initShader(scene.getResources());
    }

    //初始化頂點坐標著色資料的方法
    public void initVertexData(float[] vertices, float[] normals, float texCoors[], float alpha, Bitmap bmp) {
        this.mAlpha = alpha;
        this.mBmp = bmp;
        //頂點坐標資料的初始化================begin============================
        vCount = vertices.length / 3;

        //建立頂點坐標資料緩衝
        //vertices.length*4是因為一個整數四個字節
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//設置字節順序
        mVertexBuffer = vbb.asFloatBuffer();//轉換為Float型緩衝
        mVertexBuffer.put(vertices);//向緩衝區中放入頂點坐標資料
        mVertexBuffer.position(0);//設置緩衝區起始位置
        //特别提示：由於不同平台字節順序不同，資料單元不是字節的一定要經過ByteBuffer
        //轉換，關鍵是要通過ByteOrder設置nativeOrder()，否則有可能會出問題
        //頂點坐標資料的初始化================end============================

        //頂點法向量資料的初始化================begin============================
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length * 4);
        cbb.order(ByteOrder.nativeOrder());//設置字節顺序
        mNormalBuffer = cbb.asFloatBuffer();//轉換為Float型緩衝
        mNormalBuffer.put(normals);//向緩衝區中放入頂點法向量資料
        mNormalBuffer.position(0);//設置緩衝區起始位置
        //特别提示：由於不同平台字節順序不同，資料單元不是字節的一定要經過ByteBuffer
        //轉換，關鍵是要通過ByteOrder設置nativeOrder()，否則有可能會出問題
        //頂點著色資料的初始化================end============================

        //頂點紋理坐標資料的初始化================begin============================
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length * 4);
        tbb.order(ByteOrder.nativeOrder());//設置字節順序
        mTexCoorBuffer = tbb.asFloatBuffer();//轉換為Float型緩衝
        mTexCoorBuffer.put(texCoors);//向緩衝區中放入頂點紋理坐標資料
        mTexCoorBuffer.position(0);//設置緩衝區起始位置
        //特别提示：由於不同平台字節順序不同，資料單元不是字節的一定要經過ByteBuffer
        //轉換，關鍵是要通過ByteOrder設置nativeOrder()，否則有可能會出問題
        //頂點紋理坐標資料的初始化================end============================
    }

    //初始化shader
    public void initShader(Resources res) {
        //載入頂點著色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("shader/texture_vertex.sh", res);
        //載入片元著色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("shader/texture_frag.sh", res);
        //基於頂點著色器與片元著色器建立程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //取得程序中頂點位置屬性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //取得程序中頂點顏色屬性引用
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        //取得程序中總變換矩陣引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //取得位置、旋轉變換矩陣引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //取得程序中光源位置引用
        maLightLocationHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //取得程序中頂點紋理坐標屬性引用
        maTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //取得程序中攝影機位置引用
        maCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");
        // 頂點顏色
        muColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        // alpha
        muOpacityHandle = GLES20.glGetUniformLocation(mProgram, "uOpacity");
    }

    /**
     * 初始化紋理
     */
    private void initTexture() {
        //兩球之間連線的紋理图片
        if (mBmp != null) {
            textureId = TextureUtil.getTextureIdByBitmap(mBmp);
        }
    }


    public void drawSelf(long drawTime) {
        //載入紋理
        if (isInintFinsh == false) {
            initTexture();
            isInintFinsh = true;
        }

        //制定使用某套著色器程序
        GLES20.glUseProgram(mProgram);
        //將最终變換矩陣傳入著色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //將位置、旋轉變換矩陣傳入著色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //將光源位置傳入著色器程序
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //將攝影機位置傳入著色器程序
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //將頂點位置資料傳入彩現管線
        GLES20.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //將頂點法向量資料傳入彩現管線
        GLES20.glVertexAttribPointer
                (
                        maNormalHandle,
                        3,
                        GLES20.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );
        //顏色相關

        //為畫筆指定頂點紋理坐標資料
        GLES20.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES20.GL_FLOAT,
                        false,
                        2 * 4,
                        mTexCoorBuffer
                );
        //材質alpha
        GLES20.glUniform1f(muOpacityHandle, mAlpha);
        //啟用頂點紋理陣列
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        //啟用頂點位置、法向量、紋理坐標資料
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        //繪製載入的物體
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }

}
