package tw.edu.ntpc.show3dobj.gl.anima;

import android.animation.TimeInterpolator;

import java.lang.reflect.Method;

import tw.edu.ntpc.show3dobj.gl.spiritgroup.LeGLAnimaSprite;

/**
 * 角色屬性動畫
 *
 * @author xiaxl1
 */
public class SpriteAnima {
    private static final String TAG = "SpriteAnima";

    // ######################介面相關啟動#######################

    SpriteAnimaListener mSpriteAnimaListener = null;

    /**
     * @param spriteAnimationListener
     */
    public void setAnimationListener(
            SpriteAnimaListener spriteAnimationListener) {
        mSpriteAnimaListener = spriteAnimationListener;
    }

    /**
     * 動畫的啟動與結束回呼函式
     */
    public interface SpriteAnimaListener {
        // 動畫啟動
        void onAnimaStart();

        // 進度
        void onAnimaProgress(float percent);

        // 動畫結束
        void onAnimaFinish();
    }


    // --------介面相關結束-------

    // #################################動畫屬性相關##################################

    // 動畫結束
    private boolean isAnimaRuning = false;
    // 動畫持續時間
    private int mAnimaDuration = 1000;
    // 動畫啟動時間
    private long mAnimaStartTime = 0;

    // ---動畫屬性---
    private float fromValue = 0;
    private float toValue = 0;
    private float currentValue = 0;

    /**
     * 動畫是否已經執行結束
     *
     * @return
     */
    public boolean isAnimaFinished() {
        if (mAnimaStartTime > 0 && isAnimaRuning == false) {
            return true;
        }
        return false;
    }


    //####################################反射set方法###################################
    LeGLAnimaSprite mSprite = null;
    // 對應的setter方法
    private Method mSetterMethod = null;

    /**
     * 用來反射 sprite屬性的set方法
     *
     * @param sprite
     * @param mathodName 方法名稱(方法參數，方法參數為float類型的方法)
     */
    public void setAnimaMethod(LeGLAnimaSprite sprite, String mathodName) {
        this.mSprite = sprite;
        try {
            mSetterMethod = sprite.getClass().getMethod(mathodName, float.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改資料
     */
    public void changeSpriteValue(float currentValue) {
        try {
            if (mSetterMethod != null && mSprite != null) {
                mSetterMethod.invoke(mSprite, currentValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // #################################執行動畫##################################

    /**
     * @param fromValue 啟動值
     * @param toValue   結束值
     */
    public void setAnimaValue(float fromValue, float toValue, int duration) {
        // #######################
        this.fromValue = fromValue;
        this.toValue = toValue;
        //
        this.currentValue = fromValue;
        // #######################
        this.mAnimaDuration = duration;
    }

    /**
     * 啟動動畫
     */
    public void startAnima() {
        mAnimaStartTime = -1;
        isAnimaRuning = true;
    }

    /**
     * 在ondraw中呼叫該方法，透過在ondraw中不斷循環呼叫
     *
     * @param drawTime
     */
    public void runAnimation(long drawTime) {
        if (!isAnimaRuning) {
            return;
        }
        //
        if (mAnimaStartTime == -1) {
            mAnimaStartTime = drawTime;
            // 回呼函式介面，動畫啟動
            if (mSpriteAnimaListener != null) {
                mSpriteAnimaListener.onAnimaStart();
            }
        }
        // 計算 時間差
        long runTime = drawTime - mAnimaStartTime;
        // 計算 進度
        float percent = (float) runTime / mAnimaDuration;
        if (percent > 1) {
            // 動畫結束
            percent = 1;
            // 更改動畫影格資料
            changeFrameData(percent);
            // 動畫結束
            isAnimaRuning = false;
            // 回呼函式介面，動畫結束
            if (mSpriteAnimaListener != null) {
                mSpriteAnimaListener.onAnimaFinish();
            }
            return;
        }
        // 執行動畫
        changeFrameData(percent);
    }

    /**
     * 更改動畫影格資料
     *
     * @param percent
     */
    private void changeFrameData(float percent) {
        // 回呼進度
        if (mSpriteAnimaListener != null) {
            mSpriteAnimaListener.onAnimaProgress(percent);
        }
        // 計算 動畫影格
        if (isAnimaRuning) {
            // 動畫差值器
            if (mTimeInterpolator != null) {
                percent = mTimeInterpolator.getInterpolation(percent);
            }

            // 計算 動畫影格
            currentValue = fromValue + percent * (toValue - fromValue);
            // 更改角色屬性值
            changeSpriteValue(currentValue);
        }
        //
    }


    // #################################動畫差值器##################################
    // The time interpolator to be used if none is set on the animation
    private TimeInterpolator mTimeInterpolator = null;


    public void setInterpolator(TimeInterpolator value) {
        mTimeInterpolator = value;
    }

}
