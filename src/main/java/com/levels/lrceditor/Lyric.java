package com.levels.lrceditor;

/**
 *
 * @author Leandro Valdearenas
 */
public class Lyric {

    private Timestamp timestamp;
    private String lyric;

    public Lyric() {
    }

    public Lyric(Timestamp timestamp, String lyric) {
        this.timestamp = timestamp;
        this.lyric = lyric;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
    
    public String getTimestampString() {
        // If timestamp is null, return empty space
        if (timestamp == null) {
            return "";
        }
        return timestamp.toString();
    }

    @Override
    public String toString() {
        // Two spaces for better visibility
        return String.format("%s  %s", getTimestampString(), lyric);
    }

    public String toLrcString() {
        // Only one space, line breaks
        return String.format("\n%s %s", getTimestampString(), lyric);
    }

    public void moveTimestamp(Timestamp offset, boolean addition) {
        this.timestamp = addition ? timestamp.plus(offset) : timestamp.minus(offset);
    }
}
