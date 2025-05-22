package com.levels.lrceditor;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.IOException;

/**
 *
 * @author Leandro Valdearenas
 */
public class Song {

    private String absolutePath;
    private String length;
    private int frameCount;
    private double frameRatePerMillis;

    public Song() {
    }

    public Song(String absolutePath) throws Exception {
        this.absolutePath = absolutePath;
        setValuesFromMp3();
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getFrameCount() {
        return frameCount;
    }
    
    public double getFrameRatePerMillis() {
        return frameRatePerMillis;
    }

    private void setValuesFromMp3() throws IOException, UnsupportedTagException, InvalidDataException {
        var mp3File = new Mp3File(absolutePath);
        this.frameCount = mp3File.getFrameCount();
        this.frameRatePerMillis = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
        this.length = String.format("%02d:%02d.%03d",
                mp3File.getLengthInSeconds() / 60,
                mp3File.getLengthInSeconds() % 60,
                mp3File.getLengthInMilliseconds() % 1000);
    }

}
