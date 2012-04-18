package edu.spbstu.dcn.consumer;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import edu.spbstu.dcn.utils.Phaser;

public class ConsumerFactory {
	public static final int MP3 = 0;
	public static final int WEBPAGE = 1;
	
	public static AbstractConsumer createConsumer(String folder, ExecutorService executor, LinkedBlockingQueue<String> links, Phaser phaser, int type){
		switch(type){
		case MP3:{
			try {
				return new Mp3Consumer(folder, executor, links, phaser);
			} catch (FileNotFoundException e) {
				System.out.println("Can't create Mp3Consumer!");
				e.printStackTrace();
			}break;
		}
		case WEBPAGE:{
			return null;
		}
		}
		return null;
	}
}
