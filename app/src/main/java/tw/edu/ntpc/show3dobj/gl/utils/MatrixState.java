package tw.edu.ntpc.show3dobj.gl.utils;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

//儲存系统矩陣狀態的類
public class MatrixState {
    private static float[] mProjMatrix = new float[16];//4x4矩陣 投影用
    private static float[] mVMatrix = new float[16];//攝影機位置朝向9參數矩陣
    private static float[] currMatrix;//當前變換矩陣
    public static float[] lightLocation = new float[]{0, 0, 0};//定位光光源位置
    public static FloatBuffer cameraFB;
    public static FloatBuffer lightPositionFB;

    public static Stack<float[]> mStack = new Stack<float[]>();//保護變換矩陣的堆疊

    public static void setInitStack()//取得不變換初始矩陣
    {
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
    }

    public static void pushMatrix()//保護變換矩陣
    {
        mStack.push(currMatrix.clone());
    }

    public static void popMatrix()//恢復變換矩陣
    {
        currMatrix = mStack.pop();
    }

    public static void translate(float x, float y, float z)//設置沿xyz軸移動
    {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    public static void rotate(float angle, float x, float y, float z)//設置繞xyz軸旋轉
    {
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        Matrix.scaleM(currMatrix, 0, x, y, z);
    }



    //設置攝影機
    public static void setCamera
    (
            float cx,   //攝影機位置x
            float cy,   //攝影機位置y
            float cz,   //攝影機位置z
            float tx,   //攝影機目標点x
            float ty,   //攝影機目標点y
            float tz,   //攝影機目標点z
            float upx,  //攝影機UP向量X分量
            float upy,  //攝影機UP向量Y分量
            float upz   //攝影機UP向量Z分量
    ) {
        Matrix.setLookAtM
                (
                        mVMatrix,
                        0,
                        cx,
                        cy,
                        cz,
                        tx,
                        ty,
                        tz,
                        upx,
                        upy,
                        upz
                );

        float[] cameraLocation = new float[3];//攝影機位置
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;

        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());//設置字節順序
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    //設置透視投影參數
    public static void setProjectFrustum
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距離
            float far       //far面距離
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //設置正交投影參數
    public static void setProjectOrtho
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距離
            float far       //far面距離
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //取得具體物體的總變換矩陣
    public static float[] getFinalMatrix() {
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    //取得具體物體的變換矩陣
    public static float[] getMMatrix() {
        return currMatrix;
    }

    //設置燈光位置的方法
    public static void setLightLocation(float x, float y, float z) {
        lightLocation[0] = x;
        lightLocation[1] = y;
        lightLocation[2] = z;
        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());//設置字節順序
        lightPositionFB = llbb.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
    }
}
