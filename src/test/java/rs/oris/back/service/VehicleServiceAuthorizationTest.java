package rs.oris.back.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import rs.oris.back.domain.Driver;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserVehicleGroup;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.VehicleGroup;
import rs.oris.back.domain.VehicleVehicleGroup;
import rs.oris.back.domain.dto.VehicleWithGroupsDTO;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.Map;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VehicleServiceAuthorizationTest {

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private FirmRepository firmRepository;
    @Mock
    private DriverRepository driverRepository;

    private VehicleService vehicleService;

    @Before
    public void setUp() {
        vehicleService = new VehicleService();
        ReflectionTestUtils.setField(vehicleService, "vehicleRepository", vehicleRepository);
        ReflectionTestUtils.setField(vehicleService, "firmRepository", firmRepository);
        ReflectionTestUtils.setField(vehicleService, "driverRepository", driverRepository);
    }

    @Test
    public void filterAccessibleImeis_returnsAssignedVehiclesForRegularUser() throws Exception {
        Firm firm = firm(1);
        User user = new User();
        user.setFirm(firm);
        user.setAdmin(false);
        user.setSuperAdmin(false);
        user.setUserVehicleGroupSet(new HashSet<>(Collections.singletonList(userVehicleGroup(vehicleGroup(vehicle("IMEI-1", firm), vehicle("IMEI-2", firm))))));

        List<String> filteredImeis = vehicleService.filterAccessibleImeis(user, firm.getFirmId(), Arrays.asList("IMEI-2", "OTHER", "IMEI-1"));

        assertEquals(Arrays.asList("IMEI-2", "IMEI-1"), filteredImeis);
    }

    @Test
    public void filterAccessibleImeis_discardsInvalidPlaceholderValues() throws Exception {
        Firm firm = firm(1);
        User user = new User();
        user.setFirm(firm);
        user.setAdmin(false);
        user.setSuperAdmin(false);
        user.setUserVehicleGroupSet(new HashSet<>(Collections.singletonList(userVehicleGroup(vehicleGroup(vehicle("353201355980449", firm), vehicle("353691845435105", firm))))));

        List<String> filteredImeis = vehicleService.filterAccessibleImeis(
                user,
                firm.getFirmId(),
                Arrays.asList("/", "", "   ", null, "null", "undefined", "353201355980449", "353691845435105", "/")
        );

        assertEquals(Arrays.asList("353201355980449", "353691845435105"), filteredImeis);
    }

    @Test
    public void filterAccessibleImeis_returnsEmptyListWhenAllValuesAreInvalid() throws Exception {
        Firm firm = firm(1);
        User user = new User();
        user.setFirm(firm);
        user.setAdmin(false);
        user.setSuperAdmin(false);
        user.setUserVehicleGroupSet(new HashSet<>(Collections.singletonList(userVehicleGroup(vehicleGroup(vehicle("353201355980449", firm))))));

        List<String> filteredImeis = vehicleService.filterAccessibleImeis(
                user,
                firm.getFirmId(),
                Arrays.asList("/", "", "   ", null, "null", "undefined", "n/a")
        );

        assertEquals(Collections.emptyList(), filteredImeis);
    }

    @Test
    public void filterAccessibleImeis_returnsAllFirmVehiclesForAdmin() throws Exception {
        Firm firm = firm(1);
        User user = new User();
        user.setFirm(firm);
        user.setAdmin(true);
        user.setSuperAdmin(false);

        when(vehicleRepository.findByFirmFirmIdAndDeletedDate(eq(1), any())).thenReturn(Arrays.asList(vehicle("IMEI-1", firm), vehicle("IMEI-3", firm)));

        List<String> filteredImeis = vehicleService.filterAccessibleImeis(user, firm.getFirmId(), Arrays.asList("IMEI-3", "OTHER", "IMEI-1"));

        assertEquals(Arrays.asList("IMEI-3", "IMEI-1"), filteredImeis);
    }

    @Test
    public void filterAccessibleImeis_returnsRequestedFirmVehiclesForSuperAdmin() throws Exception {
        Firm firm = firm(2);
        User user = new User();
        user.setSuperAdmin(true);
        user.setAdmin(true);

        when(firmRepository.findById(2)).thenReturn(Optional.of(firm));
        when(vehicleRepository.findByFirmFirmIdAndDeletedDate(eq(2), any())).thenReturn(Collections.singletonList(vehicle("IMEI-9", firm)));

        List<String> filteredImeis = vehicleService.filterAccessibleImeis(user, 2, Arrays.asList("IMEI-9", "IMEI-10"));

        assertEquals(Collections.singletonList("IMEI-9"), filteredImeis);
    }

    @Test
    public void getAllVehicles_setsDriverNameFromFirmDrivers() throws Exception {
        Firm firm = firm(7);
        User user = new User();
        user.setSuperAdmin(true);
        user.setAdmin(true);

        Vehicle withDriver = vehicle("IMEI-A", firm);
        withDriver.setVehicleId(101);
        Vehicle withoutDriver = vehicle("IMEI-B", firm);
        withoutDriver.setVehicleId(102);

        Driver driver = new Driver();
        driver.setName("Pera Perić");
        driver.setVehicle(withDriver);

        when(firmRepository.findById(7)).thenReturn(Optional.of(firm));
        when(vehicleRepository.findWithGroupsByFirmIdAndActive(7))
                .thenReturn(Arrays.asList(withDriver, withoutDriver));
        when(driverRepository.findByFirmFirmId(7))
                .thenReturn(Collections.singletonList(driver));

        Map<String, List<VehicleWithGroupsDTO>> result =
                vehicleService.getAllVehicles(user, 7).getData();

        List<VehicleWithGroupsDTO> dtos = result.get("vehicles");
        assertEquals(2, dtos.size());

        VehicleWithGroupsDTO dtoWith = dtos.stream()
                .filter(d -> d.getVehicle().getVehicleId() == 101).findFirst().get();
        VehicleWithGroupsDTO dtoWithout = dtos.stream()
                .filter(d -> d.getVehicle().getVehicleId() == 102).findFirst().get();

        assertEquals("Pera Perić", dtoWith.getDriverName());
        assertNull(dtoWithout.getDriverName());
    }

    private UserVehicleGroup userVehicleGroup(VehicleGroup vehicleGroup) {
        UserVehicleGroup userVehicleGroup = new UserVehicleGroup();
        userVehicleGroup.setVehicleGroup(vehicleGroup);
        return userVehicleGroup;
    }

    private VehicleGroup vehicleGroup(Vehicle... vehicles) {
        VehicleGroup vehicleGroup = new VehicleGroup();
        HashSet<VehicleVehicleGroup> relations = new HashSet<>();
        for (Vehicle vehicle : vehicles) {
            VehicleVehicleGroup relation = new VehicleVehicleGroup();
            relation.setVehicle(vehicle);
            relation.setVehicleGroup(vehicleGroup);
            relations.add(relation);
        }
        vehicleGroup.setVehicleVehicleGroupSet(relations);
        return vehicleGroup;
    }

    private Vehicle vehicle(String imei, Firm firm) {
        Vehicle vehicle = new Vehicle();
        vehicle.setImei(imei);
        vehicle.setFirm(firm);
        vehicle.setDeletedDate(null);
        return vehicle;
    }

    private Firm firm(int firmId) {
        Firm firm = new Firm();
        firm.setFirmId(firmId);
        return firm;
    }
}
