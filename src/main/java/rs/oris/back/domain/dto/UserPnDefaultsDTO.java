package rs.oris.back.domain.dto;

public class UserPnDefaultsDTO {

    private String companyOwner;
    private String companyAddress;
    private String placeOfIssue;
    private String transportType;
    private String garageAddress;

    public UserPnDefaultsDTO() {
    }

    public UserPnDefaultsDTO(String companyOwner, String companyAddress, String placeOfIssue, String transportType, String garageAddress) {
        this.companyOwner = companyOwner;
        this.companyAddress = companyAddress;
        this.placeOfIssue = placeOfIssue;
        this.transportType = transportType;
        this.garageAddress = garageAddress;
    }

    public String getCompanyOwner() {
        return companyOwner;
    }

    public void setCompanyOwner(String companyOwner) {
        this.companyOwner = companyOwner;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getPlaceOfIssue() {
        return placeOfIssue;
    }

    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getGarageAddress() {
        return garageAddress;
    }

    public void setGarageAddress(String garageAddress) {
        this.garageAddress = garageAddress;
    }
}
