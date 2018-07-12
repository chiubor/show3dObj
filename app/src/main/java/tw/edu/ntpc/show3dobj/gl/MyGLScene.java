package tw.edu.ntpc.show3dobj.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;

import tw.edu.ntpc.show3dobj.gl.anima.SpriteAnima;
import tw.edu.ntpc.show3dobj.gl.scene.LeGLBaseScene;
import tw.edu.ntpc.show3dobj.gl.spiritgroup.LeGLObjSpriteGroup;
import tw.edu.ntpc.show3dobj.gl.utils.MatrixState;
import tw.edu.ntpc.show3dobj.objloader.ObjLoaderUtil;

/*
 * GL SurfaceView
 */
public class MyGLScene extends LeGLBaseScene {

    private static final String TAG = "MyGLScene";
    private String objName = "superman.obj";
    private float objScale = 1;
    private float objY = -5;
    private float objAngleY = 0;

    public MyGLScene(Context context) {
        super(context);

        // 初始化render
        initRender();
    }

    public MyGLScene(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化render
        initRender();

    }

    public MyGLScene(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        // 初始化render
        initRender();

    }

    public void initRender() {
        // 初始化render
        MyGLSceneRenderer render = new MyGLSceneRenderer(this);
        this.setRenderer(render);
        // 彩現模式(被動彩現)
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        this.setSceneWidthAndHeight(this.getMeasuredWidth(),
                this.getMeasuredHeight());
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
    }

    @Override
    public void drawSelf(long drawTime) {
        super.drawSelf(drawTime);

        if (!isInintFinsh) {
            //
            initUI();
            //
            initTexture();
            //
            isInintFinsh = true;
        }

        MatrixState.pushMatrix();
        MatrixState.translate(0, objY, -15);
        MatrixState.scale(objScale, objScale, objScale);
        MatrixState.rotate(objAngleY,0,1,0);
        //
        mSpriteGroup.drawSelf(drawTime);

        MatrixState.popMatrix();

    }

    /**
     * 資料
     */
    // 是否初始化
    private boolean isInintFinsh = false;
    // 寬
    private float mSceneWidth = 720;
    // 高
    private float mSceneHeight = 1280;
    // obj資料
    ArrayList<ObjLoaderUtil.ObjData> mObjList ;


    /**
     * UI
     */
    LeGLObjSpriteGroup mSpriteGroup = null;


    /**
     * 初始化場景中的角色實體類
     */
    private void initUI() {

        try {
//            mObjList = ObjLoaderUtil.load("camaro.obj", this.getResources());
            mObjList = ObjLoaderUtil.load(this.objName, this.getResources());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        mSpriteGroup = new LeGLObjSpriteGroup(this, mObjList);

    }

    /**
     * 初始化紋理
     */
    private void initTexture() {
        //
    }

    public void setObjName(String objName) {
        this.objName = objName;
        this.isInintFinsh = false;
        this.requestRender();
    }

    public String getObjName() {
        return this.objName;
    }

    public void setObjAttr(float objScale, float objY, float objAngleY) {
        this.objScale = objScale;
        this.objY = objY;
        this.objAngleY = objAngleY;
    }

    public void setObjScale(float objScale) {
        this.objScale = objScale;
    }

    public float getObjScale() {
        return this.objScale;
    }

    public float getSceneWidth() {
        return mSceneWidth;
    }

    public float getSceneHeight() {
        return mSceneHeight;
    }

    public void setSceneWidthAndHeight(float mSceneWidth, float mSceneHeight) {
        this.mSceneWidth = mSceneWidth;
        this.mSceneHeight = mSceneHeight;
    }


    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度縮放比例
    private float mPreviousY;//上次的觸控位置Y坐標
    private float mPreviousX;//上次的觸控位置X坐標

    //觸摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//計算觸控筆Y位移
                float dx = x - mPreviousX;//計算觸控筆X位移
                //
                float yAngle = mSpriteGroup.getSpriteAngleY();
                yAngle += dx * TOUCH_SCALE_FACTOR;


                Log.e("xiaxl: ","yAngle: "+yAngle);

                mSpriteGroup.setSpriteAngleY(yAngle);

                this.requestRender();//重繪畫面
        }
        mPreviousY = y;//記錄觸控筆位置
        mPreviousX = x;//記錄觸控筆位置
        return true;
    }

    //##################################################

    /**
     *
     */
    public void startXZAnima() {
        //###################旋轉動畫###################
        // 建立動畫
        SpriteAnima rotateAnima = new SpriteAnima();
        // 動畫更改的方法
        rotateAnima.setAnimaMethod(mSpriteGroup, "setSpriteAngleY");
        // 从0到360度
        rotateAnima.setAnimaValue(0, 360, 2100);
        // 添加動畫差值器(超過，再回来)
        rotateAnima.setInterpolator(new OvershootInterpolator());

        //###################縮放動畫###################
        SpriteAnima scaleAnima = new SpriteAnima();
        scaleAnima.setAnimaMethod(mSpriteGroup, "setSpriteScale");
        scaleAnima.setAnimaValue(0.1f, 1.0f, 700);

        //###################
        // 添加動畫
        mSpriteGroup.addAnima(rotateAnima);
        mSpriteGroup.addAnima(scaleAnima);
        //##################
        // 啟動動畫
        mSpriteGroup.startAnimas();
    }


}