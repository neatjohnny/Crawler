package edu.spbstu.dcn.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import edu.spbstu.dcn.producer.Producer;

public class Watcher implements Runnable{
	private Boolean stop = false;
	private LinkedBlockingQueue<String> links;
	private ExecutorService executor;
	private Phaser phaser;

	public Watcher(ExecutorService executor, LinkedBlockingQueue<String> links, Phaser phaser){
		phaser.register();
		this.executor = executor;
		this.links = links;
		this.phaser = phaser;
	}
	
	public void run(){
System.out.println("Watcher started.");
		String next;
		while(!stop || links.size()>0){
			try {
				next = links.poll(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				System.out.println("Watcher interrupted!");
				e.printStackTrace();
				return;
			}
			if(next!=null)
				executor.submit(new Producer(next, phaser));
		}
System.out.println("Watcher waiting...");
		phaser.arriveAndAwaitAdvance();
System.out.println("Watcher stopped.");
	}
	
	public void stop(){
		stop = true;
	}
}
