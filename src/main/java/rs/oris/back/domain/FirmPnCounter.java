package rs.oris.back.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "firm_pn_counter")
public class FirmPnCounter {

    @Id
    @Column(name = "firm_id")
    private Integer firmId;

    @Column(name = "last_passenger_no", nullable = false)
    private Integer lastPassengerNo = 0; //putnicka vozila

    @Column(name = "last_cargo_no", nullable = false)
    private Integer lastCargoNo = 0; //teretna vozila

    @Version
    private Long version;

    public FirmPnCounter() {}

    public FirmPnCounter(Integer firmId, Integer lastPassengerNo, Integer lastCargoNo) {
        this.firmId = firmId;
        this.lastPassengerNo = lastPassengerNo == null ? 0 : lastPassengerNo;
        this.lastCargoNo = lastCargoNo == null ? 0 : lastCargoNo;
    }

    public Integer getFirmId() {
        return firmId;
    }

    public void setFirmId(Integer firmId) {
        this.firmId = firmId;
    }

    public Integer getLastPassengerNo() {
        return lastPassengerNo;
    }

    public void setLastPassengerNo(Integer lastPassengerNo) {
        this.lastPassengerNo = lastPassengerNo;
    }

    public Integer getLastCargoNo() {
        return lastCargoNo;
    }

    public void setLastCargoNo(Integer lastCargoNo) {
        this.lastCargoNo = lastCargoNo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

