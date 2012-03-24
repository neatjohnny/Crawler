package edu.spbstu.dcn.main;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Phaser;

import edu.spbstu.dcn.consumer.Consumer;

public class Main {
	private static final int POOL_SIZE = 1;

	public static void main(String[] args) {
		Consumer consumer;
		ExecutorService consumerExecutor = Executors.newFixedThreadPool(POOL_SIZE);
		ExecutorService producerExecutor = Executors.newFixedThreadPool(POOL_SIZE);
		LinkedBlockingQueue<String> links = new LinkedBlockingQueue<String>();
		Phaser consumerPhaser = new Phaser();
		Phaser producerPhaser = new Phaser();
		consumerPhaser.register();
		producerPhaser.register();
		Watcher watcher = new Watcher(producerExecutor, links, producerPhaser);
		
		System.out.println("Program started.");
		
		(new Thread(watcher, "crawler_watcher")).start();
		try{
			 consumer = new Consumer(args[0], consumerExecutor, links, consumerPhaser);
		}catch(FileNotFoundException e){
			System.out.println("Directory \""+args[0]+"\" not found!");
			return;
		}

		consumerExecutor.submit(consumer);

		consumerPhaser.arriveAndAwaitAdvance();
		consumerExecutor.shutdown();
		watcher.stop();
		producerPhaser.arriveAndAwaitAdvance();
		producerExecutor.shutdown();

		System.out.println("Program stopped.");
	}

}
