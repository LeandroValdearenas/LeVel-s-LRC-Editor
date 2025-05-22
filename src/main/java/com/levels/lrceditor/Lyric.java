package com.levels.lrceditor;

import java.time.Duration;

/**
 *
 * @author Leandro Valdearenas
 */
public class Lyric {

    private Duration timestamp;
    private String lyric;

    public Lyric() {
    }

    public Lyric(Duration timestamp, String lyric) {
        this.timestamp = timestamp;
        this.lyric = lyric;
    }
    
    public Duration getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Duration timestamp) {
        this.timestamp = timestamp;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    @Override
    public String toString() {
        // Two spaces for better visibility
        return String.format("[%s]  %s", timestampToString(this.timestamp), lyric);
    }
    
    public String toLrcString() {
        // Only one space, line breaks
        return String.format("\n[%s] %s", timestampToString(this.timestamp), lyric);
    }

    public void moveTimestamp(Duration offset, boolean addition) {
        this.timestamp = addition ? timestamp.plus(offset) : timestamp.minus(offset);
    }

    public static String timestampToString(Duration timestamp) {
        if (timestamp == null) {
            return "";
        }
        return String.format("%02d:%02d.%03d", timestamp.toMinutesPart(), timestamp.toSecondsPart(), timestamp.toMillisPart());
    }
}
