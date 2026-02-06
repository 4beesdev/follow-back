package rs.oris.back.domain.dto;

public class DTOSms {
    private String sender = "Oris34";
    private String phoneNumber;
    private String text;
    private String priority = "NORMAL";

    public DTOSms() {
    }

    public DTOSms(String sender, String phoneNumber, String text, String priority) {
        this.sender = sender;
        this.phoneNumber = phoneNumber;
        this.text = text;
        this.priority = priority;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
