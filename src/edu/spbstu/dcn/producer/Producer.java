package edu.spbstu.dcn.producer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Phaser;

import javax.print.attribute.standard.OutputDeviceAssigned;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileReader;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;

public class Producer implements Runnable{
	private final String file;
	private static Phaser phaser;
	
	public Producer(String file, Phaser phaser){
		phaser.register();
		Producer.phaser = phaser;
		this.file = file;
	}
	
	public void run(){
//System.out.println("-----"+Thread.currentThread().getId()+": "+file);
		File f = new File(file);
		FileWriter newFile;
		try {
			MP3File mp3File = new MP3File(f);
			if(mp3File.hasID3v1Tag()){
				ID3v1Tag tag = mp3File.getID3v1Tag();
				try {
					if(tag.getFirstArtist()!="" && !tag.getFirstArtist().contains("?")){
						File subfolder = new File("C:\\ALEX\\University\\courses2\\Java\\workspace\\Crawler\\_out\\"+tag.getFirstArtist());
						if(!subfolder.exists())
							subfolder.mkdir();
						newFile = new FileWriter(subfolder.getAbsolutePath() + "\\" + f.getName().replace(".mp3", ".txt"));
					}else
						newFile = new FileWriter("C:\\ALEX\\University\\courses2\\Java\\workspace\\Crawler\\_out\\" + f.getName().replace(".mp3", ".txt"));
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				newFile.write("Artist: " + tag.getFirstArtist() + "\n");
				newFile.write("Song: " + tag.getFirstTitle() + "\n");
				newFile.write("Album: " + tag.getFirstAlbum() + "\n");
				newFile.write("\npath: " + file + "\n");
				newFile.close();
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
