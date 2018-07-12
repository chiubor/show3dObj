package tw.edu.ntpc.show3dobj.objloader;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * obj model
 */
public class ObjLoaderUtil {

    private static final String TAG = "ObjLoaderUtil";


    /**
     * 解析
     *
     * @param fname assets的obj檔案路徑
     * @param res   Resources
     * @return
     */
    public static ArrayList<ObjData> load(String fname, Resources res) throws Exception {
        Log.d(TAG, "---loadObj---");
        // 返回的資料列表
        ArrayList<ObjData> objectList = new ArrayList<ObjData>();
        //
        if (res == null || TextUtils.isEmpty(fname)) {
            return objectList;
        }

        /**
         * 所有頂點信息
         */
        // 頂點資料
        ArrayList<Float> vertices = new ArrayList<Float>();
        // 紋理資料
        ArrayList<Float> texCoords = new ArrayList<Float>();
        // 法向量資料
        ArrayList<Float> normals = new ArrayList<Float>();
        // 全部材質列表
        HashMap<String, MtlLoaderUtil.MtlData> mtlMap = null;

        // Ojb索引資料
        ObjData currObjData = new ObjData();
        // 當前材質名稱
        String currMaterialName = null;
        // 是否有面資料的標識
        boolean currObjHasFaces = false;

        //###############################解析開始#################################
        try {
            // 每一行的信息
            String line = null;
            // 讀取assets下文件
            InputStream in = res.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader buffer = new BufferedReader(isr);

            // 循環讀取每一行的資料
            while ((line = buffer.readLine()) != null) {
                // 忽略 空行和註釋
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                // 以空格分割String
                StringTokenizer parts = new StringTokenizer(line, " ");
                int numTokens = parts.countTokens();
                if (numTokens == 0) {
                    continue;
                }
                // 打頭的字符
                String type = parts.nextToken();

                // 材質
                if (type.equals(ObjLoaderUtil.MTLLIB)) {
                    if (!parts.hasMoreTokens()) {
                        continue;
                    }

                    String materialLibPath = parts.nextToken();
                    //#########載入材質信息############
                    if (!TextUtils.isEmpty(materialLibPath)) {
                        mtlMap = MtlLoaderUtil.load(materialLibPath, res);
                    }

                }
                // 物件名稱
                else if (type.equals(ObjLoaderUtil.O)) {
                    // 物件名稱
                    String objName = parts.hasMoreTokens() ? parts.nextToken() : "def";
                    // 面資料
                    if (currObjHasFaces) {
                        // 添加到陣列中
                        objectList.add(currObjData);
                        // 建立新的索引物件
                        currObjData = new ObjData();
                        currObjHasFaces = false;
                    }
                    currObjData.name = objName;
                    // 對應材質
                    if (!TextUtils.isEmpty(currMaterialName) && mtlMap != null) {
                        currObjData.mtlData = mtlMap.get(currMaterialName);
                    }
                }
                // "v" 頂點屬性 添加到頂點資料
                else if (type.equals(ObjLoaderUtil.V)) {
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                }
                // 紋理
                else if (type.equals(ObjLoaderUtil.VT)) {
                    // 這裡紋理的Y值，需要(Y = 1-Y0)
                    texCoords.add(Float.parseFloat(parts.nextToken()));
                    texCoords.add(1f - Float.parseFloat(parts.nextToken()));
                }
                // 法向量
                else if (type.equals(ObjLoaderUtil.VN)) {
                    normals.add(Float.parseFloat(parts.nextToken()));
                    normals.add(Float.parseFloat(parts.nextToken()));
                    normals.add(Float.parseFloat(parts.nextToken()));
                }
                // 使用材質
                else if (type.equals(ObjLoaderUtil.USEMTL)) {
                    // 材質名稱
                    currMaterialName = parts.nextToken();
                    if (currObjHasFaces) {
                        // 添加到資料中
                        objectList.add(currObjData);
                        // 创建一个index物件
                        currObjData = new ObjData();
                        currObjHasFaces = false;
                    }
                    // 材質名稱
                    if (!TextUtils.isEmpty(currMaterialName) && mtlMap != null) {
                        currObjData.mtlData = mtlMap.get(currMaterialName);
                    }
                }
                // "f"面属性  索引資料
                else if (type.equals(F)) {
                    // 當前obj物件有面資料
                    currObjHasFaces = true;
                    // 是否為矩形(android 均為三角形，這裡暫時先忽略多邊形的情况)
                    boolean isQuad = numTokens == 5;
                    int[] quadvids = new int[4];
                    int[] quadtids = new int[4];
                    int[] quadnids = new int[4];

                    // 如果含有"//" 替換
                    boolean emptyVt = line.indexOf("//") > -1;
                    if (emptyVt) {
                        line = line.replace("//", "/");
                    }
                    // "f 103/1/1 104/2/1 113/3/1"以" "分割
                    // "頂點索引/uv點索引/法線索引"
                    parts = new StringTokenizer(line);
                    // “f”
                    parts.nextToken();
                    // "103/1/1 104/2/1 113/3/1"再以"/"分割
                    StringTokenizer subParts = new StringTokenizer(parts.nextToken(), "/");
                    int partLength = subParts.countTokens();

                    // 紋理資料
                    boolean hasuv = partLength >= 2 && !emptyVt;
                    // 法向量資料
                    boolean hasn = partLength == 3 || (partLength == 2 && emptyVt);
                    // 索引index
                    int idx;
                    for (int i = 1; i < numTokens; i++) {
                        if (i > 1) {
                            subParts = new StringTokenizer(parts.nextToken(), "/");
                        }
                        // 頂點索引
                        idx = Integer.parseInt(subParts.nextToken());
                        if (idx < 0) {
                            idx = (vertices.size() / 3) + idx;
                        } else {
                            idx -= 1;
                        }
                        if (!isQuad) {
                            currObjData.vertexIndices.add(idx);
                        } else {
                            quadvids[i - 1] = idx;
                        }
                        // 紋理索引
                        if (hasuv) {
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0) {
                                idx = (texCoords.size() / 2) + idx;
                            } else {
                                idx -= 1;
                            }
                            if (!isQuad) {
                                currObjData.texCoordIndices.add(idx);
                            } else {
                                quadtids[i - 1] = idx;
                            }
                        }
                        // 法向量資料
                        if (hasn) {
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0) {
                                idx = (normals.size() / 3) + idx;
                            } else {
                                idx -= 1;
                            }
                            if (!isQuad) {
                                currObjData.normalIndices.add(idx);
                            } else {
                                quadnids[i - 1] = idx;
                            }
                        }
                    }
                    // 如果是多邊形
                    if (isQuad) {
                        int[] indices = new int[]{0, 1, 2, 0, 2, 3};
                        for (int i = 0; i < 6; ++i) {
                            int index = indices[i];
                            currObjData.vertexIndices.add(quadvids[index]);
                            currObjData.texCoordIndices.add(quadtids[index]);
                            currObjData.normalIndices.add(quadnids[index]);
                        }
                    }
                }
            }
            //
            buffer.close();
            // 存在索引面資料，添加到index陣列中
            if (currObjHasFaces) {
                // 添加到資料中
                objectList.add(currObjData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage(), e.getCause());
        }

