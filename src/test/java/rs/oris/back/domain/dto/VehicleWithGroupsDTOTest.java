package rs.oris.back.domain.dto;

import org.junit.Test;
import rs.oris.back.domain.Vehicle;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class VehicleWithGroupsDTOTest {

    @Test
    public void from_leavesDriverNameNullByDefault() {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("ZZ-000-AA");

        VehicleWithGroupsDTO dto = VehicleWithGroupsDTO.from(vehicle, null);

        assertNull(dto.getDriverName());
        assertTrue(dto.getVehicleVehicleGroupSet().isEmpty());
    }

    @Test
    public void driverName_getterSetter_roundTrips() {
        VehicleWithGroupsDTO dto = new VehicleWithGroupsDTO();
        dto.setDriverName("Pera Perić");
        assertEquals("Pera Perić", dto.getDriverName());
    }
}
