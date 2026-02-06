package rs.oris.back.domain.projection;

import java.util.Optional;

public interface ReportEngineProjection {
    String getImei();
    double getEngineSize();
    String getRegistration();
    String getModel();
    String getManufacturer();
    Integer getFuelMargine();

    Optional<DriverSummary> getDriver();

    interface DriverSummary {
        String getName();
    }
}
