package tw.edu.ntpc.show3dobj.gl.spiritgroup;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import tw.edu.ntpc.show3dobj.gl.scene.LeGLBaseScene;
import tw.edu.ntpc.show3dobj.gl.spirit.LeGLBaseSpirit;
import tw.edu.ntpc.show3dobj.gl.spirit.LeGLObjColorSpirit;
import tw.edu.ntpc.show3dobj.gl.spirit.LeGLObjTextureSpirit;
import tw.edu.ntpc.show3dobj.gl.utils.BitmapUtil;
import tw.edu.ntpc.show3dobj.gl.utils.MatrixState;
import tw.edu.ntpc.show3dobj.objloader.ObjLoaderUtil;

/**
 * @author xiaxveliang
 */
public class LeGLObjSpriteGroup extends LeGLAnimaSprite {
    private static final String TAG = LeGLObjSpriteGroup.class.getSimpleName();


    private ArrayList<LeGLBaseSpirit> mObjSprites = new ArrayList<LeGLBaseSpirit>();

    public LeGLObjSpriteGroup(LeGLBaseScene scene, ArrayList<ObjLoaderUtil.ObjData> objDatas) {
        super(scene);
        //
        initObjs(objDatas);
    }

    private void initObjs(ArrayList<ObjLoaderUtil.ObjData> objDatas) {
        mObjSprites.clear();
        if (objDatas != null) {
            for (int i = 0; i < objDatas.size(); i++) {

                Log.e("xiaxl: ", "i: " + i);

                ObjLoaderUtil.ObjData data = objDatas.get(i);
                //
                int diffuseColor = data.mtlData != null ? data.mtlData.Kd_Color : 0xffffffff;
                float alpha = data.mtlData != null ? data.mtlData.alpha : 1.0f;
                String texturePath = data.mtlData != null ? data.mtlData.Kd_Texture : "";

                // 建構物件
                if (data.aTexCoords != null && data.aTexCoords.length != 0 && !TextUtils.isEmpty(texturePath)) {
                    Log.e("xiaxl: ", "texture");

                    Bitmap bmp = BitmapUtil.getBitmapFromAsset(getBaseScene().getContext(), texturePath);
                    LeGLBaseSpirit spirit = new LeGLObjTextureSpirit(getBaseScene(), data.aVertices, data.aNormals, data.aTexCoords, alpha, bmp);
                    mObjSprites.add(spirit);
                } else {

                    Log.e("xiaxl: ", "color");

                    LeGLBaseSpirit spirit = new LeGLObjColorSpirit(getBaseScene(), data.aVertices, data.aNormals, diffuseColor, alpha);
                    mObjSprites.add(spirit);
                }
            }
        }
    }


    @Override
    public void drawSelf(long drawTime) {
        super.drawSelf(drawTime);

        MatrixState.pushMatrix();

        // 縮放
        MatrixState.scale(this.getSpriteScale(),
                this.getSpriteScale(), this.getSpriteScale());
        // 旋轉
        //MatrixState.rotate(this.getSpriteAngleX(), 1, 0, 0);
        // 旋轉
        MatrixState.rotate(this.getSpriteAngleY(), 0, 1, 0);
        // 繪製
        for (int i = 0; i < mObjSprites.size(); i++) {
            LeGLBaseSpirit sprite = mObjSprites.get(i);
            sprite.drawSelf(drawTime);
        }

        MatrixState.popMatrix();

    }

}