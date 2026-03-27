package rs.oris.back.controller;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.User;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
import rs.oris.back.service.ReportService;
import rs.oris.back.service.UserService;
import rs.oris.back.service.VehicleService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportsControllerAuthorizationTest {

    @Mock
    private ReportService reportService;
    @Mock
    private UserService userService;
    @Mock
    private VehicleService vehicleService;

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getMonthlyFuelReports_usesFilteredImeis() throws Exception {
        ReportsController controller = new ReportsController(reportService, userService, vehicleService);
        User user = new User();
        Firm firm = new Firm();
        firm.setFirmId(1);
        user.setFirm(firm);
        user.setUsername("report-user");

        List<String> requestedImeis = Arrays.asList("IMEI-1", "IMEI-2", "IMEI-3");
        List<String> filteredImeis = Collections.singletonList("IMEI-2");
        List<MonthlyFuelConsumptionReport> reports = Collections.emptyList();
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 31, 23, 59);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("report-user", null, Collections.emptyList()));
        when(userService.findByUsername("report-user")).thenReturn(user);
        when(vehicleService.filterAccessibleImeis(user, null, requestedImeis)).thenReturn(filteredImeis);
        when(reportService.getMonthFuelReport(from, to, filteredImeis, 2, 20)).thenReturn(reports);

        ResponseEntity<List<MonthlyFuelConsumptionReport>> response = controller.getMonthlyFuelReports(from, to, requestedImeis, 2, 20);

        assertSame(reports, response.getBody());
        verify(reportService).getMonthFuelReport(from, to, filteredImeis, 2, 20);
    }

    @Test
    public void getMonthlyFuelReports_ignoresInvalidImeisAndUsesOnlyValidOnes() throws Exception {
        ReportsController controller = new ReportsController(reportService, userService, vehicleService);
        User user = new User();
        Firm firm = new Firm();
        firm.setFirmId(1);
        user.setFirm(firm);
        user.setUsername("report-user");

        List<String> requestedImeis = Arrays.asList("/", "353201355980449", "353691845435105");
        List<String> filteredImeis = Arrays.asList("353201355980449", "353691845435105");
        List<MonthlyFuelConsumptionReport> reports = Collections.emptyList();
        LocalDateTime from = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 31, 23, 59);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("report-user", null, Collections.emptyList()));
        when(userService.findByUsername("report-user")).thenReturn(user);
        when(vehicleService.filterAccessibleImeis(user, null, requestedImeis)).thenReturn(filteredImeis);
        when(reportService.getMonthFuelReport(from, to, filteredImeis, 2, 20)).thenReturn(reports);

        ResponseEntity<List<MonthlyFuelConsumptionReport>> response = controller.getMonthlyFuelReports(from, to, requestedImeis, 2, 20);

        assertSame(reports, response.getBody());
        verify(reportService).getMonthFuelReport(from, to, filteredImeis, 2, 20);
    }
}
