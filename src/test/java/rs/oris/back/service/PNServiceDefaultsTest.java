package rs.oris.back.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Driver;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.FirmPnCounter;
import rs.oris.back.domain.PN;
import rs.oris.back.domain.User;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.FirmPnCounterRepository;
import rs.oris.back.repository.PNRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PNServiceDefaultsTest {

    @Mock
    private PNRepository pnRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private FirmPnCounterRepository firmPnCounterRepository;
    @Mock
    private UserPnDefaultsService userPnDefaultsService;

    private PNService pnService;

    @Before
    public void setUp() {
        pnService = new PNService();
        ReflectionTestUtils.setField(pnService, "pNRepository", pnRepository);
        ReflectionTestUtils.setField(pnService, "driverRepository", driverRepository);
        ReflectionTestUtils.setField(pnService, "vehicleRepository", vehicleRepository);
        ReflectionTestUtils.setField(pnService, "firmPnCounterRepository", firmPnCounterRepository);
        ReflectionTestUtils.setField(pnService, "userPnDefaultsService", userPnDefaultsService);
    }

    @Test
    public void createPN_appliesCurrentUserDefaultsBeforeSave() throws Exception {
        Firm firm = new Firm();
        firm.setFirmId(11);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId(4);
        vehicle.setFirm(firm);

        Driver driver = new Driver();
        driver.setDriverId(9);

        FirmPnCounter counter = new FirmPnCounter(11, 5, 2);
        PN pn = new PN();
        pn.setTrailer(false);

        User user = new User();
        user.setUserId(15);

        when(vehicleRepository.findById(4)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(9)).thenReturn(Optional.of(driver));
        when(firmPnCounterRepository.findForUpdate(11)).thenReturn(Optional.of(counter));
        when(firmPnCounterRepository.save(counter)).thenReturn(counter);
        when(pnRepository.save(pn)).thenReturn(pn);

        Response<PN> response = pnService.createPN(pn, 4, 9, user);

        verify(userPnDefaultsService).applyDefaultsToPnIfMissing(pn, user);
        assertEquals(Integer.valueOf(6), response.getData().getNoSeq());
        assertEquals(vehicle, response.getData().getVehicle());
        assertEquals(driver, response.getData().getDriver());
    }
}
