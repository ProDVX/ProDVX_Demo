package android_serialport_api;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import android_serialport_api.SerialPort;
import android_serialport_api.UartSend;

public class LedUtils {
    private static final String TAG = "LedUtils";
    private static final boolean DEBUG= true;
    private static String ttyUSB_left  = "11";
    private static String ttyUSB_right = "11";
    private static String ttyUSB_bottom = "11";
    private static String ttyUSB_left_old = "11";
    private static String ttyUSB_right_old= "11";
    private static String ttyUSB_bottom_old= "11";
    public static Object lock = new Object();
    public  static int x = 0;
    public  static int y = 0;
    public static int x_2 = 0;
    public static int send = 0;
    SerialPort ttyUSBRight;
    SerialPort ttyUSBLeft;
    SerialPort ttyUSBBottom;

    public void turnLedOff(){
        getTtyUSB();
        try {
        	if(!ttyUSB_left.equals("11")){
        		if(getUSB("/dev/"+ttyUSB_left) != null) {
            	ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);
            	UartSend.UartAllOff(ttyUSBLeft, ttyUSB_left).run();
          	}
        	}
            if (DEBUG) Log.d(TAG, "onClick: flag_7N======"+!ttyUSB_right.equals("11"));
            if(!ttyUSB_right.equals("11")){
            	if(getUSB("/dev/"+ttyUSB_right) != null) {
                ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);
                UartSend.UartAllOff(ttyUSBRight, ttyUSB_right).run();
            	}
          	}

