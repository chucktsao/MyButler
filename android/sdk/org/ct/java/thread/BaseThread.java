package org.ct.java.thread;

public class BaseThread extends Thread {

	private boolean cancle = false;

	public boolean isCancle() {
		return cancle;
	}

	public void cancle() {
		this.cancle = true;
	}
	

}
