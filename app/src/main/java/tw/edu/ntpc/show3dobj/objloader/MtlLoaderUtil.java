package tw.edu.ntpc.show3dobj.objloader;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author xiaxl
 *         <p>
 *         create by xiaxl on 2017.08.09
 *         載入材質工具類
 */
public class MtlLoaderUtil {

    private static final String TAG = "MtlLoaderUtil";


    /**
     * 載入材質的方法
     *
     * @param fname assets的mtl文件路徑
     * @param res
     * @return
     */
    public static HashMap<String, MtlData> load(String fname, Resources res) throws Exception {
        // 材質陣列
        HashMap<String, MtlData> mMTLMap = new HashMap<String, MtlData>();
        //
        if (res == null || TextUtils.isEmpty(fname)) {
            return mMTLMap;
        }
        //
        MtlData currMtlData = null;
        try {
            // 讀取assets下文件
            InputStream in = res.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader buffer = new BufferedReader(isr);
            // 行資料
            String line;
            //
            while ((line = buffer.readLine()) != null) {
                // Skip comments and empty lines.
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                //
                StringTokenizer parts = new StringTokenizer(line, " ");
                int numTokens = parts.countTokens();
                if (numTokens == 0) {
                    continue;
                }
                //
                String type = parts.nextToken();
                type = type.replaceAll("\\t", "");
                type = type.replaceAll(" ", "");

                // 定義一個名為 'xxx'的材質
                if (type.equals(MtlLoaderUtil.NEWMTL)) {
                    String name = parts.hasMoreTokens() ? parts.nextToken() : "def";
                    // 將上一個物件加入到陣列中
                    if (currMtlData != null) {
                        mMTLMap.put(currMtlData.name, currMtlData);
                    }
                    // 創建材質物件
                    currMtlData = new MtlData();
                    // 材質物件名稱
                    currMtlData.name = name;
                }
                // 環境光
                else if (type.equals(MtlLoaderUtil.KA)) {
                    currMtlData.Ka_Color = getColorFromParts(parts);
                }
                // 散射光
                else if (type.equals(MtlLoaderUtil.KD)) {
                    currMtlData.Kd_Color = getColorFromParts(parts);
                }
                // 鏡面光
                else if (type.equals(MtlLoaderUtil.KS)) {
                    currMtlData.Ks_Color = getColorFromParts(parts);
                }
                // 高光調整參數
                else if (type.equals(MtlLoaderUtil.NS)) {
                    String ns = parts.nextToken();
                    currMtlData.ns = Float.parseFloat(ns);
                }
                // 溶解度，為0時完全透明，1完全不透明
                //else if (type.equals(MtlLoaderUtil.D) || type.equals(MtlLoaderUtil.TR)) {
                else if (type.equals(MtlLoaderUtil.D)) {
                    currMtlData.alpha = Float.parseFloat(parts.nextToken());
                }
                // map_Ka，map_Kd，map_Ks：材质的环境（ambient），散射（diffuse）和镜面（specular）贴图
                else if (type.equals(MtlLoaderUtil.MAP_KA)) {
                    currMtlData.Ka_Texture = parts.nextToken();
                } else if (type.equals(MtlLoaderUtil.MAP_KD)) {
                    currMtlData.Kd_Texture = parts.nextToken();
                } else if (type.equals(MtlLoaderUtil.MAP_KS)) {
                    currMtlData.Ks_ColorTexture = parts.nextToken();
                } else if (type.equals(MtlLoaderUtil.MAP_NS)) {
                    currMtlData.Ns_Texture = parts.nextToken();
                } else if (type.equals(MtlLoaderUtil.MAP_D) || type.equals(MtlLoaderUtil.MAP_TR)) {
                    currMtlData.alphaTexture = parts.nextToken();
                } else if (type.equals(MtlLoaderUtil.MAP_BUMP)) {
                    currMtlData.bumpTexture = parts.nextToken();
                }
            }
            if (currMtlData != null) {
                mMTLMap.put(currMtlData.name, currMtlData);
            }
            buffer.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e.getMessage(), e.getCause());
        }
        return mMTLMap;
    }

    //####################################################################################


    /**
     * 材質需解析字段
     */
    // 定義一個名為 'xxx'的材質
    private static final String NEWMTL = "newmtl";
    // 材質的環境光（ambient color）
    private static final String KA = "Ka";
    // 散射光（diffuse color）用Kd
    private static final String KD = "Kd";
    // 鏡面光（specular color）用Ks
    private static final String KS = "Ks";
    // 反射指数 定義了反射高光度。該值越高則高光越密集，一般取值範圍在0~1000。
    private static final String NS = "Ns";
    // 漸隱指數描述 參數factor表示物體融入背景的數量，取值範圍為0.0~1.0，取值為1.0表示完全不透明，取值为0.0时表示完全透明。
    private static final String D = "d";
    // 濾光透射率
    private static final String TR = "Tr";
    // map_Ka，map_Kd，map_Ks：材質的環境（ambient），散射（diffuse）和鏡面（specular）貼圖
    private static final String MAP_KA = "map_Ka";
    private static final String MAP_KD = "map_Kd";
    private static final String MAP_KS = "map_Ks";
    private static final String MAP_NS = "map_Ns";
    private static final String MAP_D = "map_d";
    private static final String MAP_TR = "map_Tr";
    private static final String MAP_BUMP = "map_Bump";

    public static class MtlData {

        // 材質物件名稱
        public String name;
        // 環境光
        public int Ka_Color;
        // 散射光
        public int Kd_Color;
        // 鏡面光
        public int Ks_Color;
        // 高光調整參數
        public float ns;
        // 溶解度，為0時完全透明，1完全不透明
        public float alpha = 1f;
        // map_Ka，map_Kd，map_Ks：材質的環境（ambient），散射（diffuse）和鏡面（specular）貼圖
        public String Ka_Texture;
        public String Kd_Texture;
        public String Ks_ColorTexture;
        public String Ns_Texture;
        public String alphaTexture;
        public String bumpTexture;
    }


    //####################################################################################

    /**
     * 返回一个oxffffffff格式的顏色值
     *
     * @param parts
     * @return
     */
    private static int getColorFromParts(StringTokenizer parts) {
        int r = (int) (Float.parseFloat(parts.nextToken()) * 255f);
        int g = (int) (Float.parseFloat(parts.nextToken()) * 255f);
        int b = (int) (Float.parseFloat(parts.nextToken()) * 255f);
        return Color.rgb(r, g, b);
    }


}
