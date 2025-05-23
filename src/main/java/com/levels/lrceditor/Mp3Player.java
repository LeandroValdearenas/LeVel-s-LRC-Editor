package com.levels.lrceditor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public boolean isPaused() {
        return isPaused;
    }
    
    public Song getSong() {
        return song;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getCurrentMillis() {
        return currentMillis;
    }
    
    public void setCurrentMillis(int currentMillis) {
        this.currentMillis = currentMillis;
    }
    
    // Whether or not a song has been selected
    public boolean hasSelectedSong() {
        return this.song.getAbsolutePath() != null;
    }

    // Add Song Length, only if song length is null.
    public void addSongLength(String length) {
        if (this.song.getLength() == null) {
            this.song.setLength(length);
        }
    }

    public void loadSong(Song song) {
        // Stop song first
        if (!hasFinished) {
            pauseSong();
        }

        // Set song
        this.song = song;

        // Play song
        if (this.song != null) {
            currentFrame = 0;
            currentMillis = 0;
            lrcEditor.setSldPlaybackMaximum(song.getFrameCount());
            lrcEditor.setSldPlaybackValue(0);
            lrcEditor.enableMp3Player();
        }
    }

    public void pauseSong() {
        isPaused = true;
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

                hasFinished = false;

                startMusicThread();
                startSongSliderThread();

            } catch (FileNotFoundException | JavaLayerException e) {
                Logger.getLogger(Mp3Player.class.getName()).log(Level.INFO, "An error has occurred while trying to play song: {0}", e.getMessage());
            }
        }
    }

    public void endSong() {
        pauseSong();
        resetValues();
    }

    private void resetValues() {
        isPaused = false;
        hasFinished = true;
        currentFrame = 0;
        currentMillis = 0;
        lrcEditor.setSldPlaybackValue(0);
        lrcEditor.enablePlay();
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
                Logger.getLogger(Mp3Player.class.getName()).log(Level.INFO, "An error has occurred while trying to play music file: {0}", e.getMessage());
            }
        }).start();
    }

    // START SONG SLIDER THREAD
    private void startSongSliderThread() {
        if (isPaused) {
            try {
                synchronized (threadSync) {
                    threadSync.wait();
                }

            } catch (InterruptedException e) {
                Logger.getLogger(Mp3Player.class.getName()).log(Level.INFO, "An error has occurred while trying to set up song slider: {0}", e.getMessage());
            }
        }

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
        executor.scheduleAtFixedRate(() -> {
            if (isPaused || hasFinished) {
                executor.shutdown();
                return;
            }
            
            currentMillis += 50;
            
            // Update current timestamp  UI
            lrcEditor.setLblSongTimestampValue(currentMillis);
            
            var calculatedFrame = (int) (currentMillis * song.getFrameRatePerMillis());

            // Update slider UI
            lrcEditor.setSldPlaybackValue(calculatedFrame);
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        if (isPaused) {
            currentFrame += (int) (evt.getFrame() * song.getFrameRatePerMillis());
        } else {
            advancedPlayer = null;
            resetValues();
        }
    }
}
