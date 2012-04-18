package edu.spbstu.dcn.producer;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import edu.spbstu.dcn.utils.Phaser;

public abstract class AbstractProducer implements Runnable{
	protected final String link;
	protected static Phaser phaser;
	protected static LinkedBlockingQueue<List<String>> resultEntries;	
	
	public AbstractProducer(String link, Phaser phaser, LinkedBlockingQueue<List<String>> results){
		phaser.register();
		AbstractProducer.phaser = phaser;
		this.link = link;
		AbstractProducer.resultEntries = results;
	}

}
