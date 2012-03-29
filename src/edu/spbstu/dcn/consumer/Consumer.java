package edu.spbstu.dcn.consumer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;

public class Consumer implements Runnable{
	private File home;
	private static ExecutorService executor;
	private static LinkedBlockingQueue<String> files;
	private static Phaser phaser;

	public Consumer(String folder, ExecutorService executor, LinkedBlockingQueue<String> files, Phaser phaser) throws FileNotFoundException{
		phaser.register();
		Consumer.executor = executor;
		Consumer.files = files;
		Consumer.phaser = phaser;
		home = new File(folder);
		if(!home.exists())
			throw new FileNotFoundException();
		if(!home.isDirectory())
			home = new File(home.getParent());
	}
	
	public void run(){
		ArrayList<String> subfolders = new ArrayList<String>();
//System.out.println("   consumer_"+Thread.currentThread().getId()+" started");

		for(File file : home.listFiles()){
			if(file.isDirectory())
				subfolders.add(file.getAbsolutePath());
			else
				if(file.getName().endsWith(".mp3"))
					try {
						files.put(file.getAbsolutePath());
					} catch (InterruptedException e) {
						e.printStackTrace();
						//TODO: graceful shutdown on timeout.
					}
		}
		for(String file : subfolders){
			try {
				executor.submit(new Consumer(file, executor, files, phaser));
			} catch (FileNotFoundException e) {
				System.out.println("FORBIDDEN STATE. Subfolder not found.");
				e.printStackTrace();
			}
		}
//System.out.println("   consumer_"+Thread.currentThread().getId()+" stopped");
		phaser.arrive();
	}
	
}

