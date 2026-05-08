package rs.oris.back.domain.dto;

import java.util.List;

public class RouteFalloutExportRequest {
    private List<String> imeis;
    private List<Integer> routeIds;

    public RouteFalloutExportRequest() {
    }

    public List<String> getImeis() {
        return imeis;
    }

    public void setImeis(List<String> imeis) {
        this.imeis = imeis;
    }

    public List<Integer> getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(List<Integer> routeIds) {
        this.routeIds = routeIds;
    }
}
