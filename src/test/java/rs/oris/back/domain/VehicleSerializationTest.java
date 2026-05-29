package rs.oris.back.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VehicleSerializationTest {

    @Test
    public void vehicleJson_doesNotEmbedDriverObject_norLeakSensitiveData() throws Exception {
        Driver driver = new Driver();
        driver.setName("Pera Perić");
        driver.setJmbg("1234567890123");
        driver.setPhone("063111222");

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ZZ-000-AA");
        vehicle.setDriver(driver);

        String json = new ObjectMapper().writeValueAsString(vehicle);

        // Podaci samog vozila ostaju
        assertTrue(json.contains("ZZ-000-AA"));
        // Pun Driver objekat ne sme da se serijalizuje pod vozilom
        assertFalse(json.contains("\"driver\""));
        // Osetljivi podaci vozača ne smeju da procure
        assertFalse(json.contains("1234567890123"));
        assertFalse(json.contains("063111222"));
    }
}
