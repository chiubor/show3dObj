package tw.edu.ntpc.show3dobj.gl.config;

/**
 * 近平面的可視區間為2 ~ -22
 * 
 * @author xiaxl1
 *
 */
public class LeGLConfig {

	private static final String TAG = LeGLConfig.class.getSimpleName();

	/**
	 * near far
	 */
	public static final float PROJECTION_NEAR = 2;
	public static final float PROJECTION_FAR = 1000;

	/**
	 * camera position
	 */
	public static final float EYE_X = 0f;
	public static final float EYE_Y = 0f;
	public static final float EYE_Z = 0f;
	public static final float VIEW_CENTER_X = 0f;
	public static final float VIEW_CENTER_Y = 0f;
	public static final float VIEW_CENTER_Z = -1f;

}
