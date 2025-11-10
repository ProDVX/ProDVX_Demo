package android_serialport_api;

import java.io.FileOutputStream;

public class UartSend {
	public static FileOutputStream out = null;

	/**
	 *
	 * @param sp
	 * @param name
	 * @param r red value
	 * @param g green value
	 * @param b blue value
	 * @return
	 */
	public static Thread UartLedsController(final SerialPort sp, final String name, final int r, final int g, final int b) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllLedsOnController(r,g,b);
							out.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public static Thread UartAllOn(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {				
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllOnLamps();
							out.close();						
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	
	
	public static Thread UartAllOff(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {				
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllOffLamps();
							out.close();						
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public static Thread UartAllR(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {				
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllRLamps();
							out.close();						
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public static Thread UartAllG(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {				
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllGLamps();
							out.close();						
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public static Thread UartAllB(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {				
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllBLamps();
							out.close();						
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}


	public static Thread UartAllY(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllYLamps();
							out.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static Thread UartAllC(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllCLamps();
							out.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static Thread UartAllP(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllPLamps();
							out.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static Thread UartAllW(final SerialPort sp, final String name) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				if (LedUtils.lock == null) {
					System.out.println("lock=null");
				} else {
					try {
						synchronized (LedUtils.lock) {
							out = sp.getFileOutputStream();
							LampsUtil.AllWLamps();
							out.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}


}
