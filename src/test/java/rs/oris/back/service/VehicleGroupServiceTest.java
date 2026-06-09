package rs.oris.back.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.User;
import rs.oris.back.domain.VehicleGroup;
import rs.oris.back.repository.VehicleGroupRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VehicleGroupServiceTest {

    @Mock
    private VehicleGroupRepository vehicleGroupRepository;

    private VehicleGroupService vehicleGroupService;

    @Before
    public void setUp() {
        vehicleGroupService = new VehicleGroupService();
        ReflectionTestUtils.setField(vehicleGroupService, "vehicleGroupRepository", vehicleGroupRepository);
    }

    private VehicleGroup existingGroup(int id, String name, int firmId) {
        Firm firm = new Firm();
        firm.setFirmId(firmId);
        VehicleGroup group = new VehicleGroup();
        group.setVehicleGroupId(id);
        group.setName(name);
        group.setFirm(firm);
        return group;
    }

    @Test
    public void updateVehicleGroup_updatesNameAndSaves() throws Exception {
        VehicleGroup existing = existingGroup(10, "Staro ime", 5);
        VehicleGroup incoming = new VehicleGroup();
        incoming.setName("Novo ime");

        when(vehicleGroupRepository.findById(10)).thenReturn(Optional.of(existing));
        when(vehicleGroupRepository.save(any(VehicleGroup.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VehicleGroup result = vehicleGroupService
                .updateVehicleGroup(new User(), 10, incoming, 5L)
                .getData();

        assertEquals("Novo ime", result.getName());
    }

    @Test(expected = Exception.class)
    public void updateVehicleGroup_rejectsForeignFirm() throws Exception {
        VehicleGroup existing = existingGroup(10, "Staro ime", 5);

        when(vehicleGroupRepository.findById(10)).thenReturn(Optional.of(existing));

        // firmId 999 != firma grupe (5) -> mora baciti Exception
        vehicleGroupService.updateVehicleGroup(new User(), 10, new VehicleGroup(), 999L);
    }

    @Test(expected = Exception.class)
    public void updateVehicleGroup_throwsWhenNotFound() throws Exception {
        when(vehicleGroupRepository.findById(123)).thenReturn(Optional.empty());

        vehicleGroupService.updateVehicleGroup(new User(), 123, new VehicleGroup(), 5L);
    }
}
