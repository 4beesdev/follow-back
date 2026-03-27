package rs.oris.back.controller;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.User;
import rs.oris.back.service.UserService;
import rs.oris.back.service.VehicleService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerAuthorizationTest {

    @Mock
    private VehicleService vehicleService;
    @Mock
    private UserService userService;

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void monthlyLegacyReport_returnsEmptyResultWhenNothingIsAuthorized() throws Exception {
        ReportController controller = new ReportController();
        ReflectionTestUtils.setField(controller, "vehicleService", vehicleService);
        ReflectionTestUtils.setField(controller, "userService", userService);

        User user = new User();
        user.setUsername("legacy-user");
        Firm firm = new Firm();
        firm.setFirmId(1);
        user.setFirm(firm);

        List<String> requestedImeis = Arrays.asList("IMEI-1", "IMEI-2");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("legacy-user", null, Collections.emptyList()));
        when(userService.findByUsername("legacy-user")).thenReturn(user);
        when(vehicleService.filterAccessibleImeis(user, 1, requestedImeis)).thenReturn(Collections.emptyList());
        when(vehicleService.findAllByImeiIn(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<?> result = ReflectionTestUtils.invokeMethod(controller, "izvestajOPredjenomPutuMesecni3", requestedImeis, 1, "2026-01-01", "2026-01-31");

        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void monthlyLegacyReport_returnsEmptyResultWhenPayloadContainsOnlyInvalidImeis() throws Exception {
        ReportController controller = new ReportController();
        ReflectionTestUtils.setField(controller, "vehicleService", vehicleService);
        ReflectionTestUtils.setField(controller, "userService", userService);

        User user = new User();
        user.setUsername("legacy-user");
        Firm firm = new Firm();
        firm.setFirmId(1);
        user.setFirm(firm);

        List<String> requestedImeis = Arrays.asList("/", "", "   ");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("legacy-user", null, Collections.emptyList()));
        when(userService.findByUsername("legacy-user")).thenReturn(user);
        when(vehicleService.filterAccessibleImeis(user, 1, requestedImeis)).thenReturn(Collections.emptyList());
        when(vehicleService.findAllByImeiIn(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<?> result = ReflectionTestUtils.invokeMethod(controller, "izvestajOPredjenomPutuMesecni3", requestedImeis, 1, "2026-03-01", "2026-03-31");

        assertTrue(result.isEmpty());
    }
}
