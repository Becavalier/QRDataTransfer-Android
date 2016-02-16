package com.yhspy.zbartest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener{
	EditTextPreference etpContent = null;
	EditTextPreference etpSize = null;
	EditTextPreference etpInterval = null;
	EditTextPreference etpDelay = null;
	EditTextPreference etpAppear = null;
	EditTextPreference etpAppearPriority = null;
	ListPreference etpReceiverScanDensity = null;
	EditTextPreference etpReceivePreviewWidth = null;
	EditTextPreference etpReceivePreviewHeight = null;
	EditTextPreference etpErrorGroupNums = null;
	EditTextPreference etpErrorSoundLow = null;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_settings);
        SharedPreferences defaultSp = PreferenceManager.getDefaultSharedPreferences(this);  
        //获得当前设置参数
        String preBarcodeContent = defaultSp.getString("per_barcode_content", "2500");
        String preBarcodeSize = defaultSp.getString("per_barcode_size", "900");
        String preBarcodeInterval = defaultSp.getString("per_barcode_interval", "280");
        String preBarcodeStartDelay = defaultSp.getString("barcode_start_delay", "1000");
        String preThreadAppear = defaultSp.getString("barcode_product_thread_num", "1");
        String preThreadAppearPriority = defaultSp.getString("barcode_product_thread_priority", "0");
        String preBarcodeScanDensity = defaultSp.getString("barcode_receive_scan_density", "1");
        String preScanPreviewWidth = defaultSp.getString("barcode_receive_preview_width", "720");
        String preScanPreviewHeight = defaultSp.getString("barcode_receive_preview_height", "480");
        String preErrorGroupNums = defaultSp.getString("error_ctl_group_nums", "5");
        String preErrorSoundLow = defaultSp.getString("error_ctl_sound_low", "76");
        
        
        etpContent = (EditTextPreference) findPreference("per_barcode_content");
        etpSize = (EditTextPreference) findPreference("per_barcode_size");
        etpInterval = (EditTextPreference) findPreference("per_barcode_interval");
        etpDelay = (EditTextPreference) findPreference("barcode_start_delay");
        etpAppear = (EditTextPreference) findPreference("barcode_product_thread_num");
        etpAppearPriority = (EditTextPreference) findPreference("barcode_product_thread_priority");
        etpReceiverScanDensity = (ListPreference) findPreference("barcode_receive_scan_density");
        etpReceivePreviewWidth = (EditTextPreference) findPreference("barcode_receive_preview_width");
        etpReceivePreviewHeight = (EditTextPreference) findPreference("barcode_receive_preview_height");
        etpErrorGroupNums = (EditTextPreference) findPreference("error_ctl_group_nums");
    	etpErrorSoundLow = (EditTextPreference) findPreference("error_ctl_sound_low");
        
        //显示当前设定值
        etpContent.setSummary(preBarcodeContent + " 字符");
        etpSize.setSummary(preBarcodeSize);
        etpInterval.setSummary(preBarcodeInterval + " MS");
        etpDelay.setSummary(preBarcodeStartDelay + " MS");
        etpAppear.setSummary(preThreadAppear + " 个");
        etpAppearPriority.setSummary(preThreadAppearPriority);
        etpReceiverScanDensity.setSummary(preBarcodeScanDensity);
        etpReceivePreviewWidth.setSummary(preScanPreviewWidth);
        etpReceivePreviewHeight.setSummary(preScanPreviewHeight);
        etpErrorGroupNums.setSummary(preErrorGroupNums);
    	etpErrorSoundLow.setSummary(preErrorSoundLow + " db");
        
        //绑定监听器
        etpContent.setOnPreferenceChangeListener(this);
        etpSize.setOnPreferenceChangeListener(this);
        etpInterval.setOnPreferenceChangeListener(this);
        etpDelay.setOnPreferenceChangeListener(this);
        etpAppear.setOnPreferenceChangeListener(this);
        etpAppearPriority.setOnPreferenceChangeListener(this);
        etpReceiverScanDensity.setOnPreferenceChangeListener(this);
        etpReceivePreviewWidth.setOnPreferenceChangeListener(this);
        etpReceivePreviewHeight.setOnPreferenceChangeListener(this);
        etpErrorGroupNums.setOnPreferenceChangeListener(this);
    	etpErrorSoundLow.setOnPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//    	Toast.makeText(Settings.this, "onPreferenceTreeClick", Toast.LENGTH_SHORT).show();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	  
	@Override
	public boolean onPreferenceClick(Preference preference) {
//		Toast.makeText(Settings.this, "onPreferenceClick", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		//分组判断
		if(preference.getKey().equals("per_barcode_content")){
			if(newValue.toString().equals(""))return false;
			preference.setSummary(newValue.toString().trim() + " 字符");
		}else if(preference.getKey().equals("per_barcode_size")){
			if(newValue.toString().equals(""))return false;
			preference.setSummary(newValue.toString().trim());
		}else if(preference.getKey().equals("per_barcode_interval")){
			if(newValue.toString().equals(""))return false;
			preference.setSummary(newValue.toString().trim() + " MS");
		}else if(preference.getKey().equals("barcode_start_delay")){
			if(newValue.toString().equals(""))return false;
			preference.setSummary(newValue.toString().trim() + " MS");
		}else if(preference.getKey().equals("barcode_product_thread_num")){
			if(newValue.toString().equals("") || Integer.valueOf(newValue.toString()) > 3 || Integer.valueOf(newValue.toString()) < 0)return false;
			preference.setSummary(newValue.toString().trim() + " 个");
		}else if(preference.getKey().equals("barcode_product_thread_priority")){
			if(newValue.toString().equals("") || Integer.valueOf(newValue.toString()) > 0 || Integer.valueOf(newValue.toString()) < -16)return false;
			preference.setSummary(newValue.toString().trim());
		}else if(preference.getKey().equals("barcode_receive_scan_density")){
			preference.setSummary(newValue.toString().trim());
		}else if(preference.getKey().equals("barcode_receive_preview_width")){
			preference.setSummary(newValue.toString().trim());
		}else if(preference.getKey().equals("barcode_receive_preview_height")){
			preference.setSummary(newValue.toString().trim());
		}else if(preference.getKey().equals("error_ctl_group_nums")){
			preference.setSummary(newValue.toString().trim());
		}else if(preference.getKey().equals("error_ctl_sound_low")){
			preference.setSummary(newValue.toString().trim() + " db");
		}
		Toast.makeText(Settings.this, "设置已保存..", Toast.LENGTH_SHORT).show();
		return true;  //返回Ture保存最新的数据
	}
}