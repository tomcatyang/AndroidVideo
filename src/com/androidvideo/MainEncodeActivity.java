package com.androidvideo;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainEncodeActivity extends Activity implements SurfaceHolder.Callback
{

	
	RandomAccessFile file2 = null;
	byte[] pic = null;
	Bitmap bitmap = null;
	
	AvcEncoder avcCodec;
    SurfaceView   m_prevewview;
    SurfaceHolder m_surfaceHolder;

    
    byte[] h264 = new byte[MainActivity.width*MainActivity.height*3/2];

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads().detectDiskWrites().detectAll().penaltyLog()
//				.build());
//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//				.penaltyLog().penaltyDeath().build());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.getWindow().getDecorView().setKeepScreenOn(true);
		avcCodec = new AvcEncoder(MainActivity.width, MainActivity.height, MainActivity.framerate, MainActivity.bitrate);

		m_prevewview = (SurfaceView) findViewById(R.id.SurfaceViewPlay);
		m_surfaceHolder = m_prevewview.getHolder(); // 绑定SurfaceView，取得SurfaceHolder对象
		m_surfaceHolder.setFixedSize(MainActivity.width, MainActivity.height); // 预览大小設置
		m_surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		m_surfaceHolder.addCallback((Callback) this);



	}
	
	public void onResume(){
		super.onResume();
		
		try {
			
			File f = new File("/sdcard/myh264.264");
			if(f.exists())
				f.delete();
			
			file2 = new RandomAccessFile("/sdcard/myh264.264","rw");
			
			InputStream mInputStream = this.getAssets().open("testpic.data");
			pic = new byte[mInputStream.available()];
			int len = mInputStream.read(pic);
			//Log.e("onResume", "lenght:"+len);
			mInputStream.close();
			
			//YuvImage mYuvImage = new YuvImage(pic,ImageFormat.YV12,MainActivity.width,MainActivity.height,null);
			
			
			//Log.e("onResume", TypeConversion.byte2hex(pic, 0, 100));
			
//			byte[] buffer = new byte[MainActivity.width*MainActivity.height*4];
//			Log.e("onResume", TypeConversion.byte2hex(buffer, 0, 100));
//			YUV2RGB(pic,buffer,MainActivity.width,MainActivity.height);
//			Log.e("onResume", TypeConversion.byte2hex(buffer, 0, 100));
//			bitmap = Bitmap.createBitmap(MainActivity.width, MainActivity.height, Config.ARGB_8888);
//			bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(buffer));
			
			new Thread(){
				public void run(){
					int sleeptime = 30;
					try {
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
						toEncode(pic);
						Thread.sleep(sleeptime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}.start();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public void onPause(){
		super.onPause();
		if(file2!=null){
			try {
				file2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			file2 = null;
		}

		if(bitmap!=null){
			bitmap.recycle();
			bitmap = null;
		}
		if(pic!=null)
			pic = null;
		avcCodec.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) 
	{
		if(bitmap!=null){
//			Canvas canvas = arg0.lockCanvas();
//	        RectF rectF = new RectF(0, 0 , bitmap.getWidth(), bitmap.getHeight()); 
//        	canvas.drawBitmap(bitmap, null, rectF, null);
//        	
//        	Paint mPaint = new Paint();
//			mPaint.setColor(Color.RED);
//			mPaint.setAntiAlias(true);
//			mPaint.setStyle(Paint.Style.STROKE);
//			mPaint.setStrokeCap(Paint.Cap.ROUND);
//			mPaint.setStrokeWidth(3);
//        	
//        	canvas.drawLine(0, 0, 100, 100, mPaint);
//			Log.e("surfaceChanged", "surfaceChanged===");
//			
//			try{
//				arg0.unlockCanvasAndPost(canvas);
//			}catch(Exception e){
//				
//			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) 
	{
        nativeSetVideoSurface(arg0.getSurface());  
        nativeShowYUV(pic,MainActivity.width,MainActivity.height);  
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) 
	{

	}


	public void toEncode(byte[] data) 
	{
		Log.e("h264", "YV12 source data length:"+data.length);
		int ret = avcCodec.offerEncoder(data,h264);
		
		if(ret > 0)
		{
			try {	
				if(file2!=null){
					//Log.v("h264", "h264 send:"+ret);
					file2.write(h264, 0, ret);
				}

			} catch (IOException e)
			{
			
			}
		}
		//Log.v("h264", "h264 end");
		
	}
	

	public static void YUV2RGB(byte[] yv12, byte[] rgb,int width,int height) {
		// R = Y + 1.4075 *（V-128）
		// G = Y – 0.3455 *（U –128） – 0.7169 *（V –128）
		// B = Y + 1.779 *（U – 128）
		Log.e("YUV2RGB", "width*height:"+width*height);
		int index = 0;
		int size = width*height;
		byte[] ya = new byte[size];
		byte[] va = new byte[size/4];
		byte[] ua = new byte[size/4];
		System.arraycopy(yv12, 0, ya, 0, ya.length);
		System.arraycopy(yv12, size, va, 0, va.length);
		System.arraycopy(yv12, size+size/4, ua, 0, ua.length);
		
		/*
		int iSrc = 0; // (0, width*height)
		// YCbCr转换为RGB
		for(int i =0 ; i < rgb.length;)
		{
		   byte y,u,v;
		   y = ya[iSrc];
		   u = ua[iSrc / 4];
		   v = va[iSrc / 4];
		   double temp;
		   temp = 1.772f*v;
		   rgb[i+2] = (byte)(temp < 0 ? 0 : (temp > 255 ? 255 : temp));
		   temp = 0.344f*v + 0.714f*u;
		   rgb[i + 1] = (byte)(temp < 0 ? 0 : (temp > 255 ? 255 : temp));
		   temp = 1.402f*u;
		   rgb[i] = (byte)(temp < 0 ? 0 : (temp > 255 ? 255 : temp));
		   i += 4;
		   ++iSrc;
		}
		*/

		
		int argb = 0;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				index = i*width+j; 
				
				/*
				yuv2rgb(yv12[index],yv12[size+(index)/4],yv12[size+size/4+index/4]);
				rgb[index*4+1] = rgbs[0];
				rgb[index*4+2] = rgbs[1];
				rgb[index*4+3] = rgbs[2];
				*/
				
				/*
				rgb[index*4+1]=(byte)(yv12[index] + (int)(1.4075 *(yv12[size+(index)/4]-128)));
				rgb[index*4+2]= (byte)(yv12[index] - (int)(0.3455 *(yv12[size+size/4+index/4]-128)+0.7169 *(yv12[size+index/4]-128)));
				rgb[index*4+3]= (byte)(yv12[index] + (int)(1.779 *(yv12[size+size/4+index/4]-128)));
				*/
				try{
					byte[] rgbs = yuv2rgb(ya[index],va[i*width/4+j/4],ua[i*width/4+j/4]);
					rgb[index*4] = rgbs[2];
					rgb[index*4+1] = rgbs[1];
					rgb[index*4+2] = rgbs[0];
					
				}catch(ArrayIndexOutOfBoundsException e){
					Log.e("YUV2RGB", "YUV2RGB i:"+i+":j:"+j);
					throw e;
				}

			}
		}
		
	
		Log.e("YUV2RGB", "YUV2RGB:"+index+":size+size/4+index/4:"+(size+size/4+index/4));
	}

	//static byte[] rgbs = new byte[3];
	
	public static byte[] yuv2rgb(int y, int u, int v) {
		// R = Y + 1.4075 *（V-128）
		// G = Y – 0.3455 *（U –128） – 0.7169 *（V –128）
		// B = Y + 1.779 *（U – 128）
		
		byte[] rgbs = new byte[3];
		
		int R = (int) (y + (1.4075 *(v-128)));
		int G = (int) (y - 0.3455 *(u-128)+0.7169 *(v-128));
		int B = (int) (y + 1.779 *(u-128));
		if (R < 0)
			R = 0;
		if (G < 0)
			G = 0;
		if (B < 0)
			B = 0;
		if (R > 255)
			R = 255;
		if (G > 255)
			G = 255;
		if (B > 255)
			B = 255;
		
		rgbs[0]|=R;
		rgbs[1]|=G;
		rgbs[2]|=B;
		
		return rgbs;
	}


	 private native void nativeTest();  
	 private native boolean nativeSetVideoSurface(Surface surface);  
	 private native void nativeShowYUV(byte[] yuvArray,int width,int height);  
	 static { 
		 System.loadLibrary("showYUV");  
	 }  

}
