package edu.spbstu.dcn.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import edu.spbstu.dcn.producer.ProducerFactory;
import edu.spbstu.dcn.utils.Phaser;

public class Watcher implements Runnable{
	private volatile Boolean stop = false;
	private LinkedBlockingQueue<String> links;
	private ExecutorService executor;
	private Phaser phaser;
	private String file;

	public Watcher(ExecutorService executor, LinkedBlockingQueue<String> links, Phaser phaser, String outputFile){
		phaser.register();
		this.executor = executor;
		this.links = links;
		this.phaser = phaser;
		this.file = outputFile;
	}
	
	public void run(){
		System.out.println("Watcher started.");
		Phaser producerPhaser = new Phaser();
		producerPhaser.register();
		FileWriter writer;
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			System.out.println("  Can't open output file \"" + file + "\"!");
			e.printStackTrace();
			return;
		}
		String next;
		LinkedBlockingQueue<List<String>> results = new LinkedBlockingQueue<List<String>>();
		while(!stop || links.size()>0){
			try {
				next = links.poll(100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				System.out.println("  Watcher interrupted!");
				e.printStackTrace();
				return;
			}
			if(next!=null)
				executor.submit(ProducerFactory.createProducer(next, producerPhaser, results, ProducerFactory.MP3));
		}
		System.out.println("Watcher waiting...");
		producerPhaser.arriveAndAwaitAdvance();
		System.out.println("  Watcher writes output.");
		//TODO Sort the 'results' array.
		//TODO Sorting and output depends on the task. Make an interface for Watcher or
		//		create separate class&interface for sorting and writing.
		try {
			writer.write("Total songs: " + results.size() + "\n\n");
			for(List<String> entry : results){
				writer.write("Artist: " + entry.get(0) + "\n");
				writer.write("Song: " + entry.get(1) + "\n");
				writer.write("Album: " + entry.get(2) + "\n");
				writer.write("filepath: " + entry.get(3) + "\n\n");
			}
			writer.flush();
		} catch (IOException e) {
			System.out.println("  Error while writing in file!");
			e.printStackTrace();
		}
		System.out.println("Watcher stopped.");
		phaser.arrive();
	}
	
	public void stop(){
		stop = true;
	}
}
