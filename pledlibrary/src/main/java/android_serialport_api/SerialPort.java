
package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd ,mFd1 ,mFd2,mFd3 ,mFd4,mFd5;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public static FileDescriptor f1;
	public static FileDescriptor f2;
	public static FileDescriptor f3;


	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					//throw new SecurityException();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				//throw new SecurityException();
			}
		}

		
		LedUtils.x++;
		if(LedUtils.x == 1 ||  LedUtils.x == 2 || LedUtils.x == 3 || LedUtils.x == 4 || LedUtils.x == 5 ){
			
			mFd1 = open(device.getAbsolutePath(), baudrate, flags);
			mFd = mFd1;
			f1 = mFd1;
		}
		
		/*if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);*/
		
		if (f1 == null) {
			Log.e(TAG, "native open returns null");
			//throw new IOException();
		}else{
			mFileInputStream = new FileInputStream(f1);
			mFileOutputStream = new FileOutputStream(f1);
		}
		
		
		//mFileInputStream.close();
		//mFileOutputStream.close();
	}
	
	////
	public SerialPort(File device, int baudrate, int flags, int i) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				//666
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					//throw new SecurityException();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				//throw new SecurityException();
			}
		}


		LedUtils.y++;
		if(LedUtils.y == 1 ||  LedUtils.y == 2 || LedUtils.y == 3 || LedUtils.y == 4 || LedUtils.y == 5 ){
			
			
			mFd2 = open(device.getAbsolutePath(), baudrate, flags);
			mFd3 = mFd2;	
			f2 = mFd2;
		}
		
		/*if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);*/
		
		if (f2 == null) {
			Log.e(TAG, "native open returns null  2");
			//throw new IOException();
		}else{
			mFileInputStream = new FileInputStream(f2);
			mFileOutputStream = new FileOutputStream(f2);
		}
		
	}
	
	
	public SerialPort(File device, int baudrate, int flags, int a, int b) throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 660 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					//throw new SecurityException();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				//throw new SecurityException();
			}
		}


		LedUtils.x_2++;
		if(LedUtils.x_2 == 1 ||  LedUtils.x_2 == 2 || LedUtils.x_2 == 3 || LedUtils.x_2 == 4 || LedUtils.x_2 == 5 ){
			
			
			mFd4 = open(device.getAbsolutePath(), baudrate, flags);
			mFd5 = mFd4;	
			f3 = mFd4;
		}
		
		/*if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);*/
		
		if (f3 == null) {
			Log.e(TAG, "native open returns null");
			//throw new IOException();
		}else{
			mFileInputStream = new FileInputStream(f3);
			mFileOutputStream = new FileOutputStream(f3);
		}
		
	}

	// Getters and setters
	public FileInputStream getFileInputStream() {
		 
		return mFileInputStream;
	}

	public FileOutputStream getFileOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();
	static {
		//System.out.println("dfdgdfadsfas");
		System.loadLibrary("serial_port");
	}
	
}
