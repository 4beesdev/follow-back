package rs.oris.back.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DriverSerializationTest {

    @Test
    public void driverJson_doesNotIncludeVehicle_andDoesNotRecurse() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ZZ-000-AA");

        Driver driver = new Driver();
        driver.setName("Pera Perić");
        driver.setVehicle(vehicle);   // dvosmerna veza koja bi inače pravila rekurziju

        // Obe strane upućuju jedna na drugu
        vehicle.setDriver(driver);

        String json = new ObjectMapper().writeValueAsString(driver);

        // Ime vozača mora ostati
        assertTrue(json.contains("Pera Perić"));
        // Inverzno polje 'vehicle' na vozaču NE sme da se serijalizuje
        assertFalse(json.contains("\"vehicle\""));
    }
}
