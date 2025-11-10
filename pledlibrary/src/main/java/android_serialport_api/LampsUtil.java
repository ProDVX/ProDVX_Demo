package android_serialport_api;

import android.util.Log;

import java.io.IOException;

public class LampsUtil {
	private static final String TAG = "LampsUtil";
	private static int num1 = stringsimpleToByte("55");
	private static int num2 = stringsimpleToByte("AA");	
	
	private static int numB_off = stringsimpleToByte("00");
	private static int numR_off = stringsimpleToByte("00");
	private static int numG_off = stringsimpleToByte("00");

	private static int numB = stringsimpleToByte("ff");   //blue values
	private static int numG = stringsimpleToByte("ff");   //green values
	private static int numR = stringsimpleToByte("ff");   //red values
	
	/*public LampsUtil(){
		num1=stringsimpleToByte("55");
		num2=stringsimpleToByte("AA");
		
		numB_off = stringsimpleToByte("00");
		numR_off = stringsimpleToByte("00");
		numG_off = stringsimpleToByte("00");

		//LedUtils.lock = new Object();
	}*/
	
	public static byte[] intToByteArray(int a) {  
	    return new byte[] {     
	        (byte) (a & 0xFF)  
	    };  
	}
	
	public static int  stringsimpleToByte(String in){
        int b= Integer.parseInt(in,16);
        return b;
        //Arrays.fill(b, (byte)t);
    }

	public static void AllLedsOnController(int red,int green, int blue) throws IOException {  //All LEDs on: White color.
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));

		UartSend.out.write(intToByteArray(red));
		UartSend.out.write(intToByteArray(red));

		UartSend.out.write(intToByteArray(green));
		UartSend.out.write(intToByteArray(green));

		UartSend.out.write(intToByteArray(blue));
		UartSend.out.write(intToByteArray(blue));
		Log.d(TAG, "LED turn by specific value");
	}

	public static void AllOnLamps() throws IOException {  //All LEDs on: White color.
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));
		
		UartSend.out.write(intToByteArray(numR));
		UartSend.out.write(intToByteArray(numR));
		
		UartSend.out.write(intToByteArray(numG));
		UartSend.out.write(intToByteArray(numG));
				
		UartSend.out.write(intToByteArray(numB));
		UartSend.out.write(intToByteArray(numB));
		Log.d(TAG, "LED turn White");
	}

	public static void AllOffLamps() throws IOException {   //All LEDs Off
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));
		UartSend.out.write(intToByteArray(numR_off));
		UartSend.out.write(intToByteArray(numR_off));
		UartSend.out.write(intToByteArray(numG_off));
		UartSend.out.write(intToByteArray(numG_off));
		UartSend.out.write(intToByteArray(numB_off));
		UartSend.out.write(intToByteArray(numB_off));
		Log.d(TAG, "LED turn Off");
	}

	public static void AllRLamps() throws IOException {    //All Red light On
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));
		
		UartSend.out.write(intToByteArray(numR));
		UartSend.out.write(intToByteArray(numR));
		
		UartSend.out.write(intToByteArray(numG_off));
		UartSend.out.write(intToByteArray(numG_off));
		
		UartSend.out.write(intToByteArray(numB_off));
		UartSend.out.write(intToByteArray(numB_off));
		Log.d(TAG, "LED turn Red");
	}
	
	public static void AllGLamps() throws IOException {    //All Green light On
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));
		
		UartSend.out.write(intToByteArray(numR_off));
		UartSend.out.write(intToByteArray(numR_off));
		
		UartSend.out.write(intToByteArray(numG));
		UartSend.out.write(intToByteArray(numG));
		
		UartSend.out.write(intToByteArray(numB_off));
		UartSend.out.write(intToByteArray(numB_off));
		Log.d(TAG, "LED turn Green");
	}
	
	public static void AllBLamps() throws IOException {  //All Blue light On
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));
		
		UartSend.out.write(intToByteArray(numR_off));
		UartSend.out.write(intToByteArray(numR_off));
		
		UartSend.out.write(intToByteArray(numG_off));
		UartSend.out.write(intToByteArray(numG_off));
		
		UartSend.out.write(intToByteArray(numB));
		UartSend.out.write(intToByteArray(numB));
		Log.d(TAG, "LED turn Blue");
	}


	public static void AllYLamps() throws IOException { 	//All Red & Green light on, it's Yellow color.
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));

		UartSend.out.write(intToByteArray(numR));
		UartSend.out.write(intToByteArray(numR));

		UartSend.out.write(intToByteArray(numG));
		UartSend.out.write(intToByteArray(numG));

		UartSend.out.write(intToByteArray(numB_off));
		UartSend.out.write(intToByteArray(numB_off));
		Log.d(TAG, "LED turn Yellow");
	}

	public static void AllCLamps() throws IOException { 	//All Green & Blue light on, it's Cyan color.
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));

		UartSend.out.write(intToByteArray(numR_off));
		UartSend.out.write(intToByteArray(numR_off));

		UartSend.out.write(intToByteArray(numG));
		UartSend.out.write(intToByteArray(numG));

		UartSend.out.write(intToByteArray(numB));
		UartSend.out.write(intToByteArray(numB));
		Log.d(TAG, "LED turn Cyan");
	}

	public static void AllPLamps() throws IOException { 	//All Red & Blue light on, it's Purple color.
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));

		UartSend.out.write(intToByteArray(numR));
		UartSend.out.write(intToByteArray(numR));

		UartSend.out.write(intToByteArray(numG_off));
		UartSend.out.write(intToByteArray(numG_off));

		UartSend.out.write(intToByteArray(numB));
		UartSend.out.write(intToByteArray(numB));
		Log.d(TAG, "LED turn Purple");
	}

	public static void AllWLamps() throws IOException { 	//All of the light on, it's White color.
		UartSend.out.write(intToByteArray(num1));
		UartSend.out.write(intToByteArray(num2));

		UartSend.out.write(intToByteArray(numR));
		UartSend.out.write(intToByteArray(numR));

		UartSend.out.write(intToByteArray(numG));
		UartSend.out.write(intToByteArray(numG));

		UartSend.out.write(intToByteArray(numB));
		UartSend.out.write(intToByteArray(numB));
		Log.d(TAG, "LED turn White");
	}

}
