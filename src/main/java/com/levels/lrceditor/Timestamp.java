package com.levels.lrceditor;

/**
 *
 * @author Leandro Valdearenas
 */
public class Timestamp {

    private int minutes;
    private int seconds;
    private int millis;

    public Timestamp(int millis) {
        var totalSeconds = millis / 1000;
        this.minutes = totalSeconds / 60;
        this.seconds = totalSeconds % 60;
        this.millis = millis % 1000;
    }

    public Timestamp(int minutes, int seconds, int millis) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.millis = millis;
    }
    
    public int toMillis() {
        return 1000 * (60 * minutes + seconds) + millis;
    }

    public int compareTo(Timestamp other) {
        return this.toMillis() - other.toMillis();
    }

    public Timestamp plus(Timestamp other) {
        return new Timestamp(this.toMillis() + other.toMillis());
    }

    public Timestamp minus(Timestamp other) {
        return new Timestamp(this.toMillis() - other.toMillis());
    }

    /**
     * Turns timestamp to string with deciseconds instead of millis (Ex: 00:00.0)
     * @return String with one-digit deciseconds
     */
    public String toShortString() {
        return String.format("%02d:%02d.%d", minutes, seconds, millis / 100);
    }
    
    /**
     * Turns timestamp to string with milliseconds (Ex: 00:00.000)
     * @return String with three-digit milliseconds
     */
    public String toLongString() {
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }

    @Override
    public String toString() {
        return String.format("[%s]", toLongString());
    }
}
