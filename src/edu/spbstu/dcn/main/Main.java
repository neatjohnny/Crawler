package edu.spbstu.dcn.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.spbstu.dcn.consumer.AbstractConsumer;
import edu.spbstu.dcn.consumer.ConsumerFactory;
import edu.spbstu.dcn.utils.Phaser;

public class Main {
	private static final int FOLDER_PARSER_POOL_SIZE = 10;
	private static final int FILE_PARSER_POOL_SIZE = 10;

	public static void main(String[] args) {
		if(args.length!=2){
			System.out.println("Two arguments needed!");
			return;
		}
		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
		
		AbstractConsumer consumer; 
		ExecutorService consumerExecutor = Executors.newFixedThreadPool(FOLDER_PARSER_POOL_SIZE);
		ExecutorService producerExecutor = Executors.newFixedThreadPool(FILE_PARSER_POOL_SIZE);
		LinkedBlockingQueue<String> links = new LinkedBlockingQueue<String>();
		Phaser consumerPhaser = new Phaser();
		Phaser watcherPhaser = new Phaser();
		consumerPhaser.register();
		watcherPhaser.register();
		Watcher watcher = new Watcher(producerExecutor, links, watcherPhaser, args[1]);
		
		System.out.println("Program started.");
		
		(new Thread(watcher, "crawler_watcher")).start();
		consumer = ConsumerFactory.createConsumer(args[0], consumerExecutor, links, consumerPhaser, ConsumerFactory.MP3);

		long elapsedTime = System.currentTimeMillis();
		consumerExecutor.submit(consumer);
		consumerPhaser.arriveAndAwaitAdvance();
		consumerExecutor.shutdown();
		watcher.stop();
		watcherPhaser.arriveAndAwaitAdvance();
		producerExecutor.shutdown();
		elapsedTime = System.currentTimeMillis() - elapsedTime;
		System.out.println("Program stopped.");
		System.out.println("\n  folder parser threadpool size: " + FOLDER_PARSER_POOL_SIZE);
		System.out.println("  file parser threadpool size: " + FILE_PARSER_POOL_SIZE);
		System.out.println("  elapsed time: " + elapsedTime);
	}

}