            if(!ttyUSB_bottom.equals("11")){
                if(getUSB("/dev/"+ttyUSB_bottom) != null) {
                    ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);
                    UartSend.UartAllOff(ttyUSBBottom, ttyUSB_bottom).run();
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ledsController(String hex) throws IOException {
        if (hex.startsWith("#")){
            hex = hex.substring(1);
        }
        int red = Integer.parseInt(hex.substring(0,2),16);
        int green = Integer.parseInt(hex.substring(2,4),16);
        int blue = Integer.parseInt(hex.substring(4,6),16);
        ledsController(red,green,blue);
    }

    private void ledsController(int red,int green,int blue) throws IOException {
        getTtyUSB();

        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartLedsController(ttyUSBLeft, ttyUSB_left,red,green,blue).run();                      //The light is on the left (red light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);  //Open the baud rate
            UartSend.UartLedsController(ttyUSBRight, ttyUSB_right,red,green,blue).run();                   //The light is on the right (red light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartLedsController(ttyUSBBottom, ttyUSB_bottom,red,green,blue).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }


    public void turnRed() throws IOException {
        getTtyUSB();
        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartAllR(ttyUSBLeft, ttyUSB_left).run();                      //The light is on the left (red light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);  //Open the baud rate
            UartSend.UartAllR(ttyUSBRight, ttyUSB_right).run();                   //The light is on the right (red light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartAllR(ttyUSBBottom, ttyUSB_bottom).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }

    public void turnGreen() throws IOException {
        getTtyUSB();
        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartAllG(ttyUSBLeft, ttyUSB_left).run();  				//The light is on the left (green light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);     //Open the baud rate
            UartSend.UartAllG(ttyUSBRight, ttyUSB_right).run();  			//The light is on the right (green light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartAllG(ttyUSBBottom, ttyUSB_bottom).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }

    public void turnBlue() throws IOException {
        getTtyUSB();
        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartAllB(ttyUSBLeft, ttyUSB_left).run();				//The light is on the left (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);     //Open the baud rate
            UartSend.UartAllB(ttyUSBRight, ttyUSB_right).run();				//The light is on the right (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartAllB(ttyUSBBottom, ttyUSB_bottom).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }


    public void turnYellow() throws IOException {
        getTtyUSB();
        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartAllY(ttyUSBLeft, ttyUSB_left).run();				//The light is on the left (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);     //Open the baud rate
            UartSend.UartAllY(ttyUSBRight, ttyUSB_right).run();				//The light is on the right (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartAllY(ttyUSBBottom, ttyUSB_bottom).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }

    public void turnCyan() throws IOException {
        getTtyUSB();
        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartAllC(ttyUSBLeft, ttyUSB_left).run();				//The light is on the left (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);     //Open the baud rate
            UartSend.UartAllC(ttyUSBRight, ttyUSB_right).run();				//The light is on the right (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartAllC(ttyUSBBottom, ttyUSB_bottom).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }

    public void turnPurple() throws IOException {
        getTtyUSB();
        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartAllP(ttyUSBLeft, ttyUSB_left).run();				//The light is on the left (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);     //Open the baud rate
            UartSend.UartAllP(ttyUSBRight, ttyUSB_right).run();				//The light is on the right (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartAllP(ttyUSBBottom, ttyUSB_bottom).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }

    public void turnWhite() throws IOException {
        getTtyUSB();
        if(getUSB("/dev/"+ttyUSB_left) != null){
            ttyUSBLeft = new SerialPort(new File("/dev/"+ttyUSB_left), 9600, 0);     //Open the baud rate
            UartSend.UartAllW(ttyUSBLeft, ttyUSB_left).run();				//The light is on the left (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_left"+"/dev/"+ttyUSB_left );
        }

        if(getUSB("/dev/"+ttyUSB_right) != null){
            ttyUSBRight = new SerialPort(new File("/dev/"+ttyUSB_right), 9600, 0,1);     //Open the baud rate
            UartSend.UartAllW(ttyUSBRight, ttyUSB_right).run();				//The light is on the right (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_right"+"/dev/"+ttyUSB_right );
        }

        if(getUSB("/dev/"+ttyUSB_bottom) != null){
            ttyUSBBottom = new SerialPort(new File("/dev/"+ttyUSB_bottom), 9600, 0,1,2);     //Open the baud rate
            UartSend.UartAllW(ttyUSBBottom, ttyUSB_bottom).run();				//The light is on the bottom (blue light)
            if (DEBUG) Log.d(TAG, "run:  ttyUSB_bottom"+"/dev/"+ttyUSB_bottom );
        }
    }

    public String getUSB(String path){
        FileInputStream fin;
        try {
            fin = new FileInputStream(path);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            fin.close();
            return buffer.toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void getTtyUSB() {
        try {
            Process process = Runtime.getRuntime().exec("ls -l /sys/class/tty/");
            InputStream in1 = process.getInputStream();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(in1));
            String line1;

            while ((line1 = reader1.readLine()) != null) {

                if(isrk3399()){  //only for RK3399 platform.
                    //Log.d(TAG,line1);
                    //POGO_L
                    //10P-RS v9.0 :  2-1:1.0
                    //15N-RS v9.0 : 1-1.2:1.0
                    if (PledDataUtil.is3399Left(line1)) {
                        Log.d(TAG, "getTtyUSB: Person's Left hand side:");
                        String[] sArray = line1.split("/");
                        int count = sArray.length;
                        String left = "22";
                        for (int i = 0; i < sArray.length; i++) {
                            if (DEBUG)
                                Log.e("getTtyUSB", "getTtyUSB:left sArray[" + i + "]" + sArray[i]);
                            if (count == i + 1) {
                                if (sArray[i].contains("ttyUSB") && PledDataUtil.isCurrentLeft(line1)) {
                                    ttyUSB_left = sArray[i];
                                    PledDataUtil.addPath(sArray[i-3],sArray[i],0);
//							if(ttyUSB_left.equals("ttyXRUSB0")){
//								ttyUSB_left="ttyUSB0";
//							}
                                    if (!ttyUSB_left.equals(ttyUSB_left_old)) {
                                        x = 1;
                                        send = 1;
                                    }
                                    left = ttyUSB_left;
                                    ttyUSB_left_old = left;
                                }
                            }

                        }
                        if (DEBUG) Log.e("getTtyUSB", "getTtyUSB:ttyUSB_left " + ttyUSB_left);
                    }
                    //POGO_R
				/*	10P-RS v9.0 :  *-1.3:1.0
					15N-RS v9.0 : *-1.5:1.0
				 */
                    if ( PledDataUtil.is3399Right(line1)) {
                        String[] sArray = line1.split("/");
                        int count = sArray.length;
                        String right = "22";
                        for (int i = 0; i < sArray.length; i++) {
                            if (DEBUG)
                                Log.e("getTtyUSB", "getTtyUSB:right sArray[" + i + "]" + sArray[i]);
                            if (count == i + 1) {
                                if (sArray[i].contains("ttyUSB") && PledDataUtil.isCurrentRight(line1)) { //@S add for judgement is really PLED device. 20190905
                                    ttyUSB_right = sArray[i];
                                    PledDataUtil.addPath(sArray[i-3],sArray[i],1);
                                    Log.d(TAG, "getTtyUSB: ttyUSB_right1" + ttyUSB_right);
                                    if (!ttyUSB_right.equals(ttyUSB_right_old)) {
                                        y = 1;
                                        send = 1;
                                    }
                                    right = ttyUSB_right;
                                    ttyUSB_right_old = right;
                                    Log.d(TAG, "getTtyUSB: ttyUSB_right2" + ttyUSB_right);
                                }
                            }
                        }
                        if (DEBUG)
                            Log.e("getTtyUSB", "getTtyUSB:ttyUSB_right_old " + ttyUSB_right_old);
                    }
                    if(	PledDataUtil.is3399Bottom(line1) ){
                        String[] sArray=line1.split("/") ;
                        int count = sArray.length;
                        String bottom = "22";
                        for(int i=0; i<sArray.length;i++){
                            if(DEBUG) Log.e("getTtyUSB", "getTtyUSB:Bottom sArray["+i+"]"+sArray[i] );
                            if(count == i+1) {
                                if (sArray[i].contains("ttyUSB") && PledDataUtil.isCurrentBottom(line1)) { //@S add for judgement is really PLED device. 20190905
                                    ttyUSB_bottom = sArray[i];
                                    PledDataUtil.addPath(sArray[i-3],sArray[i],2);
                                    if (!ttyUSB_bottom.equals(ttyUSB_bottom_old)) {
                                        x_2 = 1;
                                        send = 1;
                                    }
                                    bottom = ttyUSB_bottom;
                                    ttyUSB_bottom_old = bottom;
                                }
                            }
                        }
                        if(DEBUG) Log.e("getTtyUSB", "getTtyUSB:ttyUSB_bottom_old "+ttyUSB_bottom_old );
                    }
                } else { //others platform.
                    //Log.d(TAG,line1);
                    //According to the port to send commands
                    //Person's Left hand side:
					/*	10P-RK: v4.4 & v5.1:  2-1.2:1.0,
						10P-RM v6.0: (rk3288)3-1.2:1.0, (rk3288w)4-1.2:1.0, v8.1: "1-1.2:1.0" ,
						10PD-RM V6.0: 4-1.2:1.0, 10PD-RM 8.1: "1-1.2:1.0",
						15-RK v4.4 & v5.1: 3-1.4:1.0,
						15-RM v6.0: (rk3288) 3-1.1.2:1.0 3-1.1:1.0,  (rk3288w) 	4-1.1:1.0, 	v8.1: 1-1.1:1.0

						@S add for 12N bottom pogo pin support.
						12N-RM v6.0 : 4-1.7:1.0
						12N-RM v8.1 : 1-1.7:1.0
						10VB-RN v8.1 : 1-1.4:1.0
						//7NB-RN v8.1 bottom : 1-1.2:1.0
						32P-RM v8.1&v9.0 : 3-1:1.0
						22P-RM v8.1&v9.0 : 1-1.1:1.0
					*/
                    if (PledDataUtil.isLeft(line1)
                    ) {
                        Log.d(TAG, "getTtyUSB: Person's Left hand side:");
                        String[] sArray = line1.split("/");
                        int count = sArray.length;
                        String left = "22";
                        for (int i = 0; i < sArray.length; i++) {
                            if (DEBUG)
                                Log.e("getTtyUSB", "getTtyUSB:left sArray[" + i + "]" + sArray[i]);
                            if (count == i + 1) {
                                if (sArray[i].contains("ttyUSB") && PledDataUtil.isCurrentLeft(line1)) {
                                    ttyUSB_left = sArray[i];
                                    PledDataUtil.addPath(sArray[i-3],sArray[i],0);
//							if(ttyUSB_left.equals("ttyXRUSB0")){
//								ttyUSB_left="ttyUSB0";
//							}
                                    if (!ttyUSB_left.equals(ttyUSB_left_old)) {
                                        x = 1;
                                        send = 1;
                                    }
                                    left = ttyUSB_left;
                                    ttyUSB_left_old = left;
                                }
                            }

                        }
                        if (DEBUG) Log.e("getTtyUSB", "getTtyUSB:ttyUSB_left " + ttyUSB_left);
                    }

                    //Person's Right hand Side:
					/*		10P-RK v4.4 & v5.1: 2-1.5:1.0
							10P-RM v6.0: (rk3288)3-1.5:1.0, (rk3288w)4-1.5:1.0, v8.1: "1-1.5:1.0" ,
							10PD-RM V6.0: 4-1.6:1.0; 10PD-RM V8.1: "1-1.6:1.0";
							15-RK v4.4 & v5.1: 3-1.5.1:1.0,
							15-RM v6.0: (rk3288)3-1.4.1.2:1.0 3-1.4.1:1.0; 	(rk3288w) 4-1.4.1:1.0; v8.1: 1-1.4.1:1.0
							10VB-RN v8.1 : 1-1.5:1.0
							22P-RM v8.1&v9.0 : 2-1.1:1.0
					*/
                    //According to the port to send commands
                    if (PledDataUtil.isRight(line1)
                    ) {
                    		Log.d(TAG, "getTtyUSB: Person's Right hand side:");
                        String[] sArray = line1.split("/");
                        int count = sArray.length;
                        String right = "22";
                        for (int i = 0; i < sArray.length; i++) {
                            if (DEBUG)
                                Log.e("getTtyUSB", "getTtyUSB:right sArray[" + i + "]" + sArray[i]);
                            if (count == i + 1) {
                                if (sArray[i].contains("ttyUSB") && PledDataUtil.isCurrentRight(line1)) { //@S add for judgement is really PLED device. 20190905
                                    ttyUSB_right = sArray[i];
                                    PledDataUtil.addPath(sArray[i-3],sArray[i],1);
                                    Log.d(TAG, "getTtyUSB: ttyUSB_right1" + ttyUSB_right);
                                    if (!ttyUSB_right.equals(ttyUSB_right_old)) {
                                        y = 1;
                                        send = 1;
                                    }
                                    right = ttyUSB_right;
                                    ttyUSB_right_old = right;
                                    Log.d(TAG, "getTtyUSB: ttyUSB_right2" + ttyUSB_right);
                                }
                            }
                        }
                        if (DEBUG)
                            Log.e("getTtyUSB", "getTtyUSB:ttyUSB_right_old " + ttyUSB_right_old);
                    }
                }
				/*
				* Pogo Pin Bottom side

					12N-RM v6.0 : 4-1.7:1.0
					12N-RM v8.1 : 1-1.7:1.0
					15N-RM v6.0 :
					15N-RN v8.1 : 1-1.6:1.0
				*/
                //According to the port to send commands
                if(	PledDataUtil.isBottom(line1) ){
					String[] sArray=line1.split("/") ;
					int count = sArray.length;
					String bottom = "22";
					for(int i=0; i<sArray.length;i++){
						if(DEBUG) Log.e("getTtyUSB", "getTtyUSB:Bottom sArray["+i+"]"+sArray[i] );
						if(count == i+1) {
							if (sArray[i].contains("ttyUSB") && PledDataUtil.isCurrentBottom(line1)) { //@S add for judgement is really PLED device. 20190905
								ttyUSB_bottom = sArray[i];
								PledDataUtil.addPath(sArray[i-3],sArray[i],2);
								if (!ttyUSB_bottom.equals(ttyUSB_bottom_old)) {
									x_2 = 1;
									send = 1;
								}
								bottom = ttyUSB_bottom;
								ttyUSB_bottom_old = bottom;
							}
						}
					}
					if(DEBUG) Log.e("getTtyUSB", "getTtyUSB:ttyUSB_bottom_old "+ttyUSB_bottom_old );
				}
            }
            if (PledDataUtil.judgeDeviceInfo()){
                Log.d(TAG,"current devices info："+ PledDataUtil.getCurrentDeviceInfo());
                if (PledDataUtil.getCurrentLeftPath()!=null && !PledDataUtil.getCurrentLeftPath().equals("11"))
                    ttyUSB_left = PledDataUtil.leftUSB;
                if (PledDataUtil.getCurrentRightPath()!=null && !PledDataUtil.getCurrentRightPath().equals("11"))
                    ttyUSB_right = PledDataUtil.rightUSB;
                if (PledDataUtil.getCurrentBottomPath()!=null && !PledDataUtil.getCurrentBottomPath().equals("11"))
                    ttyUSB_bottom = PledDataUtil.bottomUSB;
            }
            in1.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static boolean isrk3399() {
        String plat="";
        try{
            Class systemProperties= Class.forName("android.os.SystemProperties");
            Method get=systemProperties.getDeclaredMethod("get", String.class);
            plat=(String)get.invoke(null, "ro.board.platform");
            //Log.d("zz", "Fingerprint:"+plat);
        }catch (Exception e){

        }
        return plat.contains("3399");
    }
}
