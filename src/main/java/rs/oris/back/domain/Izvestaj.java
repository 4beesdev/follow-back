package rs.oris.back.domain;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "izvestaj_test")
public class Izvestaj {

    @Id
    @GeneratedValue
    private int id;
    private Date date;
    private int brojSati;
    private int brojNaplativihSati;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advokat_id")
    private User user;
    private boolean potvrdjeno;

    public Izvestaj() {
    }

    public Izvestaj(int id, Date date, int brojSati, int brojNaplativihSati, User user, boolean potvrdjeno) {
        this.id = id;
        this.date = date;
        this.brojSati = brojSati;
        this.brojNaplativihSati = brojNaplativihSati;
        this.user = user;
        this.potvrdjeno = potvrdjeno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getBrojSati() {
        return brojSati;
    }

    public void setBrojSati(int brojSati) {
        this.brojSati = brojSati;
    }

    public int getBrojNaplativihSati() {
        return brojNaplativihSati;
    }

    public void setBrojNaplativihSati(int brojNaplativihSati) {
        this.brojNaplativihSati = brojNaplativihSati;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isPotvrdjeno() {
        return potvrdjeno;
    }

    public void setPotvrdjeno(boolean potvrdjeno) {
        this.potvrdjeno = potvrdjeno;
    }
}
