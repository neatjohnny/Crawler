package edu.spbstu.dcn.consumer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import edu.spbstu.dcn.utils.Phaser;

public class Mp3Consumer extends AbstractConsumer{
	private File home;

	public Mp3Consumer(String folder, ExecutorService executor, LinkedBlockingQueue<String> files, Phaser phaser) throws FileNotFoundException{
		super(folder, executor, files, phaser);
		home = new File(folder);
		if(!home.exists())
			throw new FileNotFoundException();
		if(!home.isDirectory())
			home = new File(home.getParent());
	}
	
	public void run(){
		ArrayList<String> subfolders = new ArrayList<String>();
		for(File file : home.listFiles()){
			if(file.isDirectory())
				subfolders.add(file.getAbsolutePath());
			else
				if(file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3") || file.getName().endsWith(".Mp3") || file.getName().endsWith(".mP3"))
					try {
						files.put(file.getAbsolutePath());
					} catch (InterruptedException e) {
						e.printStackTrace();
						//TODO: graceful shutdown on timeout.
					}
		}
		for(String file : subfolders){
			try {
				executor.submit(new Mp3Consumer(file, executor, files, phaser));
			} catch (FileNotFoundException e) {
				System.out.println("FORBIDDEN STATE. Subfolder not found.");
				e.printStackTrace();
			}
		}
		phaser.arrive();
	}
	
}

