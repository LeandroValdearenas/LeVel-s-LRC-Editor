package com.levels.lrceditor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 *
 * @author Leandro Valdearenas
 */
public class Mp3Player extends PlaybackListener {

    // Thread synchronization field
    private final Object threadSync = new Object();

    // Song states
    private boolean isPaused;
    private boolean isPlaying;
    private boolean hasFinished;
    private int currentFrame;
    private int currentMillis;

    // Song
    private Song song = new Song();

    // Advanced Player
    private AdvancedPlayer advancedPlayer;

    // Back-reference to LRCEditor
    private LRCEditor lrcEditor;

    public Mp3Player(LRCEditor lrcEditor) {
        this.lrcEditor = lrcEditor;
    }

    public boolean wasPlaying() {
        return isPlaying;
    }
    
    public Song getSong() {
        return song;
    }
    
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public void setCurrentMillis(int currentMillis) {
        this.currentMillis = currentMillis;
    }
    
    // Add Song Length, only if song length is null.
    public void addSongLength(String length) {
        if (this.song.getLength() == null) {
            this.song.setLength(length);
        }
    }

    public void loadSong(Song song) {
        this.song = song;

        // Stop song first
        if (!hasFinished) {
            stopSong();
        }

        // Play song
        if (this.song != null) {
            currentFrame = 0;
            currentMillis = 0;
            lrcEditor.setSldSongMaximum(song.getFrameCount());
            lrcEditor.setSldSongValue(0);
            lrcEditor.enableMp3PlayerButtons();
        }
    }

    // Set is playing to false and pause the song
    public void hitPauseSong() {
        isPlaying = false;
        pauseSong();
    }
    
    // If advanced player is not null, set as paused and stop the song
    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playSong() {
        if (song.getAbsolutePath() != null) {
            try {
                // Read data
                FileInputStream fis = new FileInputStream(song.getAbsolutePath());
                BufferedInputStream bis = new BufferedInputStream(fis);
                
                // Instance advanced player
                advancedPlayer = new AdvancedPlayer(bis);
                advancedPlayer.setPlayBackListener(this);
                startMusicThread();
                startSongSliderThread();
                
                // Set is playing as true
                isPlaying = true;

            } catch (FileNotFoundException | JavaLayerException e) {
                System.out.println("An error has occurred while trying to play song: " + e.getMessage());
            }
        }
    }

    // START MUSIC THREAD
    private void startMusicThread() {
        new Thread(() -> {
            try {
                if (isPaused) {
                    synchronized (threadSync) {
                        isPaused = false;
                        threadSync.notify();
                    }
                    // Resume last played
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    // Play from beginning
                    advancedPlayer.play();
                }
            } catch (JavaLayerException e) {
                System.out.println("An error has occurred while trying to play music file: " + e.getMessage());
            }
        }).start();
    }

    // START SONG SLIDER THREAD
    private void startSongSliderThread() {
        new Thread(() -> {
            if (isPaused) {
                try {
                    synchronized (threadSync) {
                        threadSync.wait();
                    }
                    
                } catch (InterruptedException e) {
                    System.out.println("An error has occurred while trying to set up song slider: " + e.getMessage());
                }
            }

            while (!isPaused && !hasFinished) {
                try {
                    currentMillis++;
                    var calculatedFrame = (int) (currentMillis * 2.08 * song.getFrameRatePerMillis());
                    
                    // Update slider UI
                    lrcEditor.setSldSongValue(calculatedFrame);
                    
                    // Move forward by 1 millisecond (intentional Thread.sleep in loop)
                    Thread.sleep(1);
                    
                } catch (InterruptedException e) {
                    System.out.println("An error has occurred while trying to set up song slider: " + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        hasFinished = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        if (isPaused) {
            currentFrame += (int) (evt.getFrame() * song.getFrameRatePerMillis());
        } else {
            advancedPlayer = null;
            isPlaying = false;
            isPaused = true;
            hasFinished = true;
            currentFrame = 0;
            currentMillis = 0;
            lrcEditor.setSldSongValue(0);
            lrcEditor.enableMp3PlayerButtons();
        }
    }
}
