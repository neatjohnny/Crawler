package edu.spbstu.dcn.producer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.ID3v1Tag;

import edu.spbstu.dcn.utils.Phaser;

public class Mp3Producer extends AbstractProducer{
	public Mp3Producer(String link, Phaser phaser, LinkedBlockingQueue<List<String>> results){
		super(link, phaser, results);
	}
	
	public void run(){
		File f = new File(link);
		try {
			MP3File mp3File = new MP3File(f);
			if(mp3File.hasID3v1Tag()){
				ID3v1Tag tag = mp3File.getID3v1Tag();
				List<String> result = new ArrayList<String>();
				result.add(tag.getFirstArtist());
				result.add(tag.getFirstTitle());
				result.add(tag.getFirstAlbum());
				result.add(link);
				resultEntries.add(result);
			}else if(mp3File.hasID3v2Tag()){
/*				System.out.println("ID3v2 tag format!");
				AbstractID3v2Tag tag = mp3File.getID3v2Tag();
				System.out.println("Title = "+tag.getTitle());
				System.out.println("album name = "+tag.getAlbum());
				System.out.println("track name = "+tag.getArtist());*/
			}
		} catch (Exception e) {
			System.out.println("Can't get file info!");
			e.printStackTrace();
		}
		phaser.arrive();
	}

}
