package com.mad.calculator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.Inflater;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class TeachersMode extends ListActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener{
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	ArrayList<String> answers = new ArrayList<String>();
    String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mp;
	final int REQUEST_PERMISSION_CODE = 1000;
	int g = 0; // variable for answer list increment
	int last_number;

	ImageView record, stopRec, save;
	TextView n;
	Button deleteButton;
	Button pauseButton;
	TextView time_count;
	int time = 0;

	private int currentSongIndex = 0;
	private boolean isRepeat = false;
	private boolean recordButtonState = false;
	private boolean lastState = false;
	private boolean deleteState = false;
	private boolean lastDeleteState = false;
	private boolean playState = false;
	private boolean lastPlayState = false;
	private boolean pauseState = false;
	String deleteItem;

	Timer timer = new Timer();
	private GestureDetector gestureDetector;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teachersmode);

		record = findViewById(R.id.rec);
		stopRec = findViewById(R.id.stp);
		save = findViewById(R.id.sv);
		n = findViewById(R.id.ans);
		deleteButton = findViewById(R.id.delete_button);
		deleteButton.setVisibility(View.GONE);
		time_count = findViewById(R.id.textView4);
		pauseButton = findViewById(R.id.pauseButton);

		pauseButton.setVisibility(View.GONE);

		n.setText("");
		time_count.setText("Press record button to record questions");


		displayList();

		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlert();
			}
		});

		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mp.isPlaying()){
					pauseState = true;
					mp.pause();
					pauseButton.setText("Play");
					pauseButton.setBackgroundColor(Color.parseColor("#232323"));
					pauseButton.setTextColor(Color.parseColor("#FFFFFF"));
					pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.playbutton, 0,0, 0 );
					n.setTextColor(Color.parseColor("#FFDA1A"));
					time_count.setText("Paused");

				}
				else {
					n.setTextColor(Color.parseColor("#25DC00"));
					pauseButton.setBackgroundColor(Color.parseColor("#5092e4"));
					pauseButton.setTextColor(Color.parseColor("#616161"));
					pauseButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pausebutton, 0,0, 0 );
					pauseButton.setText("Pause");
					time_count.setText("Playing....");
					mp.start();
					pauseState = false;
				}
			}
		});

		record.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!lastState && !mp.isPlaying()){
					recordButtonState = true;
					record.setImageDrawable(getResources().getDrawable(R.drawable.soundbars));
				}
				else {
					recordButtonState = false;
					record.setImageDrawable(getResources().getDrawable(R.drawable.soundbarsblack));
				}

				lastState = recordButtonState;

				if (recordButtonState) {
					SongsManager songsManager = new SongsManager();
					last_number = songsManager.getLastNumber();
					if (last_number == 0 ){
						last_number = 1001;
					}
					else {
						last_number++;
					}
					time_count.setText("seconds");
					startTimer();
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(400);
					if (checkPermissionFromDevice()) {

						pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Calcu/" + last_number + "_audio_record.3gp";

						setupMediaRecorder();
						try {
							mediaRecorder.prepare();
							mediaRecorder.start();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						requestPermission();
					}

				}
				else {
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(200);
					mediaRecorder.stop();
					time = 0;
					timer.cancel();
					displayList();
					Toast.makeText(TeachersMode.this, "Record Successfully", Toast.LENGTH_SHORT).show();
					n.setText("");
					time_count.setText("");
				}

			}
		});

		stopRec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TeachersMode.this, MainActivity.class));
				finish();
			}
		});



		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				savePDF();

			}
		});

	}



	private void startTimer(){
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!pauseState){
					time++;
					String a = String.valueOf(time);
					n.setText(a);
				}

				//TimerMethod();
			}
		}, 30, 1000);

	}

	private void showAlert(){
		AlertDialog alertDialog = new AlertDialog.Builder(
				TeachersMode.this).create();

		// Setting Dialog Title
		alertDialog.setTitle("DELETE");

		// Setting Dialog Message
		alertDialog.setMessage("Are you sure you want to delete "+ deleteItem);

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.delete);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SongsManager songsManager = new SongsManager();
				songsManager.deleteItem(deleteItem);
				deleteButton.setVisibility(View.GONE);
				lastDeleteState = false;
				displayList();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}


	private void displayList(){
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
		SongsManager plm = new SongsManager();
		this.songsList = plm.getPlayList();

		// looping through activity_teachersmode
		for (int i = 0; i < songsList.size(); i++)
		{
			// creating new HashMap
			HashMap<String, String> song = songsList.get(i);

			// adding HashList to ArrayList
			songsListData.add(song);
		}

		// Adding menuItems to ListView
		final ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
				R.id.songTitle });

		setListAdapter(adapter);

		// selecting single ListView item
		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!lastDeleteState) {
					if (!lastPlayState && !mp.isPlaying()) {
						playState = true;
						mp.isPlaying();
						setPlay(position);
					} else {
						playState = false;
					}
					lastPlayState = playState;
				}
			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (!lastDeleteState){
					deleteState = true;

					TextView textView = findViewById(R.id.songTitle);
					deleteItem = textView.getText().toString()+".3gp";

					LinearLayout layout = view.findViewById(R.id.item_back);
					layout.setBackgroundColor(Color.parseColor("#232323"));
					deleteButton.setVisibility(View.VISIBLE);
				}else {
					deleteState = false;
					LinearLayout layout = view.findViewById(R.id.item_back);
					layout.setBackgroundColor(Color.parseColor("#5092e4"));
					deleteButton.setVisibility(View.GONE);
				}
				lastDeleteState = deleteState;

				return false;
			}
		});

	}

	private void setPlay(int position){
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
		mp.reset();
		try {
			mp.setDataSource(songsList.get(position).get("songPath"));
			mp.prepare();
			mp.start();
			n.setTextColor(Color.parseColor("#25DC00"));
			startTimer();
			pauseButton.setVisibility(View.VISIBLE);
			time_count.setText("Playing....");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void setupMediaRecorder()
	{
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		mediaRecorder.setOutputFile(pathSave);

	}

	private void savePDF()
	{
		com.itextpdf.text.Document mDoc = new com.itextpdf.text.Document();
		//String mFileName = new SimpleDateFormat("YYYY-MM-DD-HH-MM-SS", Locale.getDefault()).format(System.currentTimeMillis());
		String mFilePath = Environment.getExternalStorageDirectory() + "/" + "Teacher's_Answers"  + ".pdf";
		try
		{
			PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
			mDoc.open();
			for(int d = 0; d < g; d++)
			{
				String mtext = answers.get(d);
				mDoc.add(new Paragraph(mtext));
			}
			mDoc.close();
		}
		catch (Exception e)
		{
		}

	}

	private void requestPermission()
	{
		ActivityCompat.requestPermissions(this, new String[]{
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.RECORD_AUDIO
		}, REQUEST_PERMISSION_CODE);

	}

	private boolean checkPermissionFromDevice()
	{
		int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

		return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		switch (requestCode)
		{
			case REQUEST_PERMISSION_CODE:
			{
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
					//Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
					savePDF();

				else
					Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mp.release();
	}

	@Override
	protected void onPause() {
		if(mp.isPlaying()){
			mp.pause();
		}
		super.onPause();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.stop();
		mp.reset();
		timer.cancel();
		time = 0;
		n.setText("");
		pauseState = false;
		lastPlayState = false;
		pauseButton.setVisibility(View.GONE);
		n.setTextColor(Color.parseColor("#FF6161"));
		time_count.setText("");
	}

}

