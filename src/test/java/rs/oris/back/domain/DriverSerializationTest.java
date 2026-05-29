package rs.oris.back.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DriverSerializationTest {

    @Test
    public void driverWithAssignedVehicle_serializesWithoutRecursion() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ZZ-000-AA");

        Driver driver = new Driver();
        driver.setName("Pera Perić");
        driver.setVehicle(vehicle);
        // Povratna referenca koja bi bez @JsonIgnore na Vehicle.driver pravila rekurziju
        vehicle.setDriver(driver);

        // Ako rekurzija nije prekinuta, ovo baca StackOverflowError; uspešan poziv je sam po sebi provera.
        String json = new ObjectMapper().writeValueAsString(driver);

        // Ime vozača ostaje
        assertTrue(json.contains("Pera Perić"));
        // Vozač zadržava dodeljeno vozilo u JSON-u (potrebno za edit formu i za deserijalizaciju pri dodeli)
        assertTrue(json.contains("ZZ-000-AA"));
    }
}
