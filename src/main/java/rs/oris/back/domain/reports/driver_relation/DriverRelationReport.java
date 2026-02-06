package rs.oris.back.domain.reports.driver_relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRelationReport {

    private Double totalTraveledDistance;
    private Long totalTotalTime;
    private Long totalMovementTime;
    private Long totalIdleTime;
    private Double totalAvgSpeed;
    private Integer totalMaxSpeed;
    List<DriverRelationReportData> reports;



}
