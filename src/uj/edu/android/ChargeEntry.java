package uj.edu.android;

import java.sql.Time;
import java.util.Date;

/**
 * Created by shybovycha on 24.01.15.
 */
public class ChargeEntry {
    protected Long startTime;
    protected Long endTime;
    protected Long id;

    public ChargeEntry() {
        this.id = null;
        this.startTime = new Date().getTime();
        this.endTime = null;
    }

    public void stopCharging() {
        this.endTime = new Date().getTime();
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDuration() {
        if (this.endTime != null)
            return endTime - startTime;

        return (new Date().getTime()) - startTime;
    }
}
