package edu.spbstu.dcn.producer;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import edu.spbstu.dcn.utils.Phaser;

public class ProducerFactory {
	public static final int MP3 = 0;
	public static final int WEBPAGE = 1;
	
	public static AbstractProducer createProducer(String link, Phaser phaser, LinkedBlockingQueue<List<String>> results, int type){
		switch(type){
		case MP3:{
			return new Mp3Producer(link, phaser, results);
		}
		case WEBPAGE:{
			return null;
		}
		}
		return null;
	}
}
