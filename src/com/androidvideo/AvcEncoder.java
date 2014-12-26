package com.androidvideo;

import java.nio.ByteBuffer;
import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Base64;
import android.util.Log;



public class AvcEncoder 
{

	String TAG = "AvcEncoder"; 
	private MediaCodec mediaCodec;
	int m_width;
	int m_height;
	byte[] m_info = null;


	private byte[] yuv420 = null; 
	@SuppressLint("NewApi")
	public AvcEncoder(int width, int height, int framerate, int bitrate) { 
		
		m_width  = width;
		m_height = height;
		yuv420 = new byte[width*height*3/2];
	
	    mediaCodec = MediaCodec.createEncoderByType("video/avc");
	    MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
	    mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
	    mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
	    mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);    
	    mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); //关键帧间隔时间 单位s
	    
//	    mediaFormat.setInteger("profile", 0x01);
	    mediaFormat.setInteger("level", 0x200);
//	    mediaFormat.setInteger("level", 0x10);
	    
	    mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
	    mediaCodec.start();
	    
	    
	    
	    Log.e(TAG, " mediaCodec.start()");
	}

	@SuppressLint("NewApi")
	public void close() {
	    try {
	        mediaCodec.stop();
	        mediaCodec.release();
	    } catch (Exception e){ 
	        e.printStackTrace();
	    }
	}

	@SuppressLint("NewApi")
	public int offerEncoder(byte[] input, byte[] output) 
	{	
		//Log.e("inputBuffers11", TypeConversion.byte2hex(input,0,12)+":"+Base64.encodeToString(input,0,12, Base64.DEFAULT));
		int pos = 0;
		swapYV12toI420(input, yuv420, m_width, m_height);
		//Log.e("inputBuffers22", TypeConversion.byte2hex(input,0,12)+":"+Base64.encodeToString(input,0,12, Base64.DEFAULT));
	    try {
	        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
	        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
	        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
	        if (inputBufferIndex >= 0) 
	        {
	            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
	            inputBuffer.clear();
	            inputBuffer.put(yuv420);
	            mediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv420.length, 0, 0);
	        }

	        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
	        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
	       
	        while (outputBufferIndex >= 0) {
	            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
	            
//	            Log.e("Encoder", "MediaCodec.BufferInfo length:"+bufferInfo.size);
	            
	            byte[] outData = new byte[bufferInfo.size];
	            outputBuffer.get(outData);
	            
	            if(m_info != null)
	            {            	
	            	System.arraycopy(outData, 0,  output, pos, outData.length);
	 	            pos += outData.length;
	 	           Log.e("Encoder", "encode result length:"+outData.length);
	            }else //保存pps sps 只有开始时 第一个帧里有， 保存起来后面用
	            {
	            	 ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);  
	                 if (spsPpsBuffer.getInt() == 0x00000001) 
	                 {  
	                	 m_info = new byte[outData.length];
	                	 System.arraycopy(outData, 0, m_info, 0, outData.length);
	                	 Log.e("Encoder", "sps&pps:"+TypeConversion.byte2hex(m_info)+":"+new String(Base64.encode(m_info, Base64.DEFAULT)));
	                 } 
	                 else 
	                 {  
	                        return -1;
	                 }  	
	            }
	            
	            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
	            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
	        }

	        if(output[4] == 0x65) //key frame   编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上
	        {
	        	System.arraycopy(output, 0,  yuv420, 0, pos);
	        	System.arraycopy(m_info, 0,  output, 0, m_info.length);
	        	System.arraycopy(yuv420, 0,  output, m_info.length, pos);
	        	pos += m_info.length;
	        }
	        
	    } catch (Throwable t) {
	        t.printStackTrace();
	    }

	    return pos;
	}
	 //yv12 转 yuv420p  yvu -> yuv
    private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height) 
    {      
    	System.arraycopy(yv12bytes, 0, i420bytes, 0,width*height);
    	System.arraycopy(yv12bytes, width*height+width*height/4, i420bytes, width*height,width*height/4);
    	System.arraycopy(yv12bytes, width*height, i420bytes, width*height+width*height/4,width*height/4);  
    }  

	
}
