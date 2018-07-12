package tw.edu.ntpc.show3dobj.gl.utils;

import java.util.Set;

public class Normal {
   public static final float DIFF=0.0000001f;

   float nx;
   float ny;
   float nz;
   
   public Normal(float nx,float ny,float nz) {
	   this.nx=nx;
	   this.ny=ny;
	   this.nz=nz;
   }
   
   @Override 
   public boolean equals(Object o)
   {
	   if (o instanceof  Normal) {
		   Normal tn=(Normal)o;
		   if(Math.abs(nx-tn.nx)<DIFF && Math.abs(ny-tn.ny)<DIFF && Math.abs(ny-tn.ny)<DIFF) {
			   return true;
		   }
		   else {
			   return false;
		   }
	   }
	   else {
		   return false;
	   }
   }
   
   @Override
   public int hashCode() {
	   return 1;
   }
   
   public static float[] getAverage(Set<Normal> sn) {
	   float[] result=new float[3];

	   for(Normal n:sn) {
		   result[0]+=n.nx;
		   result[1]+=n.ny;
		   result[2]+=n.nz;
	   }	   
	   return vectorNormal(result);
   }

	//求兩個向量的叉積(外積、向量積)
	public static float[] getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2)
	{
		//求出兩個向量叉積向量在XYZ軸的分量ABC
		float A=y1*z2-y2*z1;
		float B=z1*x2-z2*x1;
		float C=x1*y2-x2*y1;

		return new float[]{A,B,C};
	}

	//向量正規化
	public static float[] vectorNormal(float[] vector)
	{
		//求向量的模(長度)
		float module=(float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
		return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
	}

}
