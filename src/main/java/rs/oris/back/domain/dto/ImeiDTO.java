package rs.oris.back.domain.dto;

public class ImeiDTO {
    private String oldImei;
    private String newImei;

    public ImeiDTO() {
    }

    public ImeiDTO(String oldImei, String newImei) {
        this.oldImei = oldImei;
        this.newImei = newImei;
    }

    public String getOldImei() {
        return oldImei;
    }

    public void setOldImei(String oldImei) {
        this.oldImei = oldImei;
    }

    public String getNewImei() {
        return newImei;
    }

    public void setNewImei(String newImei) {
        this.newImei = newImei;
    }
}
