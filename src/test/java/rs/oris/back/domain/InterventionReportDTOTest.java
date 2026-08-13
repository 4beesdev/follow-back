package rs.oris.back.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * FMS izvestaj intervencija ne prikazuje priloge, pa red izvestaja ne sme
 * da nosi sadrzaj fajlova (Postgres large object) - njegovo citanje van
 * transakcije je rusilo ceo izvestaj.
 */
public class InterventionReportDTOTest {

    private Intervention interventionWithAttachment() {
        Vehicle vehicle = new Vehicle();
        vehicle.setRegistration("SA 166-FO");
        vehicle.setModel("Mercedes Actros");

        ServiceLocation serviceLocation = new ServiceLocation();
        serviceLocation.setName("PSC Vukovic");

        InterventionFiles file = new InterventionFiles();
        file.setFileName("2026-VPDS.pdf");
        file.setFileType("application/pdf");
        file.setFileContent("SADRZAJ-PRILOGA-KOJI-NE-SME-U-IZVESTAJ".getBytes());

        Intervention intervention = new Intervention();
        intervention.setInterventionId(628215);
        intervention.setDoneDate(new Date());
        intervention.setDoneTime("10:30");
        intervention.setDescription("Zamenjen set kvacila");
        intervention.setPrice(104847);
        intervention.setNote("napomena");
        intervention.setVehicle(vehicle);
        intervention.setServiceLocation(serviceLocation);
        intervention.setInterventionFiles(new ArrayList<>(Collections.singletonList(file)));
        return intervention;
    }

    @Test
    public void reportRow_keepsColumnsThatReportShows() throws Exception {
        String json = new ObjectMapper().writeValueAsString(InterventionDTO.from(interventionWithAttachment()));

        assertTrue(json.contains("SA 166-FO"));
        assertTrue(json.contains("Mercedes Actros"));
        assertTrue(json.contains("PSC Vukovic"));
        assertTrue(json.contains("Zamenjen set kvacila"));
        assertTrue(json.contains("10:30"));
        assertTrue(json.contains("104847"));
        assertTrue(json.contains("napomena"));
    }

    @Test
    public void reportRow_doesNotCarryFileBlobs() throws Exception {
        String json = new ObjectMapper().writeValueAsString(InterventionDTO.from(interventionWithAttachment()));

        assertFalse(json.contains("interventionFiles"));
        assertFalse(json.contains("fileContent"));
        assertFalse(json.contains("2026-VPDS.pdf"));
        assertFalse(json.contains("SADRZAJ-PRILOGA-KOJI-NE-SME-U-IZVESTAJ"));
    }
}