        //###############################頂點、法向量、紋理一一對應#################################

        // 循環索引物件列表
        int numObjects = objectList.size();
        for (int j = 0; j < numObjects; ++j) {
            ObjData objData = objectList.get(j);

            int i;
            // 頂點資料 初始化
            float[] aVertices = new float[objData.vertexIndices.size() * 3];
            // 頂點紋理資料 初始化
            float[] aTexCoords = new float[objData.texCoordIndices.size() * 2];
            // 頂點法向量資料 初始化
            float[] aNormals = new float[objData.normalIndices.size() * 3];
            // 按照索引，重新組織頂點資料
            for (i = 0; i < objData.vertexIndices.size(); ++i) {
                // 頂點索引，三個一組做為一個三角形
                int faceIndex = objData.vertexIndices.get(i) * 3;
                int vertexIndex = i * 3;
                try {
                    // 按照索引，重新組織頂點資料
                    aVertices[vertexIndex] = vertices.get(faceIndex);
                    aVertices[vertexIndex + 1] = vertices.get(faceIndex + 1);
                    aVertices[vertexIndex + 2] = vertices.get(faceIndex + 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 按照索引組織 紋理資料
            if (texCoords != null && texCoords.size() > 0) {
                for (i = 0; i < objData.texCoordIndices.size(); ++i) {
                    int texCoordIndex = objData.texCoordIndices.get(i) * 2;
                    int ti = i * 2;
                    aTexCoords[ti] = texCoords.get(texCoordIndex);
                    aTexCoords[ti + 1] = texCoords.get(texCoordIndex + 1);
                }
            }
            // 按照索引組織 法向量資料
            for (i = 0; i < objData.normalIndices.size(); ++i) {
                int normalIndex = objData.normalIndices.get(i) * 3;
                int ni = i * 3;
                if (normals.size() == 0) {
                    throw new Exception("There are no normals specified for this model. Please re-export with normals.");
                }
                aNormals[ni] = normals.get(normalIndex);
                aNormals[ni + 1] = normals.get(normalIndex + 1);
                aNormals[ni + 2] = normals.get(normalIndex + 2);
            }

            // 資料設置到oid.targetObj中
            objData.aVertices = aVertices;
            objData.aTexCoords = aTexCoords;
            objData.aNormals = aNormals;
            //
            if (objData.vertexIndices != null) {
                objData.vertexIndices.clear();
            }
            if (objData.texCoordIndices != null) {
                objData.texCoordIndices.clear();
            }
            if (objData.normalIndices != null) {
                objData.normalIndices.clear();
            }
        }
        return objectList;
    }

    //###################################################################################
    /**
     * obj需解析字段
     */
    // obj對應的材質文件
    private static final String MTLLIB = "mtllib";
    // 組名稱
    private static final String G = "g";
    // o 物件名稱(Object name)
    private static final String O = "o";
    // 頂點
    private static final String V = "v";
    // 紋理坐標
    private static final String VT = "vt";
    // 頂點法線
    private static final String VN = "vn";
    // 使用的材質
    private static final String USEMTL = "usemtl";
    // v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3(索引起始於1)
    private static final String F = "f";


    //###################################################################################

    public static class ObjData {

        // 物件名稱
        public String name;
        // 材質
        public MtlLoaderUtil.MtlData mtlData;

        /**
         * 頂點、紋理、法向量一一對應後的資料
         */
        public float[] aVertices;
        // 頂點紋理可能會没有
        public float[] aTexCoords;
        public float[] aNormals;

        /**
         * index資料(頂點、紋理、法向量一一對應後，以下三個列表會清空)
         */
        // 頂點index資料
        public ArrayList<Integer> vertexIndices = new ArrayList<Integer>();
        // 紋理index資料
        public ArrayList<Integer> texCoordIndices = new ArrayList<Integer>();
        // 法向量index資料
        public ArrayList<Integer> normalIndices = new ArrayList<Integer>();

    }
}


