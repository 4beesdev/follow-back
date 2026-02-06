package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "invoiced_transport")
public class InvoicedTransport {
    @Id
    @GeneratedValue
    @Column(name = "invoice_transport_id")
    private int invoicedTransportId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    private Date date;
    @Column(name = "invoice_number")
    private String invoiceNumber;
    private String route;
    private String expenses;
    @Column(name = "drivers_expense")
    private String driversExpense;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;


    public InvoicedTransport() {
    }

    public InvoicedTransport(int invoicedTransportId, Vehicle vehicle, Date date, String invoiceNumber, String route, String expenses, String driversExpense, Driver driver) {
        this.invoicedTransportId = invoicedTransportId;
        this.vehicle = vehicle;
        this.date = date;
        this.invoiceNumber = invoiceNumber;
        this.route = route;
        this.expenses = expenses;
        this.driversExpense = driversExpense;
        this.driver = driver;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public int getInvoicedTransportId() {
        return invoicedTransportId;
    }

    public void setInvoicedTransportId(int invoicedTransportId) {
        this.invoicedTransportId = invoicedTransportId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getExpenses() {
        return expenses;
    }

    public void setExpenses(String expenses) {
        this.expenses = expenses;
    }

    public String getDriversExpense() {
        return driversExpense;
    }

    public void setDriversExpense(String driversExpense) {
        this.driversExpense = driversExpense;
    }
}
