
package com.yhspy.zbartest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import java.lang.System;
/* Import ZBar Class files */
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CameraTestActivity extends Activity{
	//初始化其他变量
    private Camera mCamera;
    private FrameLayout preview = null;
    private int maxPreviewFps = 0;
    private CameraPreview mPreview;
    private boolean imageEndFlag = false;
    private boolean imageEndFlag_2 = false;
    private int startTimeFlag = 0;
    //结果过滤设置变量
	private int scanTimes = 0;
	private int scanTimes_2 = 0;
	private long startTime;
	private long endTime;
    private boolean previewing = true;
    private String endStr = "END000OPTIMUS000ENDEND000OPTIMUS000ENDEND000OPTIMUS000END";
    private Image barcode = new Image();
    private Image barcode_2 = new Image();
    private char [][] barcodeContentArr = null;
    private char [][] barcodeContentArr_2 = null;
    private SharedPreferences defaultSp = null;
    private int preBarcodeContent = 0;
    private int barcodeNum = 0;
    private int scanDensity = 1;
    private int scanWidth = 0;
    private int scanHeight = 0;
    private TextView scanText;
    private TextView scanText_2;
    private TextView progress;
    private TextView progress_2;
    private Button cancelButton;
    private ImageScanner scanner;
    private ImageScanner scanner_2;
    private int result = 0;
    private int result_2 = 0;
    private int scanNull = 0;
    private int scanNull_2 = 0;
    private int point = 0;
    private int point_2 = 0;
    private int index = 0;
    private SoundPool soundPool = null;
    private int preErrorGroupNums = 6;
    private Handler autoFocusHandler = new Handler();
    Timer timer = null;
    TimerTask timerTask = null;
    Timer timer_2 = null;
    TimerTask timerTask_2 = null;
    boolean twice = false;
    boolean twice_2 = false;
    
    private int counterTimer = 0;
    private int counterTimer_2 = 0;
    private int focusTimes = 0;
    static {
        System.loadLibrary("iconv");
    } 

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化SharedPreferences相关参数
        requestWindowFeature(Window.FEATURE_NO_TITLE);                  //没有标题
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);      //设置全屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);      //拍照过程屏幕一直处于高亮
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //初始化SoundPool
        soundPool= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(CameraTestActivity.this, R.raw.audio, 1);
        defaultSp = PreferenceManager.getDefaultSharedPreferences(this);  
        preBarcodeContent = getSpValue(defaultSp,0);
        barcodeNum = getSpValue(defaultSp,7);
        scanDensity = getSpValue(defaultSp,8);
        scanWidth = getSpValue(defaultSp,9);
        scanHeight = getSpValue(defaultSp,10);
        preErrorGroupNums = getSpValue(defaultSp,11);
        
        barcodeContentArr = new char [barcodeNum + 1][preBarcodeContent];
        barcodeContentArr_2 = new char [barcodeNum + 1][preBarcodeContent];
        //初始化过滤变量
        setContentView(R.layout.activity_scan);
        //设置屏幕方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mCamera = getCameraInstance();
        Parameters params = mCamera.getParameters();
        //调整相机的预览帧速率到可用最大
        List<int[]> previewFps = params.getSupportedPreviewFpsRange();  
        for(int j = 0;j < previewFps.size();j ++) {  
            int[] r = previewFps.get(j);  
            maxPreviewFps = r[1];
        } 
        params.setPreviewFpsRange(maxPreviewFps, maxPreviewFps);
        params.setPreviewSize(scanWidth, scanHeight);//图片大小
        //列出所有支持的预览尺寸
        List<Camera.Size> sizeList = params.getSupportedPreviewSizes();  
        if (sizeList.size() > 1) {  
            Iterator<Camera.Size> itor = sizeList.iterator();  
            while (itor.hasNext()) {  
                Camera.Size cur = itor.next();  
                System.out.println("size==" + cur.width + " " + cur.height);  
            }  
        }  
        mCamera.setParameters(params);
        /* Instance barcode scanner */
        scanner = new ImageScanner();
        //设置扫描精度
        scanner.setConfig(0, Config.X_DENSITY, scanDensity);
        scanner.setConfig(0, Config.Y_DENSITY, scanDensity);
        //禁用其余的扫码格式
        scanner.setConfig(Symbol.CODABAR, Config.ENABLE, 0);
        scanner.setConfig(Symbol.CODE128, Config.ENABLE, 0);
        scanner.setConfig(Symbol.CODE39, Config.ENABLE, 0);
        scanner.setConfig(Symbol.CODE93, Config.ENABLE, 0);
        scanner.setConfig(Symbol.DATABAR, Config.ENABLE, 0);
        scanner.setConfig(Symbol.EAN13, Config.ENABLE, 0);
        scanner.setConfig(Symbol.EAN8, Config.ENABLE, 0);
        scanner.setConfig(Symbol.I25, Config.ENABLE, 0);
        scanner.setConfig(Symbol.ISBN10, Config.ENABLE, 0);
        scanner.setConfig(Symbol.ISBN13, Config.ENABLE, 0);
        scanner.setConfig(Symbol.UPCA, Config.ENABLE, 0);
        scanner.setConfig(Symbol.UPCE, Config.ENABLE, 0);
        scanner.setConfig(Symbol.DATABAR_EXP, Config.ENABLE, 0);
        scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);
        //禁用扫描器缓存
        scanner.enableCache(false);
        /* Instance barcode scanner_2 */
        scanner_2 = new ImageScanner();
        //设置扫描精度
        scanner_2.setConfig(0, Config.X_DENSITY, scanDensity);
        scanner_2.setConfig(0, Config.Y_DENSITY, scanDensity);
        //禁用其余的扫码格式
        scanner_2.setConfig(Symbol.CODABAR, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.CODE128, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.CODE39, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.CODE93, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.DATABAR, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.EAN13, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.EAN8, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.I25, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.ISBN10, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.ISBN13, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.UPCA, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.UPCE, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.DATABAR_EXP, Config.ENABLE, 0);
        scanner_2.setConfig(Symbol.QRCODE, Config.ENABLE, 1);
        //禁用扫描器缓存
        scanner_2.enableCache(false);
       
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        
        cancelButton = (Button)findViewById(R.id.cancelButton);
        
        scanText = (TextView) findViewById(R.id.textView);
        scanText_2 = (TextView) findViewById(R.id.textView_2);
        progress = (TextView) findViewById(R.id.progress);
        progress_2 = (TextView) findViewById(R.id.progress_2);
        
        cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//释放相机
            	if (mCamera != null) {
                    previewing = false;
                    mCamera.setPreviewCallback(null);
                    mCamera.release();
                    mCamera = null;
                }
            	finish();                                                  //取消Scanning按钮
            }
        });
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
        	System.out.println("BackCamera-OPEN");
            c = Camera.open(0);
            c .autoFocus(null);
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
    
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);                                 //自动对焦
        }
    };
	
    PreviewCallback previewCb = new PreviewCallback() {
			public void onPreviewFrame(byte[] data, Camera camera) {
				android.os.Process.setThreadPriority(-20);
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();
                //扫描的相机预览图片大小
                barcode = new Image(size.width, size.height, "Y800");
                barcode_2 = new Image(size.width, size.height, "Y800");
                barcode.setCrop(0, 0, size.width/2, size.height);
                barcode_2.setCrop(size.width/2, 0, size.width, size.height);
                barcode.setData(data);
                barcode_2.setData(data);
                result = scanner.scanImage(barcode);
                result_2 = scanner_2.scanImage(barcode_2);
                
                //处理二维码数据
                if (result != 0 || result_2 != 0) {
                	if(startTimeFlag == 0) startTime = System.currentTimeMillis();
                	startTimeFlag = 1;
                	if(imageEndFlag == false){
	                    SymbolSet syms = scanner.getResults();
	                    for (Symbol sym : syms) {
	                        StringBuffer resultString = new StringBuffer(sym.getData());
	                        // FIXME
	                        if(!(resultString.toString().equals(endStr))){
//	                        	if(twice == true){
//	                        		timerTask.cancel();
//	                        		timer.cancel();
//	                        	}
		                        char [] tempCharArr_all = resultString.toString().toCharArray();
		                        char [] tempCharArr_data = new char[preBarcodeContent];
		                        char [] tempCharArr_point = new char[3];
		                        System.arraycopy(tempCharArr_all, resultString.toString().length() - 3, tempCharArr_point, 0, 3);
		                       //获取数据索引
		                        point = Integer.parseInt(String.valueOf(tempCharArr_point));
		                        if(barcodeContentArr[point][0] != 0){
		                        	continue;
		                        }
		                        System.arraycopy(tempCharArr_all, 0, tempCharArr_data, 0, resultString.toString().length() - 3);
		                        //获取数据体
		                        barcodeContentArr[point] = tempCharArr_data;
		                        progress.setText(String.valueOf(((point + 1) * 1.0/barcodeNum * 1.0) * 100).substring(0, 3) + "0 %");
		                        scanText.setText(String.valueOf(scanTimes ++)); 
		                        counterTimer ++;
//		                        timer = new Timer();
//		                    	timerTask = new TimerTask(){
//		    						@Override
//		    						public void run() {
//		    							soundPool.play(1 ,1, 1, 0, 0, 1);
//		    						}
//		                    	};
//		                    	twice = true;
//		                    	timer.schedule(timerTask, 350);
	                        }else{
	                        	progress.setText(String.valueOf("100") + " %");
	                        	StringBuffer tempStr = new StringBuffer();
	                        	for(int i = 0;i < barcodeContentArr.length;i ++){
	                        		if(barcodeContentArr[i][1] == 0) scanNull ++;
	                        		tempStr.append(String.valueOf(barcodeContentArr[i]));
	                        	}
		                        //存储图像文件
		                        if(!(tempStr.toString().equals("") || tempStr.toString().equals(null))) saveFile(stringToCode(tempStr.toString()), "source-A", "jpg");
		                        vibrateMethod.Vibrate(CameraTestActivity.this, 200);       //震动提醒
		                        imageEndFlag = true;
	                        }
	                    }
                	}
                	
                    //
                	if(imageEndFlag_2 == false){
	                    SymbolSet syms_2 = scanner_2.getResults();
	                    for (Symbol sym_2 : syms_2) {
	                        StringBuffer resultString = new StringBuffer(sym_2.getData());
	                        // FIXME
	                        if(!(resultString.toString().equals(endStr))){
//	                        	if(twice_2 == true){
//	                        		timerTask_2.cancel();
//	                        		timer_2.cancel();
//	                        	}
		                        char [] tempCharArr_all = resultString.toString().toCharArray();
		                        char [] tempCharArr_data = new char[preBarcodeContent];
		                        char [] tempCharArr_point = new char[3];
		                        System.arraycopy(tempCharArr_all, resultString.toString().length() - 3, tempCharArr_point, 0, 3);
		                        //获取数据索引
		                        point_2 = Integer.parseInt(String.valueOf(tempCharArr_point));
		                        if(barcodeContentArr_2[point_2][0] != 0) {
		                        	continue;
		                        }
		                        System.arraycopy(tempCharArr_all, 0, tempCharArr_data, 0, resultString.toString().length() - 3);
		                        //获取数据体
		                        barcodeContentArr_2[point_2] = tempCharArr_data;
		                        progress_2.setText(String.valueOf(((point_2 + 1) * 1.0/barcodeNum * 1.0) * 100).substring(0, 3) + "0 %");
		                        scanText_2.setText(String.valueOf(scanTimes_2 ++));
		                        counterTimer_2 ++;
//		                        timer_2 = new Timer();
//		                    	timerTask_2 = new TimerTask(){
//		    						@Override
//		    						public void run() {
//		    							soundPool.play(1 ,1, 1, 0, 0, 1);
//		    						}
//		                    	};
//		                    	twice_2 = true;
//		                    	timer_2.schedule(timerTask_2, 350);
	                        }else{
	                        	progress_2.setText(String.valueOf("100") + " %");
	                        	StringBuffer tempStr_2 = new StringBuffer();
	                        	for(int i = 0;i < barcodeContentArr_2.length;i ++){
	                        		if(barcodeContentArr_2[i][0] == 0) scanNull_2 ++;
	                        		tempStr_2.append(String.valueOf(barcodeContentArr_2[i]));
	                        	}
		                        //存储图像文件
		                        if(!(tempStr_2.toString().equals("") || tempStr_2.toString().equals(null))) saveFile(stringToCode(tempStr_2.toString()), "source-B", "jpg");
		                        vibrateMethod.Vibrate(CameraTestActivity.this, 200);       //震动提醒
		                        imageEndFlag_2 = true;
	                        }
	                    }
                	}
                	
                	//分组延时法
                	if(counterTimer_2 == preErrorGroupNums){
                		soundPool.play(1 ,1, 1, 0, 0, 1);
                		counterTimer = 0;
                		counterTimer_2 = 0;
                	}
                	
                	//处理结果
                    if(imageEndFlag && imageEndFlag_2){	
                    	endTime = System.currentTimeMillis();
                    	//向主ResultReceiver传递结果消息
                        Intent resultIntent = new Intent(CameraTestActivity.this, ResultReceiver.class);
        	            Bundle bundle = new Bundle();
        	            bundle.putLong("useTime", (endTime - startTime));
        	            bundle.putInt("scanNull", scanNull);
        	            bundle.putInt("scanNull_2", scanNull_2);
        	            resultIntent.putExtras(bundle);
        	            startActivity(resultIntent);
        	            
                    	previewing = false;
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
         				if (mCamera != null) {
        		            previewing = false;
        		            mCamera.setPreviewCallback(null);
        		            mCamera.release();
        		            mCamera = null;
        		        }
         				finish();
                    }
                    //
                }
            }
        };
    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
        	if(focusTimes < 1){ 
        		autoFocusHandler.postDelayed(doAutoFocus, 0);
            } 
        	focusTimes ++;
        }
    };
    
    public byte[] stringToCode(String string) {  
		byte[] bitmapArray = null;
        try {  
            bitmapArray = Base64.decodeBase64(string.getBytes());                       //Commons-Codec-1.9Base64解码
        } catch (Exception e) {  
                e.printStackTrace();  
        }  
        return bitmapArray;
	} 
    
    public void saveFile(byte[] data, String fileName, String fileType){
		File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "kpds" + File.separator + fileName + "." + fileType); 
		if (f.exists()) { 
			f.delete();
		} 
		try { 
			FileOutputStream out = new FileOutputStream(f, true); 
			BufferedOutputStream bos = new BufferedOutputStream(out);
			bos.write(data);
			bos.flush();
			bos.close();
			out.close();
			
		} catch (Exception e) { 
			Toast.makeText(CameraTestActivity.this, "Saving File Failed", Toast.LENGTH_SHORT).show();
		}
	}
    
    //定义震动类
    public static class vibrateMethod {  
        public static void Vibrate(final Activity activity, long milliseconds) { 
            Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE); 
            vib.vibrate(milliseconds); 
        } 
        public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) { 
            Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE); 
            vib.vibrate(pattern, isRepeat ? 1 : -1); 
        } 
    } 
    
    public int getSpValue(SharedPreferences sp, int index){
		int [] temp = new int[15];
		temp[0] = Integer.valueOf(sp.getString("per_barcode_content", "1800"));
		temp[1] = Integer.valueOf(sp.getString("per_barcode_size", "900"));
		temp[2] = Integer.valueOf(sp.getString("per_barcode_interval", "280"));
		temp[3] = Integer.valueOf(sp.getString("barcode_start_delay", "1000"));
		temp[4] = Integer.valueOf(sp.getString("barcode_product_thread_num", "1"));
		temp[5] = Integer.valueOf(sp.getString("barcode_product_thread_priority", "0"));
		temp[7] = sp.getInt("barcodeNum", 0);
		temp[8] = Integer.valueOf(sp.getString("barcode_receive_scan_density", "2"));
		temp[9] = Integer.valueOf(sp.getString("barcode_receive_preview_width", "720"));
		temp[10] = Integer.valueOf(sp.getString("barcode_receive_preview_height", "480"));
		temp[11] = Integer.valueOf(sp.getString("error_ctl_group_nums", "5"));
		return temp[index];
	}
}
