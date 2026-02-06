package rs.oris.back.domain.dto;

public class DTORoute {
    private String name;
    private String routeString;

    public DTORoute() {
    }

    public DTORoute(String name, String routeString) {
        this.name = name;
        this.routeString = routeString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteString() {
        return routeString;
    }

    public void setRouteString(String routeString) {
        this.routeString = routeString;
    }
}
