package rs.oris.back.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DaysDTO {

    private List<String> days;

    public DaysDTO() {
    }

    public DaysDTO(List<String> days) {
        this.days = days;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}
