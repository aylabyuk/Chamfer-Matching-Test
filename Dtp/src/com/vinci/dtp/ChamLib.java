package com.vinci.dtp;

public class ChamLib {
	public native static float getScore(long addrDrawing, long addrImage);
	
	static {
		System.loadLibrary("com_vinci_dtp_ChamLib");
	}
}

