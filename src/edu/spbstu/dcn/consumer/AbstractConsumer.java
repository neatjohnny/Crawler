package edu.spbstu.dcn.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import edu.spbstu.dcn.utils.Phaser;

public abstract class AbstractConsumer implements Runnable{
	protected static ExecutorService executor;
	protected static LinkedBlockingQueue<String> files;
	protected static Phaser phaser;

	public AbstractConsumer(String folder, ExecutorService executor, LinkedBlockingQueue<String> files, Phaser phaser){
		phaser.register();
		AbstractConsumer.executor = executor;
		AbstractConsumer.files = files;
		AbstractConsumer.phaser = phaser;
	}
}
