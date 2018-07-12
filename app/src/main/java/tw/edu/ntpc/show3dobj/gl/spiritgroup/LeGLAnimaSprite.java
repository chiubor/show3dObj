package tw.edu.ntpc.show3dobj.gl.spiritgroup;


import java.util.ArrayList;

import tw.edu.ntpc.show3dobj.gl.anima.SpriteAnima;
import tw.edu.ntpc.show3dobj.gl.scene.LeGLBaseScene;

/**
 * 基礎的角色類
 *
 * @author xiaxl1
 */
public class LeGLAnimaSprite {
    private static final String TAG = "LeGLAnimaSprite";

    //####################物件上下文######################
    /**
     * 物件上下文
     */
    private LeGLBaseScene mBaseScene = null;

    /**
     * 建構方法
     *
     * @param scene
     */
    public LeGLAnimaSprite(LeGLBaseScene scene) {
        this.mBaseScene = scene;

    }

    /**
     * 取得物件上下文
     *
     * @return
     */
    public LeGLBaseScene getBaseScene() {
        return mBaseScene;
    }

    //###################角色的屬性值#######################

    /**
     * 角色的屬性值
     */
    // 角色縮放大小
    private float mSpriteScale = 1;
    // 角色的alpha数值
    private float mSpriteAlpha = 1;
    // 旋转
    private float mSpriteAngleX = 0;
    private float mSpriteAngleY = 0;
    private float mSpriteAngleZ = 0;

    public float getSpriteScale() {
        return mSpriteScale;
    }

    public void setSpriteScale(float mSpriteScale) {
        this.mSpriteScale = mSpriteScale;
    }

    public float getSpriteAlpha() {
        return mSpriteAlpha;
    }

    public void setSpriteAlpha(float mSpriteAlpha) {
        this.mSpriteAlpha = mSpriteAlpha;
    }

    public float getSpriteAngleX() {
        return mSpriteAngleX;
    }

    public void setSpriteAngleX(float mSpriteAngleX) {
        this.mSpriteAngleX = mSpriteAngleX;
    }

    public float getSpriteAngleY() {
        return mSpriteAngleY;
    }

    public void setSpriteAngleY(float mSpriteAngleY) {
        this.mSpriteAngleY = mSpriteAngleY;
    }

    public float getSpriteAngleZ() {
        return mSpriteAngleZ;
    }

    public void setSpriteAngleZ(float mSpriteAngleZ) {
        this.mSpriteAngleZ = mSpriteAngleZ;
    }

    //###################動畫#######################
    //
    private ArrayList<SpriteAnima> mAnimaList = new ArrayList<SpriteAnima>();

    /**
     * 添加動畫
     *
     * @param anima
     */
    public void addAnima(SpriteAnima anima) {
        if (anima != null) {
            mAnimaList.add(anima);
        }
    }

    /**
     * 啟動動畫
     */
    public void startAnimas() {
        for (int i = 0; i < mAnimaList.size(); i++) {
            mAnimaList.get(i).startAnima();
        }
        // 請求刷新頁面
        this.getBaseScene().requestRender();
    }

    private int index = 0;

    /**
     * 繪製方法
     *
     * @param drawTime
     */
    public void drawSelf(long drawTime) {
        // ---執行動畫---
        if (mAnimaList != null && mAnimaList.size() != 0) {
            for (index = mAnimaList.size() - 1; index >= 0; index--) {
                SpriteAnima anima = mAnimaList.get(index);
                // 移除執行結束的動畫
                if (anima.isAnimaFinished()) {
                    mAnimaList.remove(anima);
                    continue;
                }
                // 執行動畫
                anima.runAnimation(drawTime);
            }
            // 請求刷新頁面
            this.getBaseScene().requestRender();
        }
    }


}
