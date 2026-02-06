package rs.oris.back.domain;


public class MongoItem {

    private int _id;
    private Gps gps;
    private IO IO;

    public MongoItem() {
    }

    public MongoItem(int _id, Gps gps, rs.oris.back.domain.IO IO) {
        this._id = _id;
        this.gps = gps;
        this.IO = IO;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public rs.oris.back.domain.IO getIO() {
        return IO;
    }

    public void setIO(rs.oris.back.domain.IO IO) {
        this.IO = IO;
    }
}
