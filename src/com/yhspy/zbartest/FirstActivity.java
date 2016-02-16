package com.yhspy.zbartest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

public class FirstActivity extends Activity{
	
	@Override
	protected void onResume() {
		//重新读取数据
		preBarcodeContent = getSpValue(defaultSp, 0);
		preBarcodeSize = getSpValue(defaultSp, 1);
		preBarcodeInterval = getSpValue(defaultSp, 2);
		preBarcodeStartDelay = getSpValue(defaultSp, 3);
		preThreadAppearPriority = getSpValue(defaultSp, 5);
		preThreadAppear = getSpValue(defaultSp, 4);
		preErrorGroupNums = getSpValue(defaultSp, 6);
		//重新解析图片
		barcodeNum = 1;
		byte[] tempData = null;                                                        //传输图片数据
//		InputStream is= getResources().openRawResource(R.drawable.result); 
				File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "source-A.jpg");
				//source-B
				InputStream is = null;
				try{
					is= new BufferedInputStream(new FileInputStream(file));              //将图片变成输入比特流
				}catch(Exception e){
					e.printStackTrace();
				}
		try {
			tempData = input2Byte(is);                //将输入流变成比特数组
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String encodeStringEncoder = bitmapToString(tempData);                       //BASE64编码并压缩
		charEncoderContent = encodeStringEncoder.toCharArray();                      //获得数据字符数组
		barcodeNum = (((charEncoderContent.length % preBarcodeContent) == 0) ? (charEncoderContent.length / preBarcodeContent) : ((charEncoderContent.length / preBarcodeContent) + 1)) + barcodeNum;
		Editor spEditor = defaultSp.edit();
		spEditor.putInt("barcodeNum", barcodeNum);
		spEditor.commit();
		getText.setText("共 " + String.valueOf(barcodeNum) + " 幅");
		Toast.makeText(FirstActivity.this, "Reading Complete..", Toast.LENGTH_SHORT).show();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//Destroy时释放图片资源
		if(rawBitmapEncoder != null){
			if(rawBitmapEncoder.isRecycled() == false){
				rawBitmapEncoder.recycle();
				System.gc();
			}
		}
		super.onDestroy();
	}
	//获取当前Context
	public static FirstActivity getInstance() {
        return instance;
    }
	
	private static FirstActivity instance;
	//初始化默认设置参数变量
	private int preBarcodeContent = 1800;
	private int preBarcodeSize = 900;
	private int preBarcodeInterval = 300;
	private int preBarcodeStartDelay = 1000;
	private int preThreadAppearPriority = 0;
	private int preThreadAppear = 1;
	private int preErrorGroupNums = 5;
	//初始化其他变量
	private int barcodeNum = 1;          //初始二维码数量（结束码）
	private Button scanBtn = null;
	private Button getBtn = null;
	private Button btnTest = null;
	private Button btnInitialize = null;
	private Button btnSos = null;
	private Button btnChgPos = null;
	private TextView getText = null;
	private ImageView getImg = null;
	private Timer barEncoderTimer;
	private TimerTask barEncoderTimerTask;
	private Handler encoderHandler;
	private Bitmap rawBitmapEncoder;
	private Intent startScan;
	private Intent startSetting;
	private String endStr = "END000OPTIMUS000ENDEND000OPTIMUS000ENDEND000OPTIMUS000END";
	private Bitmap [] quCode = null;
	private boolean threadCT = true;
	private boolean threadCtl = true;
	//二维码数组截取标志位
	private int countNum = 0;
	private int flag = 0;
	private int startIndex = 0;
	private int endIndex = 0;
	private char[][] tempChar = null;
	private int i = 0;
	private int j = 0;
	//设置读取参数变量
	private SharedPreferences defaultSp = null;
	private Thread [] barcodeTherad = null;
	private int ido = 0;                             //调整线程同步
	private char[] charEncoderContent = null;
	private AudioRecordDemo audioRecord = null;
	private Timer voiceCtlTimer = null;
	private TimerTask voiceCtlTimerTask = null;
	private Timer voiceCtlTimer_2 = null;
	private TimerTask voiceCtlTimerTask_2 = null;
	private boolean isPassed = false;
	private int counterTimes = 0;
	private boolean isFirst = true;
	private boolean timerCtl = false;
	private Timer timerFK  = null;
	private TimerTask timerTaskFK = null;
	private int tempJ = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_first);
		//初始化默认SharedPreferences
		defaultSp = PreferenceManager.getDefaultSharedPreferences(this);  
		preBarcodeContent = getSpValue(defaultSp, 0);
		preBarcodeSize = getSpValue(defaultSp, 1);
		preBarcodeInterval = getSpValue(defaultSp, 2);
		preBarcodeStartDelay = getSpValue(defaultSp, 3);
		preThreadAppearPriority = getSpValue(defaultSp, 5);
		preThreadAppear = getSpValue(defaultSp, 4);
		preErrorGroupNums = getSpValue(defaultSp, 6);
		
		scanBtn = (Button) findViewById(R.id.scanButton);
		scanBtn.setOnClickListener(new BtnListener());
		
		getBtn = (Button) findViewById(R.id.makeButton);
		getBtn.setOnClickListener(new BtnListener());
		
		btnTest = (Button) findViewById(R.id.btnTest);      
		btnTest.setOnClickListener(new BtnListener());
		
		btnInitialize = (Button) findViewById(R.id.btnInitialize);
		btnInitialize.setOnClickListener(new BtnListener());
		
		btnSos = (Button) findViewById(R.id.sosButton);
		btnSos.setOnClickListener(new BtnListener());
		
		btnChgPos = (Button) findViewById(R.id.chgPosButton);
		btnChgPos.setOnClickListener(new BtnListener());
		
		getText = (TextView) findViewById(R.id.getText);
		
		getImg = (ImageView) findViewById(R.id.img);
		getImg.setAdjustViewBounds(true);
