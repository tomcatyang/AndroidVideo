package com.androidvideo;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class MainActivity extends Activity implements SurfaceHolder.Callback, PreviewCallback 
{

	boolean mediaRecoder = false;
	
	RandomAccessFile file = null;
	RandomAccessFile file2 = null;
	
	AvcEncoder avcCodec;
    public Camera camera;  
	MediaRecorder mediaRecorder;
    SurfaceView   m_prevewview;
    SurfaceHolder m_surfaceHolder;
    public static final int width = 640;
    public static final int height = 480;
    public static final int framerate = 20;
    public static final int bitrate = 960000;
    
    byte[] h264 = new byte[width*height*3/2];

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

		if(!mediaRecoder)
			avcCodec = new AvcEncoder(width, height, framerate, bitrate);

		m_prevewview = (SurfaceView) findViewById(R.id.SurfaceViewPlay);
		m_surfaceHolder = m_prevewview.getHolder(); // 绑定SurfaceView，取得SurfaceHolder对象
		m_surfaceHolder.setFixedSize(width, height); // 预览大小設置
		m_surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		m_surfaceHolder.addCallback((Callback) this);



	}
	
	public void onResume(){
		super.onResume();
		if(!mediaRecoder){
			try {
				File f = new File("/sdcard/h264");
				if(f.exists())
					f.delete();
				file = new RandomAccessFile("/sdcard/h264","rw");
				
				f = new File("/sdcard/testpic.data");
				if(f.exists())
					f.delete();
				file2 = new RandomAccessFile("/sdcard/testpic.data","rw");
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			
			
			
		}else{
			try {
				File f = new File("/sdcard/love.3gp");
				if(f.exists())
					f.delete();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			
		}

	}
	
	public void onPause(){
		super.onPause();
		if(file!=null)
			try {
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(file2!=null)
			try {
				file2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
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
		if(mediaRecoder){
			try {
				camera.unlock();
				initRecord(arg0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) 
	{
		try {
			initCamera(arg0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) 
	{
		freeRecordResource();
		freeCameraResource();
	}

	int record = 0;
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) 
	{
		int ret = 0;
		//Log.v("h264", "h264 start");
		ret = avcCodec.offerEncoder(data,h264);
		Log.e("h264", "data length:"+data.length+";picture length:"+ret);
		try {
			if(record++==10){
				file2.write(data, 0, data.length);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(ret > 0)
		{
			try {	
				if(file!=null){
					//Log.v("h264", "h264 send:"+ret);
					file.write(h264, 0, ret);
				}

			} catch (IOException e)
			{
			
			}
		}
		//Log.v("h264", "h264 end");
		
	}
	
	
	private void initCamera(SurfaceHolder arg0) throws IOException {
		if (camera != null) {
			freeCameraResource();
		}
		camera = Camera.open(0);
		camera.setPreviewDisplay(m_surfaceHolder);
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(width, height);
		parameters.setPictureSize(width, height);
		parameters.setPreviewFormat(ImageFormat.YV12);
		parameters.setPreviewFrameRate(framerate);
		camera.setParameters(parameters);	
		camera.setPreviewCallback((PreviewCallback) this);
		
		camera.startPreview();
		
		
	}
	
	public void setCameraParams() {
		if (camera != null) {
			Parameters params = camera.getParameters();
			List<String> list = params.getSupportedFocusModes();
			if (list.contains(Parameters.FOCUS_MODE_AUTO)) {
				params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			params.set("orientation", "portrait");
			camera.setParameters(params);
		}
	}

	private void initRecord(SurfaceHolder arg0) throws IOException {
		if (mediaRecorder != null) {
			freeRecordResource();
		}
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setCamera(camera);
		mediaRecorder.setPreviewDisplay(arg0.getSurface());
		mediaRecorder.setVideoSource(VideoSource.CAMERA);
		mediaRecorder.setOutputFormat(OutputFormat.THREE_GPP);
		//mediaRecorder.setVideoEncodingBitRate(bitrate);
		mediaRecorder.setVideoEncoder(VideoEncoder.H264);
		mediaRecorder.setOutputFile("/sdcard/love.3gp");
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  
//		mediaRecorder.setVideoSize(352, 288);  
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
		mediaRecorder.setVideoFrameRate(framerate);  
        
		mediaRecorder.prepare();
		mediaRecorder.start();
		

	}

	private void freeCameraResource() {
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.lock();
			camera.release();
			camera = null;
		}
	}

	private void freeRecordResource() {
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
		}
	}



}
