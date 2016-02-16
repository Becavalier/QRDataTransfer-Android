package com.yhspy.zbartest;

import android.annotation.SuppressLint;

@SuppressLint("UseValueOf")
public class CompressByteCore {
	
	
	
	public static String showByte(byte[] data){
		String s = "";
		for(int i = 0;i < data.length;i++){
			s = s + " " + data[i];
		}
		return s;
	}
	
	
	/////////////////////////////////////////////////////////算法对大数据量的字符串处理效率太低，且ZBar对IOS88591的读取有问题
	@SuppressWarnings({ "unused", "static-access" })
	public static byte[] encodeCore(String txt, int bit){
		int length = txt.length();
		float tmpRet1=0,tmpRet2=0;
		if(bit==6){
			tmpRet1=3.0f;
			tmpRet2=4.0f;
		}else if(bit==5){
			tmpRet1=5.0f;
			tmpRet2=8.0f;
		}
		byte encoded[]=new byte[(int)(tmpRet1*Math.ceil(length/tmpRet2))];
		char str[]=new char[length];
		txt.getChars(0,length,str,0);
		int chaVal = 0;
		String temp;
		String strBinary = new String("");
		for (int i = 0;i<length; i++){
			temp = Integer.toBinaryString(toValue(str[i]));
			while(temp.length()%bit != 0){
				temp="0"+temp;
			}
			strBinary=strBinary+temp;
		}
		while(strBinary.length()%8 != 0){
		   strBinary=strBinary+"0";
		}
		Integer tempInt =new Integer(0);
		for(int i=0 ; i<strBinary.length();i=i+8){
			tempInt = tempInt.valueOf(strBinary.substring(i,i+8),2);
			encoded[i/8]=tempInt.byteValue();
		}
		return encoded;
	}
	
	@SuppressWarnings("static-access")
	public static String decodeCore(byte[] encoded, int bit){
		String strTemp = new String("");
		String strBinary = new String("");
		String strText = new String("");
		Integer tempInt =new Integer(0);
		int intTemp=0;
		for(int i = 0;i<encoded.length;i++){         
			if(encoded[i]<0){
				intTemp = (int)encoded[i]+256;
			}else
				intTemp = (int)encoded[i];
			strTemp = Integer.toBinaryString(intTemp);
			while(strTemp.length()%8 != 0){
				strTemp="0"+strTemp;
			}
			strBinary = strBinary+strTemp;
		}
		for(int i=0 ; i<strBinary.length();i=i+bit){
			tempInt = tempInt.valueOf(strBinary.substring(i,i+bit),2);
			strText = strText + toChar(tempInt.intValue()); 
		}
		return strText;
	}
	
	
	private static int toValue(char ch){
		int chaVal = 0;
		switch(ch){
			case' ':chaVal=0;break; case'a':chaVal=1;break;
			case'b':chaVal=2;break; case'c':chaVal=3;break;
			case'd':chaVal=4;break; case'e':chaVal=5;break;
			case'f':chaVal=6;break; case'g':chaVal=7;break;
			case'h':chaVal=8;break; case'i':chaVal=9;break;
			case'j':chaVal=10;break; case'k':chaVal=11;break;
			case'l':chaVal=12;break; case'm':chaVal=13;break;
			case'n':chaVal=14;break; case'o':chaVal=15;break;
			case'p':chaVal=16;break; case'q':chaVal=17;break;
			case'r':chaVal=18;break; case's':chaVal=19;break;
			case't':chaVal=20;break; case'u':chaVal=21;break;
			case'v':chaVal=22;break; case'w':chaVal=23;break;
			case'x':chaVal=24;break; case'y':chaVal=25;break;
			case'z':chaVal=26;break; case'.':chaVal=27;break;
			case'+':chaVal=28;break; case'=':chaVal=29;break;
			case'/':chaVal=30;break; case'2':chaVal=31;break;
			case'A':chaVal=32;break; case'B':chaVal=33;break;
			case'C':chaVal=34;break; case'D':chaVal=35;break;
			case'E':chaVal=36;break; case'F':chaVal=37;break;
			case'G':chaVal=38;break; case'H':chaVal=39;break;
			case'I':chaVal=40;break; case'J':chaVal=41;break;
			case'K':chaVal=42;break; case'L':chaVal=43;break;
			case'M':chaVal=44;break; case'N':chaVal=45;break;
			case'O':chaVal=46;break; case'P':chaVal=47;break;
			case'Q':chaVal=48;break; case'R':chaVal=49;break;
			case'S':chaVal=50;break; case'T':chaVal=51;break;
			case'U':chaVal=52;break; case'V':chaVal=53;break;
			case'W':chaVal=54;break; case'0':chaVal=55;break;
			case'1':chaVal=56;break; case'3':chaVal=57;break;
			case'4':chaVal=58;break;case'5':chaVal=59;break;
			case'6':chaVal=60;break;case'7':chaVal=61;break;
			case'8':chaVal=62;break;case'9':chaVal=63;break;
			default:chaVal=0;
		}
		return chaVal;
	}
	
	private static char toChar(int val){
		char ch = ' ';
		switch(val){
			case 0:ch=' ';break; case 1:ch='a';break;
			case 2:ch='b';break; case 3:ch='c';break;
			case 4:ch='d';break; case 5:ch='e';break;
			case 6:ch='f';break; case 7:ch='g';break;
			case 8:ch='h';break; case 9:ch='i';break;
			case 10:ch='j';break; case 11:ch='k';break;
			case 12:ch='l';break; case 13:ch='m';break;
			case 14:ch='n';break; case 15:ch='o';break;
			case 16:ch='p';break; case 17:ch='q';break;
			case 18:ch='r';break; case 19:ch='s';break;
			case 20:ch='t';break; case 21:ch='u';break;
			case 22:ch='v';break; case 23:ch='w';break;
			case 24:ch='x';break; case 25:ch='y';break;
			case 26:ch='z';break; case 27:ch='.';break;
			case 28:ch='+';break; case 29:ch='=';break;
			case 30:ch='/';break; case 31 :ch='2';break;
			case 32:ch='A';break; case 33:ch='B';break;
			case 34:ch='C';break; case 35:ch='D';break;
			case 36:ch='E';break; case 37:ch='F';break;
			case 38:ch='G';break; case 39:ch='H';break;
			case 40:ch='I';break; case 41:ch='J';break;
			case 42:ch='K';break; case 43:ch='L';break;
			case 44:ch='M';break; case 45:ch='N';break;
			case 46:ch='O';break; case 47:ch='P';break;
			case 48:ch='Q';break; case 49:ch='R';break;
			case 50:ch='S';break; case 51:ch='T';break;
			case 52:ch='U';break; case 53:ch='V';break;
			case 54:ch='W';break; case 55:ch='0';break;
			case 56:ch='1';break; case 57:ch='3';break;
			case 58:ch='4';break; case 59:ch='5';break;
			case 60:ch='6';break; case 61:ch='7';break;
			case 62:ch='8';break; case 63:ch='9';break;
			default:ch=' ';
		}
		return ch;
	}
	
	
	
}
