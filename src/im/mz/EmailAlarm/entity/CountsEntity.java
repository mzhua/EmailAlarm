package im.mz.EmailAlarm.entity;

/**
 * Created by Hua on 2014/11/7.
 */
public class CountsEntity {
    private long id;
    private long date;
    private int counts;
    private String week;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
