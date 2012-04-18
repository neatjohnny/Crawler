package edu.spbstu.dcn.utils;

public class Phaser {
	private int counter = 0;
	private Object lock = new Object();
	
	public void register(){
		synchronized(lock){
			counter++;
		}
	}
	
	public void arrive(){
		synchronized(lock){
			counter--;
			lock.notifyAll();
		}
	}
	
	public void arriveAndAwaitAdvance(){
		synchronized(lock){
			counter--;
			lock.notifyAll();
			try {
				while(counter>0)
					lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
