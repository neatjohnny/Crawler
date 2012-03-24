package edu.spbstu.dcn.producer;

import java.util.concurrent.Phaser;

public class Producer implements Runnable{
	private final String file;
	private static Phaser phaser;
	
	public Producer(String file, Phaser phaser){
		phaser.register();
		Producer.phaser = phaser;
		this.file = file;
	}
	
	public void run(){
System.out.println("-----"+Thread.currentThread().getId()+": "+file);
		phaser.arrive();
	}

}
