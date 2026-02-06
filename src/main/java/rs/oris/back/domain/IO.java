package rs.oris.back.domain;

import java.util.List;

public class IO {
    List<IOItem> IO;

    public IO() {
    }

    public IO(List<IOItem> IO) {
        this.IO = IO;
    }

    public List<IOItem> getIO() {
        return IO;
    }

    public void setIO(List<IOItem> IO) {
        this.IO = IO;
    }
}
