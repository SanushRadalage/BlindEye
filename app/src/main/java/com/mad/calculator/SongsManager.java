package com.mad.calculator;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class SongsManager {
	final String MEDIA_PATH = new String(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Calcu/");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	TextToSpeech mTTS;
	
	// Constructor
	public SongsManager(){
		
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){
		File home = new File(MEDIA_PATH);

		try {
			if (home.listFiles(new FileExtensionFilter()).length > 0) {
				for (File file : home.listFiles(new FileExtensionFilter())) {
					HashMap<String, String> song = new HashMap<String, String>();
					song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
					song.put("songPath", file.getPath());


					//input
					// Adding each song to SongList
					songsList.add(song);
				}
			}

		}catch (Exception i){
			//Toast.makeText(Context, "", Toast.LENGTH_SHORT).show();
		}

		// return songs list array
		return songsList;
	}

	public boolean deleteItem(String filename){
		boolean state = true;
		File home = new File(MEDIA_PATH);
		File file = new File(home, filename);
		state = file.delete();
		return state;
	}

	public int getLastNumber(){
		File home = new File(MEDIA_PATH);
		int max = 0;

		try {
			if (home.listFiles(new FileExtensionFilter()).length > 0) {
				for (File file : home.listFiles(new FileExtensionFilter())) {
					if ( max < Integer.valueOf(file.getName().substring(0, (file.getName().length() - 17))) ){
						max = Integer.valueOf(file.getName().substring(0, (file.getName().length() - 17)));
					}
				}
			}

		}catch (Exception i){
		}

		// return songs list array
		return max;
	}


	
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith("_audio_record.3gp") || name.endsWith(".mp3"));
		}
	}
}
