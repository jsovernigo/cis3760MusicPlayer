/**
   The Pretty Good Music Player
   Copyright (C) 2014  Tyler Smith
 
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.smithdtyler.prettygoodmusicplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class MusicPlaybackService extends Service {
	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_UNREGISTER_CLIENT = 2;

	// Playback control
	static final int MSG_PLAYPAUSE = 3;
	static final int MSG_NEXT = 4;
	static final int MSG_PREVIOUS = 5;
	static final int MSG_SET_PLAYLIST = 6;
	static final int MSG_PAUSE = 7;
	static final int MSG_PAUSE_IN_ONE_SEC = 8;
	static final int MSG_CANCEL_PAUSE_IN_ONE_SEC = 9;
	
	// State management
	static final int MSG_REQUEST_STATE = 17;
	static final int MSG_SERVICE_STATUS = 18;
	static final int MSG_STOP_SERVICE = 19;

	public enum PlaybackState {
		PLAYING, PAUSED, UNKNOWN
	}

	static final String PRETTY_SONG_NAME = "PRETTY_SONG_NAME";
	static final String PRETTY_ARTIST_NAME = "PRETTY_ARTIST_NAME";
	static final String PRETTY_ALBUM_NAME = "PRETTY_ALBUM_NAME";
	static final String ALBUM_NAME = "ALBUM_NAME";
	static final String PLAYBACK_STATE = "PLAYBACK_STATE";
	static final String TRACK_DURATION = "TRACK_DURATION";
	static final String TRACK_POSITION = "TRACK_POSITION";

	private static final ComponentName cn = new ComponentName(
			MusicBroadcastReceiver.class.getPackage().getName(),
			MusicBroadcastReceiver.class.getName());

	private FileInputStream fis;
	private File songFile;
	private String[] songAbsoluteFileNames;
	private int songAbsoluteFileNamesPosition;

	private Timer timer;

	private AudioManager am;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private MediaPlayer mp;
	private static final String TAG = "MusicPlaybackService";
	private static boolean isRunning = false;

	private static int uniqueid = new String("Music Playback Service")
			.hashCode();

	private OnAudioFocusChangeListener audioFocusListener = new PrettyGoodAudioFocusChangeListener();

	private static IntentFilter filter = new IntentFilter();
	static{
		filter.addAction("android.intent.action.HEADSET_PLUG");
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
	}
	private static MusicBroadcastReceiver receiver = new MusicBroadcastReceiver();
	
	/**
	 * Keeps track of all current registered clients.
	 */
	List<Messenger> mClients = new ArrayList<Messenger>(); 

	final Messenger mMessenger = new Messenger(new IncomingHandler(this));

	public AudioManager mAudioManager;
	
	// These are used to report song progress when the song isn't started yet. 
	private int lastDuration = 0;
	private int lastPosition = 0;
	public long audioFocusLossTime = 0;
	private long pauseTime = Long.MAX_VALUE;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "ServiceHandler got a message!" + msg);
		}
	}

	@Override
	public synchronized void onCreate() {
		Log.i(TAG, "Music Playback Service Created!");
		isRunning = true;

		mp = new MediaPlayer();

		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.i(TAG, "Song complete");
				next();
			}

		});

		// https://developer.android.com/training/managing-audio/audio-focus.html
		audioFocusListener = new PrettyGoodAudioFocusChangeListener();

		// Get permission to play audio
		am = (AudioManager) getBaseContext().getSystemService(
				Context.AUDIO_SERVICE);

		HandlerThread thread = new HandlerThread("ServiceStartArguments");
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		// https://stackoverflow.com/questions/19474116/the-constructor-notification-is-deprecated
		// https://stackoverflow.com/questions/6406730/updating-an-ongoing-notification-quietly/15538209#15538209
		Intent resultIntent = new Intent(this, NowPlaying.class);
		// Use the FLAG_ACTIVITY_CLEAR_TOP to prevent launching a second
		// NowPlaying if one already exists.
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, 0);

		Builder builder = new NotificationCompat.Builder(
				this.getApplicationContext());

		String contentText = getResources().getString(R.string.ticker_text);
		if (songFile != null) {
			contentText = Utils.getPrettySongName(songFile);
		}

		Notification notification = builder
				.setContentText(contentText)
				.setSmallIcon(R.drawable.ic_pgmp_launcher)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.setContentTitle(
						getResources().getString(R.string.notification_title))
				.build();

		startForeground(uniqueid, notification);

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				onTimerTick();
			}
		}, 0, 500L);

		Log.i(TAG, "Registering event receiver");
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// Apparently audio registration is persistent across lots of things...
		// restarts, installs, etc.
		mAudioManager.registerMediaButtonEventReceiver(cn);
		// I tried to register this in the manifest, but it doesn't seen to accept it, so I'll do it this way.
		getApplicationContext().registerReceiver(receiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("MyService", "Received start id " + startId + ": " + intent);
		int command = intent.getIntExtra("Message", -1);
		if (command != -1) {
			Log.i(TAG, "I got a message! " + command);
			if (command == MSG_PLAYPAUSE) {
				Log.i(TAG, "I got a playpause message");
				playPause();
			} else if (command == MSG_PAUSE) {
				Log.i(TAG, "I got a pause message");
				pause();
			} else if (command == MSG_NEXT) {
				Log.i(TAG, "I got a next message");
				next();
			} else if (command == MSG_PREVIOUS) {
				Log.i(TAG, "I got a previous message");
				previous();
			} else if (command == MSG_STOP_SERVICE) {
				Log.i(TAG, "I got a stop message");
				timer.cancel();
				stopForeground(true);
				stopSelf();
			} else if (command == MSG_PAUSE_IN_ONE_SEC) {
				pauseTime  = System.currentTimeMillis() + 1000;
			} else if (command == MSG_CANCEL_PAUSE_IN_ONE_SEC) {
				pauseTime = Long.MAX_VALUE;
			}
			return START_STICKY;
		}

		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	// Receives messages from activities which want to control the jams
	private static class IncomingHandler extends Handler {
		MusicPlaybackService _service;

		private IncomingHandler(MusicPlaybackService service) {
			_service = service;
		}

		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "Music Playback service got a message!");
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				Log.i(TAG, "Got MSG_REGISTER_CLIENT");
				_service.mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				Log.i(TAG, "Got MSG_UNREGISTER_CLIENT");
				_service.mClients.remove(msg.replyTo);
				break;
			case MSG_PLAYPAUSE:
				// if we got a playpause message, assume that the user can hear
				// what's happening and wants to switch it.
				Log.i(TAG, "Got a playpause message!");
				// Assume that we're not changing songs
				_service.playPause();
				break;
			case MSG_NEXT:
				Log.i(TAG, "Got a next message!");
				_service.next();
				break;
			case MSG_PREVIOUS:
				Log.i(TAG, "Got a previous message!");
				_service.previous();
				break;
			case MSG_SET_PLAYLIST:
				Log.i(TAG, "Got a set playlist message!");
				_service.songAbsoluteFileNames = msg.getData().getStringArray(
						SongList.SONG_ABS_FILE_NAME_LIST);
				_service.songAbsoluteFileNamesPosition = msg.getData().getInt(
						SongList.SONG_ABS_FILE_NAME_LIST_POSITION);
				_service.songFile = new File(
						_service.songAbsoluteFileNames[_service.songAbsoluteFileNamesPosition]);
				_service.startPlayingFile();
				_service.updateNotification();
				break;
			case MSG_REQUEST_STATE:
				Log.i(TAG, "Got a state request message!");
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private void onTimerTick() {
		long currentTime = System.currentTimeMillis();
		if(pauseTime < currentTime){
			pause();
		}
		sendUpdateToClients();
	}

	private void sendUpdateToClients() {
		List<Messenger> toRemove = new ArrayList<Messenger>();
		for (Messenger client : mClients) {
			Message msg = Message.obtain(null, MSG_SERVICE_STATUS);
			Bundle b = new Bundle();
			b.putString(PRETTY_SONG_NAME, Utils.getPrettySongName(songFile));
			b.putString(PRETTY_ALBUM_NAME, songFile.getParentFile().getName());
			b.putString(PRETTY_ARTIST_NAME, songFile.getParentFile()
					.getParentFile().getName());
			if (mp.isPlaying()) {
				b.putInt(PLAYBACK_STATE, PlaybackState.PLAYING.ordinal());
			} else {
				b.putInt(PLAYBACK_STATE, PlaybackState.PAUSED.ordinal());
			}
			// We might not be able to send the position right away if mp is
			// still being created
			// so instead let's send the last position we knew about.
			if (mp.isPlaying()) {
				lastDuration = mp.getDuration();
				lastPosition = mp.getCurrentPosition();
			}
			b.putInt(TRACK_DURATION, lastDuration);
			b.putInt(TRACK_POSITION, lastPosition);
			msg.setData(b);
			try {
				client.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
				toRemove.add(client);
			}
		}

		for (Messenger remove : toRemove) {
			mClients.remove(remove);
		}
	}

	public static boolean isRunning() {
		return isRunning;
	}

	@Override
	public synchronized void onDestroy() {
		super.onDestroy();
		am.abandonAudioFocus(MusicPlaybackService.this.audioFocusListener);
		mAudioManager.unregisterMediaButtonEventReceiver(cn);
		getApplicationContext().unregisterReceiver(receiver);
		mp.stop();
		mp.reset();
		mp.release();
		Log.i("MyService", "Service Stopped.");
		isRunning = false;
	}

	private synchronized void previous() {
		// if we're playing, and we're more than 3 seconds into the file, then just 
		// start the song over
		if(mp.isPlaying()){
			int progressMillis = mp.getCurrentPosition();
			if(progressMillis > 3000){
				mp.seekTo(0);
				return;
			}
		}
		
		mp.stop();
		mp.reset();
		try {
			fis.close();
		} catch (IOException e) {
			Log.w(TAG, "Failed to close the file");
			e.printStackTrace();
		}
		songAbsoluteFileNamesPosition = songAbsoluteFileNamesPosition - 1;
		if (songAbsoluteFileNamesPosition < 0) {
			songAbsoluteFileNamesPosition = songAbsoluteFileNames.length - 1;
		}
		String next = songAbsoluteFileNames[songAbsoluteFileNamesPosition];
		try {
			songFile = new File(next);
			fis = new FileInputStream(songFile);
			mp.setDataSource(fis.getFD());
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			Log.w(TAG, "Failed to open " + next);
			e.printStackTrace();
			// Just go to the next song back
			previous();
		}
	}

	private synchronized void startPlayingFile() {
		// Have we loaded a file yet?
		if (mp.getDuration() > 0) {
			pause();
			mp.stop();
			mp.reset();
		}

		// open the file, pass it into the mp
		try {
			fis = new FileInputStream(songFile);
			mp.setDataSource(fis.getFD());
			mp.prepare();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void playPause() {
		if (mp.isPlaying()) {
			pause();
		} else {
			pauseTime = Long.MAX_VALUE;
			play();
		}
	}

	private synchronized void play() {
		if (mp.isPlaying()) {
			// do nothing
		} else {
			// Request audio focus for playback
			int result = am.requestAudioFocus(
					MusicPlaybackService.this.audioFocusListener,
					// Use the music stream.
					AudioManager.STREAM_MUSIC,
					// Request permanent focus.
					AudioManager.AUDIOFOCUS_GAIN);
			Log.d(TAG, "requestAudioFocus result = " + result);
			Log.i(TAG, "About to play " + songFile);

			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				Log.d(TAG, "We got audio focus!");
				mp.start();
				updateNotification();
			} else {
				Log.e(TAG, "Unable to get audio focus");
			}
		}
	}

	private synchronized void pause() {
		if (mp.isPlaying()) {
			mp.pause();
		} else {
			// do nothing
		}
		updateNotification();
	}

	private synchronized void next() {
		mp.stop();
		mp.reset();
		try {
			fis.close();
		} catch (IOException e) {
			Log.w(TAG, "Failed to close the file");
			e.printStackTrace();
		}
		songAbsoluteFileNamesPosition = (songAbsoluteFileNamesPosition + 1)
				% songAbsoluteFileNames.length;
		String next = songAbsoluteFileNames[songAbsoluteFileNamesPosition];
		try {
			songFile = new File(next);
			fis = new FileInputStream(songFile);
			mp.setDataSource(fis.getFD());
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			Log.w(TAG, "Failed to open " + next);
			e.printStackTrace();
			// I think our best chance is to go to the next song
			next();
		}
		updateNotification();
	}

	private void updateNotification() {
		// https://stackoverflow.com/questions/5528288/how-do-i-update-the-notification-text-for-a-foreground-service-in-android
		Intent resultIntent = new Intent(this, NowPlaying.class);
		// Use the FLAG_ACTIVITY_CLEAR_TOP to prevent launching a second
		// NowPlaying if one already exists.
		resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, 0);

		Builder builder = new NotificationCompat.Builder(
				this.getApplicationContext());
		int icon = R.drawable.ic_pgmp_launcher;
		String contentText = getResources().getString(R.string.ticker_text);
		if (songFile != null) {
			SharedPreferences prefs = getSharedPreferences("PrettyGoodMusicPlayer", MODE_PRIVATE);
	        prefs.edit();
	        File bestGuessMusicDir = Utils.getBestGuessMusicDirectory();
	        String musicRoot = prefs.getString("ARTIST_DIRECTORY", bestGuessMusicDir.getAbsolutePath());
			contentText = Utils.getArtistName(songFile, musicRoot)
					+ ": " + Utils.getPrettySongName(songFile);
			if(mp != null){
				if(mp.isPlaying()){
					icon = R.drawable.ic_pgmp_launcher;
				}
			}
		}

		Notification notification = builder
				.setContentText(contentText)
				.setSmallIcon(icon)
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.setContentTitle(
						getResources().getString(R.string.notification_title))
				.build();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(uniqueid, notification);
	}

	private class PrettyGoodAudioFocusChangeListener implements
			AudioManager.OnAudioFocusChangeListener {
		
		private PlaybackState stateOnFocusLoss = PlaybackState.UNKNOWN;

		public void onAudioFocusChange(int focusChange) {
			Log.w(TAG, "Focus change received " + focusChange);
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
				if(mp.isPlaying()){
					stateOnFocusLoss = PlaybackState.PLAYING;
				} else {
					stateOnFocusLoss = PlaybackState.PAUSED;
				}
				pause();
				MusicPlaybackService.this.audioFocusLossTime  = System.currentTimeMillis();
				// Pause playback
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				Log.i(TAG, "AUDIOFOCUS_GAIN");
				// If it's been less than 20 seconds, resume playback
				long curr = System.currentTimeMillis();
				if(((curr - MusicPlaybackService.this.audioFocusLossTime) < 30000) && stateOnFocusLoss == PlaybackState.PLAYING){
					play();
				} else {
					Log.i(TAG, "It's been more than 30 seconds or we were paused, don't auto-play");
				}
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				Log.i(TAG, "AUDIOFOCUS_LOSS");
				if(mp.isPlaying()){
					stateOnFocusLoss = PlaybackState.PLAYING;
				} else {
					stateOnFocusLoss = PlaybackState.PAUSED;
				}
				pause();
				MusicPlaybackService.this.audioFocusLossTime  = System.currentTimeMillis();
				// Stop playback
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
				Log.i(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
				MusicPlaybackService.this.audioFocusLossTime  = System.currentTimeMillis();
				if(mp.isPlaying()){
					stateOnFocusLoss = PlaybackState.PLAYING;
				} else {
					stateOnFocusLoss = PlaybackState.PAUSED;
				}
				pause();
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
				Log.i(TAG, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
				long curr = System.currentTimeMillis();
				if(((curr - MusicPlaybackService.this.audioFocusLossTime) < 30000) && stateOnFocusLoss == PlaybackState.PLAYING){
					play();
				} else {
					Log.i(TAG, "It's been more than 30 seconds or we were paused, don't auto-play");
				}
			}
		}
	}

}
