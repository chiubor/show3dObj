package tw.edu.ntpc.show3dobj;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tw.edu.ntpc.show3dobj.gl.MyGLScene;

public class MainActivity extends Activity {
    private MyGLScene mGLSurfaceView;
    //
    private Button mBtn = null;
    private Button mBtnPrevious = null;
    private Button mBtnNext = null;
    private String[] objNames = {
            "superman", "plant", "chair", "sofa", "bench", "camaro", "android", "multiobjects"
    };
    private float[] objScale = {1.2f,  0.9f,  0.5f, 0.35f, 0.35f, 2f, 0.3f,  1};
    private float[] objY     = { -6f, -4.5f, -5.5f, -2.5f, -2.5f, -1,   -5, -1};
    private float[] objAngleY= {   0,     0,     0,     0,   180,  0,    0,  0};
    private int objIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.main_activity);
        //
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private void initUI() {
        //初始化GLSurfaceView
        mGLSurfaceView = (MyGLScene) findViewById(R.id.glscene);
        mGLSurfaceView.requestFocus();//獲取焦點
        mGLSurfaceView.setFocusableInTouchMode(true);//設置為可觸控
        mGLSurfaceView.setObjAttr(objScale[0], objY[0], objAngleY[0]);

        mBtn = (Button) findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLSurfaceView.startXZAnima();
            }
        });

        mBtnPrevious = (Button) findViewById(R.id.btnPrevious);
        mBtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objIdx > 0) {
                    objIdx--;
                    mGLSurfaceView.setObjAttr(objScale[objIdx], objY[objIdx], objAngleY[objIdx]);
                    mGLSurfaceView.setObjName(objNames[objIdx] + ".obj");
                }
            }
        });

        mBtnNext = (Button) findViewById(R.id.btnNext);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objIdx < objNames.length - 1) {
                    objIdx++;
                    mGLSurfaceView.setObjAttr(objScale[objIdx], objY[objIdx], objAngleY[objIdx]);
                    mGLSurfaceView.setObjName(objNames[objIdx] + ".obj");
                }
            }
        });
    }
}
