package rs.oris.back.domain;

public class Green {
    double slat;
    double slng;
    double elat;
    double elng;
    String type;
    long timestmap;
    double sspeed;//startspeed
    double espeed;//endspeed

    public Green() {
    }

    public Green(double slat, double slng, double elat, double elng, String type, long timestmap, double sspeed, double espeed) {
        this.slat = slat;
        this.slng = slng;
        this.elat = elat;
        this.elng = elng;
        this.type = type;
        this.timestmap = timestmap;
        this.sspeed = sspeed;
        this.espeed = espeed;
    }

    public double getSlat() {
        return slat;
    }

    public void setSlat(double slat) {
        this.slat = slat;
    }

    public double getSlng() {
        return slng;
    }

    public void setSlng(double slng) {
        this.slng = slng;
    }

    public double getElat() {
        return elat;
    }

    public void setElat(double elat) {
        this.elat = elat;
    }

    public double getElng() {
        return elng;
    }

    public void setElng(double elng) {
        this.elng = elng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestmap() {
        return timestmap;
    }

    public void setTimestmap(long timestmap) {
        this.timestmap = timestmap;
    }

    public double getSspeed() {
        return sspeed;
    }

    public void setSspeed(double sspeed) {
        this.sspeed = sspeed;
    }

    public double getEspeed() {
        return espeed;
    }

    public void setEspeed(double espeed) {
        this.espeed = espeed;
    }
}
