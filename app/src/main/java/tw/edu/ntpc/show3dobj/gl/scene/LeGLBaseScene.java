package tw.edu.ntpc.show3dobj.gl.scene;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * 場景的基礎類
 * 
 * @author xiaxl
 *
 */
public abstract class LeGLBaseScene extends GLSurfaceView {

	/**
	 * LeBaseGLSurfaceView
	 * 
	 * @param context
	 */
	public LeGLBaseScene(Context context) {
		super(context);
		//
		initES2();
	}

	/**
	 * LeBaseGLSurfaceView
	 * 
	 * @param context
	 * @param attrs
	 */
	public LeGLBaseScene(Context context, AttributeSet attrs) {
		super(context, attrs);
		//
		initES2();
	}

	/**
	 * LeBaseGLSurfaceView
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public LeGLBaseScene(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		//
		initES2();
	}

	/**
	 * init es 2.0
	 */
	private void initES2() {
		// 使用OpenGL ES 2.0
		setEGLContextClientVersion(2);
	}

	/**
	 * 繪製方法
	 * 
	 * @param drawTime
	 */
	public void drawSelf(long drawTime) {

	}

	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();

	}

	/**
	 * 
	 */
	@Override
	public void onPause() {
		super.onPause();

	}

}
