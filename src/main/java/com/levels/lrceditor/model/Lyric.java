package com.levels.lrceditor.model;

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

    public String getTimestampString() {
        if (this.timestamp == null) {
            return "";
        }
        return String.format("[%02d:%02d.%03d]", this.timestamp.toMinutesPart(), this.timestamp.toSecondsPart(), this.timestamp.toMillisPart());
    }
    
    public String toLyricString() {
        // Only one space, line breaks
        return '\n' + getTimestampString() + ' ' + lyric;
    }

    @Override
    public String toString() {
        // Two spaces for better visibility
        return getTimestampString() + "  " + lyric;
    }
}
