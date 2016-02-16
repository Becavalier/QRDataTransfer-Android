package com.yhspy.zbartest;

import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;
import android.util.Log;
   
public class AudioRecordDemo {  
  
    static final int SAMPLE_RATE_IN_HZ = 8000;  
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,  
                    AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);  
    AudioRecord mAudioRecord;  
    boolean isGetVoiceRun;  
    Object mLock;  
    Thread mainThread = null;
    double volume = 0.0;
    Boolean flag = true;
    SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(FirstActivity.getInstance());
    
    public AudioRecordDemo() {  
        mLock = new Object();  
    }  
  
    public void getNoiseLevel() {  
        if (isGetVoiceRun) {  
            return;  
        }  
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,  
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,  
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);  
        if (mAudioRecord == null) {  
            Log.e("sound", "mAudioRecord初始化失败");  
        }  
        isGetVoiceRun = true;  
  
        mainThread = new Thread(new Runnable() {  
            @Override  
            public void run() {
                mAudioRecord.startRecording();  
                short[] buffer = new short[BUFFER_SIZE];  
                while (isGetVoiceRun) {  
                    //r是实际读取的数据长度，一般而言r会小于buffersize  
                    int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);  
                    long v = 0;  
                    // 将 buffer 内容取出，进行平方和运算  
                    for (int i = 0; i < buffer.length; i++) {  
                        v += buffer[i] * buffer[i];  
                    }  
                    // 平方和除以数据总长度，得到音量大小  
                    double mean = v / (double) r;  
                    volume = 10 * Math.log10(mean); 
                    if(volume > 70) {
                    	flag = false;
                    	System.out.println("Sound_Captured------------------------------------------------>"  + volume);
                    }else{
                    	flag = true;
                    }
                    synchronized (mLock) {  
                        try {  
                            mLock.wait(5);  
                        } catch (InterruptedException e) {  
                            e.printStackTrace();  
                        }  
                    }  
                }  
                mAudioRecord.stop();  
                mAudioRecord.release();  
                mAudioRecord = null; 
            }  
        });
        mainThread.start();  
    } 
    
    public void stop(){
    	isGetVoiceRun = false;
    }
    
    public int getSpValue(SharedPreferences sp, int index){
		int [] temp = new int[9];
		temp[0] = Integer.valueOf(sp.getString("per_barcode_content", "1200"));
		temp[1] = Integer.valueOf(sp.getString("per_barcode_size", "300"));
		temp[2] = Integer.valueOf(sp.getString("per_barcode_interval", "180"));
		temp[3] = Integer.valueOf(sp.getString("barcode_start_delay", "500"));
		temp[4] = Integer.valueOf(sp.getString("barcode_product_thread_num", "2"));
		temp[5] = Integer.valueOf(sp.getString("barcode_product_thread_priority", "-15"));
		temp[6] = Integer.valueOf(sp.getString("error_ctl_group_nums", "5"));
		temp[7] = Integer.valueOf(sp.getString("error_ctl_sound_low", "70"));		
		return temp[index];
	}
}  