//		getImg.setMaxHeight(600);
//		getImg.setMaxWidth(600);
		getImg.setScaleType(ScaleType.FIT_XY);
		
		encoderHandler = new Handler();
		getImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.center));
		audioRecord = new AudioRecordDemo();
		//开始解析图片
		byte[] tempData = null;                                                        //传输图片数据
//		InputStream is= getResources().openRawResource(R.drawable.result); 
		File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "source-A.jpg");
		InputStream is = null;
		try{
			is= new BufferedInputStream(new FileInputStream(file));              //将图片变成输入比特流
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			tempData = input2Byte(is);                //将输入流变成比特数组
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String encodeStringEncoder = bitmapToString(tempData);                       //BASE64编码并压缩
		charEncoderContent = encodeStringEncoder.toCharArray();                      //获得数据字符数组
		barcodeNum = (((charEncoderContent.length % preBarcodeContent) == 0) ? (charEncoderContent.length / preBarcodeContent) : ((charEncoderContent.length / preBarcodeContent) + 1)) + barcodeNum;
		Editor spEditor = defaultSp.edit();
		spEditor.putInt("barcodeNum", barcodeNum);
		spEditor.commit();
		getText.setText("共 " + barcodeNum + " 幅");
		Toast.makeText(FirstActivity.this, "Reading Complete..", Toast.LENGTH_SHORT).show();
	}
	
	class BtnListener implements OnClickListener{

		public void onClick(View v) {
			switch(v.getId()){
				case R.id.scanButton: 
					audioRecord.getNoiseLevel();
					barcodeTherad = new Thread[getSpValue(defaultSp, 4)];
					threadCT = true;       
					threadCtl = true;//编码线程开关
					scanBtn.setEnabled(false);
					btnTest.setEnabled(false);
					btnSos.setEnabled(false);
					btnChgPos.setEnabled(false);
					getBtn.setEnabled(false);
					
					//分配二维码数组数据线程
					for(int k = 0;k < preThreadAppear;k ++){
						barcodeTherad[k] = new Thread() {
							@Override
					        public void run() {
									//设置线程优先级
									Process.setThreadPriority(preThreadAppearPriority);
									//初始化临时数据分配数组
									tempChar = new char[barcodeNum - 1][preBarcodeContent + 3];
									quCode = new Bitmap[barcodeNum - 1];
									//初始化首尾索引
									startIndex = flag;
									endIndex = flag + preBarcodeContent - 1;
									//设置循环条件startIndex=0
								while(threadCtl){
									//System.out.println(String.valueOf(i - j) + " OUT");
									if((i - j) <= 5) threadCT = true;
									while(startIndex < charEncoderContent.length && threadCT == true){
										if(quCode[i] != null && ido != 1){
											i ++;
											ido = 0;
											continue;
										}
										ido = 1;
										//获得当前二维码容量
										countNum = preBarcodeContent;
										if(endIndex >= charEncoderContent.length){
											endIndex = charEncoderContent.length - 1;
											countNum = charEncoderContent.length - startIndex;
											tempChar[i] = new char[countNum + 3];
										}
										char [] midArr = new char [countNum + 3];
										System.arraycopy(charEncoderContent, startIndex, midArr, 0, countNum);
										tempChar[i] = midArr;
										//为每张二维码添加序列号
										if(String.valueOf(i).length() == 1){
											tempChar[i][midArr.length - 3] = '0';
											tempChar[i][midArr.length - 2] = '0';
											tempChar[i][midArr.length - 1] = String.valueOf(i).toCharArray()[0];
										}else if(String.valueOf(i).length() == 2){
											tempChar[i][midArr.length - 3] = '0';
											tempChar[i][midArr.length - 2] = String.valueOf(i).toCharArray()[0];
											tempChar[i][midArr.length - 1] = String.valueOf(i).toCharArray()[1];
										}else if(String.valueOf(i).length() == 3){
											tempChar[i][midArr.length - 3] = String.valueOf(i).toCharArray()[0];
											tempChar[i][midArr.length - 2] = String.valueOf(i).toCharArray()[1];
											tempChar[i][midArr.length - 1] = String.valueOf(i).toCharArray()[2];
										}
										//生成二维码数组
										try {
											quCode[i] = EncodingHandler.createQRCode(String.valueOf(tempChar[i]), preBarcodeSize);
										} catch (WriterException e) {
											e.printStackTrace();
										}
										flag += preBarcodeContent;
										//首尾索引
										startIndex = flag;
										endIndex = flag + preBarcodeContent - 1;
										i ++;
										ido = 0;
										//System.out.println(String.valueOf(i - j) + " IN");
										if((i - j) > 5) threadCT = false;
									}
								}
					        }
				        };
				        barcodeTherad[k].start();
					}
					
					getText.setText("剩 " + barcodeNum + " 幅");
					
					//分组扫描法
					voiceCtlTimer_2 = new Timer();
					voiceCtlTimerTask_2 = new TimerTask(){
						@Override
						public void run() {
							if(audioRecord.flag == false){
								timerCtl = false;
								if(timerTaskFK != null) timerTaskFK.cancel();
								if(timerFK != null) timerFK.cancel();
								j = tempJ;
								counterTimes = 0;
								isPassed = true;
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					};
					voiceCtlTimer_2.schedule(voiceCtlTimerTask_2, 0, 5);
					
					//扫描延时法
					voiceCtlTimer = new Timer();
					voiceCtlTimerTask = new TimerTask(){
						@Override
						public void run() {
							if(audioRecord.flag == false){
								barEncoderTimer.cancel();
								barEncoderTimerTask.cancel();
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								barEncoderTimer = new Timer();
								barEncoderTimerTask = new TimerTask() {
									@Override
									public void run() {
										Runnable r = new Runnable(){                            //在UI线程中运行
											@Override
											public void run() {
												if(j == barcodeNum){
													audioRecord.stop();
													threadCT = false;
													barEncoderTimerTask.cancel();
													barEncoderTimer.cancel();
													Thread.currentThread().interrupt(); 
												}else if(j == (barcodeNum - 1)){
													try {
														getImg.setImageBitmap(null);
														getImg.setImageBitmap(EncodingHandler.createQRCode(endStr, preBarcodeSize));
													} catch (WriterException e) {
														e.printStackTrace();
													}
													getText.setText("剩 " + (barcodeNum - 1 - j) + " 幅");
													j ++;
												}else{
													if(quCode[j] != null){
														getImg.setImageBitmap(null);
														getImg.setImageBitmap(quCode[j]);
														if(j > 1){
															if(quCode[j-1]!= null){         //回收Bitmap
																quCode[j-1] = null;
															}
														}
														getText.setText("剩 " + (barcodeNum - 1 - j) + " 幅");
														j ++;
												    }
												}
											}
										};
										encoderHandler.post(r);
									}
								};
								barEncoderTimer.schedule(barEncoderTimerTask, 0, preBarcodeInterval);
							}
						}
					};
					//voiceCtlTimer.schedule(voiceCtlTimerTask, 0, 5);
					
					//循环产生二维码
					barEncoderTimer = new Timer();
					barEncoderTimerTask = new TimerTask() {
						@Override
						public void run() {
							Runnable r = new Runnable(){                            //在UI线程中运行
								@Override
								public void run() {
									if(j == barcodeNum){
										audioRecord.stop();
										threadCT = false;
										barEncoderTimerTask.cancel();
										barEncoderTimer.cancel();
										System.out.println("END--------------------------->");
										Thread.currentThread().interrupt(); 
									}else if(j == (barcodeNum - 1)){
										try {
											getImg.setImageBitmap(null);
											getImg.setImageBitmap(EncodingHandler.createQRCode(endStr, preBarcodeSize));
										} catch (WriterException e) {
											e.printStackTrace();
										}
										getText.setText("剩 " + (barcodeNum - 1 - j) + " 幅");
										j ++;
									}else{
										if(isFirst) isPassed = true;
										if(counterTimes == preErrorGroupNums){
											timerCtl = true;
											isPassed = false;
											timerFK = new Timer();
											timerTaskFK = new TimerTask(){
												@Override
												public void run() {
													if(timerCtl){
														counterTimes = 0;
														System.out.println("-------------------------->" + String.valueOf(j));
														tempJ = j;
														j = j - preErrorGroupNums;
														isPassed = true;
														timerCtl = false;
													}
												}
											};
											timerFK.schedule(timerTaskFK, 0, 99999);
											
										}
										if(quCode[j] != null && isPassed){
											if(timerTaskFK != null) timerTaskFK.cancel();
											if(timerFK != null) timerFK.cancel();
											isFirst = false;
											counterTimes ++;
											System.out.println("CounterTimers ---------------------> " + String.valueOf(counterTimes));
											getImg.setImageBitmap(null);
											getImg.setImageBitmap(quCode[j]);
											if(j > 7){
												if(quCode[j-7]!= null){         //回收Bitmap
													quCode[j-7] = null;
												}
											}
											getText.setText("剩 " + (barcodeNum - 1 - j) + " 幅");
											j ++;
									    }
									}
								}
							};
							encoderHandler.post(r);
						}
					};
					barEncoderTimer.schedule(barEncoderTimerTask, preBarcodeStartDelay, preBarcodeInterval);
				break;
				case R.id.makeButton:               //解码二维码按钮
					File folder = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "kpds"); 
					if(!folder.exists()){
						folder.mkdir();
					}
					startScan = new Intent(FirstActivity.this, CameraTestActivity.class);            //启动扫描Activity
        			startActivity(startScan);
				break;
				case R.id.btnInitialize:            //初始化按钮
					if(barEncoderTimer != null && barEncoderTimerTask != null){
						barEncoderTimer.cancel();
						barEncoderTimerTask.cancel();
					}
					if(voiceCtlTimer != null && voiceCtlTimerTask != null){
						voiceCtlTimer.cancel();
						voiceCtlTimerTask.cancel();
					}
					if(timerTaskFK != null) timerTaskFK.cancel();
					if(timerFK != null) timerFK.cancel();
					counterTimes = 0;
					isPassed = true;
					audioRecord.stop();
					audioRecord.flag = true;
					threadCT = false;
					threadCtl = false;
					scanBtn.setEnabled(true);
					btnTest.setEnabled(true);
					btnSos.setEnabled(true);
					btnChgPos.setEnabled(true);
					getBtn.setEnabled(true);
					countNum = 0;
					flag = 0;
					startIndex = 0;
					endIndex = 0;
					tempChar = null;
					i = 0;
					j = 0;
					getText.setText("共 " + barcodeNum + " 幅");
					if(rawBitmapEncoder != null){
						if(rawBitmapEncoder.isRecycled() == false){
						rawBitmapEncoder.recycle();
						System.gc();
						}
					}
					getImg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.center));
				break;
				case R.id.btnTest: 
					//设置菜单Activity
					startSetting = new Intent(FirstActivity.this, Settings.class);            //启动扫描Activity
        			startActivityForResult(startSetting, 0);
				break;
				case R.id.sosButton: 
					//
				break;
				case R.id.chgPosButton:
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getImg.getLayoutParams();
					//获得手机屏幕宽度
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					int screenWidth = dm.widthPixels;
					if((params.width + params.leftMargin) < screenWidth){
						params.leftMargin = screenWidth - params.width;
						Toast.makeText(FirstActivity.this, "左对齐..", Toast.LENGTH_SHORT).show();
					}else{
						params.leftMargin = 0;
						Toast.makeText(FirstActivity.this, "右对齐..", Toast.LENGTH_SHORT).show();
					}
					getImg.setLayoutParams(params);
				break;
			}
		}
	}
	
	//存储文件函数
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
			Toast.makeText(FirstActivity.this, "Saving File Failed!", Toast.LENGTH_SHORT).show();
		}
	}

	//存储图像函数（存储Bitmap）
	public void saveBitmap(Bitmap b, String fileName) { 
		File f = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + fileName + ".jpg"); 
		if (f.exists()) { 
			f.delete(); 
		} 
		try { 
			FileOutputStream out = new FileOutputStream(f); 
			b.compress(Bitmap.CompressFormat.JPEG, 100, out); 
			out.flush(); 
			out.close(); 
		} catch (Exception e) { 
			Toast.makeText(FirstActivity.this, "Saving Pic Failed!", Toast.LENGTH_SHORT).show(); 
		}
	} 
		
	public String bitmapToString(byte[] data) {  
		
        String string = "";   
        string = Base64.encodeBase64String(data);                           //Commons-Codec-1.9Base64编码
        return string;
	}  
	
	public Bitmap stringToBitmap(String string) {  
        Bitmap bitmap = null;  
        try {  
            byte[] bitmapArray; 
            bitmapArray = Base64.decodeBase64(string.getBytes());                       //Commons-Codec-1.9Base64解码
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);  
        } catch (Exception e) {  
                e.printStackTrace();  
        }  
        return bitmap;  
	} 
	
	public byte[] stringToCode(String string) {  
		byte[] bitmapArray = null;
        try {  
            bitmapArray = Base64.decodeBase64(string.getBytes());                       //Commons-Codec-1.9Base64解码
        } catch (Exception e) {  
                e.printStackTrace();  
        }  
        return bitmapArray;
	} 
	
	public static final byte[] input2Byte(InputStream inStream) throws IOException {  
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] in2b = swapStream.toByteArray();  
        return in2b;  
    }  
	
	public int getSpValue(SharedPreferences sp, int index){
		int [] temp = new int[9];
		temp[0] = Integer.valueOf(sp.getString("per_barcode_content", "1800"));
		temp[1] = Integer.valueOf(sp.getString("per_barcode_size", "900"));
		temp[2] = Integer.valueOf(sp.getString("per_barcode_interval", "280"));
		temp[3] = Integer.valueOf(sp.getString("barcode_start_delay", "1000"));
		temp[4] = Integer.valueOf(sp.getString("barcode_product_thread_num", "1"));
		temp[5] = Integer.valueOf(sp.getString("barcode_product_thread_priority", "0"));
		temp[6] = Integer.valueOf(sp.getString("error_ctl_group_nums", "5"));
		temp[7] = Integer.valueOf(sp.getString("error_ctl_sound_low", "76"));		
		return temp[index];
	}
	
	//--------------------------------------------------自定义编码解码算法----------------------------------------------
	
	/**
	 * Byte转Bit
	 */
	public static String byteToBit(byte b) {
		return "" +(byte)((b >> 7) & 0x1) + 
		(byte)((b >> 6) & 0x1) + 
		(byte)((b >> 5) & 0x1) + 
		(byte)((b >> 4) & 0x1) + 
		(byte)((b >> 3) & 0x1) + 
		(byte)((b >> 2) & 0x1) + 
		(byte)((b >> 1) & 0x1) + 
		(byte)((b >> 0) & 0x1);
	}

	/**
	 * Bit转Byte
	 */
	public static byte BitToByte(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit处理
			if (byteStr.charAt(0) == '0') {// 正数
				re = Integer.parseInt(byteStr, 2);
			} else {// 负数
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {//4 bit处理
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}


}
