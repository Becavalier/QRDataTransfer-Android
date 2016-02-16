package com.yhspy.zbartest;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ResultReceiver extends Activity {

	private Long useTime = 0l;
	private TextView txtView = null;
	private ImageView img = null;
	private ImageView img_2 = null;
	private Button cancelBtn = null;
	private int scanNull = 0;
	private int scanNull_2 = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_receiver);
		txtView = (TextView)findViewById(R.id.txtView);
		img = (ImageView)findViewById(R.id.img);
		img_2 = (ImageView)findViewById(R.id.img_2);
		cancelBtn = (Button)findViewById(R.id.cancelBtn);
		
		Intent intent = this.getIntent();       
		Bundle bundle = intent.getExtras();    
		useTime = bundle.getLong("useTime");
		scanNull = bundle.getInt("scanNull");
		scanNull_2 = bundle.getInt("scanNull_2");
		txtView.setText(String.valueOf("结果：  " + useTime/1000) + " S SN1:" + (scanNull-1) + " SN2: " + (scanNull_2-1));
		
		Bitmap imgBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + File.separator + "kpds" +  File.separator + "source-A.jpg");
		Bitmap img_2Bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() + File.separator + "kpds" +  File.separator + "source-B.jpg");
		
		img.setAdjustViewBounds(true);
		img.setMaxHeight(800);
		img.setMaxWidth(800);
		img.setScaleType(ScaleType.FIT_CENTER);
		img.setImageBitmap(imgBitmap);
		
		img_2.setAdjustViewBounds(true);
		img_2.setMaxHeight(800);
		img_2.setMaxWidth(800);
		img_2.setScaleType(ScaleType.FIT_CENTER);
		img_2.setImageBitmap(img_2Bitmap);
		
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
